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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlcDetail  {
	
	/**
	 * Se é classe de detalhe com relacionamento ManyToMany (lado secundário)
	 */
	boolean manyToMany() default false;
	
	/**
	 * Nome da propriedade que, quando não informada, faz com que o objeto todo seja desconsiderado nas GUIs.
	 */
	Class classePropRelacionamento() default Object.class;
	
	/**
	 * Se é classe de subdetalhe
	 */
	boolean subDetalhe() default false;
	
	/**
	 * Se o detalhe é somente para leitura
	 */
	boolean readonly() default false;

}
