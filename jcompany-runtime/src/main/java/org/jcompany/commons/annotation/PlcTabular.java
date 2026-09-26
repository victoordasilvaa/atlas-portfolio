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
public @interface PlcTabular {
	
	/**
	 * Título da classe a ser utilizado em exibições dinamicamente criadas. Iniciar com "#" para não usar I18N. O default
	 *  é compreedido como chave I18n do título.
	 * @since jCompany 3.03
	 */
	int numNovos() default 4;
	
	/**
	 * Nome da propriedade que, quando não informada, faz com que o objeto todo seja desconsiderado nas GUIs.
	 */
	String propReferenceDespise() default "";

	/**
	 * Se é para testar e evitar duplicatas da propriedade de referencia.
	 */
	boolean testDuplicity() default true;

}
