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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anota a classe que implementa a interface ou a fábrica (factory) usada para recupera-la.
 * Os atributos dessa anotação são mutuamente exclusivos, e não deve ser utilizado mais de um atributo.
 * A ordem de prioridade será sempre: classe, value, fabricaClasse, fabrica.
 * A opção por valores em String permite o desacoplamento entre classes.
 * 
 * <ul>
 * <li>value/classe indicam diretamente a implementação da interface.</li>
 * <li>fabrica/fabricaClasse indicam a classe de Factory Singleton (que possua getInstance())
 *                           e que retorne serviços na forma de interfaces.</li>
 * </ul>
 * @since 3.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface PlcImplementation {
	String value() default "";
	Class classe() default Object.class;
	String fabrica() default "";
	Class fabricaClasse() default Object.class;
}