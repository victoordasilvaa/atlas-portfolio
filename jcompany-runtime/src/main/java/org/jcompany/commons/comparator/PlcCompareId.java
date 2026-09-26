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
import org.jcompany.commons.PlcBaseEntity;


/**
 * jCompany 2.5.3. Comparator. Classe que implementa Comparator para ordenar
 * descendentes de PlcBaseEntity por id. <br>
 * Permite informar a order que será aplicada: ascendente (default) ou
 * descendente. <br>
 * Trata valor nulo.
 * 
 * @author Paulo Alvim
 * @author Roberto Badaró
 */
public class PlcCompareId implements Comparator {



    /**
     * Define se a organização será ascendente (1) ou descendente (-1).
     */
    private int order = 1;


    /**
     * @since jCompan 3.0
     *
     */
    public PlcCompareId() {

    }


    /**
     * @since jCompan 3.0
     *
     */
    public PlcCompareId(boolean orderDesc) {

        if (orderDesc) {

            order = -1;

        }

    }



    /**
     * @since jCompan 3.0
     *
     */
    public boolean equals(Object parameter1) {

        return false;

    }



    /**
     * @since jCompan 3.0
     *
     */
    public int compare(Object obj1, Object obj2) {

    	try {
	
    	      Long id1 = (Long) PropertyUtils.getProperty(obj1,"id");

	        Long id2 = (Long) PropertyUtils.getProperty(obj2,"id");

	        int ret = 0;

	        if (id1 != null && id2 != null) {

	            ret = id1.compareTo(id2);

	        } else if (id1 == null && id2 != null) {

	            ret = -1;

	        } else if (id1 != null && id2 == null) {

	            ret = 1;

	        }

	        return (ret * order);

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
  
    }

}



