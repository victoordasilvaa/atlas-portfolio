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
@PlcMetaConfig(root=true/*Pode ser ou não raiz*/, scope=SCOPE.APP, layer=LAYER.COMMONS)
@PlcMetaEditor(label="Serviço", description="Define classes alternativas (descendentes, tipicamente), para serviços (Service) utilizados com IoC.")
/**
 * @since jCompany 5.0 
 * Permite anotações que definem classes alternativas (descendentes, tipicamente), para serviços (Service)
 * utilizados com IoC.
 * Pode ser utilizada na camada comuns na raiz do package-info ou num array em {@link PlcConfigServices#value()} para o caso de mais de um.
 * O array anula o uso desse na raiz.
 * 
 * Pode também ser utilizado na camada controle em {@link org.jcompany.config.control.geral.PlcConfigControleIoC#service()},
 * para configurações de serviços da camada de controle.
 */
public @interface PlcConfigService {

    /**
     * Classe do jCompany de referencia. É a classe utilizada pelo jCompany quando uma alternativa nao é declarada
     */
	@PlcMetaEditorParameter(label="Classe Referência", description="Classe do jCompany de referencia")
    Class classReference();
    
    /**
     * Classe descendente da de referência, devendo tipicamente ser um descendente desta
     */
	@PlcMetaEditorParameter(label="Classe Alternativa", description="Classe descendente da de referência")
    Class classAlternative();


}
