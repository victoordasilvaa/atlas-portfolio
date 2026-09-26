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

@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.0
 * Configurações para definição de opçoes relacionadas as formulários.
 */
public @interface PlcConfigNavigator {

	/**
	 * Indica ligeiras variações sobre a lógica padrão.
	 */
	public enum DynamicType {
		/**
		 * Navegador pré-definido em termos de numero de objetos e somente permitindo paginacao sequencial
		 */
		STATIC,
		/**
		 * Permite usuário saltar direto para qualquer página
		 */
		DYNAMIC_PAGE,
		/**
		 * Permite usuário alterar o núm. de registros.
		 */
		DYNAMIC_NUMRECORDS,
		/**
		 * Permite ao usuário alterar o número de registro e saltar direto para qualquer página
		 */
		DYNAMIC_BOTH
	}
	
	/**
	 * Indica o posicionamento do topo com relação ao registros exibidos.
	 */
	public enum HeaderPosition {
		/**
		 * Topo abaixo da relação de registro
		 */
		BOTTOM,
		/**
		 * (Default) Topo acima da relação de registro
		 */
		TOP,
		/**
		 * Topo acima e abaixo
		 */
		BOTH
	}
	
	/**
	 * Indica o estilo da IHM do navegador.
	 */
	public enum HeaderStyle {
		/**
		 * (Default) Setas de navegação
		 */
		PAGINATION_ARROWS,
		/**
		 * Links com índices por página
		 */
		INDEX_BY_PAGE
	}
	
	/**
	 * Define o numero de registros por página
	 */
	int numberByPage() default -1;
	/**
	 * Define o type de variação da lógica do navegador
	 */
	DynamicType dynamicType() default DynamicType.STATIC;
	/**
	 * Define estilo de exibição do navegador
	 */
	HeaderStyle headerStyle() default HeaderStyle.PAGINATION_ARROWS;
	/**
	 * Define posicionamento do topo com relação aos registros exibidos
	 */
	HeaderPosition headerPosition() default HeaderPosition.TOP;
	
}
