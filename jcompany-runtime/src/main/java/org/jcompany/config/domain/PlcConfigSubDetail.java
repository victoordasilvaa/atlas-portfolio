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
import java.util.Comparator;

import org.jcompany.commons.comparator.PlcCompareId;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 3.2.1 Configurações para definição de classes de sub-detalhe
 */
public @interface PlcConfigSubDetail {

    /**
     * Define a classe de subdetalhe
     */
    Class clazz() default Object.class;
    
    /**
     * Define o numero padrão de novos detalhes no formulário
     */
    int numberNew() default 2;
    
    /**
     * Define o nome da propriedade no mestre onde está a coleção de detalhes 
     */
    String collectionName() default "";
    
    /**
     * Define as regras de cardinalidade para o relacionamento
     */
    String cardinality() default "0..*";
    
    /**
     * propriedade utilizada para desprezar as linhas do subDetalhe que não deverão ser gravadas, ou seja,
     * se não informada a linha 'registro' não será gravada
     */
    String referencePropertyDespise() default "";
    
    /**
     * Se a coluna flag, com valor "0" (zero) será considerada informada (nulo) ou desconsiderada ("0" como valor)
     */
    boolean significativeZero() default false;
    
    /**
     * Testa unicidade entre os detalhes
     */
    boolean testDuplicity() default true;
    
    /**
     * Define a classe utilizada para a comparação das chaves entre detalhes 
     */
    Class<? extends Comparator> comparator() default PlcCompareId.class;

}
