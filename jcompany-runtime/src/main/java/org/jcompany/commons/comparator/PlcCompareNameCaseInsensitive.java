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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/**
 * jCompany 2.5.3. Comparator. Classe que implementa Comparator para ordenar
 * descendentes de PlcObjetoNegocioVO por nome case insensitive
 * @author Paulo Alvim
 */
public class PlcCompareNameCaseInsensitive implements Comparator {

	protected static final Logger log = Logger.getLogger(PlcCompareNameCaseInsensitive.class);
	
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

   	try {
		return ((String)PropertyUtils.getProperty(obj1,"name")).toLowerCase().compareTo(
				((String)PropertyUtils.getProperty(obj2,"name")).toLowerCase()); 
		
    } catch (Exception e) {
		log.error("Error trying to order. Object "+obj1.getClass().getName()+" doens't have property name "+e);
		e.printStackTrace();
		return 0;
	}

    }
}

