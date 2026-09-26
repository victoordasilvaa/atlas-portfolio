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
package org.jcompany.commons;

import org.apache.log4j.Logger;

/**
 * Exceçao disparada em serviços de AOP.
 * @since jCompany 3.0
 */
public class PlcAopException extends PlcException {

	private static final long serialVersionUID = 721466570248697971L;

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public PlcAopException(String messageKeyLoc,Object[] messageArgsLoc,Throwable cause, Logger logCause)   {
		super(messageKeyLoc,messageArgsLoc,cause,logCause);
	}
    
}

