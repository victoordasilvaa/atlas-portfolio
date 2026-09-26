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

import org.jcompany.commons.PlcConstantsCommons;


/**
 * @since jCompany 5.0
 * Configurações para argumentos para lógica de seleção
 */
public @interface PlcConfigArgument {

    public enum Operator {
    	LESS_THAN,
    	DIFFERENT,
    	EQUALS_TO,
    	GREATER_THAN,
    	GREATER_THAN_OR_EQUALS_TO,
    	LESS_THAN_OR_EQUALS_TO,
    	LIKE_PERC_FINAL,
    	LIKE_PERC_BEGIN,
    	LIKE_PERC_TOTAL;

	@Override
	public String toString() {
	    switch (this) {
	    case LESS_THAN: return "<";//PlcConstants.DEFAULT_LOGIC.CONSULTA.QBE.QBE_MENOR_QUE;
	    case DIFFERENT: return "<>";//PlcConstants.DEFAULT_LOGIC.CONSULTA.QBE.QBE_DIFERENTE;
	    case EQUALS_TO: return "=";//PlcConstants.DEFAULT_LOGIC.CONSULTA.QBE.QBE_IGUAL_A;
	    case GREATER_THAN: return ">";//PlcConstants.DEFAULT_LOGIC.CONSULTA.QBE.QBE_MAIOR_QUE;
	    case GREATER_THAN_OR_EQUALS_TO: return ">=";//PlcConstants.DEFAULT_LOGIC.CONSULTA.QBE.QBE_MAIOR_OU_IGUAL_QUE;
	    case LESS_THAN_OR_EQUALS_TO: return "<=";//PlcConstants.DEFAULT_LOGIC.CONSULTA.QBE.QBE_MENOR_OU_IGUAL_QUE;
	    case LIKE_PERC_FINAL: return PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_FINAL;
	    case LIKE_PERC_BEGIN: return PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_BEGIN;
	    case LIKE_PERC_TOTAL: return PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_TOTAL;
	    }
	    return null;
	}
    }

    public enum Format {
	DATE,
	LONG,
	BIGDECIMAL,
	STRING,
	BOOLEAN;

	@Override
	public String toString() {
	    switch (this) {
	    case DATE: return PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_DATE;
	    case LONG: return PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_LONG;
	    case BIGDECIMAL: return PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_BIGDECIMAL;
	    case STRING: return PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_STRING;
	    case BOOLEAN: return PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_BOOLEAN;
	    }
	    return null;
	}

    }
    
    /**
     * nome da propriedade com sufixo padrao
     */
    String property();
    
    /**
     * operador para o argumento (igual,maior,menor,diferente,maiorOuIgual,menorOuIgual,*%,%*%,%*)
     */
    Operator operator();
    
    /**
     * Se a lógica deve considerar zero como não informado (combobox!)
     */
    String despiseZero() default "";
    
    /**
     * Alias a ser utilizando para prexixar o argumento (obj default)
     */
    String alias() default "obj";
    
    /**
     * Format DATE, LONG, BIGDECIMAL ou STRING
     */
    Format format();
    
    /**
     * Indica se deve montar "or is null" no operando
     */
    boolean orIsNull() default false;
    
    /**
     * Indica se o parâmetro é obrigatorio na seleção quando utilizando URLRastfull 
     */
    boolean mandatory() default false;
    
}
