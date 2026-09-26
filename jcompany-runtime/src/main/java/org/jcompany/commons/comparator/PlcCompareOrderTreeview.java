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
package org.jcompany.commons.comparator;

import java.util.Comparator;

import org.jcompany.commons.PlcBussinesObjectTreeEntity;


/**
 * jCompany 2.5.3. Classe que implementa Comparator para ordenar descendentes de
 * PlcObjetoNegocioVO por nome
 */
public class PlcCompareOrderTreeview implements Comparator
{

    
    /**
     * @since jCompany 3.0
     *
     */
	public boolean equals( Object parameter1 )
    {
        /** @todo: implement this function */
        return false; 
    }

    /**
     * @since jCompany 3.0
     *
     */
	public int compare(Object obj1, Object obj2) { 

		PlcBussinesObjectTreeEntity entity1 = (PlcBussinesObjectTreeEntity) obj1; 

		PlcBussinesObjectTreeEntity entity2 = (PlcBussinesObjectTreeEntity) obj2; 

		return entity1.getOrderPlc().compareTo(entity2.getOrderPlc()); 

    }
}

