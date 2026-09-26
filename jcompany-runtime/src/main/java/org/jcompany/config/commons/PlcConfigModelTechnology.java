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
@PlcMetaEditor(label="Technology de Modelo", description="Configurações globais da tecnologia usada na camada modelo")

/**
 * @since jCompany 5.0 
 * Configurações globais da tecnologia usada na camada modelo
 */
public @interface PlcConfigModelTechnology {

	/**
	 * Tipos de tecnologias suportadas na camada modelo
	 */
	public enum Technology {
		EJB3,
		POJO
	}
	/**
	 * Indica se a aplicação está usando EJB ou POJO
	 */
	@PlcMetaEditorParameter(label="Technology", description="Indica se a aplicação está usando EJB ou não")
	Technology technology() default Technology.POJO;

}
