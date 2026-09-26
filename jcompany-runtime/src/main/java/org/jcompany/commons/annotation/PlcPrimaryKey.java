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
package org.jcompany.commons.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.0
 * Meta-dado para identificação da chave primária composta da entidade.
 * @author Bruno Grossi
 */
public @interface PlcPrimaryKey {
	/**
	 * Define a classe da Entidade que possui a chave composta 
	 */
	Class classe();
	/**
	 *  Define a lista das propriedas que montam a chave composta
	 */
	String[] properties();
	/**
	 * 
	 */
	boolean autoRecuperacao() default false;
}
