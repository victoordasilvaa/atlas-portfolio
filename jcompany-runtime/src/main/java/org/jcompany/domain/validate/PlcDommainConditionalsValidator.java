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

import org.hibernate.validator.Validator;

public class PlcDommainConditionalsValidator implements Validator<PlcDommainConditionals>{

	public PlcDommainConditional[] conditionalDomain;
	//public PlcDommainConditionalValidator condicionalValidator;
	
	public boolean isValid(Object bean) {
		
		try {

			boolean isValidGeneral = true;
			PlcDommainConditionalValidator conditionalValidator=null;
			for (int i = 0; i < conditionalDomain.length; i++) {
				conditionalValidator = PlcDommainConditionalValidator.class.newInstance();
				conditionalValidator.initialize(conditionalDomain[i]);
				boolean isValid = conditionalValidator.isValid(bean);
				if (isValidGeneral && !isValid)
					isValidGeneral = false;
			}
			
			return isValidGeneral;
		

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void initialize(PlcDommainConditionals parameters) {
		
		// Pega a coleção de parametros
		this.conditionalDomain=parameters.value();

	}

}
