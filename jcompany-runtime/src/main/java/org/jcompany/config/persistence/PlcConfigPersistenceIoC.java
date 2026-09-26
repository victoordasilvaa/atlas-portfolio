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
package org.jcompany.config.persistence;

import static java.lang.annotation.ElementType.PACKAGE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jcompany.config.commons.PlcConfigService;
import org.jcompany.persistence.hibernate.PlcBaseHibernateDAO;
import org.jcompany.persistence.hibernate.PlcHibernateManager;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.0
 * Definições para registro de Inversões de Controle da Aplicação na camada de Persistência
 */
public @interface PlcConfigPersistenceIoC {

    /**
     * Define a classe ancestral parão para implementação do acesso aos dados
     */
    Class implementationDefault() default PlcBaseHibernateDAO.class;

    /**
     * Define classes para serviços de gerenciamento de persistência. 
     */    
    Class[] serviceManagers() default {PlcHibernateManager.class};

    /**
     * Define classes alternativas (descendentes, tipicamente), para serviços (Service) 
     */
    PlcConfigService[] service() default {};
}
