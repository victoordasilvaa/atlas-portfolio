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
package org.jcompany.facade;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.ioc.PlcBaseLocator;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.model.PlcAopModelCallbackService;
import org.jcompany.model.PlcModelFactoryIoCService;
import org.jcompany.model.PlcModelLocator;


/**
 * DP Service Locator para Façades.
 * @since jCompany 3.0
 * @version $Id: PlcFacadeLocator.java,v 1.1 2006/07/24 13:43:13 alvim Exp $
 */
public class PlcFacadeLocator extends PlcBaseLocator {

	protected static Logger log = Logger.getLogger(PlcFacadeLocator.class);
	private static PlcFacadeLocator INSTANCE = new PlcFacadeLocator();

	private PlcFacadeLocator() { }
	public static PlcFacadeLocator getInstance(){
   		return INSTANCE;
	}
    
	/**
	 * @return  retorna serviço de IoC da camada modelo via delegação para PlcModelLocator.
	 */
	private PlcModelFactoryIoCService getIoCService() {
		return PlcModelLocator.getInstance().getIoCService();
	}
	
	/**
	 * Recebe uma classe de Façade e devolve o objeto correpondente do caching.<br>
	 * @return instancia de Façade
	 */
	public Object get(Class facade) throws PlcException {

		if (logModelo.isDebugEnabled())
			logModelo.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":get("+facade.getName()+"):"));

		log.debug("############### Entered 'get'");

		if (facade == null) {
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+facade.getName()+"):null:"));
			return null;
		}
				
		if (PlcModelLocator.getCache().containsKey(facade)) {
			
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+facade.getName()+"):cache:"));

			return PlcModelLocator.getCache().get(facade);
		} else {
			// Verifica se existe anotação de IoC para a classe.
			facade = PlcConfigHelper.getInstance().getAlternativeClass(facade);
			Object facadeObj = getIoCService().registerIoC(facade,PlcModelLocator.getCache(),getAopService());
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+facade.getName()+"=>"+facadeObj.getClass().getName()+"):IoC:"));

			return facadeObj;
		}

	}
	
	/**
	 * @return retorna o serviço de AOP da camada Modelo via delegacao para PlcModelLocator
	 */
	public PlcAopModelCallbackService getAopService() {
		return PlcModelLocator.getInstance().getAopService();
	}

}
