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

import org.apache.log4j.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.ejb.Ejb3Configuration;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.annotation.PlcFactory;
import org.jcompany.persistence.PlcConstantsPersistence;


@PlcFactory(nome="default",autoDetectDialect=true)
public class PlcJpaManager extends PlcBaseJpaManager {
	
	protected static Logger log = Logger.getLogger(PlcJpaManager.class); 
	
	protected String defineMappingFile(String persistenceUnit) {
		
		String cfgFileName = null;
		
		if (persistenceUnit.equals(PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT)) {
  			cfgFileName = "/"+PlcConstantsPersistence.CONFIG.HIBERNATE.PREFIX_CFG_FILE+PlcConstantsPersistence.CONFIG.HIBERNATE.SUFFIX_GFG_FILE;
  		} else {
  			cfgFileName = "/"+persistenceUnit+PlcConstantsPersistence.CONFIG.HIBERNATE.SUFFIX_GFG_FILE;
  		}
		
		return cfgFileName;
		
	}

	 
	  /**
	   * @since jCompany 5.0 
	   * Registra fábricas para uma unidade de persistência específica.
	   */
	public void registerFactory(String persistenceUnit) throws PlcException {
	    
		String cfgFileName = defineMappingFile(persistenceUnit);
		
	  	try {
	  			
	  			
	  	    log.info("######## Entered to configure persistenceUnit: " + persistenceUnit +" using file: "+cfgFileName);
	  	    
	  	    //TODO Tratar autodetect dialect
	  	    //this.emf =  Persistence.createEntityManagerFactory(unidadePersistencia);
	 
	  	    //TODO Garantir que o arquivo da configuração sera o mesmo indicado no persistence.xml sem  cfg.createEntityManagerFactory();
	  	    Ejb3Configuration auxCfg = new Ejb3Configuration();
	  	    
	  	    AnnotationConfiguration cfgA = auxCfg.getHibernateConfiguration();
	  	    
	  	    configListeners(persistenceUnit,cfgA);
	  	    
	  	    this.cfg =  auxCfg.configure(cfgFileName);
	  	    this.emf = auxCfg.buildEntityManagerFactory();
	  	    
	  	    
	  		log.info("######## Configured persistenceUnit: " + persistenceUnit+" using file: "+cfgFileName);

			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"registerFactory", e }, e, log);
			}
	  }
	
	
}
