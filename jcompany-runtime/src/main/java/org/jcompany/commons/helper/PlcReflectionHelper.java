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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;


/**
 * jCompany 2.5.3. Singleton. Classe utilitária para uso de reflexão
 */
public class PlcReflectionHelper  {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1376364659576559836L;
	private static PlcReflectionHelper INSTANCE = new PlcReflectionHelper();
    private PlcReflectionHelper() { }
    /**
     * @since jCompany 3.0
     */
    public static PlcReflectionHelper getInstance(){
       return INSTANCE;
    }

	protected static final Logger log = Logger.getLogger(PlcReflectionHelper.class);

	/**
     * @since jCompany 3.0
     * Permite a execução dinâmica de métodos de um objeto utilizando reflexão
	 * (java.lang.reflect.Method).
	 * <br>
	 * <b>NÃO permite</b> argumentos nulos.
	 * <br>
	 * Se o número de argumentos esperados pelo método a ser executado for 0 (zero),
	 * o array de argumentos pode ter size 0 ou valor nulo.
	 * <br><br>
	 * Se o retorno do método for um type primitivo, ele será devolvido em um
	 * <code>Object</code> apropriado (ex.: int - Integer), se retorno for <code>void</code>,
	 * será devolvido <code>null</code>.
     *
	 * @param o - Objeto contendo o método que será executado
     * @param method - Nome do método a ser executado.
     * @param args - Array de Object com os argumentos esperados pelo método.
	 *
     * @return Object - Um Object com o retorno do método.
     *
     * Se necessário utilizar {@link PlcReflectionHelper#executeMethod(Object, String, Object[], Class[])}.
     * 			A nova assinatura permite argumentos nulos, já que os tipos são informados.
     */
    public Object executeMethod( Object o, String method, Object args[]) throws PlcException {
        Class[] c = new Class[(args != null ? args.length : 0)];
        for (int i = 0; i < c.length; i++) {
            c[i] = args[i].getClass();
        }
        return executeMethod(o, method, args, c);
    }

    /**
     * @since 2003
     * Permite a execução dinâmica de métodos de um objeto utilizando reflexão
     * (java.lang.reflect.Method).
     * <br>
	 * <b>NÃO permite</b> argumentos nulos.
     * <br>
     * Se o número de argumentos esperados pelo método a ser executado for 0
     * (zero), o array de argumentos pode ter size 0 ou valor nulo. <br>
     * <br>
     * Se o retorno do método for um type primitivo, ele será devolvido em um
     * <code>Object</code> apropriado (ex.: int - Integer), se retorno for
     * <code>void</code>, será devolvido <code>null</code>.
     *
     *
     * @param o - Objeto contendo o método que será executado
     * @param method - Nome do método a ser executado.
     * @param args - Array de Object com os argumentos esperados pelo método.
     *
     * @return Object - Um Object com o retorno do método.
     *
     * @deprecated Utilizar {@link PlcReflectionHelper#executeMethodWithoutCancel(Object, String, Object[], Class[])}
     * 			A nova assinatura permite argumentos nulos, já que os tipos são informados.
     */
     public Object executeMethodWithoutCancel (Object o, String method, Object args[]) throws PlcException {
         Class[] c = new Class[(args != null ? args.length : 0)];
         for (int i = 0; i < c.length; i++) {
             c[i] = args[i].getClass();
         }
         return executeMethodWithoutCancel(o, method, args, c);
     }

     /**
      * Permite a execução dinâmica de métodos de um objeto utilizando reflexão
      * (java.lang.reflect.Method). <br>
	  * <b>Permite</b> argumentos nulos, uma vez que o array de Class é informado, permitindo a execução
	  * do método conforme sua assinatura.
      * <br>
      * Se o número de argumentos esperados pelo método a ser executado for 0
      * (zero), o array de argumentos pode ter size 0 ou valor nulo. <br>
      * <br>
      * Se o retorno do método for um type primitivo, ele será devolvido em um
      * <code>Object</code> apropriado (ex.: int - Integer), se retorno for
      * <code>void</code>, será devolvido <code>null</code>.
      *
      * @since jCompany 2.5.3
      *
      * @param o - Objeto contendo o método que será executado
      * @param method - Nome do método a ser executado.
      * @param args - Array de Object com os argumentos esperados pelo método.
      * @param types - Array de Class com os tipos (classes) dos argumentos.
      *
      * @return Object - Um Object com o retorno do método.
      */
     public Object executeMethod(Object o, String method, Object args[], Class types[]) throws PlcException {
         try {
             Method m = o.getClass().getMethod(method, types);
             return m.invoke(o, args);
         } catch (InvocationTargetException e) {
        	 // Se exceção original já é tratada, somente repassa
        	 if (PlcException.class.isAssignableFrom(e.getTargetException().getClass()))
                throw (PlcException) e.getTargetException();
        	 else
        		 throw new PlcException("jcompany.error.generic", new Object[] {
                     "executeMethod", e.getTargetException() }, e.getTargetException(),log);
         } catch (Exception e) {
              throw new PlcException("jcompany.errors.reflection.general", new Object[] {o.getClass().getName(),method,args,e}, e,log);
         }
     }

    
     /**
      * Permite a execução dinâmica de métodos de um objeto utilizando reflexão
      * (java.lang.reflect.Method). <br>
	  * <b>Permite</b> argumentos nulos, uma vez que o array de Class é informado, permitindo a execução
	  * do método conforme sua assinatura.
      * <br>
      * Se o número de argumentos esperados pelo método a ser executado for 0
      * (zero), o array de argumentos pode ter size 0 ou valor nulo. <br>
      * <br>
      * Se o retorno do método for um type primitivo, ele será devolvido em um
      * <code>Object</code> apropriado (ex.: int - Integer), se retorno for
      * <code>void</code>, será devolvido <code>null</code>.
      *
      * @since jCompany 2.5.3
      *
      * @param o Objeto contendo o método que será executado
      * @param method Nome do método a ser executado.
      * @param args Array de Object com os argumentos esperados pelo método.
      * @param types Array de Class com os tipos (classes) dos argumentos.
      * @throws PlcException
      */
     public Object executeMethodWithoutCancel(Object o, String method,
            Object args[], Class types[]) throws PlcException {

         log.debug("######## Entered in executeMethodWithoutCancel");

         try {
        	 
             Method m = o.getClass().getMethod(method, types);
             return m.invoke(o, args);

         } catch (InvocationTargetException e) {
        	 // Se exceção original já é tratada, somente repassa
        	 if (PlcException.class.isAssignableFrom(e.getTargetException().getClass()))
                throw (PlcException) e.getTargetException();
        	 else
        		 throw new PlcException("jcompany.error.generic", new Object[] {
                     "executeMethodWithoutCancel", e.getTargetException() }, e.getTargetException(),log);
         } catch (NoSuchMethodException e) {
             return PlcConstantsCommons.SILENCER_ERROR_PLC;
         } catch (Exception e) {
              throw new PlcException("jcompany.errors.reflection.general", new Object[] {o.getClass().getName(),method,args,e}, e,log);
         }
     }
     
     /**
      * 
      * @since jCompany 3.0
      * Permite a execução dinâmica de métodos de um objeto utilizando reflexão, sem conferência do
      * type de parâmetros do método, mas somente utilizando seu nome. Deste modo, permite chamadas onde
      * os tipos exatos não são passados como argumentos, mas descendentes ou interfaces.
      *
      * @param o - Objeto contendo o método que será executado
      * @param method - Nome do método a ser executado.
      * @param args - Array de Object com os argumentos esperados pelo método.
      *
      * @return Object - Um Object com o retorno do método.
      */
     public Object executePolymorphicMethod(Object o, String method,Object args[]) throws PlcException {

         log.debug("######## Entered in executePolymorphicMethod");

    	 Exception isFinal = null;
    	 
    	 try {

             Method[] ms = o.getClass().getMethods();
             for (int i = 0; i < ms.length; i++) {
            	 
            	 if (ms[i].getName().equals(method)) {
            		try{
            		   isFinal = null;
            		   return ms[i].invoke(o, args);
            		} catch (Exception e) {
            			isFinal = e;
            		}
            		                       
            	 }
 			 
             }
             
             if (isFinal !=null)
            	 throw isFinal;
             
    	 } catch (InvocationTargetException e) {
        	 // Se exceção original já é tratada, somente repassa
        	 if (PlcException.class.isAssignableFrom(e.getTargetException().getClass()))
                throw (PlcException) e.getTargetException();
        	 else
        		 throw new PlcException("jcompany.error.generic", new Object[] {
                     "executePolymorphicMethod", e.getTargetException() }, e.getTargetException(),log);
         } catch (Exception e) {
              throw new PlcException("jcompany.errors.reflection.general", new Object[] {o.getClass().getName(),method,args,e}, e,log);
         }
         
         throw new PlcException("jcompany.errors.reflection", new Object[] {o.getClass().getName(),method,args});
     
     }
     
     /**
 	 * @since jCompany 3.0
 	 * Verifica se uma propriedade foi informada
 	 * @param columnNameDespise Nome da propriedade
 	 * @param bean Nome do Bean para busca
 	 * @param disconsiderZero Se desconsidera zero como valor deve ser informado '0'. String vazio considera '0' como valor
 	 * @return true se foi informada ou false em caso contrario
 	 * @throws PlcException Se não conseguiu pegar valor por reflexão
 	 */
 	public boolean isPropertyInformed(String columnNameDespise,Object bean,String disconsiderZero) throws PlcException {
 	    boolean valid = true;
 			if (!columnNameDespise.equals("")) {
 				//String obj = (String) chamaGetter(plcVO,nomeColDesprezar);
 			    String obj=null;
 			    try {
                obj = String.valueOf(callGetter(bean,columnNameDespise));
                if ("null".equals(obj))
                	obj = null;
 			    } catch (Exception e) {
                throw new PlcException("jcompany.error.column.despise",
                        new Object[] { columnNameDespise, bean.getClass().getName(),e,log });
            }
 			if (obj == null || (obj != null && obj.trim().equals("")) ||
 				(obj != null && obj.equals("0") && disconsiderZero.equals("0")))
 				valid = false;
 			}
 			return valid;
 	}
 	
 	/**
	 * @since jCompany. 
	 * Recebe o nome de uma propriedade e chama dinamicamente o método
	 *		getter correpondente, colocando o resultado em Object. Se a propriedade
	 *      for uma classe agregada, varre todos os niveis de getters.
	 */
	public Object callGetter(Object obj,String attributeName) throws PlcException {

		try {
				log.debug("################## Entered in callGetter");

				// Transformar em singleton
				PlcStringHelper plcS = PlcStringHelper.getInstance();

				// importante - este metodo separa por pontos, o do BO por sublinhado!!
				List l = plcS.splitListElements(attributeName,".");
				Object objAux = new Object();
				Iterator i = l.iterator();

				while (i.hasNext()) {

				   String attributeNameAux =  (String) i.next();

					objAux = PropertyUtils.getSimpleProperty(obj,attributeNameAux);

					if (obj == null) {
						log.debug("Null object:"+obj.getClass());
						return objAux;
					} // Retirado para evitar erro PMD
					   //else
					  //	obj = objAux;
				}

				return objAux;

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.getter",new Object[] {e},e,log);
		}
	}
	
	public Method getSetterMethod(PropertyDescriptor propertyDescriptor) {
		Method method = propertyDescriptor.getWriteMethod();
		if ((method == null) &&
				(propertyDescriptor instanceof IndexedPropertyDescriptor)) {
			method = ((IndexedPropertyDescriptor) propertyDescriptor).getIndexedWriteMethod();
		}
		return method;
	}
	
	public Method getGetterMethod(PropertyDescriptor propertyDescriptor) {
		Method method = propertyDescriptor.getReadMethod();
		if ((method == null) &&
				(propertyDescriptor instanceof IndexedPropertyDescriptor)) {
			method = ((IndexedPropertyDescriptor) propertyDescriptor).getIndexedReadMethod();
		}
		return method;
	}

	/**
	 * Procura um field numa classe e seus ancestrais. Retorna null se não achar.
	 * @param clazz classe base para a procura.
	 * @param name nome do field procurado
	 * @return
	 * @since jCompany 3.0.2
	 * @deprecated Verificar a possibilidade de se utilizar o método {@link #findAttributeInHierarchy(Class, String)}.
	 */
	@Deprecated
	public Field findField(Class<? extends Object> clazz, String name) {
		try {
			return findAttributeInHierarchy(clazz, name);
		} catch (NoSuchFieldException fieldException) {
			return null;
		}
	}

	/**
	 * Recupera um field de uma Entidade se a propriedade for um componente recupera do componente
	 * @deprecated Verificar a possibilidade de se utilizar o método {@link #findAttributeInHierarchy(Class, String)}.
	 */
	@Deprecated
	public Field retrieveField(Class entity, String fieldName) throws SecurityException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		String property = fieldName;

		if (property.indexOf(".")>-1) {
			String componentOrCollection 	= fieldName.toString().substring(0,property.toString().indexOf('.'));
			property 				= property.toString().substring(property.toString().indexOf('.')+1);
			Field field 				= findAttributeInHierarchy(entity, componentOrCollection);
			
			return retrieveField(field.getType(), property);

		} else 
			return findAttributeInHierarchy(entity, property);
	}

	/* Reflexão de ATRIBUTOS */

	/**
	 * Procura, recursivamente, um atributo (ou <i>field</i>) pelo nome à
	 * partir da classe base informada.
	 * 
	 * @param classBase
	 *            A classe por onde a procura deve começar.
	 * @param attributeName
	 *            O nome do atributo a ser encontrado.
	 * @return Uma referência reflexiva ao atributo indicado.
	 * @throws SecurityException
	 *             No caso de haver um <code>SecurityManager</code> instalado,
	 *             e ele negar acesso ao método
	 *             <code>java.lang.Class.getDeclaredField(java.lang.String)</code>.
	 * @throws NoSuchFieldException
	 *             Caso o atributo solicitado não seja encontrado na hierarquia
	 *             da classe base.
	 * @see Class#getDeclaredField(String)
	 */
	public Field findAttributeInHierarchy(Class<?> classBase,
			String attributeName) throws SecurityException,
			NoSuchFieldException {
		
		if (!(attributeName.indexOf(".") > -1)){
			for (Class<?> clazz = classBase; clazz != null; clazz = clazz.getSuperclass())
				try {
					return clazz.getDeclaredField(attributeName);
				} catch (NoSuchFieldException fieldException) {
				}
				throw new NoSuchFieldException(attributeName);
		} else {
			String firstAttr = attributeName.substring(0, attributeName.indexOf("."));
			Field declaredField = null;
			for (Class<?> clazz = classBase; clazz != null; clazz = clazz.getSuperclass()){
				try {
					declaredField = clazz.getDeclaredField(firstAttr);
					if(declaredField != null)
						break;
				} catch (NoSuchFieldException fieldException) {
				}
			}
			
			if (declaredField == null)
				throw new NoSuchFieldException(attributeName);
				
			return findAttributeInHierarchy(declaredField.getType(), attributeName.substring(attributeName.indexOf(".")+1));
		}
		
	}

	/**
	 * Obtém um vetor de referências reflexivas a todos os atributos (ou
	 * <i>field</i>s) não-ofuscados contidos na hierarquia da classe base
	 * informada.
	 * 
	 * @param classeBase
	 *            A classe por onde a procura deve começar.
	 * @return O vetor de referências reflexivas.
	 * @see #getAllAttributesInHierarchy(Class, boolean)
	 * @see Class#getDeclaredFields()
	 */
	public Field[] getAllAttributesInHierarchy(Class<?> classeBase) {
		return getAllAttributesInHierarchy(classeBase, false);
	}

	/**
	 * Obtém um vetor de referências reflexivas a todos os atributos (ou
	 * <i>field</i>s), incluindo ou não os atributos ofuscados contidos na
	 * hierarquia da classe base informada.
	 * 
	 * @param classeBase
	 *            A classe por onde a procura deve começar.
	 * @param includeObfuscatedFields
	 *            Se <code>true</code>, inclui os atributos ofuscados. Senão,
	 *            não.
	 * @return O vetor de referências reflexivas.
	 * @see #getAllAttributesInHierarchy(Class)
	 * @see Class#getDeclaredFields()
	 */
	public Field[] getAllAttributesInHierarchy(Class<?> classeBase,
			boolean includeObfuscatedFields) {
		return getAllAttributesInHierarchy(classeBase, true,
				includeObfuscatedFields);
	}

	/**
	 * Obtém um vetor de referências reflexivas a todos os atributos (ou
	 * <i>field</i>s), incluindo ou não os atributos ofuscados contidos na
	 * hierarquia da classe base informada.
	 * 
	 * @param classBase
	 *            A classe por onde a procura deve começar.
	 * @param includeNotMapped
	 *            Se <code>false</code>, inclui apenas os atributos
	 *            declarados em classes anotadasos atributos ofuscados. Caso
	 *            contrário, inclui todos os atributos, sem distinção.
	 * @param includeObfuscatedFields
	 *            Se <code>true</code>, inclui os atributos ofuscados. Senão,
	 *            não.
	 * @return O vetor de referências reflexivas.
	 * @see #getAllAttributesInHierarchy(Class)
	 * @see Class#getDeclaredFields()
	 */
	public Field[] getAllAttributesInHierarchy(Class<?> classBase,
			boolean includeNotMapped, boolean includeObfuscatedFields) {
		final Map<String, Field> attributeMap = new HashMap<String, Field>();
		for (Class<?> clazz = classBase; clazz != null; clazz = clazz.getSuperclass())
			if (includeNotMapped
					|| (clazz
							.isAnnotationPresent(javax.persistence.Entity.class) || clazz
							.isAnnotationPresent(javax.persistence.MappedSuperclass.class)))
				for (Field attribute : clazz.getDeclaredFields()) {
					String attrobuteName = attribute.getName();
					if (includeObfuscatedFields
							|| !attributeMap.containsKey(attrobuteName))
						attributeMap.put(attrobuteName, attribute);
				}
		final Collection<Field> attributes = attributeMap.values();
		return attributes.toArray(new Field[attributes.size()]);
	}

	/* Reflexão de MÉTODOS */

	/**
	 * Procura, recursivamente, um método pelo nome à partir da classe base
	 * informada.
	 * 
	 * @param clazzBase
	 *            A classe por onde a procura deve começar.
	 * @param methodName
	 *            O nome do método a ser encontrado.
	 * @param argumentList
	 *            A lista dos tipos dos argumentos.
	 * @return Uma referência reflexiva ao método indicado.
	 * @throws SecurityException
	 *             No caso de haver um <code>SecurityManager</code> instalado,
	 *             e ele negar acesso ao método
	 *             <code>java.lang.Class.getDeclaredField(java.lang.String)</code>.
	 * @throws NoSuchMethodException
	 *             Caso o método solicitado não seja encontrado na hierarquia da
	 *             classe base.
	 * @see Class#getDeclaredMethod(String, Class...)
	 */
	public Method searchMethodInHierarchy(Class<?> clazzBase,
			String methodName, Class<?>... argumentList)
			throws SecurityException, NoSuchMethodException {
		for (Class<?> clazz = clazzBase; clazz != null; clazz = clazz
				.getSuperclass())
			try {
				return clazz
						.getDeclaredMethod(methodName, argumentList);
			} catch (NoSuchMethodException methodException) {
			}
		throw new NoSuchMethodException(methodName);
	}

	/**
	 * Obtém um vetor de referências reflexivas a todos os métodos (ou <i>field</i>s)
	 * não sobrescritos e não-ofuscados contidos na hierarquia da classe base
	 * informada.
	 * 
	 * @param classBase
	 *            A classe por onde a procura deve começar.
	 * @return O vetor de referências reflexivas.
	 * @see #getAllMethodsInHierarchy(Class, boolean)
	 * @see Class#getDeclaredMethods()
	 */
	public Method[] getAllMethodsInHierarchy(Class<?> classBase) {
		return getAllMethodsInHierarchy(classBase, false);
	}

	/**
	 * Obtém um vetor de referências reflexivas a todos os métodos (ou <i>field</i>s),
	 * incluindo ou não os métodos sobrescritos e ofuscados contidos na
	 * hierarquia da classe base informada.
	 * 
	 * @param classBase
	 *            A classe por onde a procura deve começar.
	 * @param includeObfuscatedFields
	 *            Se <code>true</code>, inclui os métodos ofuscados. Senão,
	 *            não.
	 * @return O vetor de referências reflexivas.
	 * @see #getAllMethodsInHierarchy(Class)
	 * @see Class#getDeclaredMethods()
	 */
	public Method[] getAllMethodsInHierarchy(Class<?> classBase,
			boolean includeObfuscatedFields) {
		return getAllMethodsInHierarchy(classBase, true,
				includeObfuscatedFields);
	}

	/**
	 * Obtém um vetor de referências reflexivas a todos os métodos (ou <i>field</i>s),
	 * incluindo ou não os métodos sobrescritos e ofuscados contidos na
	 * hierarquia da classe base informada.
	 * 
	 * @param classBase
	 *            A classe por onde a procura deve começar.
	 * @param includeNotMapped
	 *            Se <code>false</code>, inclui apenas os métodos declarados
	 *            em classes anotadas como {@link javax.persistence.Entity} ou
	 *            {@link javax.persistence.MappedSuperclass}. Caso contrário,
	 *            inclui todos os métodos, sem distinção.
	 * @param incluirCamposOfuscados
	 *            Se <code>true</code>, inclui os métodos ofuscados. Senão,
	 *            não.
	 * @return O vetor de referências reflexivas.
	 * @see #getAllMethodsInHierarchy(Class)
	 * @see Class#getDeclaredMethods()
	 */
	public Method[] getAllMethodsInHierarchy(Class<?> classBase,
			boolean includeNotMapped, boolean includeObfuscatedFields) {
		final Map<String, Method> methodMap = new HashMap<String, Method>();
		for (Class<?> clazz = classBase; clazz != null; clazz = clazz
				.getSuperclass())
			if (includeNotMapped
					|| (clazz
							.isAnnotationPresent(javax.persistence.Entity.class) || clazz
							.isAnnotationPresent(javax.persistence.MappedSuperclass.class)))
				for (Method metodo : clazz.getDeclaredMethods()) {
					String methodName = metodo.getName();
					if (includeObfuscatedFields
							|| !methodMap.containsKey(methodName))
						methodMap.put(methodName, metodo);
				}
		final Collection<Method> methods = methodMap.values();
		return methods.toArray(new Method[methods.size()]);
	}
	
	/**
	 * Verify generic type (collection property declaration)
	 * @param pd Collection Property 
	 * @param mainClass Class that contains the collection
	 * @return Class
	 */
	public Class verifyGenericType(PropertyDescriptor pd, Class mainClass) throws PlcException {
		
		try {
		
			Field field = mainClass.getDeclaredField(pd.getName());
			
			 Type type = field.getGenericType();        
			 if (type instanceof ParameterizedType) {         
				 ParameterizedType pt = (ParameterizedType) type;
				 
				return (Class) pt.getActualTypeArguments()[0];
			 }

			
		} catch (Exception e) {
			throw new PlcException("jcompany.erro.generico", new Object[] {
					"verifyGenericType", e }, e, log);
		}
		
		throw new PlcException("#Não foi possível verificar tipo de objeto em coleção para "+pd.getName());
	}
}