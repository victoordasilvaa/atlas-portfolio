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
package org.jcompany.commons.ioc;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopBaseCallback;
import org.jcompany.commons.aop.PlcAopManager;


/**
 * Classe-Base para algoritmos de IoC e ID do jCompany em todas as camadas
 * @since jCompany 3.05
 */
public abstract class PlcBaseFactoryIoCService implements Serializable {

	 protected static Logger log = Logger.getLogger(PlcBaseFactoryIoCService.class);
	 protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
	 
	/**
	 * jCompany 3.0 Injeção de Dependência. Instancia classes incluindo suas dependencias recursivamente, 
	 * investigando os construtores. Baseado em "Create a Simple IoC Container Using Annotations" by Eugene Kuleshov.
	 */
	protected Object registerIoCCreateWithDI(Class mainClass, Map<Class,Object> cache, 
			PlcAopBaseCallback aopBaseCallback,int levelAop) throws PlcException {
		
		log.debug("############### Entered in registerIoCCreateWithDI");
		
		try {

			// Pega construtores. Se tiver mais de um dá erro
			Constructor[] constructors = mainClass.getDeclaredConstructors();
			
			
			//Só pode ter um construtor ou dois, sendo um deles o construtor default
			if ( (constructors.length>2 || //Nao pode ter mais que dois
					(constructors.length==2 && constructors[0].getParameterTypes().length>0 && constructors[1].getParameterTypes().length>0)) //Se for dois, um dos dois tem que ser o construtor default
				&& !mainClass.getName().equals("org.jcompany.persistence.hibernate.PlcBaseHibernateDAO"))
				throw new PlcException("jcompany.errors.dependence.injection",new Object[]{mainClass.getName()});
			
		    if (constructors.length==0)
		       return instanceByClass(mainClass,aopBaseCallback,levelAop);
			
			// Pega parametros do construtor
		    Constructor<?> constructor = constructors[0]; //Testa o primeiro construtor.
		    if (constructor.getParameterTypes().length==0 && constructors.length>1) //Se o primeiro for o construtor default, usa o segundo construtor.
		    	constructor = constructors[1];
		    Class[] dependenceClass = constructor.getParameterTypes();
			
		    // Se não declarou nenhuma dependência, aceita
		    if (dependenceClass.length==0)
		    	return instanceByClass(mainClass,aopBaseCallback,levelAop);
		    else {

		    	// Se tem classes como dependencia no construtor
		    	Object[] params = new Object[ dependenceClass.length];
		    	
		    	for (int i = 0; i < dependenceClass.length; i++) {
					
		    		// Se fugiu do padrao da arquitetura
		    		createWithDIVerifyRestrictionsApi(mainClass,dependenceClass[i]);
		    		
		    	    // Se passou, pode-se instanciar a dependencia	
		    		Object oneDependence = createWithDIResolveApi(dependenceClass[i],cache,
		    				constructor.getParameterAnnotations()[i]);
		    		params[i]=oneDependence;
		    		
				}
		    	
		    	// Instancia pelo construtor
		        Object obj = instanceByConstructor(mainClass,constructor,params,aopBaseCallback,levelAop);
		        
		        return obj;
		    }
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"registerIoCCreateWithDI", e }, e, log);
		}
	
	}
	
	/**
	 * jCompany 3.0 Único método que instancia um novo BC, considerando Proxies AOP para quando o nível for maior que zero.
	 */
	protected Object instanceByClass(Class clazz,PlcAopBaseCallback aopBaseCallback,int levelAop) throws PlcException {
		log.debug("############### Entered in instanciaBC");
		try {
			if (levelAop>0 || (levelAop==0 && clazz.getName().indexOf("Facade")>-1))
				return PlcAopManager.createProxy(clazz,aopBaseCallback);
			else
				return clazz.newInstance();			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"instanceByClass", e }, e, log);
		}
	}
	
	/**
	 * jCompany 3.0 Único método que instancia um novo BC, considerando Proxies AOP para quando nível por maior que zero
	 */
	protected Object instanceByConstructor(Class clazz,Constructor constructor,Object[] params,
			PlcAopBaseCallback aopBaseCallback,int levelAop) throws PlcException {
		log.debug("############### Entered in instanceByConstructor");
		try {
			if (levelAop>0 || (levelAop==0 && clazz.getName().indexOf("Facade")>-1))
				return PlcAopManager.createProxyByConstructor(clazz,constructor.getParameterTypes(),params,aopBaseCallback);
			else
				return constructor.newInstance(params);			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"instanceByConstructor", e }, e, log);
		}
	}
	


	protected abstract Object createWithDIResolveApi(Class dependence, Map<Class, Object> cache,
			  Annotation[] annotations) throws PlcException;

	
	/**
	 * jCompany 3.0 Não faz restrições a Business Components, mas pode ser especializado para tanto
	 * @param mainClass Classe Principal
	 * @param dependence Classe Dependente
	 * @throws PlcException Disparar no caso do instanciamento não ser padrão
	 */
	protected abstract void createWithDIVerifyRestrictionsApi(Class mainClass, Class dependence) 
										throws PlcException;
	
}
