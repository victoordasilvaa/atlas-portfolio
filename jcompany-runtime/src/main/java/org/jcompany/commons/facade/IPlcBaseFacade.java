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
package org.jcompany.commons.facade;

import org.hibernate.validator.Size;

/**
 * @since jCompany 5.0 
 * Esta interface foi mesclada a IPlcFacade
 */
@Deprecated
public interface IPlcBaseFacade {
	   	   
	    /**
	     *  @since jCompany 3.0
	     *  Recupera registro de mensagens tratadas de erros da camada de persistencia
	     *  @param rootCause Exception
	     *  @return String[0] mensagem internacionalizada, String[1]: arg1 (opcional), String[2] arg2 (opcional)
	     **/
		public String[] retrieveMessageException(Throwable rootCause);
}
