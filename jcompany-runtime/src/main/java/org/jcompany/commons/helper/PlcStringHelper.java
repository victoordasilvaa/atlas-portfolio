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
package org.jcompany.commons.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * jCompany 2.5.3. Singleton. Classe utilitária para datas
 */
public class PlcStringHelper  {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -5024354235515919974L;
	private static PlcStringHelper INSTANCE = new PlcStringHelper();
    private PlcStringHelper() { }
    public static PlcStringHelper getInstance(){
       return INSTANCE;
    }
   	
	protected static final Logger log = Logger.getLogger(PlcStringHelper.class);

	/**
	 * @since jCompany 3.0
	 * Devolve uma lista de termos que estão entre dois tokens.
	 * @param base String base
	 * @param startsWith Token inicial
	 * @param endsWith Token final
	 * @return Coleção de termos
	 */	
	public java.util.List splitParts (String base, String startsWith, String endsWith) {
				
		ArrayList parts = new ArrayList();
		int lastPositionSeparator= 0;
		int nextIni = 0;
		int nextEnd = 0;
		
		do {

			nextIni = base.indexOf(startsWith,lastPositionSeparator);

			String part = "";
			
			if (nextIni != -1) { 
				
				nextIni++;
				
				// Acha fim
				nextEnd = base.indexOf(endsWith,nextIni);
				
				if (nextEnd == -1) {
					part = base.substring(nextIni,base.length());
				} else {
					part = base.substring(nextIni,nextEnd);
				}
				
				lastPositionSeparator = nextEnd + 1;
				
				parts.add(part);
				
				if (log.isDebugEnabled()) log.debug("Part found="+part);
				
			}
			
		} while (nextIni != -1 && nextEnd != -1 && ((base.length()-lastPositionSeparator)>2));
		
		if (log.isDebugEnabled()) log.debug("Parts found "+parts.size());
		
		return new ArrayList(parts);
	}
	
	/**
	 * @since jCompany 3.0
	 * jCompany. Recebe uma string base, um termo de origem e um novo termo 
	 * e troca todas as ocorrências do original pelo novo.
	 * @param base String contendo todo o texto
	 * @param original String contendo o termo a ser substituído
	 * @param _new String contendo o novo termo
	 * @return String com termos substituídos.
	 */
	public String changePart (String base, String original, String _new) {
		
		if (log.isDebugEnabled()) 
			log.debug("Entered in changePart="+original+" to="+_new + " in:"+
							base);
		
		String result = "";
		int initial = 0;
		int next = 0;
		int endPart = 0;
		do {
			// Separa Token
			next = base.indexOf(original,initial);
			if (next != -1) {
				endPart = next + original.length();
				result = result + 
						base.substring(initial,next)+_new;
				
				if (base.substring(endPart,base.length()).indexOf("_") == -1)
					result = result+ base.substring(endPart,base.length());
				
			}
			initial = next + 1;
		
				
		} while (next != -1 && ((base.length()-initial)>2));
		
		if (result.equals("")) result = base;
		
		if (log.isDebugEnabled()) log.debug("String result="+result);
		
		return result;
	}


	
	/**
	 * @since jCompany 3.0
	 * Devolve coleção de termos separados por virgula
	 */
	public java.util.List splitListElements (String base) {
		
		return splitListElements (base,",");
				
	}
	
	/**
	 * @since jCompany 3.0
	 * Separa termos separados por um separador em uma String Base. Se a base contiver String vazio
	 * ou o caracter "#" despreza, retornando um List vazio.
	 * @param base String no padrão "termo1,termo2,termo3", onde a virgula é o separador
	 * @param separator Substituto para a vírgula. Exs: ponto, hífen, etc..
	 * @return Coleção de Strings com os termos devidamente separados
	 * TODO Refactoring para usar StringTokenizer
	 */
	public java.util.List splitListElements (String base, String separator) {
		
		if (log.isDebugEnabled()) 
			log.debug("Entered in splitListElements ="+base+" separator="+separator);
		
		ArrayList destiny = new ArrayList();
		
		if (base == null || base.equals("") || base.equals("#")) return destiny;
		
		int lastSeparatorPosition= 0;
		int next = 0;
		do {

			next = base.indexOf(separator,lastSeparatorPosition);
			String part = "";
			if (next == -1) {
				part = base.substring(lastSeparatorPosition,base.length()).trim();
			} else {
				part = base.substring(lastSeparatorPosition,next).trim();
			}
			lastSeparatorPosition = next + 1;
			log.debug("Split part="+part);
			
			destiny.add(part);
		} while (next != -1);
				
		return new ArrayList(destiny);
	}
	
	/**
	 * @since jCompany 1.5
	 * Retira trecho de uma String a partir de um identificador de início e
	 * fim. Faz isso uma única vez
	 * @param base String de base
	 * @param initialToken Identificador de inicio
	 * @param finalToken Identificador de fim
	 * @return String sem o trecho entre os dois identificadores. Se não acha token devolve o conteudo original
	 */
	public String removeSubstring(String base,String initialToken, String finalToken) {

		int posIni = base.indexOf(initialToken);
		int posFin = base.indexOf(finalToken);

		if (posIni == -1 || posFin == -1)
			return base;
		else {
		
			posFin = posFin + finalToken.length();

			if (log.isDebugEnabled())
			log.debug("Removing from "+posIni+" ato "+posFin +" and size content  " +
				base.length());
		
			base = StringUtils.replaceOnce(base,base.substring(posIni,posFin),"");
		
			return base;
		}
	}
	
	/**
	 * @since jCompany 3.0
	 * Coloca primeira letra maiúscula
	 * @param part String para retirar acentos
	 * @return String sem acentos
	 */
	public String capitalize(String part)
	{
	
		log.debug("Entered in capitalize");
		
		if(log.isDebugEnabled())
			log.debug("########initial : "+part);
		
		part = StringUtils.capitalize(part);
		
		if(log.isDebugEnabled())
			log.debug("########capitalized: "+part);

		return part;
	}
	
	/**
	 * @since jCompany 3.0
	 * Retira os acentos de uma string
	 * @param part String para retirar acentos
	 * @return String sem acentos
	 */
	public String removeAccent(String part)
	{
	
		log.debug("Entered in removeAccent");
		
		String accents 		= "áàãâäèéêëìíïîòóõôöûùúüçÁÀÃÂÄÈÉÊËÌÍÏÎÒÓÕÔÖÛÙÚÜÇ";
		String noAccents 	= "aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC";

		part = CharSetUtils.translate(part, accents, noAccents);
		
		return part;
	}
	
	/**
	 * @since jCompany 3.0
	 * Recebe uma String com vários "tokens" concatenados e separados por um caracter especial e um token
	 * a ser removido desta String. Devolve um StringBuffer sem o token.
	 * @param baseValue String contendo vários tokens
	 * @param tokenToRemove Token a ser removido do valorBase
	 * @param separator String que separa cada token
	 * @return StringBuffer baseada no valorBase sem tokenARemover.
	 */
	public String removeToken(String baseValue, String tokenToRemove, String separator) {
		
		if (log.isDebugEnabled()) 
		    log.debug("List received:"+baseValue+" to remove:"+tokenToRemove+
		            " separator:"+separator);
		
		StringTokenizer st = new StringTokenizer(baseValue,separator);
		StringBuffer sb = new StringBuffer();
		int cont = 0;

		while (st.hasMoreTokens()) {
			String idCookie = st.nextToken();
			if (!idCookie.equals(tokenToRemove)) {
				cont++;
				sb.append(idCookie);
				sb.append(separator);
			} else
				if (log.isDebugEnabled()) log.debug("Item Removed:"+idCookie);
		}
		
		// Retira o último separador, se tiver
		String ret ="";
		if (!sb.toString().equals("")) {
			ret = sb.toString().substring(0,sb.toString().length()-1);
		}
				
		if (log.isDebugEnabled()) log.debug("Retrieving:"+ret);
		return ret;
	}
	
	/**
	 * @since jCompany 1.5
	 * Troca um nome com sublinhado por pontos. Na declaração de clausulas
	 * para restrição de existência (duplicidade) e outras, no caso de propriedades
	 * de argumentos serem de classes agregadas, o usuário deve informar o separador
	 * entre propriedades como sublinhados ou invés de ponto, para não ferir a lógica
	 * automática do jCompany. Este método reconverte para a notação de ponto para envio
	 * ao SGBD.<p>
	 *  Exemplo: Declaração no struts-config.xml:
	 *
	 *      obj.idAgregado.nome=:idAgregado_nome
	 *
	 * @param name String a ser inspecionada, com sublinhado
	 * @return String alterada, com pontos
	 */
	protected String verifyName(String name) {
	   
		if (name.indexOf("_") > 0) {
			
			PlcStringHelper plUtil = PlcStringHelper.getInstance();;
			
			name = plUtil.changePart(name,"_",".");

			if (log.isDebugEnabled())
            	log.debug(" name with dots = " + name);
			
			return name;
			
		} else
			return name;

	}
	
	/**
	 * @since jCompany 3.04
	 * @param campos Recebe um Array
	 * @return String contendo valores do array concatenados.
	 */
	public String arrayToString(Object[] array) {
		if (array == null)
			return "";
		StringBuffer s = new StringBuffer("");
		for (int i = 0; i < array.length; i++) {
			Object obj = array[i];
			if (obj != null)
				s.append(obj.toString());
		}
		return s.toString();
	}
	
		/**
	 * @since jCompany 3.05
	 * @param listOfStrings List de Strings
	 * @return Vetor de Strings
	 */
	public String[] listToStrings(List<String> listOfStrings) {

		String[] valueArray = new String[listOfStrings.size()];
		int counter=0;
		for (Iterator iter = listOfStrings.iterator(); iter.hasNext();) {
			String value = (String) iter.next();
			valueArray[counter]=value;
			counter++;
		}
		return valueArray;
	}
	

	public String replaceIgnoreCase(String content, String oldString, String newString){
	    content = StringUtils.replace(content, oldString, newString);
	    content = StringUtils.replace(content, oldString.toUpperCase(), newString);
	    content = StringUtils.replace(content, oldString.toLowerCase(), newString);

	    return content;
	}

	
}