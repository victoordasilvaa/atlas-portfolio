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
package org.jcompany.persistence.listener;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcException;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.persistence.PlcContextManager;
import org.jcompany.persistence.hibernate.PlcBaseHibernateListener;
import org.jcompany.persistence.hibernate.service.PlcAuditService;


public class PlcBasePersistenceListener {
	
	protected static Logger log = Logger.getLogger(PlcBaseHibernateListener.class);

	/* ******************************************************************************************* */
	/* ********************************  INICIO SERVIÇOS APOIO *********************************** */
	/* ******************************************************************************************* */

	protected PlcAuditService serviceAudit;
	
	/**
	 * Pseudo-Inj. Dependência. Para especializar o serviço de auditoria especializar este método retornando a
	 * instancia específica!
	 * @return instancia única de PlcAuditService
	 */
	public PlcAuditService getServiceAudit() {

		try {
			if (serviceAudit==null)
				serviceAudit= (PlcAuditService) PlcConfigHelper.getInstance().createService(PlcAuditService.class);
			return serviceAudit;
		} catch (PlcException e) {
			log.fatal("Error trying to insert "+e.getRootCause(),e.getRootCause());
			throw new HibernateException("Error trying to insert: "+e.getRootCause(),e.getRootCause());
		}
	}

	/**
	 * jCompany 3.0 DP Composite. Devolve o POJO contendo informações de contexto do cliente
	 */
	protected PlcBaseContextVO getContext() {
		if (PlcContextManager.getContextEntity()==null)
			return new PlcBaseContextVO();
		else
			return PlcContextManager.getContextEntity();
	}

   

}
