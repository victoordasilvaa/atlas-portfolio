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

import org.jcompany.commons.PlcConstantsCommons;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.0 
 * Configurações para definição de opções específicas para lógicas de Versionamento e Fluxo de Aprovação.
 */
public @interface PlcConfigApprovalFlow {

    /**
     * Modalidade da logic padrao 
     */
    public enum ModalityVersion {
	/**
	 * Armazena alteracoes como historico, recursivamente, como "I".
	 */
	VERSION,
	/**
	 * Armazena alteracoes e inclusoes, para quem nao for aprovador, como "P".
	 */
	APPROVAL


    }

    /**
     * Tipo de lógica padrao para aprovação a ser utilizada
     */
    public enum ApprovalType {
	/**
	 * Aprovação Simples
	 */
	SIMPLE,
	/**
	 * Aprovação em lote
	 */
	GROUP

    }

    /**
     * Tipo de lógica padrao para aprovação a ser utilizada
     */
    public enum ExclusionOption {
	/**
	 * Mantém todos os registros em situação "P" ou "I", após a aprovação ou reprovação. (Default)
	 */
	NONE,
	/**
	 * Exclui todos os registros em situação "P" ou "I", após a aprovação ou reprovação
	 */
	EVERYTHING,
	/**
	 * Exclui todos os registros em situação "P" ou "I", após a aprovação somente
	 */
	APPROVED,
	/**
	 * Exclui todos os registros em situação "P" ou "I", após a reprovação somente
	 */
	DISAPPROVED

    }

    /**
     * Define a modalidade da lógica de aprovação
     */
    ModalityVersion modality() default ModalityVersion.VERSION;
    
    /**
     * Relação de colunas que, quando alteradas, geram histórico.
     */
    String[] historicPropsMonitored() default {""};
    
    // TODO - atributo não utilizado - rever.
    //boolean historicoAutomatico() default false;
    
    /**
     * Define o type de lógica padrão para aprovação
     */
    ApprovalType approvalType() default ApprovalType.SIMPLE;
    
    /**
     * Define se pode ser feita alteração ao aprovar.
     */    
    boolean approvalWithUpdate() default true;
    
    /**
     * Define a modalidade da aprovação na exclusão.
     */
    ExclusionOption approvalExclusion() default ExclusionOption.NONE;

}
