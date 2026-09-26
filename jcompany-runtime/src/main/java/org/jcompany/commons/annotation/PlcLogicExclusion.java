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
 * jCompany 3.0 Permite anotação em classe indicando que, em qualquer Caso de Uso onde
 * apareça como classe principal, a exclusão ocorra logicamente alterando-se 'sitHistoricoPlc' para 'I'.
 * Pode-se incluir em action-mapping esta definição 'exclusaoModo=logic' caso seja especifica
 * de cada Caso de Uso
 * TODO Implementação pendente.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlcLogicExclusion {
	
	/**
	 * Se é para o jCompany incluir sitHistoricoPlc='A' automaticamente em Named Queries Padrão 'avoidEquals'
	 */
	boolean automatizaNaoDeveExistir() default true;
	
	/**
	 * Se é para o jCompany incluir sitHistoricoPlc='A' automaticamente em Named Queries Padrão 'querySel'
	 */
	boolean automatizaQuerySel() default false;
	
	/**
	 * Se é para o jCompany incluir sitHistoricoPlc='A' automaticamente em Named Queries Padrão 'queryEdita'
	 */
	boolean automatizaEdicao() default true;
	
	/**
	 * Se é para o jCompany incluir sitHistoricoPlc='A' automaticamente em Named Queries Padrão 'queryTreeView'
	 */
	boolean automatizaTreeView() default true;
	
}
