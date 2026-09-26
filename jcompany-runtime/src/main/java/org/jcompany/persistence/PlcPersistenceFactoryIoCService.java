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
package org.jcompany.persistence;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopBaseCallback;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcEjbHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.commons.helper.PlcReflectionHelper;
import org.jcompany.commons.ioc.PlcBaseFactoryIoCService;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigModelTechnology;
import org.jcompany.config.commons.PlcConfigPackage;
import org.jcompany.config.commons.PlcConfigSuffixClass;
import org.jcompany.model.PlcBaseManager;
import org.jcompany.persistence.hibernate.PlcBaseHibernateDAO;


/**
 *  * @since jCompany 3.0 Serviços de criação de Business Component. Utilizados pelo PlcBCLocator e passível
 * de especialização. Para tanto, registrar um descendente no PlcBCLocator em um estágio inicial 
 * da aplicação
 * @version $Id: PlcPersistenceFactoryIoCService.java,v 1.1 2006/07/24 13:43:13 alvim Exp $
*/
@SuppressWarnings("serial")
public class PlcPersistenceFactoryIoCService extends PlcBaseFactoryIoCService {
	
	 protected static Logger log = Logger.getLogger(PlcPersistenceFactoryIoCService.class);
	 protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
	 
	 /**
	  * @return Devolve o nível do AOP correntemente incluido pelo usuário
	  */
	 public int getLevelAop() {
		 PlcBaseContextVO context = PlcContextManager.getContextEntity();
		 int levelAop = 0;
	     if (context != null)
	       	levelAop = context.getLevelAop();
	     return levelAop;
	 }
	 
	/**
	 * jCompany 3.0 Registra com base em anotações ou IoC padrão por convenção de nomenclatura
	 * @return Instancia de Classe de BO correspondente
	 */
	public Object registerIoC(Class classDAOorManagerorEntity, Map<Class, Object> daoCache, IPlcDAO daoDefault,
			PlcAopBaseCallback aopCallbackService) throws PlcException {
		
		log.debug("############### Entered in registerIoC");
					
			if (PlcEntityHelper.getInstance().isEntity(classDAOorManagerorEntity) ||
					PlcBaseManager.class.isAssignableFrom(classDAOorManagerorEntity)) {
				// Se passou classe ENTITY, usa associação de convenção para achar DAO
	
				// Ou contém o nome de uma classe ou null, se anotação não existir
			    String nameClassDAO = null;

			   // Se tiver anotação, usa
			   if (PlcEntityHelper.getInstance().isEntity(classDAOorManagerorEntity))
		    		nameClassDAO = registerIoCByAnnotation(classDAOorManagerorEntity);
		       
			   // Somente testa convençoes se nao tiver anotacao explicita no ENTITY
			   if (nameClassDAO == null) {
			       if (PlcEntityHelper.getInstance().isEntity(classDAOorManagerorEntity))
			    	   nameClassDAO = registerIoCByName(classDAOorManagerorEntity.getName());
			       else if (PlcBaseManager.class.isAssignableFrom(classDAOorManagerorEntity)) {
			    	   nameClassDAO = registerIoCByNameManager(classDAOorManagerorEntity.getName());
			       } else if (PlcBaseDAO.class.isAssignableFrom(classDAOorManagerorEntity))
			    	   nameClassDAO = classDAOorManagerorEntity.getName();
			       else
			    	   throw new PlcException("jcompany.error.dependence.injection.dao.invalid",new Object[]{classDAOorManagerorEntity.getName()});
			   }
			   
			   Class classDAO = null;
			   try {
				   classDAO = Class.forName(nameClassDAO);
			   } catch (Exception e) {
				   //Se ocorreu excecao é porque não há classe com o nome obtido (neste caso via nomenclatura),
				   //então assume que a classe base será atendida pelo BC padrão
		           // Deste modo, da próximo vez os algoritmos são dispensados
				   daoCache.put(classDAOorManagerorEntity, daoDefault);
				   return daoDefault;
			   }
			   
			   if(PlcConfigHelper.getInstance().get(PlcConfigModelTechnology.class).technology()== PlcConfigModelTechnology.Technology.EJB3) {
		        	Object obj = PlcEjbHelper.getInstance().lookupEJB(classDAO.getSimpleName());
		        	if(obj != null) {
		        		daoCache.put(classDAOorManagerorEntity, obj);
		        		daoCache.put(obj.getClass(),obj);
		        		return obj;
		        	} else {
//		        		 Se ocorreu excecao é porque não há classe com o nome obtido (neste caso via nomenclatura),
			        	// então assume que a classe base será atendida pelo BC padrão
			        	// Deste modo, da próximo vez os algoritmos são dispensados
		        		daoCache.put(classDAOorManagerorEntity, daoDefault);
						return daoDefault;
		        	}
		        }
			   
				try {
	
			       Object obj = registerIoCCreateWithDI(classDAO,daoCache,aopCallbackService,getLevelAop());
			       			   	
				   // Injeta serviços por setters
				   injectDependencesBySetter(obj);
					
			       daoCache.put(classDAOorManagerorEntity,obj);
			
				   return obj;
				   
				} catch (Exception e) {
					// Se nao pode instanciar, é porque não existe. Então asssume DAO Padrão
					
					// Injeta serviços por setters
					injectDependencesBySetter(daoDefault);
					
					daoCache.put(classDAOorManagerorEntity,daoDefault);
			
					return daoDefault;
				}
	
			}  else {
				
				try {
					
					// É a propria classe
					Object obj = registerIoCCreateWithDI(classDAOorManagerorEntity,daoCache,aopCallbackService,getLevelAop());
					
					// Injeta serviços por setters
					injectDependencesBySetter(obj);
					
					daoCache.put(classDAOorManagerorEntity,obj);
					
					return daoCache.get(classDAOorManagerorEntity);
					
				} catch (Exception e) {
					// Deveria conseguir instanciar
					throw new PlcException("jcompany.error.generic", new Object[] {
							"registerIoC", e }, e, log);
				}
	
			}
	
	}
		
	/**
	 * Procura nome do BC (No caso, BOs), para atendimento ao ENTITY passado, seguindo 
	 * convenções de nomenclatura padrão do jCompany
	 * @param className
	 * @return Nome candidato para BO. Pode não existir, o que deverá ser tratado posteriormente no instanciamento
	 */
	protected String registerIoCByName(String className) throws PlcException {
		
		log.debug("############### Entered in registerIoCByName");
		
		String nameClasseDAORelated = null;
   	    
		PlcConfigSuffixClass plcConfigSuffixClass = PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class);
      	PlcConfigPackage plcConfigPackage = PlcConfigHelper.getInstance().get(PlcConfigPackage.class);
      	 
    	String entitySuffixDefault 	= plcConfigSuffixClass.entitySuffix();

		if (!className.endsWith(entitySuffixDefault) && 
			className.indexOf(plcConfigPackage.entityPackage())==-1) {
		   // jCompany Free nao precisa padrao
			nameClasseDAORelated =  className + plcConfigPackage.persistence();
		
		} else {
			// Se tiver sufixo padrao entra aqui
			className = StringUtils.replace(className,plcConfigPackage.entityPackage(),plcConfigPackage.persistence());
	    
			nameClasseDAORelated = StringUtils.replace(className,entitySuffixDefault,
	    		plcConfigSuffixClass.persistence());
		}
		
        if (log.isDebugEnabled())
        	log.debug("DAO finder is trying to locate a DAO class with name "+nameClasseDAORelated);
        
        return nameClasseDAORelated;
	}
	
	/**
	 * Procura nome do BC (No caso, BOs), para atendimento ao ENTITY passado, seguindo 
	 * convenções de nomenclatura padrão do jCompany
	 * @param className
	 * @return Nome candidato para BO. Pode não existir, o que deverá ser tratado posteriormente no instanciamento
	 */
	protected String registerIoCByNameManager(String className) throws PlcException {
		
		log.debug("############### Entered in registerIoCByNameManager");
				
     	 PlcConfigSuffixClass plcConfigSuffixClass = PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class);
      	 PlcConfigPackage plcConfigPackage = PlcConfigHelper.getInstance().get(PlcConfigPackage.class);
    	 String entitySuffixDefault = plcConfigSuffixClass.entitySuffix();

		if (!className.endsWith("PlcBaseConfigListener") && !className.endsWith(plcConfigSuffixClass.entityManager()) && 
				className.indexOf(plcConfigSuffixClass.entityManager()+"$$")==-1 &&
				className.indexOf(entitySuffixDefault)==-1)
			throw new PlcException("Internal Error. As entity "+className+
			        " doesn't follow the suffix pattern "+entitySuffixDefault+
			        " and package "+ entitySuffixDefault+", " +
			   		" jCompany Service Locator wasn't able to locate a default persistence service of it.");

		// Se for proxy tira o sufixo gerado pelo CBLib
		if (className.indexOf(plcConfigSuffixClass.entityManager()+"$$")>-1)
			className = className.substring(className.indexOf("$$"));
			
        className = StringUtils.replace(className,plcConfigPackage.model(),
        		plcConfigPackage.persistence());
	   
        return StringUtils.replace(className,plcConfigSuffixClass.entityManager(),
        		plcConfigSuffixClass.persistence());
	}
	
	/**
	 * jCompany 3.0 Pega nome do serviço no ENTITY como anotação, se existir, ou default null, caso contrário
	 * @param className Classe
	 * @return nome da classe BC para ser instanciada ou null, se anotação não existir.
	 */
	protected String registerIoCByAnnotation(Class className) throws PlcException {
		
		log	.debug("############### Entered in registerIoCByAnnotation");
		
		// Verifica se nome da classe de BO para atendimento v
		return PlcAnnotationHelper.getInstance().getAnnotationIoCNameDAO(className);
		
	}
	

	
	/**
	 * jCompany 3.0 Injeção de Dependência. Procura uma classe do type no cache e, se não existir, instancia
	 * recursivamente injetando suas dependências.
	 * @param dependence Classe 
	 * @return Instância criada ou reutilizada
	 */
	protected Object createWithDIResolveApi(Class dependence, Map<Class, Object> daoCache,
			Annotation[] annotations) throws PlcException {
		
		log.debug("############### Entered in createWithDIResolveApi");
		
		Iterator i = daoCache.keySet().iterator();

		while (i.hasNext()) {
		
			Class key = (Class) i.next();
			Object daoCached = daoCache.get(key);
			if (dependence.getName().equals(daoCached.getClass().getName()))
				return daoCached;

		}
		
		// Se chegou aqui nao encontrou, entao instancia com recursividade para injecao de dependencias
		return PlcPersistenceLocator.getInstance().get(dependence);
		
	}
	
	/**
	 * jCompany 3.0 A implementação padrão garante que DAOs somente chamam DAOs.
	 * Mas pode ser especializado nos descendetes para modificações
	 * @param classMain Classe Principal
	 * @param dependence Classe Dependente
	 * @throws PlcException Disparar no caso do instanciamento não ser padrão
	 */
	protected void createWithDIVerifyRestrictionsApi(Class classMain, Class dependence) 
										throws PlcException {
		
		log.debug("############### Entered in createWithDIVerifyRestrictionsApi");
		/**
		 * TODO alvim verificar se há a necessidade de verifica "(!PlcBaseHibernateDAO.class.isAssignableFrom(mainClass))"
		 */
		if ( (!PlcBaseDAO.class.isAssignableFrom(dependence))&&(!PlcBaseHibernateDAO.class.isAssignableFrom(classMain))) {
			
			throw new PlcException("jcompany.errors.dependence.injection.default.dao",
					new Object[]{classMain.getName(),dependence.getName()});
		
		}
		
	}
	
	/**
	 * @since jCompany 3.0
	 * Injeta dependências (Services), baseado em setters
	 * que seguem a convenção setServiceXXXXX. Usa o Service Locator corrente para reusar instancias injetadas.
	 * @param plcBase Objeto que irá receber injeções.
	 */
	public void injectDependencesBySetter(Object plcBase) throws PlcException {
		log.debug("############### Entered in injetaDependenciasPorSetter");
		
		PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(plcBase);
		for (PropertyDescriptor prop : properties) {
			Class<?> propertyType = prop.getPropertyType();
			if (propertyType!=null) {
				if (propertyType.getName().endsWith("Service")) {
					Method writeMethod = PlcReflectionHelper.getInstance().getSetterMethod(prop);
					if (writeMethod!=null) { //só vai verificar se tiver um setter
						if (writeMethod.getParameterTypes().length!=1) {
							log.warn("It's found property of service type ("+propertyType.getName()+"), but setter doens't have one and only one argument. " +
									"It's ignoring DI for this method: "+writeMethod.getName());
							return;
						}
						
						injectDependencesBySetterOne(plcBase, writeMethod);
					}
				}
			}
		}

	}
	
	/**
	 * @since jCompany 3.0 
	 * Injeta uma instancia (reutilizada via Service Locator) na classe informada, 
	 * investigando o método
	 * @param plcBase Classe de controle
	 * @param method Método setter a ser utilizado para injeção
	 */
	private void injectDependencesBySetterOne(Object plcBase, Method method) throws PlcException{
		log.debug("############### Entered in injectDependencesBySetterOne");
		
		if (method.getParameterTypes().length!=1) {
			log.warn("Setter doens't have one and only one argument. " +
						"It's ignoring DI for this method: "+method.getName());
			return;
		}
			
		// Executa porque somente tem um argumento. Assumindo que seja classe
		Class clazz = method.getParameterTypes()[0];
		//Se não for abstract
		if( ! Modifier.isAbstract(clazz.getModifiers())){
			Object pojo = PlcPersistenceLocator.getInstance().get(clazz);

			try {	

				if (log.isDebugEnabled())
					log.debug("==> Injecting class "+clazz.getName()+ " in DAO "+plcBase.getClass().getName());

				method.invoke(plcBase,new Object[]{pojo});

			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"injectDependencesBySetterOne", e }, e, log);
			}
		}
	}


}