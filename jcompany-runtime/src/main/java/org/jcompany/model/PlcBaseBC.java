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
package org.jcompany.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.persistence.PlcContextManager;


/**
 * jCompany 3. BusinessComponent. Base para objetos que realizam regras de negócio<p>
 * Segundo 'Core J2EE Design Patterns': Um Businesss Component atende à solicitação do cliente, implementando o 
 * serviço de negócio em si, podendo ser implementado como um Business Object (BO) ou como um Application Service
 * (AS).
 * @since jCompany 3.0
 * @version $Id: PlcBaseBC.java,v 1.5 2006/07/24 13:43:13 alvim Exp $
*/
public class PlcBaseBC {

	protected static final Logger logModel = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_MODEL);
	protected static final Logger logWarning = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_WARNING_DEVELOPMENT);
    protected Logger log = Logger.getLogger(PlcBaseBC.class);

    /**
     * DP Composite. Pega um Business Component do type AS - Application Service (orquestrador) <p>
     * Ex: meupacote.vo.Candidato.class
     *
    public PlcBaseAS getAS(Class classeAS) throws PlcException {
        return (PlcBaseAS)PlcBCLocator.getInstance().getBusinessComponent(classeAS);
    }
*/
    
    /**
     * jCompany 3.0 DP Composite. Devolve o POJO contendo informações de contexto do cliente
     */
    protected PlcBaseContextVO getContext() throws PlcException {
        return PlcContextManager.getContextEntity();
    }
    
    /**
     * jCompany 3.0 DP Composite. Devolve o POJO contendo informações de contexto do cliente
     */
    protected PlcBaseUserProfileEntity getUserProfileEntity() throws PlcException {
    	PlcBaseContextVO context = getContext();
    	if (context == null)
    		return null;
    	else
    		return context.getUserProfile();
    }
  
	protected void beforeFinalizeConsultation(PlcBaseContextVO contextParam, 
	    		Class typeEntity, List result) throws PlcException {	}
	    
	protected void afterFinalizeConsultation(PlcBaseContextVO contextParam, 
	    		Class typeEntity, List result) throws PlcException {	}

}
