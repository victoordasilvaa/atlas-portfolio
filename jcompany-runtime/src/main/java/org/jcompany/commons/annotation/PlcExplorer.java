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
 * Meta-dados utilizados na lógicas de Explorador (Navegador em dados utilizando ergonomia do Windows-Explorer)
 * @since jCompany 3.03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlcExplorer {
	
	/**
	 * Se a classe corrente deve ser utilizada pelo explorador (o default é utilizar; se marcada para false, então a árvore do 
	 * explorador irá omitir a classe do type corrente)
	 * @since jCompany 3.03
	 */
	boolean usa() default true;
	
	/**
	 * Relação de classes a serem omitidas, separadas por vírgula e sem incluir o package (pressupõe-se que não haverá mais de um
	 * relacionamento desta classe com duas outras homônimas e em packages diferentes). O default é utilizar todas as classes
	 * que possuam relacionamento "many-to-one" para esta classe.
	 * @since jCompany 3.03
	 */
	String omiteClasses() default "";

}
