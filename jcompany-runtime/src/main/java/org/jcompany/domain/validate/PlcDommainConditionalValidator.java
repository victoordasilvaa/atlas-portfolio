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
package org.jcompany.domain.validate;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.Validator;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcDateHelper;
import org.jcompany.domain.validate.PlcDommainConditional.Informed;
import org.jcompany.domain.validate.PlcDommainConditional.Operator;


/**
 * Validação entre propriedades (nível de classe) para Data (java.util.Date) e números (Long, BigDecimal, Integer, Double, etc.)
 */
public class PlcDommainConditionalValidator implements Validator<PlcDommainConditional>{

	protected static final Logger log = Logger.getLogger(PlcDommainConditionalValidator.class);
	
	private String propertyA;
	private String propertyB;
	private Operator operator;
	private Informed validateOkIf;
	
	public boolean isValid(Object bean) {
		
		try {
		
			Object valuePropA = PropertyUtils.getProperty(bean,propertyA);
			Object valuePropB = PropertyUtils.getProperty(bean,propertyB);
			
			if (validateOkIf.equals(Informed.BOTH_AND_PASSED_VALIDATION) && valuePropA!=null && valuePropB!=null &&
				!("".equals(valuePropA.toString())) && !("".equals(valuePropB.toString()))) {
				
				if (Date.class.isAssignableFrom(PropertyUtils.getPropertyType(bean,propertyA))) {
					// Se for data
					if (operator.equals(Operator.GREATER_THAN)) {
						return PlcDateHelper.getInstance().finalDateGreaterThanInitialInSeconds((Date)valuePropB,(Date)valuePropA);
					} else if (operator.equals(Operator.GREATER_THAN_OR_EQUAL_TO)) {
						return PlcDateHelper.getInstance().finalDateGreatThanEqualInitial((Date)valuePropB,(Date)valuePropA);
					} else if (operator.equals(Operator.LESS_THAN)) {
						return PlcDateHelper.getInstance().finalDateGreaterThanInitialInSeconds((Date)valuePropA,(Date)valuePropB);
					} else if (operator.equals(Operator.LESS_THAN_OR_EQUAL_TO)) {
						return PlcDateHelper.getInstance().finalDateGreatThanEqualInitial((Date)valuePropA,(Date)valuePropB);
					} else if (operator.equals(Operator.DIFFERENT)) {
						return !((Date)valuePropA).equals(valuePropB);
					} else
						throw new PlcException("#Internal Error trying to validadte date interval. Verify with administrator.");
					
				} else {
					// Se for numero
					if (operator.equals(Operator.GREATER_THAN)) {
						
						if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(bean,propertyA)))
							// decimal
							return  ((BigDecimal)valuePropA).compareTo((BigDecimal)valuePropB) > 0;
						else
							// inteiro
							return NumberUtils.compare(((java.lang.Double)valuePropA).doubleValue(),
									                   ((java.lang.Double)valuePropB).doubleValue()) > 0;
									                   
					} else if (operator.equals(Operator.GREATER_THAN_OR_EQUAL_TO)) {
						
						if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(bean,propertyA)))
							// decimal
							return  ((BigDecimal)valuePropA).compareTo((BigDecimal)valuePropB) >= 0;
						else
							// inteiro
							return NumberUtils.compare((new Double(valuePropA.toString())).doubleValue(),
									                   ((new Double(valuePropB.toString())).doubleValue())) >= 0;
					} else if (operator.equals(Operator.LESS_THAN)) {

						if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(bean,propertyA)))
							// decimal
							return  ((BigDecimal)valuePropA).compareTo((BigDecimal)valuePropB) < 0;
						else
							// inteiro
							return NumberUtils.compare(((java.lang.Double)valuePropA).doubleValue(),
									                   ((java.lang.Double)valuePropB).doubleValue()) < 0;
						
					} else if (operator.equals(Operator.LESS_THAN_OR_EQUAL_TO)) {
						
						if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(bean,propertyA)))
							// decimal
							return  ((BigDecimal)valuePropA).compareTo((BigDecimal)valuePropB) <= 0;
						else
							// inteiro
							return NumberUtils.compare(((java.lang.Double)valuePropA).doubleValue(),
									                   ((java.lang.Double)valuePropB).doubleValue()) <= 0;
						
					} else if (operator.equals(Operator.DIFFERENT)) {
						
						if (BigDecimal.class.isAssignableFrom(PropertyUtils.getPropertyType(bean,propertyA)))
							// decimal
							return  ((BigDecimal)valuePropA).compareTo((BigDecimal)valuePropB) != 0;
						else
							// inteiro
							return NumberUtils.compare(((java.lang.Double)valuePropA).doubleValue(),
									                   ((java.lang.Double)valuePropB).doubleValue()) != 0;
						
					} else
						throw new PlcException("jcompany.errors.validation.between.fields.invalid",new Object[]{valuePropA,valuePropB});
				
				}
				
			} else if (validateOkIf.equals(Informed.NONE) && valuePropA==null && valuePropB==null) {
				return true;
			} else if (validateOkIf.equals(Informed.ONE_OF_BOTH) && (valuePropA==null || valuePropB==null)) {
				return true;
			} else if (validateOkIf.equals(Informed.ONLY_PROP_A) && valuePropA!=null) {
				return true;
			} else if (validateOkIf.equals(Informed.ONLY_PROP_B) && valuePropB!=null) {
				return true;	
			} else if (validateOkIf.equals(Informed.BOTH_AND_PASSED_VALIDATION))
				return true;
			else
				return false;
		
		} catch (Exception e) {
			log.error("Error trying to capture values to validation between properties: "+e,e);
			return false;
		}
	
	}

	public void initialize(PlcDommainConditional parameters) {
		this.propertyA=parameters.valuePropA();
		this.propertyB=parameters.valuePropB();
		this.operator=parameters.operator();
		this.validateOkIf=parameters.validateOkIf();
	}

}
