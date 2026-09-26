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
package org.jcompany.persistence.hibernate;

/**
 * jCompany 3.0. Constantes de Uso para a camada de pesistência
 * @since jCompany 3.0
 * @version $Id: PlcConstantsHibernate.java,v 1.2 2006/05/17 20:47:40 rogerio_baldini Exp $
 */
public interface PlcConstantsHibernate  {

	/**
     * Constantes para modos diversos
     */
    public interface CONFIG {
        
    	/**
         * Nome do arquivo principal de configuração associado à fábrica principal de persistência.
         */
        String FILE_CFG = "hibernate.cfg.xml";
        
        /**
         * Nome do sufixo padrão do arquivo principal de configuração associado à fábrica principal de persistência.
         */
        String FILE_CFG_SUFFIX = ".cfg.xml";
        
        /**
         * Nome da fábrica principal de persistência. É utilizado pelo jCompany quando não especificado
         */
        String FACTORY_DEFAULT_HIBERNATE = "hibernate";

        /**
         * Nome do owner que deverá preceder os SQLs
         */
        String DEFAULT_SCHEMA = "hibernate.default_schema";
        
        String DATA_SOURCE = "hibernate.connection.datasource";
        
        String DATA_SOURCE_PREFFIX = "java:comp/env/";
    }
   

}