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

/**
 * jCompany 2.5.3. Comparator. Compara Strings dentro de coleção 
 */
public class PlcCompareString implements Comparator {

    
    /**
     * @since jCompany 3.0
     *
     */
	public boolean equals( Object parameter1 )  {
        return false; 
    }

    
	 /**
     * @since jCompany 3.0
     *
     */
	public int compare(Object obj1, Object obj2) { 

		String s1 = (String) obj1; 

		String s2 = (String) obj2; 

		return s1.compareTo(s2); 

    }
}
