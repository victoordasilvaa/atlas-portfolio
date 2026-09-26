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

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;


/**
 * @since jCompany 3.0
 * Classe de Factory para Proxies CGlib que permitem AOP.
 */ 
public class PlcAopManager {
	
	protected static final Logger log = Logger.getLogger(PlcAopManager.class);
	
	/**
	 * @since jCompany 3.0
	 * Cache com enhancers do CGLib (fábrica de descendentes)
	 */
	protected static Map<Class,Enhancer> 	enhancers = new HashMap<Class,Enhancer>();
	/**
	 * @since jCompany 3.0
	 * Cache dos proxies criados
	 */
	protected static Map<Class,Object> 	enhancedClasses = new HashMap<Class,Object>();
	
	/**
	 * @since jCompany 3.0
	 * Cria um Proxy via CGLib, descendente da classe informada e com PlcTransacaoService como Interceptor
	 * @param clazz Classe a ser utilizada como base para o Proxy
	 * @return Proxy (Classe descendente dinâmica da informada)
	 */
	public static Object createProxy(Class clazz,PlcAopBaseCallback aopBaseCallback) {
		
		log.debug("############### Entered in createProxy");
		
		Object o = enhancedClasses.get(clazz);
		
		if (o == null){
			Enhancer e = getEnhancer(clazz,aopBaseCallback);
			o = e.create();
			enhancedClasses.put(clazz,o);
		}
		return o;
		
	}
	
	/**
	 * @since jCompany 3.0
	 * Cria um Proxy via CGLib, descendente da classe informada e com PlcTransacaoService como Interceptor
	 * 
	 * @param clazz Classe a ser utilizada como base para o Proxy
	 * @param types Tipos dos argumentos do construtor
	 * @param params Parametros para o construtor
	 * @return Proxy (Classe descendente dinâmica da informada)
	 */
	public static Object createProxyByConstructor(Class clazz,Class[] types, Object[] params,PlcAopBaseCallback aopBaseCallback) throws PlcException {

		log.debug("############### Entered in createProxyPorConstrutor");
		
		Object o = enhancedClasses.get(clazz);
		
		if (o == null){
			Enhancer e = getEnhancer(clazz,aopBaseCallback);
			o = e.create(types,params);
			enhancedClasses.put(clazz,o);
		}
		return o;
	}

	/**
	 * @since jCompany 3.0
	 * Pega objetos criadores de proxy (também faz caching)
	 */
	private static Enhancer getEnhancer(Class clazz,PlcAopBaseCallback aopBaseCallback) {
		log.debug("############### Entered in getEnhancer");

		Enhancer e = (Enhancer) enhancers.get(clazz);
		if (e == null) {
			e = new Enhancer();
			
			e.setSuperclass(clazz);
			e.setCallback(aopBaseCallback);
			e.setInterfaces(clazz.getInterfaces());
			
			enhancers.put(clazz,e);
		}
		return e;
		
	}

}
