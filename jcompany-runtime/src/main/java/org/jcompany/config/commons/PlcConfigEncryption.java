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
@PlcMetaEditor(label="Criptografia", description="Configurações globais para algoritmos de criptografia")
/**
 * @since jCompany 3.2.1 Configurações globais para algoritmos de criptografia
 */
public @interface PlcConfigEncryption {

	/**
     * Chave para criptografias da aplicação, podendo ser alterada para cada
     * aplicação. Utilizada pela função correspondente em PlcGeralUtil
     */
	@PlcMetaEditorParameter(label="Criptografia", description="Chave para criptografias da aplicação", size=30)
	String cryptography() default "";
	/**
     * Chave para decriptografia da aplicação, podendo ser alterada para
     * cada aplicação. Utilizada pela função correspondente em PlcGeralUtil
     */
	@PlcMetaEditorParameter(label="Decriptografia", description="Chave para descriptografia da aplicação", size=30)
	String decryptography() default "";
}
