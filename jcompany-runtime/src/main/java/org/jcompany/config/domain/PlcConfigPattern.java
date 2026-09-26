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
 * Configurações globais de definição de lógicas MVC-P padroes para Açoes
 */
public @interface PlcConfigPattern {

	
	/**
	 * Indica com qual lógica MVC-P padrão será utilizada
	 */
	public enum Logic {
		/**
		 * Lógica padrão para Manutenção de Ciclo de Vida de Objetos utilizando ergonomia "Tabular", ou seja, com 
		 * entrada de dados simultânea de todos os objetos da classe, em uma tabela com diversas linhas. 
		 */
		
		TABULAR,
		CRUD,
		CRUDTABULAR,
		MASTER_DETAIL,
		MANTAIN_DETAIL,
		SUBDETAIL,
		SELECTION,
		SELECTION_MANTAIN_DETAIL,
		REPORT,
		CONSULTATION,
		USERPREF,
		APPLICATIONPREF,
		CONTROL, 
		MANTAIN_SUBDETAIL,
		AUTOMATIC_CONVENTION_BASED;

		/**
		 * @since jCompany 5.0
		 * para manter a compatibilidade com a camada modelo
		 */
		@Override
		public String toString() {
			switch (this) {
				case TABULAR:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR;
				case CRUD:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD;
				case CRUDTABULAR:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR;
				case MASTER_DETAIL:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL;
				case MANTAIN_DETAIL:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL_MANTAIN_DETAIL;
				case SUBDETAIL:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL_SUB_DETAIL;
				case SELECTION:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_SELECTION;
				case SELECTION_MANTAIN_DETAIL:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_SELECTION_MANTAIN_DETAIL;
				case REPORT:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_REPORT;
				case CONSULTATION:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CONSULTATION;
				case USERPREF:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_USER_PREF;
				case APPLICATIONPREF:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_PREF_APPLICATION;
				case CONTROL:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CONTROL;
				case MANTAIN_SUBDETAIL:
					return PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL_MANTAIN_DETAIL;
				default:
					return this.name();
			}
		}
	}
	
	/**
	 * Indica a complexidade da Ação a ser realizada
	 */
	public enum Complexity {
		/**
		 * Complexity Baixa: Ação atendida somente com programação declarativa
		 */
		SIMPLE,
		/**
		 * Complexity Média: Ação atendida com programação declarativa e extensões padroes, de media complexidade
		 */
		MEDIUM,
		/**
		 * Complexity Alta: Ação atendida com extensões sofisticadas ou sobreposições complexas de códigos do framework. 
		 */
		COMPLEX;
		
		/**
		 * @since jCompany 5.0
		 * para manter a compatibilidade com a camada modelo
		 */
		@Override
		public String toString() {
			switch (this) {
				case SIMPLE:
					return PlcConstantsCommons.DEFAULT_LOGIC.COMPLEXITY.COMPLEXITY_SIMPLE;
				case MEDIUM:
					return PlcConstantsCommons.DEFAULT_LOGIC.COMPLEXITY.COMPLEXITY_MEDIUM;
				case COMPLEX:
					return PlcConstantsCommons.DEFAULT_LOGIC.COMPLEXITY.COMPLEXITY_HIGH;
				default:
					return this.name();
			}
		}
	}
	
	/**
	 * Indica a complexidade da Ação a ser realizada
	 */
	public enum ExclusionMode {
		/**
		 * Exclusao retira efetivamente os objetos do SGBD
		 */
		PHYSICAL,
		/**
		 * Exclusao somente altera os objetos do SGBD para 'situação de inativo', seguindo o padrão:<P>
		 * sitHistoricoPlc='A' (Ativo)<br>
		 * sitHistoricoPlc='I' (Inativo)<p>
		 * O sitHistoricoPlc pode ser mapeado para qualquer coluna String.
		 */
		LOGICAL;
		
		/**
		 * @since jCompany 5.0
		 * para manter a compatibilidade com a camada modelo
		 */
		@Override
		public String toString() {
			switch (this) {
				case PHYSICAL:
					return PlcConstantsCommons.DEFAULT_LOGIC.EXCLUSION.PHYSICAL_MODE; 
				case LOGICAL:
					return PlcConstantsCommons.DEFAULT_LOGIC.EXCLUSION.LOGICAL_MODE;
				default:
					return this.name();
			}
		}
		
	}
	
	/**
	 * Indica ligeiras variações sobre a lógica padrão.
	 */
	public enum ModalityLogic {
		/**
		 * Forma padrão
		 */
		A,
		/**
		 * Primeira variação
		 */
		B,
		/**
		 * Segunda variação
		 */
		C
	}
	
	/**
	 * Define a lógica da colaboração
	 */
	Logic logic() default Logic.AUTOMATIC_CONVENTION_BASED;
	/**
	 * Define complexidade da colaboração
	 */
	Complexity complexity() default Complexity.MEDIUM;
	/**
	 * Define como o jCompany vai processar um exclusão
	 * @see ExclusionMode 
	 */
	ExclusionMode exclusionMode() default ExclusionMode.PHYSICAL;
	/**
	 * Define o type de variação da colaboração 
	 */
	ModalityLogic modality() default ModalityLogic.A;
	
}
