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
package org.jcompany.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-dados utilizados em componentes e lógicas genéricas do jCompany, tais como o explorer.
 * @since jCompany 3.03
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlcEntityMetadata {
	
	/**
	 * Título da classe a ser utilizado em exibições dinamicamente criadas. Iniciar com "#" para não usar I18N. O default
	 *  é compreedido como chave I18n do título.
	 * @since jCompany 3.03
	 */
	String title() default "";
	
	/**
	 * Nome padrão de propriedade agregada para a classe (ex: cliente para Cliente)
	 *  @since jCompany 3.03
	 */
	String nameDefaultProp() default "";
	
	/**
	 * Url a ser utilizada em montagem de navegação (hiperlink) dinamicamente.
	 * @since jCompany 3.03
	 */
	String urlMaintenance() default "";
	
	/**
	 * Url a ser utilizada em seleção ou consulta de ítens desta Entidade. Se omitida e a urlManutencao for informada,
	 * assume nome através da convençao de sufixos, trocando "...man" por "...sel".
	 * @since jCompany 3.03
	 */
	String urlSelecao() default "";
	
	/**
	 * Relação de propriedades a serem utilizadas na montagem de links
	 * @since jCompany 3.03
	 */
	String navegacao() default "id";
	
	/**
	 * Ícone a ser utilizado em componentes visuais dinamicamente montados, tais como treeview, tabs, menus etc.
	 *  @since jCompany 3.03
	 */
	String icone() default "";
	
	/**
	 * Ícone diferenciado a ser apresentando alternativamente ao icone, quando o item está selecionado.
	 *  @since jCompany 3.03
	 */
	String iconeSel() default "";
	
	/**
	 * Relação de propriedades a serem utilizadas na ordenaçao de coleçoes cuja classe principal seja do type corrente.
	 * Importante: Frameworks de persistencia realizam ordenação declarativa em 'detalhes', que deve ser utilizada neste
	 * caso nas anotações do mapeamento. Esta ordenação é utilizada em componentes dinamicos do jCompany, tais como treeview
	 * e navegadores como default, podendo haver sobreposição da ordenação no Caso de Uso específico.<br>
	 * Ex: "estadoCivil,estado.nome,nome desc" // indica para ordenar ascendente por Estado Civil e Nome do Estado (outra classe agregada) e
	 * descendentes por nome. 
	 * @since jCompany 3.03
	 */
	String ordination() default "";

	/**
	 * Número de registros a serem apresentados em lógicas MVC dinamicas. Se zero, o jCompany trará todos os registros. Se algum número for
	 * informado, o jCompany irá trazer paginações por default com o número informado.
	 * @since jCompany 3.03
	 */
	int numberByPage() default 0;
	
	/**
	 * Se utiliza para lógicas do componente explorer. Se alterado para false evita que ítens desta classe sejam renderizados no treeview.
	 * @since jCompany 3.04
	 */
	boolean explorerUsa() default true;
	
	/**
	 * Se utiliza campos com busca fonetica para recuperacao de entidades.
	 * @since jCompany 3.1
	 */
	boolean phoneticsUsa() default false;
	
	/**
	 * Se é utilizada como lookup (tipicamente em combos) na camada visao.
	 * @since jCompany 3.1
	 */
	boolean classeLookup() default false;
	
	/**
	 * Nome de propriedade recursiva, se tiver
	 */
	String recursivePropName() default "";
	
	boolean recursivityOnly() default false;

}
