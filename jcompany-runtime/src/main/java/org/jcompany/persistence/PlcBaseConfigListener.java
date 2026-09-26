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

import org.apache.log4j.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.event.def.DefaultPostLoadEventListener;
import org.hibernate.event.def.DefaultPreLoadEventListener;
import org.jcompany.commons.PlcException;
import org.jcompany.persistence.hibernate.PlcBaseHibernateListener;


/**
 * Configura listeners eventos
 *  
 * @author Pedro Henrique
 *
 */
public class PlcBaseConfigListener {
	
	protected static Logger log = Logger.getLogger(PlcBaseConfigListener.class); 
	
	/**
     * jCompany 3.0 Declara listeners para todos os eventos, para aplicação de lógicas de segurança
     * @param nome, Fábrica
     * @param cfg, Annotation Configuration que seá adicionado os listeners
     */
	protected void configListeners(String name, AnnotationConfiguration cfg) throws PlcException {

		log.debug("############### Entered in configListeners");

		// reusa a mesma instância em todos os listeners
		PlcBaseHibernateListener baseHibernateListener = getListener();

		// Evento antes das operaçoes CRUD
		PreInsertEventListener[] preInsertL = {baseHibernateListener};
		cfg.getEventListeners().setPreInsertEventListeners(preInsertL);

		PreLoadEventListener[] preLoadL = { baseHibernateListener, new DefaultPreLoadEventListener()};
		cfg.getEventListeners().setPreLoadEventListeners(preLoadL);

		PreUpdateEventListener[] preUpdateL = { baseHibernateListener};
		cfg.getEventListeners().setPreUpdateEventListeners(preUpdateL);

		//SaveOrUpdateEventListener[] saveUpdateL = { baseHibernateListener, new DefaultSaveOrUpdateEventListener()};
		//cfg.getEventListeners().setSaveOrUpdateEventListeners(saveUpdateL);

		PreDeleteEventListener[] preDeleteL = { baseHibernateListener};
		cfg.getEventListeners().setPreDeleteEventListeners(preDeleteL);

		// Evento antes após operaçoes CRUD
		PostInsertEventListener[] postInsertL = {baseHibernateListener};
		cfg.getEventListeners().setPostInsertEventListeners(postInsertL);

		PostLoadEventListener[] postLoadL = { baseHibernateListener, new DefaultPostLoadEventListener()};
		cfg.getEventListeners().setPostLoadEventListeners(postLoadL);

		PostUpdateEventListener[] postUpdateL = { baseHibernateListener};
		cfg.getEventListeners().setPostUpdateEventListeners(postUpdateL);

		PostDeleteEventListener[] postDeleteL = { baseHibernateListener};
		cfg.getEventListeners().setPostDeleteEventListeners(postDeleteL);

		configListenersAfter(name,cfg,baseHibernateListener);

	}

	/**
	 * Método DP Template Method para registro de novos Listeners em classes descendentes.
	 * @since jCompany 3.0
	 * @param name Nome da Fábrica
	 * @param cfg Configuration Hibernate
	 * @param baseHibernateListener Listener default do jcompany
	 */
	protected void configListenersAfter(String name, AnnotationConfiguration cfg, 
			PlcBaseHibernateListener baseHibernateListener) throws PlcException {

	}
	
	protected  PlcBaseHibernateListener listener = null;
	  /**
	   * @since jCompany 3.1
	   * Pseudo-ID. Classe que implementa eventos de Listener. Deve ser sobreposto para especializações do Listener
	   */
	  protected PlcBaseHibernateListener getListener() {
		  if (listener == null)
			  listener = new PlcBaseHibernateListener();
		  return listener;
	  }

}
