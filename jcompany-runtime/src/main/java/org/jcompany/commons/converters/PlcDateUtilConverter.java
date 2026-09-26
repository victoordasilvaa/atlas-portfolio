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
package org.jcompany.commons.converters;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * jCompany. Classe auxiliar para conversão de java.util.Date para uso com
 * BeanUtils.copy<p>
 * @author alvim
 * @version 1.0
 */
public final class PlcDateUtilConverter implements Converter {

	/**
	 * @since jCompany 3.0
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public PlcDateUtilConverter() {

		this.defaultValue = null;
		this.useDefault = false;

	}


	/**
	 * @since jCompany 3.0
	 * Create a {@link Converter} that will return the specified default value
	 * if a conversion error occurs.
	 *
	 * @param defaultValue The default value to be returned
	 */
	public PlcDateUtilConverter(Object defaultValue) {

		this.defaultValue = defaultValue;
		this.useDefault = true;

	}


	/**
	 * @since jCompany 3.0
	 * The default value specified to our Constructor, if any.
	 */
	private Object defaultValue = null;


	/**
	 * @since jCompany 3.0
	 * Should we return the default value on conversion errors?
	 */
	private boolean useDefault = true;


		// --------------------------------------------------------- Public Methods


	/**
	 * @since jCompany 3.0
	 * Convert the specified input object into an output object of the
	 * specified type.
	 *
	 * @param type Data type to which this value should be converted
	 * @param value The input value to be converted
	 *
	 * @exception ConversionException if conversion cannot be performed
	 *  successfully
	 */
	public Object convert(Class type, Object value) {

		if (value == null) {
			if (useDefault) {
				return (defaultValue);
			} else {
				throw new ConversionException("None specified value");
			}
		}

		if (value instanceof Date) {
			return (value);
		}
		String mask = "dd/MM/yyyy HH:mm";
		
		// Pega máscara pelo size. TODO Refatorar para aceitar diferencas do type DD/MM x MM/AA.
		if (value.toString().length()==5) {
			mask = "MM/yy";
		} else if (value.toString().length()==7) {
			mask = "MM/yyyy";
		}
			
		try {
			SimpleDateFormat formatter = new SimpleDateFormat (mask);
			return formatter.parse((String) value);
		} catch (Exception e) {
			if (useDefault) {
				return (defaultValue);
			} else {
				throw new ConversionException(e);
			}
		}

	}

}

