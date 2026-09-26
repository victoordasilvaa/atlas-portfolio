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
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.converters.PlcBigDecimalUtilConverter;


/**
 * jCompany 2.5.3. Comparator. Classe que implementa Comparator para ordenar
 * descendentes de PlcBaseEntity por nome. <br>
 * Permite informar a order que será aplicada: ascendente (default) ou
 * descendente. <br>
 * Trata valor nulo.
 * 
 * @version $Id: PlcCompareName.java,v 1.5 2006/06/21 19:12:19 bruno_grossi Exp $
 * @author Paulo Alvim
 * @author Roberto Badaró
 */
public class PlcCompareName implements Comparator {

	protected static final Logger log = Logger.getLogger(PlcCompareName.class);
	
    /**
     * @since jCompan 3.0
     * Define se a organização será ascendente (1) ou descendente (-1).
     */
    private int order = 1;

    /**
     * @since jCompan 3.0
     * Constrói comparador ordenando ascendentemente.
     */
    public PlcCompareName() {
    }

    /**
     * @since jCompan 3.0
     * Construtor para definir explicitamente qual ordenação será utilizada.
     * 
     * @param orderDesc
     *            <code>TRUE</code> Ordem descendente, <code>FALSE</code>
     *            ascendente (default).
     */
    public PlcCompareName(boolean orderDesc) {
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
					
	        String name1 = (String) PropertyUtils.getProperty(obj1,"nome");
	        String name2 = (String) PropertyUtils.getProperty(obj2,"nome");
	        int ret = 0;
	
	        if (name1 != null && name2 != null) {
	            ret = name1.compareTo(name2);
	        } else if (name1 == null && name2 != null) {
	            ret = -1;
	        } else if (name1 != null && name2 == null) {
	            ret = 1;
	        }
	
	        return (ret * order);
	        
    	} catch (Exception e) {
			log.error("Error trying to order. Object "+obj1.getClass().getName()+" doens't have property name "+e);
			e.printStackTrace();
			return 0;
		}
    }
}