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
package org.jcompany.model.batch;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;


/**
 *  jCompany 3.0. Ancestral para lógicas batch temporais. Descendentes devem implementar o método "run"
 * @since jCompany 3.0
 * @version $Id: PlcBatchTimerTask.java,v 1.4 2006/06/21 19:12:20 bruno_grossi Exp $
*/
abstract public class PlcBaseModelTimerTask extends TimerTask {
	
	protected static Logger log = Logger.getLogger(PlcBaseModelTimerTask.class);

	public void run() {

		try {
			
			runApi();
		
		} catch (PlcException e) {
			
			// TODO Aprimorar tratamento genérico de exceção, da camada modelo.
			if (e.getRootCause()!=null)
				log.error("Fatal error when trying to execute rotine "+this.getClass().getName()+":"+e.getRootCause(),e.getRootCause());
			else
				log.fatal("Fatal error when trying to execute rotine "+this.getClass().getName()+":"+e,e);
			
		}
	}
	
	/**
	 * Template Method para disparo de rotinas batch. O descendente deve executar uma ou mais chamadas de serviços (preferencialmente utilizando-se
	 * de transações do Façade), e encapsular exceções em PlcException, para o tratamento genérico.
	 * @since jCompany 3.04
	 * @throws PlcException Exceção controlada ou 'wrapper' contendo em causaRaiz a exceção raiz.
	 */
 	abstract protected void runApi() throws PlcException;
	
}

