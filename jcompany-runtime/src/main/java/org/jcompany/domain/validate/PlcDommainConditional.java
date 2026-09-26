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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.ValidatorClass;

/**
 * @deprecated Utilizar a versao no padrao PlcValidateDommainConditional
 */
@Documented
@ValidatorClass(PlcDommainConditionalValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlcDommainConditional {

	public enum Informed {
		BOTH_AND_PASSED_VALIDATION,
		ONLY_PROP_A,
		ONLY_PROP_B,
		NONE,
		ONE_OF_BOTH
	}
	
	public enum Operator {
		GREATER_THAN,
		GREATER_THAN_OR_EQUAL_TO,
		LESS_THAN,
		LESS_THAN_OR_EQUAL_TO,
		DIFFERENT
	}
	
	String valuePropA();
	Operator operator();
	String valuePropB();
	Informed validateOkIf() default Informed.BOTH_AND_PASSED_VALIDATION;
	String message() default "{validator.conditional}";
	String ifTitle() default "substitute.ifTitle.in.annotation";
	String thenTitle() default "substitute.thenTitle.in.annotation";
}
