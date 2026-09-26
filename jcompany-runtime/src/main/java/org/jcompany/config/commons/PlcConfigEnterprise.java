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
@PlcMetaConfig(root=true, scope=SCOPE.ENTERP, layer=LAYER.COMMONS)
@PlcMetaEditor(label="Empresa", description="Configurações globais de definição da empresa")
/**
 * @since jCompany 5.0
 * Configurações globais de definição da empresa. Informações que aparecem em alguns componentes visuais por default.
 */
public @interface PlcConfigEnterprise {

	/**
	 * Este é o nome da empresa como deve aparecer em componentes visuais tais como páginas de erro, relatórios, consultas, etc. Exemplo: Powerlogic Consultoria S/A
	 */
	@PlcMetaEditorParameter(label="Nome", description="Nome da Empresa como deve aparecer em componentes visuais tais como páginas de erro, relatórios, consultas, etc", size=30)
	String name();
	/**
	 * Dominio (DNS), sem constar o protocolo (http://). Ex: www.powerlogic.com.br
	 */
	@PlcMetaEditorParameter(label="Domínio", description="Dominio (DNS), sem constar o protocolo (http://)", size=30)
	String domain();
	
	/**
	 * Sigla da empresa. Ex: PLC
	 */
	@PlcMetaEditorParameter(label="Sigla", description="Sigla da empresa", size=15)
	String initials();
	
	/**
	 * URL relativa da image de logotipo para apresentação no login e em outros componentes padrões
	 */
	@PlcMetaEditorParameter(label="Logotipo", description="URL relativa da image de logotipo para apresentação no login e em outros componentes padrões", size=30)
	String logotype() default "/plc/midia/marca_empresa.gif";
	
	/**
	 * Endereço completo da empresa, como aparecerá em páginas de erro e rodapé das aplicações.
	 * Ex: R. Paraíba, 330. 19º andar. Funcionários. Belo Horizonte - MG. Cep: 30130-917. Email Geral: plc@powerlogic.com.br
	 */
	@PlcMetaEditorParameter(label="Endereço", description="Endereço completo da empresa, como aparecerá em páginas de erro e rodapé das aplicações", size=40)
	String address();
	
	/**
	 * Telefone de suporte a usuários da empresa, como aparecerá em páginas de erro, rodapés, etc.
	 */
	@PlcMetaEditorParameter(label="Telefone Suporte", description="Telefone de suporte a usuários da empresa, como aparecerá em páginas de erro, rodapés, etc")
	String callcenterTelephone();
	
	/**
	 * Email de suporte a usuários da empresa, como aparecerá em páginas de erro, rodapés, etc.
	 */
	@PlcMetaEditorParameter(label="E-mail Suporte", description="E-mail de suporte a usuários da empresa, como aparecerá em páginas de erro, rodapés, etc")
	String callcenterEmail();
}
