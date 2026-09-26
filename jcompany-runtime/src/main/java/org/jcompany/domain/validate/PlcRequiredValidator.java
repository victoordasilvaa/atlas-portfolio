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
 * Se for um String valida testando String vazio ("") ou espaços ("   ") ou nulo em outros casos.
 * @since jCompany 3.1.1
 */
@SuppressWarnings("serial")
public class PlcRequiredValidator implements Validator<PlcRequired>, Serializable {
	
	public boolean isValid(Object value) {
		
		if (value == null)
			return false;
		
		if (String.class.isAssignableFrom(value.getClass())) {
			return value != null && !("".equals(value.toString().trim()));
		} else
			return value != null;
		
	}

	public void initialize(PlcRequired parameters) {	}
	
}
