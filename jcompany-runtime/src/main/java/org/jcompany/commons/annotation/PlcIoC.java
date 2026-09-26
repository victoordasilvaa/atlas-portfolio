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
 * jCompany 3.0 Anotação utilizada em classes ENTITY para indicarem qual o serviço da camada modelo que irá lhe atender.
 * Altera o default, que utiliza convençao de nomenclatura.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlcIoC {	
	/**
	 * @since jCompany 3.0
	 * Nome com pacote completo do Business Component (AS ou BO) que atende a este ENTITY na camada modelo
	 * Equivale ao managerClassName registrado no construtor na versão 2.7.x
	 */
	String nameBCClass() default "";
	
	/**
	 * @since jCompany 3.0
	 * Nome com pacote completo do Data Access Object (DAO) que atenderá a este ENTITY na camada persistência.
	 * Pode ser utilizado para a resolução simplificada da arquitetura MVC. Possivelmente para lógicas que dispensem
	 * qualquer regra de negócio (cadastrais ou custodiais)
	 */
	String nameDAOClass() default "";

}
