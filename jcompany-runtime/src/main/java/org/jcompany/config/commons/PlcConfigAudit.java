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
@PlcMetaEditor(label="Auditoria", description="Preferências globais para lógicas de auditoria rígida")
/**
 * Permite anotações que definem preferências globais para lógicas de auditoria rígida (image da história do ciclo de vida dos dados)
 * @since jCompany 3.1.1
 */
public @interface PlcConfigAudit {
	
	/**
	 * Para modalidade CLASSE. Sufixo da Classe de Auditoria Associada. Default é AuditPlc
	 */
	@PlcMetaEditorParameter(label="Sufixo", description="Sufixo da Classe de Auditoria Associada", size=30)
	String classAuditSuffix() default "Audit";
	
	/**
	 * Para modalidade CLASSE. Sub-pacote da Classe de Auditoria Associada, com relação ao pacote da classe 'correlata'. Default é 'auditoria'
	 */
	@PlcMetaEditorParameter(label="Sub Pacote", description="Sub-pacote da Classe de Auditoria Associada", size=30)
	String classAuditWithoutPackage() default "auditoria";
	
	public enum PropertyAuditOptionManyToOne {
		/**
		 * Object-Id
		 */
		OID,
		/**
		 * toString()
		 */
		TO_STRING
	}
	
	/**
	 * TODO Modalidade B de auditoria rígida.
	 * Para modalidade PROPRIEDADE. Opção para manyToOne. Pegar valores com PlcAudit.PropriedadeAuditOpcaoManyToOne
	 */
	@PlcMetaEditorParameter(label="Propriedade", description="Opção para manyToOne")
	PropertyAuditOptionManyToOne propertyAuditOptionManyToOne() default PropertyAuditOptionManyToOne.TO_STRING;

}
