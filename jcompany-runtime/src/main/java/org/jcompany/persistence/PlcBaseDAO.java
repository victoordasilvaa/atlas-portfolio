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
package org.jcompany.persistence;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.type.Type;
import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ENTITY;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.commons.helper.PlcStringHelper;
import org.jcompany.persistence.helper.PlcPersistenceHelper;
import org.jcompany.persistence.hibernate.PlcBaseHibernateDAO;
import org.jcompany.persistence.hibernate.helper.PlcAnnotationPersistenceHelper;



/**
 * jCompany 3.0. DP Abstract Factory. DAO. Classe ancestral para serviços de acesso a dados<p>
 * Segundo 'Core J2EE Design Patterns': DataAcessObjects encapsulam acessos a dados e podem ser chamados
 * por BOs ou ASs, e até mesmo diretamente por Façades, em aplicações mais simples que não requeiram uma
 * intervençao do negócio (recuperação e exibição de dados)
 * @since jCompany 3.0
 * @version $Id: PlcBaseDAO.java,v 1.9 2006/08/19 15:20:34 alvim Exp $
 */
public abstract class PlcBaseDAO implements IPlcDAO, IPlcDAORemote {

	/* ******************************************************************************************* */
	/* ********************************  INICIO SERVIÇOS APOIO *********************************** */
	/* ******************************************************************************************* */

	protected static final Logger logPersistence = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_PERSISTENCE);
	protected static final Logger logWarning = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_WARNING_DEVELOPMENT);

 	/**
  	 * Serviço de localização de Data Acess Object a partir de nome da classe de BO
  	 * Design Pattern Service Locator para DAO.
  	 */
  	protected static PlcPersistenceLocator daoFactoryPlc = PlcPersistenceLocator.getInstance();

  	protected Logger log = Logger.getLogger(PlcBaseHibernateDAO.class);
  	
	   /**
	    * @return Retorna referencia ao service locator para DAOs
	    */
	   public static PlcPersistenceLocator getDaoPlc() {
	       return daoFactoryPlc;
	   }
	   
	   /**
	    * @param daoFactoryPlc
	    */
	   public static void setDaoPlc(PlcPersistenceLocator daoFactoryPlc) {
	       PlcBaseDAO.daoFactoryPlc = daoFactoryPlc;
	   }
	   
	    /**
	     * jCompany 3.0 DP Composite. Devolve o POJO contendo informações de contexto do cliente
	     */
	    public PlcBaseContextVO getContext() throws PlcException {
	        return PlcContextManager.getContextEntity();
	    }

	    /**
		 * Encupsula o helper p/ fatoração dos algoritmos dependentes
		 * @since jCompany 5.0
		 * @return
		 */
		protected abstract PlcPersistenceHelper getHelper();
	/* ******************************************************************************************* */
	/* ********************************  INICIO ACESSO META-DADOS *********************************** */
	/* ******************************************************************************************* */
	    
	/**
	 * Obtém filhos possíveis da classeBase informada, investigando nos meta-dados do framework de 
	 * mapeamento objeto-relacional quais classes possuem relacionamento "many-to-one" com a informada
	 * @since jCompany 3.03
	 */    
	public abstract List<Class> retrievePossibleDescendents(Class classBase) throws PlcException;
	
	/**
	 * Indica se há opção para otimização de atualização para a fábrica
	 * @param fabrica Fábrica
	 * @return true se "S" igual a updateOptimize
	 */
	public abstract boolean isOptimizeUpdate() throws PlcException;
   
	/* ******************************************************************************************* */
	/* ********************************  INICIO TRATAMENTO MSG *********************************** */
	/* ******************************************************************************************* */
   
	/**
	 * jCompany 3.0. Deve verificar o type da Exceção e devolver msg de erro aprpriada
	 * @param rootCause Exception a ser investigada
	 * @return null se não for de responsabilidade da persistencia ou String[0]: msg internacionalizada,
	 * String[1]: arg1 (opcional) e String[2]: arg2 (opcional)
	 */
	public abstract String[] msgExceptionHandle(Throwable rootCause) throws PlcException;
	
	/* ******************************************************************************************* */
	/* ********************************   INICIO GER. TRANSACAO  ********************************* */
	/* ******************************************************************************************* */
   
	/**
	 * jCompany 3.0. Encerra uma transação/sessao com rollback
	 * @param factory Nome da fabrica de sessões a ser utilizada
	 */
	public abstract void rollback(String factory) throws PlcException;

	/**
	 * jCompany 3.0. Encerra uma transação/sessao com rollback, usando fábrica 'default'
	 */
	public void rollback() throws PlcException {
		 rollback("default");
	}
	
	/**
	 * jCompany 3.0. Encerra uma transação/sessao com commit (confirmando gravações)
	 * @param factory Nome da fabrica a ser utilizada
	 */
	public abstract void commit(String factory) throws PlcException;
	
	/**
	 * jCompany 3.0. Encerra uma transação/sessao com commit usando fábrica 'default'
	 */
	public void commit() throws PlcException {
		 commit("default");
	}
   	

	/**
	 * jCompany 3.0 Dispara os comandos em buffer gerenciados pela engine de persistencia até o momento.
	 * Importante: Não faz confirmação final (commit, por exemplo), somente envia.
	 */
	public abstract void sendCacheCommands(Class clazz) throws PlcException;
	
	/**
	 * jCompany 3.0 Dispara os comandos em buffer gerenciados pela engine de persistencia até o momento.
	 * Importante: Não faz confirmação final (commit, por exemplo), somente envia.
	 */
	public void sendCacheCommands() throws PlcException {
    	sendCacheCommands(null);
	}
	
	/**
	 * jCompany 3.0 Torna o objeto somente de consulta durante uma sessão
	 * @param det Coleção de Detalhe a não ser considerada.
	 * @param bean Objeto Mestre
	 */
	public abstract void registerConsultationOnly(Object beanMain, Collection c) throws PlcException;
 
	/* ******************************************************************************************* */
	/* ********************************      INICIO CRUD    ************************************** */
	/* ******************************************************************************************* */

	public abstract Long insert(Object entity) throws PlcException;
	
	public abstract void update(Object entity) throws PlcException;

	public abstract void exclude(Object entity) throws PlcException;
	
	
	/**
	 * Recupera uma instância da classe identificada pelo id
	 * @since jCompany 5.0
	 * @param clazz Tipo do objeto a ser recuperado
	 * @param id identificacao do objeto
	 * @return instância da classe identificada pelo id
	 * 
	 */
	public abstract  Object retrieve( Class clazz, Object id) throws PlcException;
	
	
	/**
	 * @since jCompany 3.0
	 * Recupera um grafo de  Value Object a partir do OID (id), aplicando filtros padrões.
	 * @param id Object-id
	 * @return ENTITY recuperado ou null se não encontrou.
	 */
	public Object retrieveWithFilter(Class clazz, Object id) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveWithFilter:"));

		try {
			
			PlcBaseContextVO context = getContext();
			
			 // Somente tem filtro de segurança se não usa chave composta, para que seja montado dinamicamente
			String oql = "from obj in class "  + clazz.getName()+
							" where obj.id = " + id + " and "+ context.getVerticalFilter();
			
			List l = apiCreateExecute(clazz, oql);

			Object entity = null;
			
			if (l.size()==0)
				throw new PlcException("jcompany.errors.persistence.retrieve.filter");
			else
				entity = l.get(0);		// Transforma proxies no objeto real
			entity = transformProxyInRealObject(entity);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveWithFilter:"));
			
			return entity;
			
		} catch (JDBCException e1) {
			throw new PlcException("jcompany.errors.sql.list",new Object[] {e1},e1,log);
		} catch (HibernateException e2) {
			throw new PlcException("jcompany.errors.hibernate.list",new Object[] {e2},e2,log);
		} catch (Exception e) {
			 throw new PlcException("jcompany.errro.generic", new Object[] {"retrieveWithFilter", e }, e,log);
		}

	}
	
	public abstract Object retrieveOnDemand(Object entity,String propertyNameDetail,Class classDetail) throws PlcException;

	/* ******************************************************************************************* */
	/* **************************** INICIO AGREGADO NAVEGACAO   ********************************** */
	/* ******************************************************************************************* */

	public abstract List<Object> retrieveAggregateNavigation(Object entityMain, Object entityAggregateDestiny) throws PlcException;
    public abstract List<Object> retrieveAggregateNavigation(Class classMain, Object pk, Class aggregateDestiny) throws PlcException;

	public abstract Object retrieveLookupAggregate(Object baseEntity,
			String propertyName, Object value, String[] props) throws PlcException;

	/**
	 * Recupera apenas uma propriedade de um objeto
	 * @since jCompany 5.0
	 */
	public Object retrieveOneValue(Object entity, Long oid, String propertyName) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveOneValue:"));

		try {

			String query = "select obj."+propertyName+" from obj in "+entity.getClass() +" where obj.id="+oid;

			Object objectRetrieved = apiCreateExecuteOnlyOneResult(entity.getClass(), query);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveOneValue:"));
			
			return objectRetrieved;

		}  catch (Exception e) {
			throw new PlcException("jcompany.erro.generic", new Object[] {"retrieveOneValue", e }, e,log);
		}
	}
	
	/* ******************************************************************************************* */
	/* ********************************  INICIO SERVIÇOS QBE E CONSULTATION ************************** */
	/* ******************************************************************************************* */

	/**
	 * Aplica filtros associados a entidade para recuperacao das instâncias
	 * @ since jCompany 5.0
	 */
	protected abstract void applyFilters(Class clazz) throws PlcException;
	
	/**
     * Recupera todos os objetos de uma classe. Uso típico para recuperar listas simples, que são mantidas em caching.
     * IMPORTANTE: Este método nao obedece filtros verticais, pois é utilizado para incluir dados em caching. Neste
     * caso, o desenvolvedor deverá gerenciar o filtro manualmente, fazendo considerações acerca do caching.
	 * @since jCompany 3.0
	 * @param clazz a ser recuperada
	 * @param orderByDynamic Ordenação em OQL
	 */
	public List retrieveAll(Class clazz, String orderByDynamic) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAll:"));
		
		try {

			applyFilters(clazz);

			String query = getServiceQBE().getQuerySelDefault(clazz, getContext());

			if (orderByDynamic != null && !("".equals(orderByDynamic)) && !("null".equals(orderByDynamic))) 
				query += " order by obj." + orderByDynamic;

			List l = apiCreateExecute(clazz, query);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAll:"));

			return l;

		}catch( Exception e ){
			throw new PlcException("jcompany.error.generic", new Object[] {"retrieveAll", e }, e, log);
		}

	}

	/**
	 * Recupera um lista de objetos
	 * @since jCompany 5.0
	 */
	public List retrieveWithDefaultFilter(Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveWithDefaultFilter:"));

		//Filtro no padrão 3.0, independente do modo como irá trabalhar (Criteria ou HQL)
		applyFilters(clazz);
		
		String apiQuerySel = getContext().getApiQuerySel();
		
		if (StringUtils.isBlank(apiQuerySel))
			apiQuerySel = PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_DEFAULT; 
		
		//PlcAnnotationPersistenceHelper.getInstance().getNamedQueryPorNome(classe, apiQuerySel);
		
		boolean useCriteria = false;
		
		// If it doesn't have querySel assumes 'from Object'
		try {
			PlcAnnotationHelper.getInstance().isTypeDefaultAnnotationCriteria(clazz, apiQuerySel);
		} catch (Exception e) {}
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveWithDefaultFilter:"));

		if (useCriteria)
			return criteriaRetrieveQBE(clazz,orderByDynamic,argsQBE);
		else
			return hqlRetrieveQBE(clazz,orderByDynamic,argsQBE);
	}
	
	/**
	 * @since jCompany 3.0
	 * 
	 * DP Template Method. Ajusta query padrão programaticamente, se desejado
	 * @param clazz Classe principal
	 * @return Annotation contendo criterio padrão para QBE ou um select ""
	 */
	protected String retrieveAdjustQueryApi( Class clazz,String query, String orderByDynamic) throws PlcException {
		return query;
	}
	
	/**
	 * @since jCompany 5.0
	 * 
	 * Implementação deve recuperar listas simples
	 * @param clazz Classe a ser recuperada
	 * @param orderByDynamic Ordenação em OQL
	 */
	public List retrieveWithProgrammedFilter(Class clazz, String orderByDynamic) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveWithProgrammedFilter:"));

		applyFilters(clazz);

		String query = getServiceQBE().getQuerySelDefault(clazz,getContext());

		query = retrieveAdjustQueryApi(clazz,query,orderByDynamic);

		try {

			List l = apiCreateExecute(clazz, query.toString());

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveWithProgrammedFilter:"));

			return l;

		}catch( Exception e ){
			throw new PlcException("jcompany.erro.generic", new Object[] {"retrieveWithProgrammedFilter", e }, e, log);
		}
	}
	
	/**
	 * @since jCompany 5.0
	 * 
	 * Implementação deve recuperar listas com argumentos.
	 * @param clazz Classe a ser recuperada
	 */
	public List retrieveWithDeclaredArgs( Class clazz, String orderByDynamic,
			Object[] valueArgs,String[] typeArgs) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveWithDeclaredArgs:"));

		applyFilters(clazz);
	  
	    String query = getServiceQBE().getQuerySelDefault(clazz,getContext());
			  
		query = retrieveAdjustQueryApi(clazz,query,orderByDynamic);
				  		
		try {
			
			List listRetrieved = apiCreateQueryWithArgsExecute(clazz, query, orderByDynamic, valueArgs, typeArgs);		

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveWithDeclaredArgs:"));

			return listRetrieved;
			
		}catch( Exception e ){
			throw new PlcException("jcompany.erro.generic", new Object[] {"retrieveWithDeclaredArgs", e }, e, log);
  		}

	}
	/**
	 * @since jCompany 5.0
	 * 
	 * Trocar cláusulas HQL com ':arg' para 'is null', para todo argumento que esteja nulo
	 * @param query query HQL de origem com argumentos informados
	 * @param argQBE ENTITY contendo argumentos para substituicao
	 * @return query com substituições para is null
	 */
	protected String retrieveListQBEPaginatedChangeIsNull(String query, Object argQBE) throws PlcException {
		
		String[] nameArgs = getServiceQBE().distillHQLArguments(query);
		
		String[] nameNullArgs = PlcEntityHelper.getInstance().retrievePropsWithNullValue(argQBE, nameArgs);
		
		return getServiceQBE().changeWhereClauseToNulls(query, nameNullArgs);
	}
	
	 /**
     * Recupera uma lista de objetos da classe informada, utilizando queries anotadas no padrao "querySel" ou "queryTreeView", etc., em 
     * conformidade com o Application Pattern utilizado. Pode receber orderByDinamico (alterado pelo usuário ou desenvolvedor em cada
     * requisição), um POJO de argumentos (todos existentes na query anotada devem ser composto neste objeto) e intervalo para paginação	
     * @param clazz Classe da qual será obtida a query em anotação padrao ("querySel", "queryTreeView", etc., conforme a lógica chamadora)
     * @param orderByDynamic (Opcional - informe null ou "" para desconsiderar) Trecho de orderBy a ser adicionado dinamicamente à query anotada
     * @param argQBE POJO contendo valores preenchidos para todos os argumentos existentes fixos na query ou montados dinamicamente em whereDinamico
     * @param firstLine Primeira linha para recuperação paginada ou -1 para todos
     * @param maximumLines Máximo de linhas (size da página de recuperação) ou -1 para todos
     * @since jCompany 5.0
     * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBEPaginated(java.lang.Class, java.lang.String, org.jcompany.commons.Object, int, int)
     */
	public List retrieveListQBEPaginated(Class clazz, String orderByDynamic, Object argsQBE, int firstLine, int maximumLines) throws PlcException {
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));

		List listRetrieved = retrieveListQBEPaginated(clazz,null,orderByDynamic,argsQBE,firstLine,maximumLines);
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));
		
		return listRetrieved;
	}
	
	public abstract List retrieveListQBEPaginated(Class clazz,String whereDynamic, String orderByDynamic,Object argQBE,
			int firstLine, int maximumLines) throws PlcException;
	
	
	/**
     * Recupera uma lista de objetos da classe informada, utilizando queries anotadas no padrao "querySel" ou "queryTreeView", etc., em 
     * conformidade com o Application Pattern utilizado. Pode receber orderByDinamico (alterado pelo usuário ou desenvolvedor em cada
     * requisição) e também um trecho de where condition para ser dinamicamente composto (uso em lógicas dinamicas tais como
     * explorer), além de POJO de argumentos e intervalo para paginação
     * @param clazz Classe da qual será obtida a query em anotação padrao ("querySel", "queryTreeView", etc., conforme a lógica chamadora)
     * @param whereDinamico (Opcional - informe null ou "" para desconsiderar) Trecho a ser adicionado dinamicamente à query anotada. Importante: Nao informar o 'where' em si e utilizar o alias padrão 'obj.' (Ex:'obj.status=:status and obj.valor>:valor')
     * @param orderByDynamic (Opcional - informe null ou "" para desconsiderar) Trecho de orderBy a ser adicionado dinamicamente à query anotada
     * @param argQBE POJO contendo valores preenchidos para todos os argumentos existentes fixos na query ou montados dinamicamente em whereDinamico
     * @param firstLine Primeira linha para recuperação paginada ou -1 para todos
     * @param maximumLines Máximo de linhas (size da página de recuperação) ou -1 para todos
     * @since jCompany 5.0
     * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBEPaginated(java.lang.Class, java.lang.String, org.jcompany.commons.PlcBaseEntity, int, int)
     */
	public List retrieveListQBEPaginated(Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE, int firstLine, int maximumLines) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));

		applyFilters(clazz);

		String query = getServiceQBE().getQuerySelDefault(clazz,getContext());

		try {

			List argValues = new ArrayList();
			List argTypes = new ArrayList();

			query = getServiceQBE().hqlArgsQBE(query,clazz,argsQBE,argValues,argTypes);

			Type[] argTypesHibernate = new Type[argTypes.size()];
			int pos=0;
			for (Iterator i = argTypes.iterator(); i.hasNext();) {
				Type element = (Type) i.next();
				argTypesHibernate[pos]=element;
				pos++;
			}

			List list = apiCreateQueryWithArgsExecute(clazz, query, orderByDynamic, argValues.toArray(), argTypesHibernate, firstLine, maximumLines);

			if (log.isDebugEnabled()) log.debug("found "+list.size()+" record(s)");

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));

			return list;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListQBEPaginated", e }, e, log);
		}
	}
	
	public abstract Integer retrieveListQBETotal(Class clazz,String whereDynamic,Object argsQBE) throws PlcException;
	
	/**
	 * Devolve total de registros considerando argumentos
	 * @since jCompany  5.0 
	 */
	public Integer retrieveListQBETotal(Class clazz, List<PlcArgEntity> argsQBE) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBETotal:"));

		applyFilters(clazz);

		try {

			String queryCount = getServiceQBE().getQueryCount(clazz);

			if(null==queryCount)  {

				String query = getServiceQBE().getQuerySelDefault(clazz,getContext());

				int posFrom = query.indexOf("from");

				if (posFrom == -1)  
					throw new PlcException("jcompany.error.hql.without.from");

				queryCount = "select count(*) " + query.substring(posFrom, query.length());

				// retira order by e group by
				int posOrder = queryCount.toLowerCase().indexOf("order by");
				int posGroup = queryCount.toLowerCase().indexOf("group by");
				int length = queryCount.length();
				int end = length;

				if (posOrder != -1 || posGroup != -1) {
					if (posOrder < posGroup)
						end = posGroup;
					else
						end = posOrder;
				}

				if (end != length)
					queryCount = queryCount.substring(0, end);
			}
			List argValues = new ArrayList();
			List argTypes = new ArrayList();

			queryCount = getServiceQBE().hqlArgsQBE(queryCount,clazz,argsQBE,argValues,argTypes);

			Type[] argTypesHibernate = new Type[argTypes.size()];
			int pos=0;
			for (Iterator i = argTypes.iterator(); i.hasNext();) {
				Type element = (Type) i.next();
				argTypesHibernate[pos]=element;
				pos++;
			}

			List result = apiCreateQueryWithArgsExecute(clazz, queryCount, "", argValues.toArray(), argTypesHibernate);

			Object ret = result==null || result.isEmpty() ? null : result.get(0);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBETotal:"));

			if (ret instanceof Long) {
				return new Integer(((Long)ret).intValue());
			} else
				return (Integer) ret;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListQBETotal", e }, e, log);
		}
	}
	
	
	public abstract List retrieveExplorerList(Class clazz, Class clazzBase, Object idClassBase,long initialPosition) throws PlcException;
		
		
	/* ******************************************************************************************* */
	/* ********************************      INICIO ARQUIVO ************************************** */
	/* ******************************************************************************************* */
	
	public abstract byte[] retrieveBinaryFileById( Class clazz, Long oid, String property)throws PlcException;

	public abstract IPlcFileEntity retrieveEntityFileByUrl( Class clazz, String url) throws PlcException;
	
	/* ******************************************************************************************* */
	/* ********************************   INICIO VERSIONAMENTO/WORKFLOW ************************** */
	/* ******************************************************************************************* */

	/**
	 * @since jCompany 1.0 Nao funciona para fabricas anotadas em VOs, de versoes mais novas, pois nao recebe o vo e nem uma classe.
	 * @since jCompany 3.2 Acrescido filtro vertical com base em interpretação da query. A query devem possuir a sintaxe "xxxx from MinhaClasseVO obj", 
	 * e nao "from obj in class xxxxx", para funcionar, e também nao usar espaços em demasiado - anti-exemplo: "xxxx from    MinhaClasseVO obj"
	 * Executa query com createQuery utilizando o HQL e argumentos passados e traduzindo os tipos genéricos para tipos Hibernate
	 * @param query HQL a ser executado, com placeholders "?" nos argumentos.
	 * @param args Argumentos a serem incluidos, na ordem
	 * @param types Tipos dos argumentos a serem incluidos, na ordem e usando tipos genéricos (constantes "STRING", "LONG", etc.)
	 * @return Lista resultante
	 */
	protected List retrieveList(String query, Object[] args, String[] types) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveList:"));

		try {

			// Aplica filtro - na versao atual precisa da classe principal, entao somente aplica se
			// a query contiver apenas uma (ex: tabular), em outra hipotese, despreza.
			Class classe = searchClassByQuery(query);
			if (classe == null) {
				classe = Object.class;
			} else {
				applyFilters(classe);
			}

			List listRetrieved = apiCreateQueryWithArgsExecute(classe, query, "", args, types);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveList:"));

			return listRetrieved;

		} catch (HibernateException e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveList", e }, e, log);
		}
	}	
	
	 /**
	  * Recupera a lista de registros inativos e pendentes de aprovação para um id de registro ativo
	 * @since jCompany 5.0 
	 * @param clazz Classe principal
	 * @param pk OID do registro Ativo
	 * @return Coleção de VOs com id,dataUltAlteracao, usuarioUltAlteracao, versao e nome populados
	 */
	public List retrieveListCompleteAudit(Class clazz, Object pk) throws PlcException {

		return retrieveList("select new "+clazz.getName()+"(obj.id,obj.dataUltAlteracao, obj.usuarioUltAlteracao,obj.versao,obj.nome, obj.sitHistoricoPlc) "+
			" from obj in class "+clazz.getName()+" where obj.sitHistoricoPlc in ('P','I') and obj.idPai=? order by obj.dataUltAlteracao desc",
				new Object[]{(Long)pk},new String[]{PlcConstantsCommons.TYPES.LONG});

	}
    
	/**
	 * @since jCompany 5.0
	 * Recupera um historico recebendo o type
	 * @param statusHistoricPlc "I" ou "P" para recuperar inativo ou pendente de aprovação
	 * @return List de VOs do type desejado
	 */
	 public List retrieveListCompleteBySituation(Class clazz,String statusHistoricPlc)
	                throws PlcException {
	
	   // TODO Garantir que recupera somente o próprio ENTITY e não o grafo
	   String query = "from obj in class "+clazz.getName()+ " where obj.sitHistoricoPlc=? order by obj.dataUltAlteracao desc";
	
	   if (log.isDebugEnabled()) log.debug("retrieving with query="+query);
	
	   return retrieveList(query,new Object[]{statusHistoricPlc},
			   new String[]{PlcConstantsCommons.TYPES.STRING});
	
	}
	
	/* ******************************************************************************************* */
	/* ********************************   INTEGRIDADE DECLARATIVA       ************************** */
	/* ******************************************************************************************* */

	 /**
	  * @since jCompany 1.0
	  * Testa restrição "avoidEquals" declaradas como anotações<p>
	  *
	  * Os erros são disparados segundo uma convenção e de forma internacionalizada (I18n).
	  * Assim, no arquivo de properties, deve exitir uma mensagem com a seguinte estrutura (pressupondo um transação com url /organograma):<p>
	  *
	  * jcompany.aplicacao.naodeveexistir1.organograma=Tentativa de incluir nome/idPai do Organograma duplicado. Valor duplicado: {0}
	  * jcompany.aplicacao.naodeveexistir2.organograma=Tentativa de incluir nivel/ordem do Organograma duplicado. Valor duplicado: {0}<p>
	  *
	  * Importante: Esta lógica trata nulos, convertendo valores não informados para is null,
	  * antes de submetê-los.<P>
	  * Importante 2: A lógica também evita testar contra o próprio registro, em caso de
	  * alteração, acrescentando " and obj.id <> "+idObjetoCorrente ao teste.
	  * @param entity Value Object objeto do teste.
	  * @param mode "A" - Alteração "I" - Inclusão
	  */
	 public void avoidEqualsExecute( Object entity,String mode) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":avoidEqualsExecute:"));

		 try {

			 // Se for inclusão de pendente ou inativo, garante que não irá fazer os testes
			 if (PropertyUtils.isReadable(entity,ENTITY.STATUS_HISTORIC_PLC) &&
					 !("A".equals((String)PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC)))) {
				if (logPersistence.isDebugEnabled())
					logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":avoidEqualsExecute:return1:"));
				 return;
			 }

		 } catch (Exception e) {
			 throw new PlcException("jcompany.error.generic", new Object[] {
					 "avoidEqualsExecute", e }, e, log);
		 }

		 List<NamedQuery> avoidEquals = PlcAnnotationPersistenceHelper.getInstance().getAnnotationsQueryShouldntExist(entity.getClass());

		 if (avoidEquals == null || avoidEquals.size()==0) {
				if (logPersistence.isDebugEnabled())
					logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":avoidEqualsExecute:return2:"));
			 return;
		 }

		 Iterator j = avoidEquals.iterator();

		 // TODO Ver como anotar valor máximo
		 //	Iterator k = context.getNaoDeveExistirValMax().iterator();

		 int cont = 0;

		 String queryCount = "";
		 String valMax = "0";
		 String valueMsg = "";

		 while (j.hasNext()) {

			 cont++;
			 Integer totalResults = new Integer("0");
			 valueMsg = "";

			 //	where = (String) j.next();
			 //	valMax = (String) k.next();
			 NamedQuery nqShouldntExist = (NamedQuery) j.next();

			 queryCount = nqShouldntExist.query();

			 Object[] ret=null;

			 // Se for alteração e não for chave natural, então testa o OID

			 try {
				
			
			 if (mode.equals("A") && 
			     (!PropertyUtils.isReadable(entity,"dynamicNaturalId") ||
			        PropertyUtils.getProperty(entity,"dynamicNaturalId")==null))
				 queryCount = queryCount + " and obj.id <> :id";
			 
			 } catch (Exception e) {
				// tambem assume Object-Id neste caso
				 queryCount = queryCount + " and obj.id <> :id";
			}

			 try{
				 // jCompany 3.0 Se tiver versionamento e estiver incluindo ativo,
				 //altera automaticamente para somente considerar 'A'-Ativos
				 if (PropertyUtils.isReadable(entity,ENTITY.STATUS_HISTORIC_PLC) &&
						 "A".equals((String)PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC)))
					 queryCount = queryCount + " and obj.sitHistoricoPlc='A'";
			 } catch (Exception e){
				 // Exceção nao deve acontecer...
				 if (logWarning.isDebugEnabled()) {
					 logWarning.debug("Error trying to verify sitHistoricoPlc to avoidEquals. "+e);
					 e.printStackTrace();
				 }
			 }
			 
			 ret = verifyTotalResults(mode,queryCount,entity);

			 totalResults = (Integer) ret[0];

			 try {
				
				 // Se for alteração e for chave composta, subtrai um (o que já existe)
				 if (mode.equals("A") && 
					 PropertyUtils.getProperty(entity,"dynamicNaturalId")!=null)
					 totalResults = new Integer(totalResults.intValue()-1);

			 } catch (Exception e) {
					// nao dá excecao neste caso
			 }
			 
			 valueMsg = (String) ret[1];

			 if ((valMax.equals("0") &&
					 totalResults.intValue() > new Integer(valMax).intValue()) ||
					 (!valMax.equals("0") &&
							 totalResults.intValue() > (new Integer(valMax).intValue()-1))) {

				 String nameEntity = entity.getClass().getName();
				 nameEntity = nameEntity.substring(nameEntity.lastIndexOf("."),nameEntity.length()).toLowerCase();

				 throw new PlcException("jcompany.application."+nqShouldntExist.name(),new Object[] {valueMsg});

			 }

		 }

		 if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":avoidEqualsExecute:"));

	 }

	/**
	* @since jCompany 1.0
		* Executa um "select count(*)" do type "avoidEquals"
	    * @return Total de registros e valores informados
	    */
	protected abstract Object[] verifyTotalResults(String mode,String hql,Object entity)
	    			throws PlcException;
	
	/**
	 * @since jCompany 1.0
	 * Recebe uma string com query e troca campos nulos por is null
	 * Devolve ainda os valores informados, concatenados, para uso na mensagem de erro.
	 *
	 * @param hql query HQL a ser averiguada.
	 * @param entity Value Object objeto de persistência
	 * @return Retorna um vetor de Object, contendo o HQL modificado, uma string com
	 * os valores informados, para compor a mensagem de erro e um Map com as propriedades
	 * agregadas, necessárias para lógica do chamador.
	 */
	protected Object[] handleNullValues (String hql,Object entity) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":handleNullValues:"));

		// Refactoring para singleton
		PlcStringHelper plcString = PlcStringHelper.getInstance();

		List listArgs = plcString.splitParts(hql,":"," ");

		String attributeNames = "";
		String msgValue = "";
		String original = "";
		int attributePosition = 0;

		Map aggregatedProps = new TreeMap();

		try {

			Iterator res = listArgs.iterator();

			while (res.hasNext()) {

					attributeNames = (String) res.next();

					if (log.isDebugEnabled()) log.debug("attrbute=" + attributeNames);
				
					
					Object obj = null;
					
					if (PropertyUtils.isReadable(entity,attributeNames)) {
						obj = PropertyUtils.getProperty(entity,attributeNames);
					} else {
						// Assume que é nested com auxiliar '_' em lugar de '.'
						String attributeNameAux = attributeNames;
						if (attributeNames.indexOf("_") > 0) {
						    attributeNameAux = StringUtils.replace(attributeNameAux,"_",".");
						}
						try {
							obj = PropertyUtils.getNestedProperty(entity,attributeNameAux);
						} catch (Exception e) {}
					}

					//int posObj = 0;
					int dotPosition = 0;
					attributePosition = hql.indexOf(":"+attributeNames,attributePosition+1);

					if (obj == null) {

					    String attributeNameAux = attributeNames;
						if (attributeNames.indexOf("_") > 0) {
						    attributeNameAux = StringUtils.replace(attributeNameAux,"_",".");
						}

						dotPosition = hql.substring(0,attributePosition).lastIndexOf("."+attributeNameAux);

						original = hql.substring(dotPosition+1,attributePosition+attributeNames.length()+1);
						hql = plcString.changePart(hql,original,attributeNameAux+" is null ");

						log.debug("Finalized change Parts");
					}


				if (obj != null && !attributeNames.equals("id")) {
						msgValue = msgValue + "#" + obj;

						// Se for propriedade agregada o ponto deve ser substituido por sublinhado
						// e somente pode existir uma
						if (attributeNames.indexOf("_") > 0) {
							aggregatedProps.put(attributeNames,obj);
						}
				}
				else if (obj == null) {
						msgValue = msgValue + "#null";
				}
			}
		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.changenull",new Object[] {attributeNames,e},e,log);
		}

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":handleNullValues:"));

		return new Object[] {hql,msgValue,aggregatedProps};

	}
	
	/**
	* Recupera a quantidade de Sub-Relatórios, baseia-se nas anotações (anotations)
	* Colocadas no ENTITY do relatório (Classe do Parâmetro). As NamedQuery's tem que seguir o padrão de nomes:  
	* ' PlcConstantsCommons.ANNOTATION.SUFIXO_QUERYSELREL_SUBREPORTE_'  e o número do subreport
	*/
	public int retrieveSubReportsAmount(Class clazz) throws PlcException {
				
		int amount = 0;
		
		String apiQuerySel = getContext().getApiQuerySel();
		
		if (StringUtils.isEmpty(apiQuerySel))
			apiQuerySel = PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_DEFAULT;
		
		Annotation a = PlcAnnotationHelper.getInstance().getAnnotationQueryQbeOrSelDefault(clazz, apiQuerySel);
		
		if (a != null && NamedQueries.class.isAssignableFrom(a.getClass())){
			NamedQueries nqs = (NamedQueries)a;
			for (int i = 0; i < nqs.value().length; i++) {
				NamedQuery nq = nqs.value()[i];
				if (nq.name().indexOf(PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSELREL_SUBREPORT) > 1)
					amount++;
			}
		}
		
		return amount;
	}
	
	 /**
     * @since jCompany 5.0
     * 
     * DP Template Method. Permite que se "limpe" ou adicione valores ao ENTITY antes de QBE
     * @param query Query a ser executada
     * @param argsQBE ENTITY contendo valores informados como argumento
     */
    protected void apiHandleArgsBeforeQuery( Class clazz, String query, Object argsQBE) throws PlcException{}

	/**
	 * Ajusta primeira query inicializa, para treeview, trocando argumento pai para is null
	 */
	protected  String createQueryWithArgsAdjustTreeview(String query, Object[] valuesArgs) {
		if ((valuesArgs == null || valuesArgs.length==0) && query.indexOf(":id")>-1) {
			return StringUtils.replaceOnce(StringUtils.replaceOnce(query, "= :id"," is null"), "=:id"," is null");
		}
		return query;
	}
	
	protected String retrieveListQBEPaginatedChangeWhere(String query, String whereDynamic) throws PlcException {
		
		return getServiceQBE().composeSelectWhere(query,whereDynamic);
	}
	
	/**
	 * Recupera o total de registros baseando-se em NamedQuery anotada no padrao conforme a lógica: querySel, queryTreeView, etc.
	 * Todos os valores para argumentos anotados na query padrao devem ser colocados no POJO argsQBE.
	 * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBETotal(java.lang.Class, org.jcompany.commons.PlcBaseEntity)
	 */
	public Integer retrieveListQBETotal( Class clazz,Object argsQBE) throws PlcException {
		return retrieveListQBETotal(clazz,null,argsQBE);
	}
	
	

	/* ******************************************************************************************* */
	/* ********************************   API PARA ENCAPSULAR QUERY      ***************************/
	/* ******************************************************************************************* */

	/**
	 * Cria uma query, executa e retorna apenas um resultado
	 * @see jCompany 5.0 
	 */
	protected abstract Object apiCreateExecuteOnlyOneResult(Class classEntity, String QBE) throws PlcException;
	
	/**
	 * Cria uma query, executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */	
	protected abstract List apiCreateExecute(Class classEntity, String hql) throws PlcException;
	
	/**
	 * Cria uma query, registra seus parametros e executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */	
	protected abstract List apiCreateExecute(Class classEntity, String hql, List argTypes, Object[] argValues) throws PlcException;

	/**
	 * Cria uma query, registra seus parametros e executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */	
	protected List apiCreateQueryWithArgsExecute(Class clazz, String query, 
			String orderByDynamic, Object[] argValues, Object[] argTypes) throws PlcException {
		return apiCreateQueryWithArgsExecute(clazz, query, orderByDynamic, argValues, argTypes, -1, -1);
	}
	
	/**
	 * Cria uma query, registra seus parametros e executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */	
	protected abstract List apiCreateQueryWithArgsExecute(Class clazz, String query, 
			String orderByDynamic, Object[] argValues, Object[] argTypes, int firstLine, int maximumPages) throws PlcException;
	
	/**
	 * Transforma o proxy de uma entidade em um objeto real,
	 * recupperando todo seu grafo. 
	 * @since jCompany 5.0
	 */
	protected abstract Object transformProxyInRealObject(Object entity);
	
	/**
     * @since jCompany 5.0
     * Recebe um objeto Criteria e aplica argumentos dinamicamente.
     */
	protected abstract List criteriaRetrieveQBE(Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE)throws PlcException;
    
	/**
	 * Recupera uma lista de entidades que atendam as argsQBE informados
	 * 
	 * @since jCompany 5.o
	 * @param clazz
	 * @param orderByDynamic
	 * @param argsQBE
	 * @return
	 * @throws PlcException
	 */
	protected List hqlRetrieveQBE(Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":hqlRetrieveQBE:"));

		try {
			
			String hqlBase = getServiceQBE().getQuerySelDefault(clazz,getContext());
			
			List argValues = new ArrayList();
			List argTypes = new ArrayList();
			
			
			String hql = getServiceQBE().hqlArgsQBE(hqlBase,clazz,argsQBE,argValues,argTypes);
			
			hql = getServiceQBE().hqlOrderByDynamic(hql,orderByDynamic,clazz);
						
    		PlcBaseContextVO context = getContext();
			//Alteração 02/10/2006 - by Rodrigo Magno
    		//Verifica se existe filtro vertical informado via perfil e aplica o filtro
    		if(!getServiceQBE().existDefaultFilters(clazz))
    			hql = (getServiceQBE().applyVerticalSecurity(hql, context.getVerticalFilter())).toString();			
			    		
			
			List l = apiCreateExecute(clazz, hql, argTypes, argValues.toArray());
			
			if (log.isDebugEnabled())
				log.debug("Retrieved list with HQL finding  "+l);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":hqlRetrieveQBE:"));
			
			return l;

		}  catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"hqlRetrieveQBE", e }, e, log);
		}
	}
	
	/**
	 * Verifica a query para tentar identificar a classe utilizada no from.
	 * Para isso, deverá estar no formato:
	 *  - "from com.empresa.nome.da.Classe" ou
	 *  - "from obj in class com.empresa.nome.da.Classe"
	 * O nome da classe deverá estar com nome completo, para que a classe possa ser carregada.
	 * @since jCompany 5.0
	 * @param query
	 * @return Class representando a classe da query, ou null se não conseguir determinar.
	 */
	protected Class searchClassByQuery(String query) {
		Class clazz = null;
		
		final Pattern pat = Pattern.compile("from\\s+(?:\\w+\\s+in\\s+class\\s+)?([\\w\\.]+\\.\\w+)(\\s+|$)");
		Matcher matcher = pat.matcher(query);
		if (matcher.find()) {
			String className = matcher.group(1);
			try {
				
				clazz = Class.forName(className);
				
			} catch (Exception e) {
				// nao da erro. Considera que a query nao esta bem formada para analise.
				if (log.isDebugEnabled())
					log.debug("It is not possible to identify a default class for query "+query+". Classe interpreted: "+className);
			}
		}
		return clazz;
	}
	
	/**
	 * Recupera uma lista de objetos.
	 * @param namedQuery Query nomeada a ser utilizada
	 * @param parametrosNames Relação de nomes dos parâmetros
	 * @param parametrosValues Relação de valores dos parâmetros, na mesma ordem dos nomes.
	 * @return Lista de objetos. Deve-se fazer 'casting' utilizando generics. Ex: List minhaLista = (List<MeuObjeto>) recuperaListaComNamedQuery(...).
	 * @throws PlcException Exceção externa tratada de maneira genérica, padrão.
	 */
	protected abstract List<?> retrieveListByNamedQuery(String namedQuery,
			String[] parametrosNames, Object[] parametrosValues) throws PlcException;
	
	/**
	 * Recupera um Objeto.
	 * @param namedQuery Query nomeada a ser utilizada
	 * @param parametrosNames Relação de nomes dos parâmetros
	 * @param parametrosValues Relação de valores dos parâmetros, na mesma ordem dos nomes.
	 * @return Objeto. Deve-se fazer 'casting' para o type esperado. Ex: Integer meuTotal = (Integer) recuperaObjetoComNamedQuery(...).
	 * @throws PlcException Exceção externa tratada de maneira genérica, padrão.
	 */
	protected abstract Object retrieveObjectByNamedQuery(String namedQuery,
			String[] parametrosNames, Object[] parametrosValues) throws PlcException;
	
}

