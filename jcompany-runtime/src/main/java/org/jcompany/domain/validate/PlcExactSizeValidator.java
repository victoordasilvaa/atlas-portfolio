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

import org.hibernate.validator.Validator;

/**
 * @deprecated Utiliziar a versao PlcValidateExatSizeValidator
 */
public class PlcExactSizeValidator implements Validator<PlcExactSize>, Serializable {
	private int size;

	public void initialize(PlcExactSize parameters) {
		size = parameters.size();
	}

	public boolean isValid(Object value) {
		if ( value == null ) return true;
		if ( !( value instanceof String ) ) return false;
		String string = (String) value;
		int length = string.length();
		return length == size;
	}

}

