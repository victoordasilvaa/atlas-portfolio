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
package org.jcompany.persistence.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcStringHelper;
import org.jcompany.persistence.PlcConstantsPersistence;
import org.jcompany.persistence.hibernate.PlcConstantsHibernate;


/**
 * @since jCompany 5.0
 * 
 * Service Locator. Singleton. Implementa este Design Pattern para otimizar localização
 * de uma instancia de classe de gerenciamento especifica para JPA, vinculada
 * a uma fábrica
 * @author joaopaulo.santos
 * @version $Id
 */
public class PlcJpaManagerLocator {
	
	static private PlcJpaManagerLocator INSTANCE = new PlcJpaManagerLocator();

	private Boolean initialized = false;
	protected static Logger log = Logger.getLogger(PlcJpaManagerLocator.class);

	@SuppressWarnings("unchecked")
	private Map<String,PlcBaseJpaManager> jpaManagerClasses = new HashMap();
	
	private PlcJpaManagerLocator() {}
	
	/**
	 * @since jCompany 5.0
	 * @return a intancia "unica" da classe
	 */
	public static PlcJpaManagerLocator getInstance() {
		return INSTANCE;
	}
	
	/**
	 * @since jCompany 5.0
	 * Testa se uma fabrica especifica foi registrada 
	 * @param factoryName
	 * @throws PlcException
	 */
	public boolean isFactoryRegistered(String factoryName) throws PlcException {
		if (factoryName.equals("hibernate") || factoryName.equals("default"))
			return jpaManagerClasses.containsKey("hibernate") || jpaManagerClasses.containsKey("default");
		else
			return jpaManagerClasses.containsKey(factoryName);
	}
	
	/**
	 * @since jCompany 5.0
	 * Atribui um manager especifico , mapeando para um nome de fabrica
	 * @param factoryName
	 * @param jpaManagerSpecific
	 * @throws PlcException
	 */
	public void setJpaManagerClass(String factoryName,PlcBaseJpaManager jpaManagerSpecific) throws PlcException{
	    
	    log.debug("######## Entered in setJpaManagerClass");
	    jpaManagerSpecific.registerFactory(factoryName);
	    this.jpaManagerClasses.put(factoryName,jpaManagerSpecific);
				    
	}

	/**
	 * @since jCompany 5.0
	 * Atribui "por lote" managers especificos, mapeando os mesmos p/ nomes de fabricas
	 * @param classes
	 * @throws PlcException
	 */
	public void setJpaManagerClasses(String classes) throws PlcException {
	    
	    log.debug("######## Entered in setJpaManagerClasses");
	    if(!initialized) {
	    	List l = PlcStringHelper.getInstance().splitListElements(classes);
	    
	    		try {
				
	    			for (Iterator iter = l.iterator(); iter.hasNext();) {
	    				String className = (String) iter.next();
	    				Class clazz = Class.forName(className);
	    				String factoryName = PlcAnnotationHelper.getInstance().getFactoryName(clazz);
				
	    				if(clazz.isInstance(PlcBaseJpaManager.class)){
	    					PlcBaseJpaManager manager = (PlcBaseJpaManager)clazz.newInstance();
	    					setJpaManagerClass(factoryName, manager);
	    				}	
	    			}

	    			initialized = true;
		    
	    		} catch (Exception e) {
	    			throw new PlcException("jcompany.error.generic", new Object[] {
	    					"setJpaManagerClasses", e }, e, log);
	    			}
	    }
				    
	}
	
	/**
	 * @since jCompany 5.0
	 * recupera um manager especifico que esta mapeado para um nome de fabrica
	 * @param factoryName
	 * @return
	 * @throws PlcException
	 */
	public PlcBaseJpaManager getJpaManagerClasse(String factoryName) throws PlcException {

		log.debug("########### Entered in getJpaManagerClasse");
		if (factoryName == null) 
			   factoryName =  "default";
		
		if (jpaManagerClasses.containsKey(factoryName)) {
			
			// Deve já ter sido registrado
			return (PlcBaseJpaManager) jpaManagerClasses.get(factoryName); 
			
		}  else if (PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT.equalsIgnoreCase(factoryName) ||
			PlcConstantsHibernate.CONFIG.FACTORY_DEFAULT_HIBERNATE.equalsIgnoreCase(factoryName)) {
			// Se for fabrica default e ainda nao registrou, assume por default a classe PlcHibernateManager
			setJpaManagerClass(factoryName,new PlcJpaManager());
			PlcBaseJpaManager jpaManager = jpaManagerClasses.get(factoryName); 
			initialized=true;
			return jpaManager;
		
		} else
			throw new PlcException("jcompany.factory.not.found",new Object[]{factoryName});
		
	}
	
	/**
	 * @since jCompany 5.0
	 * @return se os mapeamentos de managers especificos já foi feito 
	 */
	public Boolean isInitialized() {
		return initialized;
	}

	

}
