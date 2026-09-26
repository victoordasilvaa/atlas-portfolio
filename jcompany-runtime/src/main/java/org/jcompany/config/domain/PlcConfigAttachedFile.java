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

import org.jcompany.commons.PlcFileEntity;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.0
 * Configurações para definição de opçoes de uso de arquivo anexado
 */
public @interface PlcConfigAttachedFile {

	/**
	 * Define a classe que irá armazenar o arquivo anexado
	 */
	Class clazz() default PlcFileEntity.class;
	
	/**
	 * Define o Tamanho máximo para o arquivo anexado
	 */
	int maximumSize() default 2048;
	
	/**
	* Tamanho ideal, em bytes, para exibição de mensagens de advertência. Se um usuário tenta fazer upload de um arquivo com size maior que o ideal, porém menor que o máximo, o jCompany aceita mas dá uma mensagem de advertência.
	*/
	int idealSize() default 1024;
	
	/**
	 * Define a lista de extensões(tipos de arquivo)
	 */
	String[] extensions() default {"gif", "jpg", "png", "txt", "html", "htm" , "jsp", "pdf"};
	
	/**
	 * Se o arquivo é uma Imagem. O default é false.
	 */
	boolean image() default false;
	
}
