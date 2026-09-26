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
package org.jcompany.persistence.hibernate.service;

import java.beans.PropertyDescriptor;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.annotation.PlcAudit;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigAudit;
import org.jcompany.persistence.hibernate.PlcBaseHibernateManager;
import org.jcompany.persistence.hibernate.PlcHibernateManagerLocator;


/**
 * Lógicas de auditoria rígida em esquema de tabelas paralelas
 * @since jCompany 3.1.1
 */
public class PlcAuditService {

	protected static Logger log = Logger.getLogger(PlcAuditService.class);
	
	/**
	 * Atualiza auditoria rígida genericamente, após alteração.
	 * Regra: Se chegou neste método e tem anotação, sempre grava auditoria. 
	 * Para evitar gravações de auditoria quando não existem modificações (quando nao se está lembrando mestre/detalhes
	 * ou quando nao se está otimizando os updates via jCompany), entao deve-se utilizar opções da Hibernate tais como "selectBeforeUpdate".
	 * @param beanCurrent Entidade Atual
	 */
	public void insertAudit(Object beanCurrent,String eventType) throws PlcException {
		
		PlcAudit audit = beanCurrent.getClass().getAnnotation(PlcAudit.class);
		
		if (audit!=null && insertAuditBefore(beanCurrent, eventType)) {
			// Cria bean para auditoria, com sufixo padrao indicado
			try {

				// TODO Retirar esta linha com genericos
				PlcConfigAudit configAudit = (PlcConfigAudit) PlcConfigHelper.getInstance().get(PlcConfigAudit.class);
				Object beanAudit = Class.forName(beanCurrent.getClass().getPackage().getName()+"."+
						configAudit.classAuditWithoutPackage()+"."+
						PlcEntityHelper.getInstance().removeSuffixEntity(beanCurrent.getClass().getSimpleName())+
						configAudit.classAuditSuffix()).newInstance();

				BeanUtils.copyProperties(beanAudit,beanCurrent);
					
				// Sessão nova e temporária para evitar problemas de caching
				Session sessTemp = insertAuditGetSession();
				
				sessTemp.beginTransaction();
				
				insertAuditNullingCollections(beanAudit);
				
				insertAuditMountingFields(beanCurrent,beanAudit,eventType);
				
				insertAuditFullApi(beanCurrent,beanAudit,eventType);
				
				sessTemp.save(beanAudit);
				sessTemp.flush();
				// IMPORTANTE: Nao deve dar este commit, porque a transacao é compartilhada
				// com a transação principal (mesmo connection()). Deste modo um commit afetaria a ULT
				// de negocio!!
				//sessTemp.getTransaction().commit();
				//---------------------------------------------------------------------------------
				sessTemp.close();
				
			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"insertAudit", e }, e, log);
			}
			
		}
	}
	
	protected boolean insertAuditBefore(Object beanCurrent, String eventType) throws PlcException {
		return true;
	}

	/**
	 * Devolve sessão de persistência
	 * @return Sessão de unidade de persistência que contém classes de auditoria
	 */
	protected Session insertAuditGetSession() throws PlcException {
		
		PlcBaseHibernateManager manager = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default");
		
		Session sess = manager.getSessionFactory().openSession(manager.getSession().connection());
		// Devolve uma sessão temporária
		return sess;
		
	}

	protected void insertAuditMountingFields(Object beanCurrent, Object beanAudit,String eventType) throws PlcException {
		
		try {
			
			PropertyUtils.setProperty(beanAudit,"id",null);
			// Precisa de reforço quando há anotaçao na propriedade e ela está replicada no descendente
			PropertyUtils.setProperty(beanAudit,"idAux",null);
			PropertyUtils.setProperty(beanAudit,"auditComp.idOriginal",PropertyUtils.getProperty(beanCurrent,"id"));
			PropertyUtils.setProperty(beanAudit,"auditComp.eventType",eventType);
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"insertAuditMountingFields", e }, e, log);
		}
		
		
	}
	
	protected void insertAuditNullingCollections(Object beanAudit) throws PlcException {
		
		try {

			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(beanAudit);
			for (int i = 0; i < pds.length; i++) {
				if (Collection.class.isAssignableFrom(pds[i].getPropertyType()))
					PropertyUtils.setProperty(beanAudit,pds[i].getName(),null);
			}

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"insertAuditNullingCollections", e }, e, log);
		}
		
		
	}
	
	
	/**
	 * Método para especialização de lógicas de auditoria rígida.
	 * @since jCompany 3.1.1 
	 * @param beanCurrent
	 * @param beanAudit
	 * @param eventType
	 * @throws PlcException
	 */
	protected void insertAuditFullApi(Object beanCurrent, Object beanAudit, String eventType) throws PlcException {}
	

	
}
