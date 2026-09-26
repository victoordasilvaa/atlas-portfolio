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

import static java.lang.annotation.ElementType.TYPE;
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
@Target({TYPE,PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@PlcMetaConfig(root=true, scope={SCOPE.APP, SCOPE.ENTERP}, layer=LAYER.COMMONS)
@PlcMetaEditor(label="Sufixos", description="Define preferências globais para nomenclatura de sufixos de classes padroes do framework.")
/**
 * @since jCompany 5.0 
 * Permite anotações que definem preferências globais para nomenclatura de sufixos de classes padroes do framework.
 */
public @interface PlcConfigSuffixClass {

	/**
	 * Sufixo-padrão para descendentes concretos de Entidades (Classes de Domínio Mapeadas). Default é "ENTITY"
	 */
	@PlcMetaEditorParameter(label="Entidade", description="Sufixo-padrão para descendentes concretos de Entidades (Classes de Domínio Mapeadas)", size=15)
	String entitySuffix() default "ENTITY";
	
	/**
	 * Sufixo-padrão para classes de serviço (ou gerenciadoras) de Entidades (Classes de Domínio Mapeadas) na camada Modelo. Default é "BO"
	 */
	@PlcMetaEditorParameter(label="Gerente", description="Sufixo-padrão para classes de serviço (ou gerenciadoras) de Entidades (Classes de Domínio Mapeadas) na camada Modelo", size=15)
	String entityManager() default "BO";
	
	/**
	 * Sufixo-padrão para classes de persistência. Default é "DAO")
	 */
	@PlcMetaEditorParameter(label="Persistência", description="Sufixo-padrão para classes de persistência", size=15)
	String persistence() default "DAO";
	
	/**
	 * Sufixo-padrão para classes de controle. Default é "Action")
	 */
	@PlcMetaEditorParameter(label="Controle", description="Sufixo-padrão para classes de controle", size=15)
	String control() default "Action";
	
	/**
	 * Sufixo-padrão para classes de apoio não especializáveis, disponiveis como Singleton. Default é "Helper"
	 */
	@PlcMetaEditorParameter(label="Apoio Simples", description="Sufixo-padrão para classes de apoio não especializáveis, disponiveis como Singleton", size=15)
	String simpleHelper() default "Helper";
	
	/**
	 * Sufixo-padrão para classes de apoio especializáveis, disponiveis com Injeção de Dependência. Default é "Service"
	 */
	@PlcMetaEditorParameter(label="Apoio", description="Sufixo-padrão para classes de apoio especializáveis, disponiveis com Injeção de Dependência", size=15)
	String service() default "Service";
	
	/**
	 * Sufixo-padrão para interfaces de Façade. Devem também iniciar com "I" (Ex: IMeuContratoFacade). Default é "Facade"
	 */
	@PlcMetaEditorParameter(label="Facade", description="Sufixo-padrão para interfaces de Façade. Devem iniciar com \"I\"", size=15)
	String facadeInterface() default "Facade";
	
	/**
	 * Sufixo-padrão para implementações de interfaces de Façade. Não Devem iniciar com "I" (Ex: MeuContratoFacadeImpl). Default é "FacadeImpl"
	 */
	@PlcMetaEditorParameter(label="Implementação do Facade", description="Sufixo-padrão para implementações de interfaces de Façade. Não Devem iniciar com \"I\" ", size=15)
	String facadeImplementation() default "FacadeImpl";
	
}
