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
package org.jcompany.commons.helper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;


/**
 * jCompany 2.5.3. Singleton. Classe utilitária para clonar bean que usam CGLIB (bug 'callback')
 */
public class PlcBeanCloneHelper  {

	 // Auxiliar para mudança de comportamento
    private BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
    
    /** Used to access properties*/
    private PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
    
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1376364659576559836L;
	private static PlcBeanCloneHelper INSTANCE = new PlcBeanCloneHelper();
    private PlcBeanCloneHelper() { }
    public static PlcBeanCloneHelper getInstance(){
       return INSTANCE;
    }

	protected static final Logger log = Logger.getLogger(PlcBeanCloneHelper.class);


	/**
     * <p>Clone a bean based on the available property getters and setters,
     * even if the bean class itself does not implement Cloneable.</p>
     *
     * <p>
     * <strong>Note:</strong> this method creates a <strong>shallow</strong> clone.
     * In other words, any objects referred to by the bean are shared with the clone
     * rather than being cloned in turn.
     * </p>
     *
     * @param bean Bean to be cloned
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InstantiationException if a new instance of the bean's
     *  class cannot be instantiated
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public Object cloneBean(Object bean)
            throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException {

        if (log.isDebugEnabled()) {
            log.debug("Cloning bean: " + bean.getClass().getName());
        }
        Class clazz = bean.getClass();
        Object newBean = null;
        if (bean instanceof DynaBean) {
            newBean = ((DynaBean) bean).getDynaClass().newInstance();
        } else {
            newBean = bean.getClass().newInstance();
        }
       copyProperties(newBean, bean);
        return (newBean);

    }

    /**
     * <p>Copy property values from the origin bean to the destination bean
     * for all cases where the property names are the same.  For each
     * property, a conversion is attempted as necessary.  All combinations of
     * standard JavaBeans and DynaBeans as origin and destination are
     * supported.  Properties that exist in the origin bean, but do not exist
     * in the destination bean (or are read-only in the destination bean) are
     * silently ignored.</p>
     *
     * <p>If the origin "bean" is actually a <code>Map</code>, it is assumed
     * to contain String-valued <strong>simple</strong> property names as the keys, pointing at
     * the corresponding property values that will be converted (if necessary)
     * and set in the destination bean. <strong>Note</strong> that this method
     * is intended to perform a "shallow copy" of the properties and so complex
     * properties (for example, nested ones) will not be copied.</p>
     *
     * <p>This method differs from <code>populate()</code>, which
     * was primarily designed for populating JavaBeans from the map of request
     * parameters retrieved on an HTTP request, is that no scalar->indexed
     * or indexed->scalar manipulations are performed.  If the origin property
     * is indexed, the destination property must be also.</p>
     *
     * <p>If you know that no type conversions are required, the
     * <code>copyProperties()</code> method in {@link PropertyUtils} will
     * execute faster than this method.</p>
     *
     * <p><strong>FIXME</strong> - Indexed and mapped properties that do not
     * have getter and setter methods for the underlying array or Map are not
     * copied by this method.</p>
     *
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if the <code>dest</code> or
     *  <code>orig</code> argument is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void copyProperties(Object dest, Object orig)
        throws IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.copyProperties(" + dest + ", " +
                      orig + ")");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty origDescriptors[] =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    // JCOMPANY 3 (AJUSTE)
                   beanUtilsBean.copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    Object value = ((Map) orig).get(name);
                   beanUtilsBean.copyProperty(dest, name, value);
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor origDescriptors[] =
                getPropertyUtils().getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                //if ("class".equals(name)) {
                /******* jCompany 3.0 :  MODIFICACAO INICIO ************/
                if ("class".equals(name) || "callback".equals(name) || 
                	(name != null && name.endsWith("Aux") && name.endsWith("Str"))) {
                    continue; // No point in trying to set an object's class
                }
                /************* FIM *****************/
                if (getPropertyUtils().isReadable(orig, name) &&
                    getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        Object value =
                            getPropertyUtils().getSimpleProperty(orig, name);

                        /******* jCompany 3.0 :  MODIFICACAO INICIO ************/
                        if (value == null)
                        	PropertyUtils.setProperty(dest,name,null);
                        else
                        /************* FIM *****************/
                            // TODO try catch colocado para resolver problema de LayInicializationException
                            // Tentar resolver na camada de controle
                            try {
                        	beanUtilsBean.copyProperty(dest, name, value);
                            } catch (Exception e) {
				log.warn("Não foi possível clonar propriedade :"+name+" "+e.getMessage());
			    }
                    } catch (NoSuchMethodException e) {
                        ; // Should not happen
                    }
                }
            }
        }

    }

    /**
     * Gets the <code>PropertyUtilsBean</code> instance used to access properties.
     */
    public PropertyUtilsBean getPropertyUtils() {
        return propertyUtilsBean;
    }
	
}
