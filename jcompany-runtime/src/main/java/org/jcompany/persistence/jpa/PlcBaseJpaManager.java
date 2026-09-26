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


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.ejb.Ejb3Configuration;
import org.jcompany.commons.PlcException;
import org.jcompany.persistence.PlcBaseConfigListener;
import org.jcompany.persistence.PlcConstantsPersistence;



public abstract class PlcBaseJpaManager extends PlcBaseConfigListener{
	
	/**
	 * Fabrica para criação de EntityManager(Sessões)
	 */
	protected EntityManagerFactory emf = null;
	/**
	 * Configurações para a fabrica de EntityManager
	 */
	protected Ejb3Configuration cfg = null;
	/**
	 * EntityManager(Sessão) ThreadSafe
	 */
	protected ThreadLocal<EntityManager> em = new ThreadLocal<EntityManager>();
	/**
	 * Flag para controle de commit e rollback
	 */
	protected boolean emSession = false;
	
	protected static Logger log = Logger.getLogger(PlcBaseJpaManager.class);
	

	/**
	 * Construtor padrão
	 */
	public PlcBaseJpaManager() {}
	
	/**
	 * jCompany 5.0
	 * Recupera uma fábrica de EntityManagers. 
	 * Se não existir (não foi criada) cria com classe default PlcHibernateManager.
	 */
	 public EntityManagerFactory getEntityManagerFactory() throws PlcException {
		 if(emf == null) {
			 registerFactory();
		 }
	     return emf;
	 }
	 
	 
	/**
	 * @since jCompany 5.0
	 * 
	 * @return o "current" EntityManager simulando o comportamento
	 * esperado do SessionFactory.getCurrentSession()
	 */
	public EntityManager getEntityManager() {		
		if(!emSession) {
			EntityManager entityManager = emf.createEntityManager();
			entityManager.getTransaction().begin();
			em.set(entityManager); //Armazena Entity Manager no ThreadLocal para simular "current" session
			emSession = true;
		}
		return em.get(); 
	}

	/**
	 * @since jCompany 5.0
	 * 
	 * @return a configuração O/R utilizada na construcao da fabrica
	 */
	public Ejb3Configuration getCfg() {
		return cfg;
	}

	/**
	 * @since jCompany 5.0
	 * 
	 * Registra a fabrica DEFAULT
	 */
	public void registerFactory() throws PlcException {
		  registerFactory(PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT);
	  }
	  
  /**
   * @since jCompany 5.0 
   * Registra fábricas para uma unidade de persistência específica.
   */
	public abstract void registerFactory(String persistenceUnit) throws PlcException;
  
  
  	/**
  	 * @since jCompany 5.0
  	 * executa rollback no "current" Entity Manager
  	 * @throws PlcException
  	 */
  	public void rollback()  throws PlcException {
	
  		EntityManager localEm = em.get();
  		if(localEm!= null) {
  			try {
  				log.debug("######## Calling ROLLBACK emSession? "+emSession+" Transaction? "+localEm.getTransaction().isActive());

  				if(localEm.getTransaction().isActive()) {
  					localEm.getTransaction().rollback();
  				}
  				emSession = false;
  			} catch (Exception e) {
		
  				throw new PlcException("jcompany.error.generic", new Object[] {
				"rolback", e }, e, log);
  			}
  		}
  	}
  	
  	/**
  	 * @since jCompany 5.0
  	 * executa commit no "current" Entity Manager
  	 * @throws PlcException
  	 */
  	public void commit() throws PlcException {
	
  		EntityManager localEm = em.get();
  		if(localEm!= null) {
  			try {
  				log.debug("######## Calling COMMIT emSession? "+emSession+" Transaction? "+localEm.getTransaction().isActive());

  				if(localEm.getTransaction().isActive()) {
  					localEm.getTransaction().commit();
  				}
		
  				emSession = false;
  			} catch (Exception e) {
		
  				throw new PlcException("jcompany.error.generic", new Object[] {
  						"commit", e }, e, log);
  			}
  		}
  	}	

}
