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
package org.jcompany.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * jCompany 3.0 Permite anotação indicando que o método deve encerrar com o tratamento
 * genérico do jCompany para encerramento das sessões/transações:<p>
 * Se ocorrer exceção ou não, fecha com rollback<br>
 * Em qualquer caso, a lógica é disparada com chamadas dos métodos apropriados
 * da camada de persistência, preservando o MVC. As sessões de persistência
 * e conexões do pool também são sempre fechadas/devolvidas, em qualquer caso.
 * @since jCompany 3.0
 * @version $Id: PlcTransactionRead.java,v 1.2 2006/05/17 20:47:40 rogerio_baldini Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PlcTransactionRead {
	/** Fábrica */
	String value() default "default";
}
