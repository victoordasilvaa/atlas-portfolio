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
import org.jcompany.config.metadata.PlcMetaConfig;
import org.jcompany.config.metadata.PlcMetaEditor;
import org.jcompany.config.metadata.PlcMetaEditorParameter;
import org.jcompany.config.metadata.PlcMetaConfig.LAYER;
import org.jcompany.config.metadata.PlcMetaConfig.SCOPE;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@PlcMetaConfig(root=true, scope={SCOPE.COLLABORATION_MAINTENANCE, SCOPE.COLLABORATION_SELECTION}, layer=LAYER.DOMAIN)
@PlcMetaEditor(label="Agregação", description="Definição de um grafo de classes agregadas envolvidas em uma \"Colaboração\" e gerenciadas pelo jCompany.")
/**
 * @since jCompany 5.0
 * Metadados para definição de um grafo de classes agregadas envolvidas em uma "Colaboração" e gerenciadas pelo jCompany.
 */
public @interface PlcConfigGroupAggregation {

    /**
     * Define a classe principal da agregação 
     */
	@PlcMetaEditorParameter(label="Entidade", description="Classe principal da agregação", mandatory=true)
    Class entity() default PlcBaseEntity.class;
    
    /**
     * Define o padrão(pattern) da agregação. É assumido o default "CONTROL" para casos onde nao há anotaçao de metadados.
     */
	@PlcMetaEditorParameter(label="Padrão", description="Padrão (pattern) da agregação")
    PlcConfigPattern configPattern() default @PlcConfigPattern;
    
    /**
     * Define a lista de componentes da agregação
     */
	@PlcMetaEditorParameter(label="Componentes", description="Lista de componentes da agregação")
    PlcConfigComponent[] components() default @PlcConfigComponent;
    
    /**
     * Define a lista de descendentes da agregação
     */
	@PlcMetaEditorParameter(label="Descendentes", description="Lista de descendentes da agregação")
    PlcConfigDescendent[] descendents() default @PlcConfigDescendent;
    
    // TODO - atributo não utilizado - rever.
    //PlcConfigInheritance heranca() default @PlcConfigInheritance;
    
    /**
     * Define a lista de detalhes da agregação
     */
	@PlcMetaEditorParameter(label="Detalhes", description="Lista de detalhes da agregação")
    PlcConfigDetail[] details() default @PlcConfigDetail;
    
    /**
     * Define as opções de segurança da agregação
     * @see PlcConfigSecurity
     */
	@PlcMetaEditorParameter(label="Segurança", description="Opções de segurança da agregação")
    PlcConfigSecurity security() default @PlcConfigSecurity;
    
    /**
     * Define as opções de preferência do usuário
     * @see PlcConfigUserPref
     */
	@PlcMetaEditorParameter(label="Preferência de Usuário", description="Configuração específica para preferência de usuário")
    PlcConfigUserPref userPreference() default @PlcConfigUserPref;
    
    /**
     * Define as opções do fluxo de aprovação
     * @see PlcConfigApprovalFlow
     */
	@PlcMetaEditorParameter(label="Fluxo de Aprovação", description="Configuração para Fluxo de Aprovação")
    PlcConfigApprovalFlow approvalFlow() default @PlcConfigApprovalFlow;
    
    /**
     * Define as opções do arquivo anexado
     * @see org.jcompany.config.commons.PlcConfigAttachedFile 
     */
	@PlcMetaEditorParameter(label="Arquivo Anexado", description="Configuração para colaborações com Arquivo Anexado")
    PlcConfigAttachedFile attachedFile() default @PlcConfigAttachedFile;
    
    /**
     * Define a lista de classes lookup da agregação
     */
	@PlcMetaEditorParameter(label="Classes de Lookup", description="Lista de Classes de Lookup da Colaboração")
    Class[] classesLookup() default {};
    
    /**
     * Defina a lista de classes de dominio discrte da agregacao
     */
    @PlcMetaEditorParameter(label="Classes de Dominio Discreto", description="Lista de Classes de Domíno Discreto (Enum) da Colaboração")
    Class<? extends Enum>[] classesDomainDiscreet() default {};

}
