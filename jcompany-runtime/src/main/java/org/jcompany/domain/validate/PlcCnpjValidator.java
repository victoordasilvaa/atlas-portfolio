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

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.validator.Validator;
import org.jcompany.domain.validate.helper.PlcUnifiedValidationHelper;


/**
 * @deprecated Utilizar o PlcValidateCnpjValidator
 */
public class PlcCnpjValidator implements Validator<PlcCnpj>, Serializable{
	
	public boolean isValid(Object cnpj) {
		
		if (cnpj == null || "".equals(cnpj.toString().trim()))
			return true;
		
		if (!NumberUtils.isNumber(cnpj.toString()))
			return false;
		
		return PlcUnifiedValidationHelper.getInstance().validateCnpj(cnpj.toString());
		
	}

	public void initialize(PlcCnpj parameters) {}


}
