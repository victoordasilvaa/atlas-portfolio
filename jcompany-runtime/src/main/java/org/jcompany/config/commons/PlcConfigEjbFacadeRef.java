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
package org.jcompany.config.commons;

import static java.lang.annotation.ElementType.PACKAGE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jcompany.config.metadata.PlcMetaConfig;
import org.jcompany.config.metadata.PlcMetaEditor;
import org.jcompany.config.metadata.PlcMetaEditorParameter;
import org.jcompany.config.metadata.PlcMetaConfig.LAYER;
import org.jcompany.config.metadata.PlcMetaConfig.SCOPE;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@PlcMetaConfig(root=true, scope=SCOPE.APP, layer=LAYER.COMMONS)
@PlcMetaEditor(label="Referência EJB", description="Configurações globais para uso de da camada modelo via facade EJB3")
/**
 * @since jCompany 5.0
 *  Configurações globais para uso de da camada modelo via facade EJB3
 */
public @interface PlcConfigEjbFacadeRef {

    /**
     * Nome de registro da Fachada de referência da Aplicação Ex: AppFacadeImpl
     */
	@PlcMetaEditorParameter(label="Nome Facade", description="Nome de registro da Fachada de referência da Aplicação", size=20)
    String nameFacadeApp() default "appfacaderef";
    
    /**
     * Nome do Prefixo para JNDI, normalmente informa o nome da Aplicação EX:  rhdemo
     */
	@PlcMetaEditorParameter(label="Prefixo JNDI", description="Nome do Prefixo para JNDI, normalmente informa o nome da Aplicação", size=20)
    String prefixNameJNDIApp() default "";
    

    /**
     * URL do servidor jndi para fazer lookup de objetos remotos. 
     */
	@PlcMetaEditorParameter(label="URL Provider", description="URL do servidor jndi para fazer lookup de objetos remotos", size=20)
    String providerUrl()			default "";
    
    /**
     * Context Factory para fazer lookup de objetos remotos.  
     */
	@PlcMetaEditorParameter(label="Context Factory", description="Context Factory para fazer lookup de objetos remotos", size=20)
    String initialContextFactory() 	default "";
    
    /**
     * URL_PKG_PREFIXES para fazer lookup de objetos remotos.  
     */
	@PlcMetaEditorParameter(label="Prefixos de Pacote", description="URL_PKG_PREFIXES para fazer lookup de objetos remotos", size=20)
    String urlPkgPrefixes()			default "";

}
