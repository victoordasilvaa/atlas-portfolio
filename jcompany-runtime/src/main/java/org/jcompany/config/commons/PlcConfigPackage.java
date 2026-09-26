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
import static java.lang.annotation.ElementType.TYPE;

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
@PlcMetaEditor(label="Pacotes", description="Configurações globais para pacotes da aplicação")
/**
 * @since jCompany 5.0 
 * Permite anotações que definem preferências globais para pacotes base
 */
public @interface PlcConfigPackage {

	/**
	 * Pacote-base para Entidades (Classes de Domínio Mapeadas). Default é ".vo."
	 */
	@PlcMetaEditorParameter(label="Entidade", description="Pacote-base para Entidades (Classes de Domínio Mapeadas)", size=15)
	String entityPackage() default ".entity.";
	/**
	 * Pacote-base para Classes comuns entre as Camadas MVC (Interfaces Facade, Utilitarios, Constantes). Default é ".commons."
	 */
	@PlcMetaEditorParameter(label="Comuns", description="Pacote-base para Classes comuns entre as Camadas MVC (Interfaces Facade, Utilitarios, Constantes)", size=15)
	String commons() default ".commons.";
	/**
	 * Pacote-base para Controladores (Classes de Controle, tais como Action). Default é ".control."
	 */
	@PlcMetaEditorParameter(label="Controle", description="Pacote-base para Controladores (Classes de Controle, tais como Action)", size=15)
	String control() default ".control.";
	/**
	 * Pacote-base para Facades (Implementações de Contratos). Default é ".facade."
	 */
	@PlcMetaEditorParameter(label="Facade", description="Pacote-base para Facades (Implementações de Contratos)", size=15)
	String facade() default ".facade.";
	/**
	 * Pacote-base para BOs e ASx (Classes de Business Components). Default é ".model."
	 */
	@PlcMetaEditorParameter(label="Modelo", description="Pacote-base para BOs e ASx (Classes de Business Components)", size=15)
	String model() default ".model.";
	/**
	 * Pacote-base para DAOs (Classes de Persistência). Default é ".persistence."
	 */
	@PlcMetaEditorParameter(label="Persistência", description="Pacote-base para DAOs (Classes de Persistência)", size=15)
	String persistence() default ".persistence.";
}
