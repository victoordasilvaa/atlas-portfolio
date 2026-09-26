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
package org.jcompany.persistence;

/**
 * jCompany 3.0. Constantes de Uso para a camada de pesistência
 * @since jCompany 3.0
 * @version $Id: PlcConstantsPersistence.java,v 1.3 2006/08/17 17:06:04 alvim Exp $
 */
public interface PlcConstantsPersistence  {

	public interface GLOBAL {
		 String MANY_TO_ONE_LAZY_OPTIMIZE  = "plc.manyToOneLazyOptimize";
		 String UPDATE_OPTIMIZE  = "plc.updateOptimize";
		 String AUDIT_RIGID  = "plc.auditRigid";
		 String AUTO_DETECT_DIALECT  = "plc.autoDetectDialect";
	}
	
	/**
     * Constantes para modos diversos
     */
    public interface CONFIG {
        
    	/**
         * Nome da fábrica principal de persistência. É utilizado pelo jCompany quando não especificado
         */
        String FACTORY_DEFAULT = "default";
        
        public interface HIBERNATE {
        	String PREFIX_CFG_FILE = "hibernate";
        	String SUFFIX_GFG_FILE = ".cfg.xml";
        	
        }

    }

    /**
     * Tokens de uso em lógicas de manipulação de queries OQL-Like.
     */
    public interface DYNAMIC_QUERY {
    	
        String PARENTHESIS_OPEN  = "(";
        String COMMA       = ",";
        String PARENTHESIS_CLOSE = ")";        
    }
    
    /**
     * Constantes relacionadas a camada modelo e persistência
     */
    public interface MODEL {

        /**
         * Chave de escopo de aplicação que contém referência a uma sessão
         */
        String FACTORY_SESSIONS_KEY = "factorySessions";
    }
    

}