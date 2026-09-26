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
package org.jcompany.config.metadata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.1
 * Metadados sobre uma anotação para editor visual do jCompany IDE
 * Informações sobre um parâmetro da anotação.
 */
public @interface PlcMetaEditorParameter {
	
	/**
	 * Rótulo para exibição no editor visual
	 */
	String label();
	
	/**
	 * Descrição do parâmetro.
	 */
	String description();
	
	/**
	 * Tamanho do campo, para campos de texto e numéricos.
	 * Para campos booleanos ou enum, será ignorado (size 0).
	 */
	int size() default 0;
	
	/**
	 * Indica se o campo é obrigatório ou não.
	 */
	boolean mandatory() default false;
	
	/**
	 * Indica restrições para uso do parâmetro.
	 */
	PlcRestriction[] restrictions() default {};
}

