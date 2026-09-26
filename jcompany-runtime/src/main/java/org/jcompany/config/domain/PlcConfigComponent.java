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
package org.jcompany.config.domain;

import static java.lang.annotation.ElementType.PACKAGE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jcompany.commons.PlcBaseEntity;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Configurações para definição de componentes
 * @since jCompany 5.0
 */
public @interface PlcConfigComponent {

    /**
     * Configuração do Value Object ancestral para componente 
     */
    Class clazz() default Object.class;

    /**
     * Nome da propriedade do componente na Entidade principal 
     */
    String property() default "";

}
