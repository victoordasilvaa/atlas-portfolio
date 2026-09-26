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
package org.jcompany.commons.helper;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigEjbFacadeRef;



public class PlcEjbHelper {
	
	private static PlcEjbHelper INSTANCE = new PlcEjbHelper();
	
	private PlcEjbHelper() {
		
	}
	
	public static PlcEjbHelper getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Faz lookup em busca de um EJB, baseado no nome do serviço, nome do artefato EJB
	 * Primeiro tenta lookup local, se nao achar faz lookup remoto
	 * 
	 * Adota o Padrão:
	 * Nome do EJB Local:  nome do serviço		Ex: AppFacadeImpl
	 * Nome do EJB Remoto: nome do serviço + 'Remote' Ex: AppFacadeImplRemote
	 * 
	 */
	public Object lookupEJB(String serviceName)throws PlcException{

		try{
			
			
			Hashtable<String, String> props = new Hashtable<String, String>();
			PlcConfigEjbFacadeRef configEJB = PlcConfigHelper.getInstance().get(PlcConfigEjbFacadeRef.class);

			if (StringUtils.isNotBlank(configEJB.initialContextFactory()))
				props.put(InitialContext.INITIAL_CONTEXT_FACTORY, 	configEJB.initialContextFactory());

			if (StringUtils.isNotBlank(configEJB.urlPkgPrefixes()))
				props.put(InitialContext.URL_PKG_PREFIXES,			configEJB.urlPkgPrefixes());

			if (StringUtils.isNotBlank(configEJB.providerUrl()))
				props.put(InitialContext.PROVIDER_URL, 				configEJB.providerUrl());

			InitialContext ic = new InitialContext(props);
			try {
				// lookup Local 
				return  ic.lookup(PlcConstantsCommons.PLC_DEFAULT_PREFIX_JNDI_NAME + serviceName );

			} catch (NamingException e) {
				
				String prefixoJNDIApp 	= PlcConfigHelper.getInstance().get(PlcConfigEjbFacadeRef.class).prefixNameJNDIApp();
				if (!serviceName.startsWith(prefixoJNDIApp))
					serviceName = prefixoJNDIApp + "/" + serviceName;
				
				// lookup Remoto
				//return  ic.lookup(nomeServico + PlcConstantsCommons.PLC_DEFAULT_SUFFIX_JNDI_NAME_REMOTE);
				return  ic.lookup(serviceName);
			}

		} catch (Exception e) {
			return null;
		}
	}


}
