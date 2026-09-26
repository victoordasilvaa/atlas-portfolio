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
package org.jcompany.commons.facade;

import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcException;

/**
 * jCompany. Façade. Contrato de Utilitários Administrativos do Menu Área Técnica, 
 * de uso interno do jCompany.
 */
public interface IPlcAdmFacade {

	/**
	 * Limpa os cachings de IoC para Bussiness Components (BOs e ASs) e DAOs
	 * @since jCompany 3.02
	 * @throws PlcException
	 */
	public void clearCacheIoC() throws PlcException;
	
	/**
	 * @since jCompany 3.0
	 * Recebe parâmetros para geração de esquema DDL
	 * @param context
	 * @param actionType
	 * @param objTable
	 * @param objConstraint
	 * @param objSequence
	 * @param delimiter
	 * @throws PlcException
	 */
   public String generateSchema(PlcBaseContextVO context,String actionType,
		   String objTable,String objConstraint,  String objSequence,
		   String objIndex, String delimiter)  throws PlcException;
   
   /**
    * @since jCompany 3.0
	* Recebe parâmetros para geração de esquema DDL
    * @param context
    * @param schema
    * @param delimiter
    * @throws PlcException
    */
  public void executeSchema(PlcBaseContextVO context, String schema, String delimiter) throws PlcException;
  

}
