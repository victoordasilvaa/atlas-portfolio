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
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.1
 * Metadados sobre uma anotação de configuração.
 */
public @interface PlcMetaConfig {
	
	public enum SCOPE {
		ENTERP,
		APP,
		COLLABORATION_MAINTENANCE,
		COLLABORATION_SELECTION
	}
	
	public enum LAYER {
		COMMONS,
		PERSISTENCE,
		DOMAIN,
		MODEL,
		CONTROL,
		VIEW
	}
	
	/**
	 * Indica se a anotação pode ser utilizada como raiz.
	 */
	boolean root() default false;
	
	/**
	 * SCOPE da anotação.
	 */
	SCOPE[] scope();
	
	/**
	 * LAYER em que pode ser utilizada.
	 */
	LAYER layer();
	
	/**
	 * Descrição da anotação.
	 */
	String description() default "";
}

