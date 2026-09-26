/*  																						
	    jCompany Full-Stack Framework - Community Version									
	    Copyright (C) 2008  Powerlogic														
																							
	    This program is free software: you can redistribute it and/or modify				
	    it under the terms of the GNU General Public License as published by				
	    the Free Software Foundation, version 3 of the License.								
	    																					
	    This program is distributed in the hope that it will be useful,						
	    but WITHOUT ANY WARRANTY; without even the implied warranty of						
	    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the						
	    GNU General Public License for more details.										
	    																					
	    You should have received a copy of the GNU General Public License					
	    along with this program.  If not, see <http://www.gnu.org/licenses/>.				
																							
	    Contact: plc@powerlogic.com.br - www.powerlogic.com.br 								
																							
 */ 
package org.jcompany.commons.aop;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.SetTopRule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcConstantsCommons;


/**
 * jCompany 3.0. Utilitário de Profiling Simplificado. Contém utilitários
 * importantes para exibição hierarquizada de métodos de classes instanciadas via AOP (CGLib), quando
 * a opção de log para "performance" está em nível DEBUG ou nível de monitoria ligado.
 */ 
public class PlcAopProfilingHelper {
	
	private static final String LINE_END = "</td></tr>";
	private static final String LINE_BEGIN = "<tr><td>";
	private static String STOPWATCH = "stopWatch";
	private static String GET_SESSION = "getSession";
	private static String LAST_METHOD = "ultMetodo";
	private static String IDENT = "ident";
	private static String TRANSACTION = "transacao";
	private static String HEAP_MEMORY = "memoriaHeap";
	//Tem que deixar null na inicialização, pois da forma not null não ativa o verificador de sla!
	private static String DOC_GENERATE = null;
	private static String INITIAL_TIME = "INITIAL_TIME";
	private static PlcAopProfilingHelper INSTANCE = null;	
 	private PlcAopProfilingHelper() { }
 	
 	/**
 	 * @since jCompany 3.0
	 */
 	public static PlcAopProfilingHelper getInstance(){
 	   if (INSTANCE==null)
 		   INSTANCE = new PlcAopProfilingHelper();
 	   return INSTANCE;
 	}
 	
 	/**
 	 * Mantém em caching o level do Profiling (nao é thread safety portanto somente para uso em desenvolvimento)
 	 */
	private int level=-1;
	private int totalCommit=0;
	private int totalRollback=0;
	
	protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
	protected static final Logger logDocAutomated = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_DOC_AUTOMATED);
	
	public void transactionCount(String type) {
		if ("commit".equals(type))
			totalCommit++;
		else if ("rollback".equals(type))
			totalRollback++;
	}
	
	
	
	/**
	 * @since jCompany 3.0
	 * Thread Local para incluir contadores quando executando em modo de "Profiling Simplificado" do jCompany.
	 */
	private static ThreadLocal<Map<String,Object>> threadProfile =  new ThreadLocal<Map<String,Object>>();
	
	  /**
	   * @since jCompany 3.0
	   * Soma 1 ou inclui 1 se for null no contador para Profiling
	   */
	  public void increaseIndent(String msg) {
		  //System.out.println("Vai incrementar de "+getIndent()+" em "+msg);
		  if (threadProfile.get()==null)
			  inicializeThread();
		  Map<String,Object> m = threadProfile.get();
		  Integer ident = (Integer)m.get(IDENT);
		  if (ident == null || ident.intValue()==0)
			  m.put(IDENT,new Integer(1));
		  else
			  m.put(IDENT,ident.intValue()+1);
	  }
	  
	  /**
	   * @param msg 
	 * @since jCompany 3.0
	   * Subtrai um do contador de identação para Profiling
	   */
	  public void decreaseIndent(String msg) {
		  //System.out.println("Vai decrementar de "+getIndent()+" em "+msg);
		  Map<String,Object> m = threadProfile.get();
		  Integer ident = (Integer)m.get(IDENT);
		  if (ident != null && ident.intValue()>0)
			  m.put(IDENT,ident.intValue()-1);
	  }
	  
	  /**
	   * @since jCompany 3.0
	   * Recupera int para endentacao para "Profiling Simplificado" do jCompany.
	   */
	  public int getIndent() {
		  if (threadProfile.get()==null)
			  inicializeThread();
		  Map<String,Object> m = threadProfile.get();
		  if (m==null) {
			  inicializeThread();
			  m = threadProfile.get();
		  }
		  if (m != null && m.containsKey(IDENT))
			  return (Integer) m.get(IDENT);
		  else
		    return 0;
	  }
	  
	  /**
	   * @since jCompany 3.0
	   * Seta identacao
	   */
	  public void setIndent(int ident) {
		  if (threadProfile.get()==null)
			  inicializeThread();
		  Map<String,Object> m = threadProfile.get();
		  m.put(IDENT,ident);
	  }

	/**
	 * @since jCompany 3.0
	 * @return Retorna cronômetro de mediçao de performance. Um diferente é mantido para cada método
	 */
	private StopWatch getChronometerByMethod(String methodName) {
		if (threadProfile.get()==null) {
		    inicializeThread();
		}
		
		StopWatch sw=null;
		Map<String,Object> mThread = threadProfile.get();
		Map<String,StopWatch> m = (Map<String,StopWatch>)mThread.get(STOPWATCH);
		if (m==null) {
			m = new HashMap<String,StopWatch>();	
			mThread.put(STOPWATCH,m);
		}
	    
		sw = m.get(methodName);
		if (sw == null) {
			sw = new StopWatch();
			sw.start();
			setChronometer(m,methodName,sw);
			return sw;
		} else
			return sw;
	}
	
	/**
	 * @since jCompany 3.0
	 * @return Retorna cronômetro de mediçao de performance.
	 */
	private void initializeFreeHeapMemory(String methodName) {
		
		if (threadProfile.get()==null) {
		    inicializeThread();
		}
		
		Map<String,Object> mThread = threadProfile.get();
		Map<String,Long> m = (Map<String,Long>)mThread.get(HEAP_MEMORY);
		if (m==null) {
			m = new HashMap<String,Long>();	
			mThread.put(HEAP_MEMORY,m);
		}
	    m.put(methodName,getFreeMemoryHeap());

	}
	
	/**
	 * @since jCompany 3.0
	 * @return Retorna diferenca de memoria livre do inicio ao fim do metodo (momento da chamada
	 */
	private Long getFreeHeapMemoryDiffByMethod(String methodName) {
		
		if (threadProfile.get()==null) {
		    inicializeThread();
		}
		
		Map<String,Object> mThread = threadProfile.get();
		Map<String,Long> m = (Map<String,Long>)mThread.get(HEAP_MEMORY);
		if (m !=null && m.get(methodName) !=null) {
			long freeHeapMemoryBegin=m.get(methodName);
			long freeHeapMemoryEnd=getFreeMemoryHeap();
			return freeHeapMemoryEnd-freeHeapMemoryBegin;
		}
		return new Long(0);

	}


	/**
	 * Inicializa objetos diversos do Thread Local para manter dados
	 * recursivamente ao longo do Profiling.
	 * @since jCompany 3.0
	 */
	private void inicializeThread() {
		
		Map<String,Object> m = new HashMap<String,Object>();
		Map<String,StopWatch> mStopWatch = new HashMap<String,StopWatch>();
		Map<String,Long> heapMemory = new HashMap<String,Long>();
		m.put(STOPWATCH,mStopWatch);
		m.put(HEAP_MEMORY,heapMemory);
		m.put(GET_SESSION,new Integer(0));
		m.put(LAST_METHOD,"");
		m.put(IDENT,new Integer(0));
		m.put(TRANSACTION,null);		
		threadProfile.set(m);
		
		// StopWatch Inicial
		getChronometerByMethod(INITIAL_TIME);
	}
	
	/**
	 * @since jCompany 3.1
	 */
	private void setMemoryAnalizer(Map<String,Long> m,String methodName) {
		m.put(methodName,getFreeMemoryHeap());
		Map<String,Object> mThread =  threadProfile.get();
		mThread.put(HEAP_MEMORY,m);
	}
	
	/**
	 * @since jCompany 3.0
	 */
	private void setChronometer(Map<String,StopWatch> m,String methodName, StopWatch stopWatch) {
		m.put(methodName,stopWatch);
		Map<String,Object> mThread =  threadProfile.get();
		mThread.put(STOPWATCH,m);
	}
	
	/**
	 * @since jCompany 3.0
	 */
	private void removeMemoryAnalizer(String methodName) {
		Map mThread = threadProfile.get();
		Map m = (Map) mThread.get(HEAP_MEMORY);
		if (m != null) {
			m.remove(methodName);
		}
	}
	
	/**
	 * @since jCompany 3.0
	 */
	private void removeChronometer(String methodName) {
		Map mThread = threadProfile.get();
		Map m = (Map) mThread.get(STOPWATCH);
		if (m != null) {
			m.remove(methodName);
		}
	}

	/**
	 * @since jCompany 3.0
	 * Forma simples para nao reinicializar
	 * @param msg
	 * @param classWithPackage
	 * @param method
	 * @param docGenerate
	 * @param parameterTypes
	 */
	public void registerBeginning(String msg,String classWithPackage, String method,String docGenerate,String applicationInitials) {
		registerBeginning(msg,classWithPackage,method,false,docGenerate,applicationInitials);
	}
	
	/**
	 * @since jCompany 3.0
	 * Registra o início de um método e emite logging identado utilizando Logger passado.
	 * Evita métodos de service locator e context, e somente exibe o primeiro getSession, para demarcar
	 * o inicio da transacao
	 * @param msg Nome do método podendo conter detalhes adicionais
	 */
	public void registerBeginning(String msg,String classWithPackage, String method,boolean initializeAll,String docGenerate,
			String applicationInitials) {

		// Verifica se a primeira, para iniciar a thread local
		if (initializeAll || threadProfile.get()==null) {
			inicializeThread();
			if (docGenerate != null)
				setDOC_GENERATE("S");
		}
		
		// Somente se for getter, setter e PlcMneuItemcontroller
		Map m = threadProfile.get();
		
		if (level==1 && ((msg ==null || msg.indexOf("http")==-1) &&
			(m != null && m.get(LAST_METHOD)!=null && 
				   ((String)m.get(LAST_METHOD)).indexOf("http")==-1)))
			return;
		
		if (method != null)
			method = highlightTransaction(method);
		
		// Verifica se é mensagem do filtro inicial, diferente das demais
		if (classWithPackage != null)
			if (method.indexOf("(") >0 )
				msg = classWithPackage.substring(classWithPackage.lastIndexOf(".")+1)+"."+method.substring(0,method.lastIndexOf("("));
			else	
				msg = classWithPackage.substring(classWithPackage.lastIndexOf(".")+1)+"."+method;
		
		getChronometerByMethod(msg);
		
		if (getIndent()==0 && level >0 && msg.indexOf("http")>-1){
			if ("S".equals(docGenerate)) {
				// Para documentacao já envia de imediato, ja que nunca manda a primeira linha!
				logDocAutomated.debug(beginningHtml(msg,applicationInitials));
			} else
				msg = "BEGINNING: "+msg+" "+getTranslateLevel(getLevel())+formatFinalMemory(false,level,0);
		}
		
		// Como as saidas do registro de inicio sao postergadas para conferencia de duplicidade,
		// se a recursividade chamar novamente o registraInicio, é aqui que as mensagens de inicio serao exibidas
		if ((level>=2 && m.get(LAST_METHOD)!=null && 
		   !("".equals(m.get(LAST_METHOD))) &&
		   !((String)m.get(LAST_METHOD)).equals(msg) &&
		    (((String)m.get(LAST_METHOD)).indexOf(".getSession")==-1 || level==3)) ||
		   (m != null && m.get(LAST_METHOD)!=null && ((String)m.get(LAST_METHOD)).indexOf("http")>-1)) {

			if ("S".equals(docGenerate)){
				// TODO Known issue: Estas linhas geradas aqui nao emitem performance nem memoria. Refatorar futuramente
				logDocAutomated.debug(LINE_BEGIN+getIndentation()+  getLinkJavadocDocumentation(null, null, msg) +LINE_END);
			} else
				logProfiling.debug(getIndentation()+m.get(LAST_METHOD));
			
			m.put(LAST_METHOD,null);
		}
		
		if (msg.indexOf(".set")==-1 && msg.indexOf(".is")==-1 && msg.indexOf(".gravaCooki")==-1 &&
			msg.indexOf("PlcMenuItemController")==-1 && showEmptyTemplateMethod(msg) &&
		   (msg.indexOf(".get")==-1 || (msg.indexOf(".getSession")>-1 && (m == null || 
		    m.get(GET_SESSION)== null || ((Integer)m.get(GET_SESSION)).intValue()==0)))) {

			increaseIndent(msg);		

			m.put(LAST_METHOD,msg);
			
			if (msg.indexOf("getSession")>-1) {
				m.put(GET_SESSION,1);
				if (level>=2 || (level==1 && (msg.indexOf("http")>-1))) {
					m.put(TRANSACTION,"BT");
				}
							
			} 
		}

		// Coleta de memoria deve ser a ultima coisa, para diminuir a influencia do profiling!		
//		if ((level==1 && msg.indexOf("http")>-1) || (level==3 && classeComPacote!=null && classeComPacote.indexOf("DAO")>-1)) {
			if (level==1 && msg.indexOf("http")>-1)
				initializeFreeHeapMemory("GERAL");
			else
				initializeFreeHeapMemory(msg);

	//	}

	}
	
	private String beginningHtml(String msg, String siglaAplicacao) {
		StringBuffer s = new StringBuffer("<HTML><HEAD><TITLE>jCompany: Automatic Documentation of Collaboration Flow</TITLE>");
		s.append("<LINK REL ='stylesheet' TYPE='text/css' HREF='../javadoc/jdstyle.css' TITLE='Style'>");	
		s.append("<HR>jCompany Developer Suite (c). Generated  "+(new Date()).toLocaleString());	
		s.append("<HR><CENTER><H1>"+siglaAplicacao+": Collaboration Flow</H1></CENTER>");	
		s.append("<HR><H3>"+msg+"</H3>");	
		s.append("<table>");
		return s.toString();
	}
	
	private String fimHtml() {
		StringBuffer s = new StringBuffer("</table>");
		s.append("</HEAD></HTML>");
		return s.toString();
	}

	/**
	 * @since jCompany 3.0
	 * Registra o fim de um método. Evita getBO, getDAO, Controller Default de Item de Menu e localizadores de contexto.
	 * @param msg Nome do método podendo conter detalhes adicionais
	 * @param slaMax Tempo máximo em milisegundos
	 */
	public void registerFinish(String msg, String classWithPackage, String method, Long slaMax,String docGenerate) {
		
		if (level==1 && (msg ==null || msg.indexOf("http")==-1))
			return;
			
		if (method != null)
			method = highlightTransaction(method);

		if (classWithPackage != null)
			if (method.indexOf("(") >0 )
				msg = classWithPackage.substring(classWithPackage.lastIndexOf(".")+1)+"."+method.substring(0,method.lastIndexOf("("));
			else	
				msg = classWithPackage.substring(classWithPackage.lastIndexOf(".")+1)+"."+method;
		
		// Contabilizacao de memoria deve ser a primeira coisa, para diminuir a influencia do profiling
		long freeMemoryDiff=0;
//		if ((level==1 && msg.indexOf("http")>-1) || (level==3 && classeComPacote!=null && classeComPacote.indexOf("DAO")>-1)) {
			if (level==1 && msg.indexOf("http")>-1)
				freeMemoryDiff = getFreeHeapMemoryDiffByMethod("GERAL");
			else
				freeMemoryDiff = getFreeHeapMemoryDiffByMethod(msg);
//		}
		
		Map m = threadProfile.get();
		long timeInMilis =0;
		
		// Se o método de inicio for contiguo, nao exibe. Se nao for, exibe o de inicio
		if (level>=2 && m.get(LAST_METHOD)!=null && 
		   !("".equals(m.get(LAST_METHOD))) && 
		   !((String)m.get(LAST_METHOD)).equals(msg) &&
		   (((String)m.get(LAST_METHOD)).indexOf(".getSession")==-1 || level==3)) 
			logProfiling.debug(getIndentation()+m.get(LAST_METHOD));
		
		// Somente se nao for getter e setter
		if (msg.indexOf(".set")==-1 && msg.indexOf(".is")==-1 && msg.indexOf(".gravaCooki")==-1 &&
			msg.indexOf("PlcMenuItemController")==-1 && showEmptyTemplateMethod(msg) &&
			(msg.indexOf(".get")==-1 || 
			 (msg.indexOf(".getSession")>-1 && ((Integer)m.get(GET_SESSION)).intValue()==1 && level>=2))) {
			
			// Calcula tempo gasto no escopo do método
			StopWatch sw = getChronometerByMethod(msg);
			sw.split();		
		
			if (msg.indexOf("http")>-1)
				setIndent(0);

			if (level>=2 || 
				msg.indexOf("http")>-1 || 
				msg.toLowerCase().indexOf("rollback")>-1 || 
				msg.toLowerCase().indexOf("commit")>-1) {
				if ("S".equals(docGenerate)){
					if (msg.indexOf("http")>-1)
						logDocAutomated.debug(LINE_BEGIN+"Total Time: "+sw.toSplitString()+" miliseconds"+
								formatFinalMemory(true,level,freeMemoryDiff)+LINE_END);
					else
						logDocAutomated.debug(LINE_BEGIN+getIndentation()+  getLinkJavadocDocumentation(classWithPackage, method, msg)  +" "+
							timeWithoutZero(sw)+formatFreeHeapMemoryDiffByMethod(freeMemoryDiff,msg)+LINE_END);
				} else 
					if (msg.indexOf("http")>-1)
						logProfiling.debug("END ----------------------------------- "+timeWithoutZero(sw)+
								formatFinalMemory(true,level,freeMemoryDiff)+formatFinalTransactionNumber());
					else
						logProfiling.debug(getIndentation()+msg+" "+timeWithoutZero(sw)+
								formatFreeHeapMemoryDiffByMethod(freeMemoryDiff,msg));
				decreaseIndent(msg);	
			}
			

			timeInMilis = sw.getSplitTime();
			sw.stop();
			removeChronometer(msg);	
			if (msg.indexOf("getSession")>-1)
				m.put(GET_SESSION,2);
			
		} else if ((msg.indexOf(".getSession")>-1 && ((Integer)m.get(GET_SESSION)).intValue()==1 && level>=2)) {
			decreaseIndent(msg);
		}
		
		registerEndTransaction(msg,docGenerate,m,classWithPackage,method);
			
		if (getIndent()==1)
			m.put(GET_SESSION,0);
		if (getIndent()==0 && level >0){
			if ("S".equals(docGenerate)){
				logDocAutomated.debug(fimHtml());
			}
			
			if (level>=2 && docGenerate==null)
				verifyLogicalUnitTransaction();
		}
		
		if (docGenerate==null)
			verifySla(slaMax,timeInMilis);
			
		// Sempre limpa ultimo metodo
		m.put(LAST_METHOD,null);
	}
	
	private String timeWithoutZero(StopWatch sw) {
		if (sw.getSplitTime()==0)
			return "";
		else 
			return "[t: "+sw.toSplitString()+"]";
	}

	/**
	 * Renderiza um trecho contendo memoria total e livre, iniciais da requisicao, somente para level 3.
	 * Contabilizar memoria com o Profiling pode dobrar as medidas! Deve ser analizado para nocao
	 * da proporcionalidade.
	 * @param level Nivel Monitor
	 * @param diffFinalConsume Diferença de Memória
	 */
	private String formatFinalMemory(boolean isFinal,int level, long diffFinalConsume) {
		// Nao faz sentido contabilizar memoria quando o profiling esta ativo, pela grande distorção.
	//	if (level==1) {
			if (isFinal) {	
				if (diffFinalConsume>0)
					return " [total: "+getTotalMemoryHeap()+"k, free: "+getFreeMemoryHeap()+"k, Garb.Collector:"+diffFinalConsume+"k]";
				else
					return " [total: "+getTotalMemoryHeap()+"k, free: "+getFreeMemoryHeap()+"k, Consumo:"+diffFinalConsume+"k]";
			} else
				return " [total: "+getTotalMemoryHeap()+"k, free: "+getFreeMemoryHeap()+"k]";
//		} else
	//		return "";
	}
	
	/**
	 * Renderiza o total de transacao no final.
	 */
	private String formatFinalTransactionNumber() {

		String totalULT = "[request transactions total: "+(getTotalCommit()+getTotalRollback())+". COMMIT: "+getTotalCommit()+". ROLLBACK: "+
		getTotalRollback()+"]";
		totalCommit=0;
		totalRollback=0;
		return totalULT;
	}
	
	/**
	 * @since jCompany 3.0
	 */
	protected String getTranslateLevel(int level) {
		if (level==1)
			return "Escuta";
		else if (level==2)
			return "Radiografia";
		else if (level==3)
			return "Tomografia";
		else
			return level+"";
	}
	/**
	 * @since jCompany 3.0
	 * Verifica template method
	 * @param methodName Nome do método no padrão "NomeClasse.nomeMetodo"
	 * @return true Devolve true se for método de especialização de um Template Method e for o ancestral (ou seja: está vazio) ou se o 
	 * level for 3 (quando estao devera
	 */
	protected boolean showEmptyTemplateMethod(String methodName) {
		if (level>=2) return true;
		
		if (methodName.startsWith("Plc") && 
			(methodName.startsWith("before") ||  methodName.startsWith("after") || methodName.startsWith("api") ||
			methodName.endsWith("Before") || methodName.endsWith("After") || methodName.endsWith("Api")))
			return false;
		else
			return true;
	}
	
	/**
	 * @since jCompany 3.0 
	 * Delega a execução para a versão com slaMax como argumento, passando null
	 */
	public void registerFinish(String msg, String classWithPackage, String method,String docGenerate) {
		registerFinish(msg,classWithPackage,method,null,docGenerate);
	}

	
	/**
	 * Exibe memória RAM para níveis acima de 1
	 * @param freeMomoryDiff
	 * @param msg
	 * @return
	 */
	private String formatFreeHeapMemoryDiffByMethod(long freeMomoryDiff, String msg) {
		
	//	if (level>=1) {
			if (freeMomoryDiff<0)
				// houve consumo
				return "[Heap: "+freeMomoryDiff+"k]";
			else if (freeMomoryDiff==0)
				// nao houve consumo
				return "";
			else 
				// houve consumo mas tambem execucao de Garbage Collector
				return "[GC: "+freeMomoryDiff+"k]";
		//} else
			//return "";
			
	}
	
	/**
	 * Passa para maiusculos metodos de abertura e fechamento de transacoes, dando destaque
	 * @param method Método
	 * @return Metodo com destaques
	 */
	private String highlightTransaction(String method) {
		if (method.startsWith("rollback"))
			method = StringUtils.replaceOnce(method,"rollback","ROLLBACK");
		else if (method.startsWith("commit"))
			method = StringUtils.replaceOnce(method,"commit","COMMIT");
		else if (method.startsWith("getSession"))
			method = StringUtils.replaceOnce(method,"getSession","getSession=>[BEGIN-TRANSACTION]");
		return method;
	}
	
	/**
	 * Desfaz destaques de metodo para link javadoc funcionarem
	 * @param msg mensagem com possiveis destaques
	 * @return msg mensagem sem detaques
	 */
	private String highlightTransactionUndoToJavadoc(String msg) {
		if (msg.startsWith("rollback"))
			return StringUtils.replaceOnce(msg,"ROLLBACK","rollback");
		else if (msg.startsWith("commit"))
			return StringUtils.replaceOnce(msg,"commit","COMMIT");
		else if (msg.startsWith("getSession"))
			return StringUtils.replaceOnce(msg,"getSession=>[BEGIN-TRANSACTION]","getSession");
		return msg;
	}
	
	/**
	 * Gera uma linha de logging de profiling indicando fim de transação.
	 * @since jCompany 3.03
	 */
	protected void registerEndTransaction(String msg,String docGenerate,Map m,String classWithPackage,String method) {
		
			if (level>=2) {
				if (msg.toLowerCase().indexOf("rollback")>-1) {
					m.put(TRANSACTION,null);
				} else if (msg.toLowerCase().indexOf("commit")>-1) {
					m.put(TRANSACTION,null);
				}
			}
		
	}
	/**
	 * jCompany 3.0 Verifica se transacoes abertas se fecharam. Nesta versao, somente
	 * trata uma fábrica.
	 */
	private void verifyLogicalUnitTransaction() {
	
		Map m = threadProfile.get();
		if (m.get(TRANSACTION)!=null) {
			logProfiling.error("   ++++ Transaction not ended Ok. Re-check  COMMIT or ROLLBACK ++++");
		} else
		m.put(TRANSACTION,null);		
	}
	
	/**
	 * Verifica se houve problemas no tratamento da ULT
	 * @return String vazio se ok ou mensagem se foi identificado problema
	 */
	public String verifyLogicalUnitTransactionPartial() {

		Map m = threadProfile.get();
		if (m.get(TRANSACTION)!=null) {
			return "Manager with Problem. Check it out";
		} else
			return "";
		
	}
	
	private void verifySla(Long slaMax, long timeInMilis) {
		Map m = threadProfile.get();
		if (slaMax != null && timeInMilis >0 && level >0) {
			
			if (timeInMilis>slaMax.longValue()) {
				logProfiling.error("SLA NOT OK ---------- [Max Allowed: "+slaMax+ "] [Real: "+timeInMilis+"]");
			} else {
				logProfiling.error("SLA OK ---------- [Max Allowed: "+slaMax+ "] [Real: "+timeInMilis+"]");
			}
		
		}
		
	}
	
	/**
	 * Verifica o SLA até o momento, com relacao ao primeiro
	 * @param slaMax
	 */
	public String verifySlaPartial(Long slaMax) {

		StopWatch sw = getChronometerByMethod(INITIAL_TIME);
		sw.split();
		long  tempoDecorridoEmMilisegundos = sw.getSplitTime();
		if (slaMax != null) {
			
			if (tempoDecorridoEmMilisegundos>slaMax.longValue()) {
				return "Não Achieved! Allowed:["+slaMax+" ms.] Realized:["+sw.toSplitString()+" ms.]";
			} else {
				return "Ok. Allowed:["+slaMax+" ms.] Realized:["+sw.toSplitString()+" ms.]";
			}
		
		} else 
			return "Ok. Allowed:[Not Defined] Realized:["+sw.toSplitString()+" ms.]";
		
	}
	
	private String getIndentation() {
		StringBuffer sb = new StringBuffer();
		int ident = getIndent();
		while( --ident > 0 ){
		    sb.append("..");
		}
		return sb.toString();  
	}

	/**
	 * @since jCompay 3.0
	 * @param level Coloca o nível como instancia.
	 */
	public void setLevel(String level) {
		this.level = new Integer(level);
	}  
	
	public int getLevel(){
		return this.level;
	}
	/**
	 * @since jCompay 3.0
	 * @return Returns the dOC_GERA.
	 */
	public static String getDOC_GENERATE() {
		return DOC_GENERATE;
	}
	/**
	 * @since jCompay 3.0
	 * @param doc_generate The dOC_GERA to set.
	 */
	public static void setDOC_GENERATE(String doc_generate) {
		DOC_GENERATE = doc_generate;
	}
	
	/**
	 * @since jCompay 3.0
	 */
	private String getLinkJavadocDocumentation(String classWithPackage, String method, String msg){
		
		if (( classWithPackage == null) || (method == null))
			return msg;
		else{	
			classWithPackage	= classWithPackage.replaceAll("\\.","/").concat(".html");
			method			= "#".concat(method).replaceAll(",",", "); // no link javadoc os parâmetros do método tem que ser separados por virgula e espaço. "," e "_"
			String link 	= "<a href='../javadoc/"+ classWithPackage + highlightTransactionUndoToJavadoc(method) + "'>" + msg + "</a>";
			
			return link;
		}
	}
	
	/**
	 * Devolve memoria livre total do heap, em KBytes
	 */
	public long getFreeMemoryHeap() {
		java.lang.Runtime r = Runtime.getRuntime();
		return r.freeMemory() / 1000;
	}
	
	/**
	 * Devolve memoria total do heap, em KBytes
	 */
	private long getTotalMemoryHeap() {
		java.lang.Runtime r = Runtime.getRuntime();
		return r.totalMemory() / 1000;
	}

	public int getTotalCommit() {
		return totalCommit;
	}

	public int getTotalRollback() {
		return totalRollback;
	}
	
	/**
	 * @param msg Mensagem para log
	 * @return Mensagem para log com endentação
	 */
	public String showLogInitial(String msg) {
		increaseIndent("UNICA");
		return getIndentation()+msg+":inicio";
	}
	
	/**
	 * @param msg Mensagem para log
	 * @return Mensagem para log com endentação
	 */
	public String showLogFinal(String msg) {
		String msgEndentada = getIndentation()+msg+":fim";
		decreaseIndent("UNICA");
		return msgEndentada;
	}
	
	/**
	 * Versão que nao incrementa em decrementa níveis de endentacao, para uso interno nos métodos.
	 * @param msg Mensagem para log
	 * @return Mensagem para log com endentação
	 */
	public String showInternalLog(String msg) {
		return getIndentation()+msg;
	}
	
}
