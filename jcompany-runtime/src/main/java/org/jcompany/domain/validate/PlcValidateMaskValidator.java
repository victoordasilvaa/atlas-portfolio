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
package org.jcompany.domain.validate;

import java.io.Serializable;
import java.util.regex.Matcher;

import org.hibernate.validator.Validator;

/**
 * Validador de máscara especializado para alteração de mensagem do original, somente.
 * @since jCompany 3.2
 */
public class PlcValidateMaskValidator implements Validator<PlcValidateMask>, Serializable {

	private java.util.regex.Pattern mask;

	public void initialize(PlcValidateMask parameters) {
		mask = java.util.regex.Pattern.compile(
				parameters.regex(),
				parameters.flags()
		);
	}
	
	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( !( value instanceof String ) ) return false;
		String string = (String) value;
		Matcher m = mask.matcher( string );
		return m.matches();
	}
	
}
