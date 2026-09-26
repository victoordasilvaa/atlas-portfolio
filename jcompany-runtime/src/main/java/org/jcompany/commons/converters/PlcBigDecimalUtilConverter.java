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
import java.math.BigDecimal;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcConstantsCommons;


/**
 * jCompany. Classe auxiliar para conversão de java.math.BigDecimal para uso com
 * BeanUtils.copy<p>
 */
public final class PlcBigDecimalUtilConverter implements Converter {

	private Logger logWarningDevelopment = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_WARNING_DEVELOPMENT);
	protected static final Logger log = Logger.getLogger(PlcBigDecimalUtilConverter.class);

	/**
	 * @since jCompany 3.0
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public PlcBigDecimalUtilConverter() {

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
	public PlcBigDecimalUtilConverter(Object defaultValue) {

		this.defaultValue = defaultValue;
		this.useDefault = true;

	}


	/**
	 * @since jCompny 3.0
	 * The default value specified to our Constructor, if any.
	 */
	private Object defaultValue = null;


	/**
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

		if (log.isDebugEnabled())
			log.debug("##BIG DECIMAL. converting value BigDecimal "+value+" of class "+type);

		if (value == null) {
			if (useDefault) {
				if (log.isDebugEnabled())
					log.debug("##BIG DECIMAL. using default value  "+defaultValue);
				return (defaultValue);
			} else {
				throw new ConversionException("None specified value");
			}
		}

		if (value instanceof BigDecimal) {
			if (log.isDebugEnabled())
				log.debug("##BIG DECIMAL. using default value "+defaultValue);
			return (value);
		} else if (value instanceof String) {
			if (log.isDebugEnabled())
				log.debug("##BIG DECIMAL. converting value string "+value);
			return (new BigDecimal((String)value));
		} else {
			logWarningDevelopment.debug(this.getClass().getCanonicalName()+": BIG DECIMAL. returning zero.  Received parameters " +
					"nor BigDecimal nor String. " +value);
			if (value != null)
				logWarningDevelopment.debug(this.getClass().getCanonicalName()+": Received Class "+value.getClass().getName());
			return (new BigDecimal(0));
		}


	}


}

