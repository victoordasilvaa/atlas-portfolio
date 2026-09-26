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
package org.jcompany.commons;

import org.jcompany.commons.annotation.PlcIoC;

/**
 * jCompany. Função para submissão de HQL Interativo<p>
 * @author Cláudia Seara
 * @version 1.0
 */
@PlcIoC(nameBCClass="org.jcompany.model.adm.PlcHqlInterativoBO")
public class PlcHqlInteractveEntity extends PlcBaseEntity{

	private static final long serialVersionUID = 7123813859930263955L;
	private String selectionCommand;
    private String selectionResult;

    /**
	 * @since jcompany 3.0
	 */
	public PlcHqlInteractveEntity()
    {
		super();
    }

	/**
	 * @since jcompany 3.0
	 */
	public String getSelectionResult()
    {
    	return( selectionResult );
    }

	/**
	 * @since jcompany 3.0
	 */
	public void setSelectionResult( String newSelectionResult )
    {
        selectionResult = newSelectionResult;
    }

	/**
	 * @since jcompany 3.0
	 */
	public String getSelectionCommand()
    {
    	return( selectionCommand );
    }

	/**
	 * @since jcompany 3.0
	 */
	public void setSelectionCommand( String newSelectionCommand )
    {
        selectionCommand = newSelectionCommand;
    }
}
