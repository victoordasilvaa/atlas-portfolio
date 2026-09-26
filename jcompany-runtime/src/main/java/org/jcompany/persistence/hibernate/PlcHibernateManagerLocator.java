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
package org.jcompany.persistence.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcStringHelper;
import org.jcompany.persistence.PlcConstantsPersistence;


/**
 * jCompany 3.0 Service Locator. Singleton. Implementa este Design Pattern para
 * otimizar localização de uma instancia de classe de gerenciamento especifica para Hibernate, vinculada
 * a uma fábrica
 * @since jCompany 3.0
 * @version $Id: PlcHibernateManagerLocator.java,v 1.8 2006/08/17 17:06:04 alvim Exp $
 */
public class PlcHibernateManagerLocator {

	private static PlcHibernateManagerLocator INSTANCE = new PlcHibernateManagerLocator();
	protected static Logger log = Logger.getLogger(PlcHibernateManagerLocator.class);
	
	Map<String, Boolean> initializedFactories = new HashMap<String, Boolean>();

	/**
	 * Classe que conterá a especialização de gerenciamento da Hibernate
	 */
	@SuppressWarnings("unchecked")
	private Map<String,PlcBaseHibernateManager> hibernateManagerClasses = new HashMap();
	
	private PlcHibernateManagerLocator() { }
	public static PlcHibernateManagerLocator getInstance(){
   		return INSTANCE;
	}
    
	/**
	 * Verifica se a fábrica já foi registrada, considerando "default" e "hibernate" nomes alternativos para a fábrica principal
	 * @since jCompany 3.0
	 * 
	 * @param factoryName "default" ou "hibernate" para a principal ou outro qualquer para secundárias
	 * @return true se existir
	 */
	public boolean isRegisteredFactory(String factoryName) throws PlcException {
		if (factoryName.equals("hibernate") || factoryName.equals("default"))
			return hibernateManagerClasses.containsKey("hibernate") || hibernateManagerClasses.containsKey("default");
		else
			return hibernateManagerClasses.containsKey(factoryName);
	}
	
    /**
	 * jCompany 3.0. Registra uma classe responsável por gerenciar o relacionamento com a Hibernate e inicializa o serviço
	 * O default é usar PlcHibernateManager para fabrica 'default'. Pode-se registrar uma diferente para cada configuração
	 */
	public <T extends PlcBaseHibernateManager> void setHibernateManagerClass(String factoryName,T hibernateManagerSpecific) throws PlcException{
	    
	    log.debug("######## Entered in setHibernateManagerClass");
	    hibernateManagerSpecific.registerFactory(factoryName);
	    if (factoryName.equals("hibernate"))
	    	factoryName = "default";
	    this.hibernateManagerClasses.put(factoryName,hibernateManagerSpecific);
				    
	}
	
	/**
	 * jCompany 3.0. Registra uma relação de classes contendo a fábrica que gerencia como anotação
	 * @param classes Relação de classes descendentes de PlcBaseHibernateManager, separadas por virgula. Ex: com.empresa.app.persistencia.AppHibernateManager
	 */
	public void setHibernateManagerClasses(String classes) throws PlcException {
	    
	    log.debug("######## Entered in setHibernateManagerClasses");

	    List l = PlcStringHelper.getInstance().splitListElements(classes);
	    
	    try {
				
		    for (Iterator iter = l.iterator(); iter.hasNext();) {
				String className = (String) iter.next();
				Class clazz = Class.forName(className);
				String factoryName = PlcAnnotationHelper.getInstance().getFactoryName(clazz);
				if (! isInitialized(factoryName)){
					PlcBaseHibernateManager hiberManager = (PlcBaseHibernateManager)clazz.newInstance();
					hiberManager.registerFactory();
					this.hibernateManagerClasses.put(factoryName,hiberManager);			
				}
			    initializedFactories.put(factoryName,true);
			}

		    
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"setHibernateManagerClasses", e }, e, log);
		}
				    
	}
	
	/**
	 * jCompany 3.0 Devolve uma instancia de classe de gerenciamento de fábricas e sessão Hibernate, sendo
	 * esta uma única ocorrencia, descendente de PlcHibernateManager, para IoC.
	 */
	public PlcBaseHibernateManager getHibernateManagerClass(String factoryName) throws PlcException {

		log.debug("########### Entered in getHibernateManagerClasse");
		if (factoryName == null) 
			   factoryName =  "default";
		
		if (hibernateManagerClasses.containsKey(factoryName)) {
			
			// Deve já ter sido registrado
			return (PlcBaseHibernateManager) hibernateManagerClasses.get(factoryName); 
			
		}  else if (PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT.equalsIgnoreCase(factoryName) ||
			PlcConstantsHibernate.CONFIG.FACTORY_DEFAULT_HIBERNATE.equalsIgnoreCase(factoryName)) {
			// Se for fabrica default e ainda nao registrou, assume por default a classe PlcHibernateManager
			setHibernateManagerClass(factoryName,new PlcHibernateManager());
			PlcBaseHibernateManager hibernateManager = hibernateManagerClasses.get(factoryName); 
			initializedFactories.put(factoryName,true);
			return hibernateManager;
		
		}
		else
			return null;
		
	}
	
	/**
	 * @return Returns the inicializado.
	 */
	public Boolean isInitialized(String factory) {
		Boolean isInitialized = (Boolean)initializedFactories.get(factory);
		if (isInitialized == null)
			return false;
		else 
			return isInitialized;
			
	}

}