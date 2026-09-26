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

/**
 * jCompany 2.5.3. Classe de Exceção do jCompany para disparo de exceções da aplicação. Não dispara
 * stack trace nem envio de email<p>
 */
public class PlcAppException extends PlcException {

	private static final long serialVersionUID = 721466570248697971L;

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public PlcAppException(String newMessageKey) {
			setMessageKey(newMessageKey);
	}
    
	/**
	 * 
	 * @since jCompany 3.0
	 */
	public PlcAppException(String messageKeyLoc,Object[] messageArgsLoc)   {
		setMessageKey(messageKeyLoc);
		setMessageArgs(messageArgsLoc);
	}
    
}

