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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica para formatação de alfabéticos ou alfanuméricos de forma simplificada.
 * @deprecated Prefira PlcValidateSimpleFormat, devido ao melhor padrão de  nomenclatura.
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface PlcSimpleFormat {
	
	/**
	 * @deprecated Usar PlcValFormatSimples.FormatoSimples
	 */
	public enum Format {
		/**
		 * Transforma a entrada de dados em maiúsculos
		 */
		CAPITAL_LETTERS,
		/**
		 * Transforma a entrada de dados em minúsculos
		 */
		MINUSCULE_LETTERS,
		/**
		 * Somente aceita números (0-9)
		 */
		NUMERIC
		
	}
	
	Format value();
	
}

