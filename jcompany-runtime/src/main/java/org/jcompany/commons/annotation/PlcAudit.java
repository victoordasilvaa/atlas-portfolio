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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação que define regras de auditoria rígida, em duas modalidades:<p>
 * 1. Auditoria para tabela paralela, que deve ser descendente com sufixo AuditPlc<br>
 * 2. Auditoria somente de propriedades, em modelo genérico pré-mapeado invertido Classes -< Atributos
 * @since jCompany 3.1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlcAudit {

	/**
	 * Modalidade de Auditoria. O default é usar uma classe 'paralela' com mesmos valores
	 */
	Modalidade modalidade() default Modalidade.CLASSE;
	
	public enum Modalidade {
		/**
		 * Utiliza Classe com sufixo "AuditPlc". Recomendado para todos os atributos ou em grande número (nao precisa mapear todos)
		 */
		CLASSE,
		/**
		 * Auditoria somente em algumas propriedades se modificadas. Recomendado para 1 ou 2 atributos. Dispensa novas tabelas específicas.
		 */
		PROPRIEDADE
	}
			
	/**
	 * Para modalidade PROPRIEDADE. Relação de propriedades para auditoria. 
	 */
	String[] propriedades() default "[]" ;
	
	/**
	 * Classe a ser utilizada para auditoria.
	 * Se contiver o default (java.lang.Object) irá utilizar a classe de auditoria segundo
	 * a seguinte convenção:
	 * [pacote_classeVO].auditoria.[nome_simples_classeVO_sem_sufixo]Audit
	 * Exemplo:
	 * para com.empresa.app.vo.FuncionarioVO: com.empresa.app.vo.auditoria.FuncionarioAudit
	 * @return
	 */
	Class classeAuditoria() default Object.class;
	
}
