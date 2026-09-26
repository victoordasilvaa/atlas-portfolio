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
package org.jcompany.persistence.jpa.listeners;

import java.util.Date;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.persistence.listener.PlcBasePersistenceListener;


public class PlcBaseEntityListener  extends PlcBasePersistenceListener {
	
	protected static Logger log = Logger.getLogger(PlcBaseEntityListener.class);
	
	 protected  void registerAuditInsert(PlcBaseEntity baseEntity) throws PlcException {
			
			log.debug("############### Entered registerAuditInsert");
			
			PlcBaseContextVO context = getContext();
			PlcBaseUserProfileEntity baseUserProfileEntity = context.getUserProfile();
			
			Date date = new Date();
			String user = null;
			
			if (baseUserProfileEntity==null || baseUserProfileEntity.getLogin()==null)
				user = "Anônimo";
			else
				user = baseUserProfileEntity.getLogin();
			
			try {
				if(PropertyUtils.isWriteable(baseEntity, PlcConstantsCommons.ENTITY.DATE_CREATION))
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.DATE_CREATION,date);
					
				if(PropertyUtils.isWriteable(baseEntity, PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE)) 
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE,date);
				
				if(PropertyUtils.isWriteable(baseEntity, PlcConstantsCommons.ENTITY.USER_CREATION)) 
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.USER_CREATION,user);
				
				if(PropertyUtils.isWriteable(baseEntity, PlcConstantsCommons.ENTITY.USER_LAST_UPDATE)) 
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.USER_LAST_UPDATE,user);
				
			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"registerAuditInsert", e }, e, log);
			}
			
		}
	 
	 protected  void registerAuditUpdate(PlcBaseEntity baseEntity) throws PlcException {
			
			log.debug("############### Entered in registweAuditUpdate");
			
			PlcBaseContextVO context = getContext();
			PlcBaseUserProfileEntity baseUserProfileEntity = context.getUserProfile();
			
			Date date = new Date();
			String user = null;
			
			if (baseUserProfileEntity==null || baseUserProfileEntity.getLogin()==null)
				user = "Anonym";
			else
				user = baseUserProfileEntity.getLogin();
			
			try {
	
				if(PropertyUtils.isWriteable(baseEntity, PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE)) 
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE,date);
				
				if(PropertyUtils.isWriteable(baseEntity, PlcConstantsCommons.ENTITY.USER_LAST_UPDATE)) 
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.USER_LAST_UPDATE,user);
				
			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"registweAuditUpdate", e }, e, log);
			}
			
		}

	@PrePersist
	public void prePersist(PlcBaseEntity entity) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in ON_PRE_INSERT for class "+entity.getClass().getName()+ " value: "+entity);

		try {
	
			//Se detalhe estiver anotado para somenteLeitura evita sua inclusão
			if (getContext()!=null && getContext().getMainClass() != null &&
					!getContext().getMainClass().isAssignableFrom(entity.getClass()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(getContext().getLogic()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(getContext().getLogic()) &&
					PlcAnnotationHelper.getInstance().existReadonlyDetail(entity.getClass())) {
					//TODO Marcar para Rollback com setRollBackOlnly no EjbContext
			}
			
				registerAuditInsert(entity);
			
			//carregaAgregadosComRowid(event.getPersister(), event.getEntity());
			
			// False permite que a inserção continue
			
		} catch (Exception e) {
			log.fatal("Error trying to insert "+e,e);
		}
	}
	
	@PreUpdate
	public void preUpdate(PlcBaseEntity vo) {
		if (log.isDebugEnabled())
			log.debug("############### Entered in ON_PRE_UPDATE for class "+vo.getClass().getName()+ " value: "+vo);

		try {
	
			//Se detalhe estiver anotado para somenteLeitura evita sua inclusão
			if (getContext()!=null && getContext().getMainClass() != null &&
					!getContext().getMainClass().isAssignableFrom(vo.getClass()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(getContext().getLogic()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(getContext().getLogic()) &&
					PlcAnnotationHelper.getInstance().existReadonlyDetail(vo.getClass())) {
					//TODO Marcar para Rollback com setRollBackOlnly no EjbContext
			}
			
				registerAuditUpdate(vo);
			
			//carregaAgregadosComRowid(event.getPersister(), event.getEntity());
			
			// False permite que a inserção continue
			
		} catch (Exception e) {
			log.fatal("EError trying to update "+e,e);
		}
	}
	
	@PreRemove
	public void preRemove(PlcBaseEntity vo) {  
		if (log.isDebugEnabled())
			log.debug("############### Entered in ON_PRE_REMOVE for class "+vo.getClass().getName()+ " value: "+vo);
	}
	
	@PostLoad
	public void postLoad(PlcBaseEntity vo) {
		if (log.isDebugEnabled())
			log.debug("############### Entered in ON_PRE_LOAD for class "+vo.getClass().getName()+ " value: "+vo);
	}
}
