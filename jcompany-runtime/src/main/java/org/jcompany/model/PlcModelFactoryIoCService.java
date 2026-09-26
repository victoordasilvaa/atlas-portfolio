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
package org.jcompany.model;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.annotation.PlcImplementation;
import org.jcompany.commons.aop.PlcAopBaseCallback;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcEjbHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.commons.helper.PlcIoCHelper;
import org.jcompany.commons.ioc.PlcBaseFactoryIoCService;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigModelTechnology;
import org.jcompany.config.commons.PlcConfigPackage;
import org.jcompany.config.commons.PlcConfigSuffixClass;
import org.jcompany.persistence.PlcBaseDAO;
import org.jcompany.persistence.PlcContextManager;
import org.jcompany.persistence.PlcPersistenceLocator;


/**
 * jCompany 3.0. Serviços de criação de Business Component. Utilizados pelo PlcBCLocator e passível
 * de especialização. Para tanto, registrar um descendente no PlcBCLocator em um estágio inicial 
 * da aplicação
 * @since jCompany 3.0
 * @version $Id: PlcModelFactoryIoCService.java,v 1.1 2006/07/24 13:43:13 alvim Exp $
*/
@SuppressWarnings("serial")
public class PlcModelFactoryIoCService extends PlcBaseFactoryIoCService {
	
	 protected static Logger log = Logger.getLogger(PlcModelFactoryIoCService.class);
	 protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);

	 protected static PlcPersistenceLocator INSTANCE_DAO = PlcPersistenceLocator.getInstance();
	 
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
	 * @param classManagerOrEntity Classe de BO ou ENTITY
	 */
	public IPlcManager registerIoC(Class classManagerOrEntity, Map<Class, Object> cache, IPlcManager managerDefault,
			PlcAopBaseCallback aopBaseCallback) throws PlcException {
	
		log.debug("############### Entered in registerIoC");
		
		// Se classe-base para IoC for um Value Object
		if (PlcEntityHelper.getInstance().isEntity(classManagerOrEntity)) {
			// Se passou classe ENTITY, usa associação de convenção para achar DAO

			// Ou contém o nome de uma classe ou null, se anotação não existir
		    String nameClassBC = registerIoCByAnnotation(classManagerOrEntity);

		    if (nameClassBC == null)
		    	nameClassBC = registrerIoCByName(classManagerOrEntity.getName());

		    // Se continou nulo, então devolve BO padrão
		    if (nameClassBC == null) {
		    	cache.put(classManagerOrEntity, managerDefault);
		    	return managerDefault;
		    } else  {
		    	
		    	IPlcManager plcManager=null;
		    	Class classBC=null;
		        try {
		        	classBC = Class.forName(nameClassBC);
		        } catch (Exception e) {
		        	// Se ocorreu excecao é porque não há classe com o nome obtido (neste caso via nomenclatura),
		        	// então assume que a classe base será atendida pelo BC padrão
		        	// Deste modo, da próximo vez os algoritmos são dispensados
		        	cache.put(classManagerOrEntity, managerDefault);
		            return managerDefault;
		        }
		        
		        if(PlcConfigHelper.getInstance().get(PlcConfigModelTechnology.class).technology() == PlcConfigModelTechnology.Technology.EJB3) {
		        	plcManager = (IPlcManager)PlcEjbHelper.getInstance().lookupEJB(classBC.getSimpleName());
		        	if(plcManager != null) {
		        		cache.put(classManagerOrEntity, plcManager);
		        		cache.put(plcManager.getClass(), plcManager);
		        		return plcManager;
		        	} else {
//		        		 Se ocorreu excecao é porque não há classe com o nome obtido (neste caso via nomenclatura),
			        	// então assume que a classe base será atendida pelo BC padrão
			        	// Deste modo, da próximo vez os algoritmos são dispensados
			        	cache.put(classManagerOrEntity, managerDefault);
			            return managerDefault;
		        	}
		        }
		        
	        	plcManager = (PlcBaseManager) registerIoCCreateWithDI(classBC, cache, aopBaseCallback, getLevelAop());
	            cache.put(classManagerOrEntity,plcManager);
	            cache.put(plcManager.getClass(),plcManager);
	            return plcManager;
		      
		    }
		}  else if (PlcBaseBC.class.isAssignableFrom(classManagerOrEntity)) {

			 try {
				 // Obs. A chamada inicialmente direta de um BO, e subsequentemente de um ENTITY, pode gerar outra
				 // instancia, mas que neste caso ocupará a chave inicial da primeira, nao trazendo problemas
				
		            PlcBaseManager plcBO = (PlcBaseManager) registerIoCCreateWithDI(classManagerOrEntity, cache, aopBaseCallback, getLevelAop());
		            cache.put(classManagerOrEntity,plcBO);
		            return plcBO;
		            
	          } catch (Exception e) {
	        	  // Neste caso como a classe é a própria passada, não pode haver exceções no instanciamento.
				throw new PlcException("jcompany.error.generic",
						new Object[] { "registerIoC", e }, e,log);
			  }

		}

	   throw new PlcException("#Internal Error. It's not possible to locate a Manager for class  "+classManagerOrEntity.getName()
			   +", probably this class is not following jCompany patterns.");
	
	}
	
	
	
	/**
	 * jCompany 3.0 Registra com base em anotações ou IoC padrão por convenção de nomenclatura
	 * @param classAS Classe de AS
	 */
	public PlcBaseAS registerIoCAS(Class classAS, Map<Class, Object> cache, IPlcManager managerDefault,
			PlcAopBaseCallback aopBaseCallback) throws PlcException {
	
		log.debug("############### Entered in registerIoCAS");

		if (PlcBaseBC.class.isAssignableFrom(classAS)) {

			try {
				// Obs. A chamada inicialmente direta de um BO, e subsequentemente de um ENTITY, pode gerar outra
				// instancia, mas que neste caso ocupará a chave inicial da primeira, nao trazendo problemas

				PlcBaseAS plcAS = (PlcBaseAS) registerIoCCreateWithDI(classAS, cache, aopBaseCallback, getLevelAop());
				cache.put(classAS,plcAS);
				return plcAS;

			} catch (Exception e) {
				// Neste caso como a classe é a própria passada, não pode haver exceções no instanciamento.
				throw new PlcException("jcompany.error.generic",
						new Object[] { "registerIoCAS", e }, e,log);
			}

		}

	   throw new PlcException("#Internal Error. It's not possible to locate a AS for class "+classAS.getName()
			   +", probably this class is not following jCompany patterns.");
	
	}
	
	/**
	 * instancia classe com DI e armazena no cache antes de devolver.
	 */
	public Object registerIoC(Class clazz, Map<Class,Object> cache, PlcAopBaseCallback aopBaseCallback) throws PlcException {
		Object obj = registerIoCCreateWithDI(clazz, cache, aopBaseCallback, getLevelAop());
		cache.put(clazz,obj);
		return obj;
	}
	
	/**
	 * Procura nome do BC (No caso, BOs), para atendimento ao ENTITY passado, seguindo 
	 * convenções de nomenclatura padrão do jCompany
	 * @param className
	 * @return Nome candidato para BO. Pode não existir, o que deverá ser tratado posteriormente no instanciamento
	 */
	public String registrerIoCByName(String className) throws PlcException {
		
		log.debug("############### Entered in registrerIoCByName");
		String classManagerName = null;
		
		try {
	
			PlcConfigPackage plcConfigPackage = PlcConfigHelper.getInstance().get(PlcConfigPackage.class);
			if (!(className.indexOf(plcConfigPackage.entityPackage())>-1) && 
					!(className.indexOf(plcConfigPackage.commons())>-1)) {
		          throw new PlcException("#Internal Erroro. As entity "+className+
				        " is not in default package "+plcConfigPackage.entityPackage()+
				        ", the jCompany  Service Locator wasn't able to locale a default service from it.");
			}
	
			if (className.indexOf(plcConfigPackage.entityPackage())>-1)
				// original:  managerClassName = nomeClasse.replaceAll("\\.vo\\.",".modelo.");
				classManagerName = StringUtils.replaceOnce(className,
						plcConfigPackage.entityPackage(),
						plcConfigPackage.model());
			if (className.indexOf(plcConfigPackage.commons())>-1)
				// managerClassName = nomeClasse.replaceAll("\\.comuns\\.",".modelo.");
				classManagerName = StringUtils.replaceOnce(className,
						plcConfigPackage.commons(),
						plcConfigPackage.model());
	
	
	      	 PlcConfigSuffixClass plcConfigSuffixClass = PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class);
	    	 String suffixDefaultEntity = plcConfigSuffixClass.entitySuffix();
			if (className.endsWith(suffixDefaultEntity) && !suffixDefaultEntity.equals(""))
				classManagerName = classManagerName.replaceAll(
						plcConfigSuffixClass.entitySuffix(),
						plcConfigSuffixClass.entityManager());
			else
				classManagerName = classManagerName+plcConfigSuffixClass.entityManager();
	
	
			return classManagerName;
		
		} catch (Exception e) {
			// Neste caso preserva o retorno com null para manter compatibilidade. Rever oportunamente.
			return null;
		}
	}
	/**
	 * jCompany 3.0 Pega nome do serviço no ENTITY como anotação, se existir, ou default null, caso contrário
	 * @param clazz Classe
	 * @return nome da classe BC para ser instanciada ou null, se anotação não existir.
	 */
	protected String registerIoCByAnnotation(Class clazz) throws PlcException {
		
		log	.debug("############### Entered in registerIoCByAnnotation");
		
		// Verifica se nome da classe de BO para atendimento v
		return PlcAnnotationHelper.getInstance().getAnnotationIoCNameBC(clazz);
		
	}
	
	/**
	 * jCompany 3.0 Injeção de Dependência. Procura uma classe do type no cache e, se não existir, instancia
	 * recursivamente injetando suas dependências.
	 * @param dependency Classe 
	 * @param cache 
	 * @param annotations 
	 * @return Instância criada ou reutilizada
	 */
	protected Object createWithDIResolveApi(Class dependency, Map<Class, Object> cache, Annotation[] annotations) 
			throws PlcException {
		
		log.debug("############### Entered in createWithDIResolveApi");
		
		// BO ou AS
		if (PlcBaseBC.class.isAssignableFrom(dependency)) {
		
			Iterator i = cache.keySet().iterator();
	
			while (i.hasNext()) {
			
				Class key = (Class) i.next();
				
				if (dependency.getName().equals(key.getName()))
					return cache.get(key);
	
			}
			
			// Se chegou aqui nao encontrou, entao instancia com recursividade para injecao de dependencias
			return PlcModelLocator.getInstance().get(dependency);
		
		} else if (PlcBaseDAO.class.isAssignableFrom(dependency)) {
			
			return INSTANCE_DAO.get(dependency);
			
		} else if (dependency.isInterface()) {
			
			// É uma interface e tem anotacao de fábrica
			try {
				PlcImplementation implementation = null;
				for (Annotation annotation : annotations) {
					if (PlcImplementation.class.isAssignableFrom(annotation.annotationType())) {
						implementation = (PlcImplementation)annotation;
						break;
					}
				}
				
				Class[] classFactoryImplementation = PlcAnnotationHelper.getInstance().getClassImplementation(implementation);
				
				//Class serviceLocatorClass = PlcAnnotationHelper.getInstance().getAnotacaoServiceLocator(annotations);
				
				if (classFactoryImplementation == null || (classFactoryImplementation[0]==null && classFactoryImplementation[1]==null)) {
					// Tenta localizar implementacao por convenção de nomenclatura
					Class implementationInterfaceClass = createImplementationByNameConvention(dependency);
					if (implementationInterfaceClass==null) {
						log.fatal("Error trying to intance a implementatio to interface "+dependency.getName());
						throw new PlcException("jcompany.error.service.locator.not.declared", new Object[] {dependency.getName()});
					} else {
						// Instancia e armazena no caching.
						Object implementationInterfaceObject = implementationInterfaceClass.newInstance();
						cache.put(dependency,implementationInterfaceObject);
						return implementationInterfaceObject;
					}
				} else if (classFactoryImplementation[0]!=null) {
					return PlcModelLocator.getInstance().get(classFactoryImplementation[0]);
				} else if (classFactoryImplementation[1]!=null) {
					return PlcIoCHelper.getInstance().getImplementationByFactory(dependency, classFactoryImplementation[1]);
				}
				
			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {"createWithDIResolveApi", e }, e, log);
			} 
		}

		throw new PlcException("jcompany.errors.dependency.injection.invalidtype",new Object[]{dependency.getName()});
		
	}
	
	/**
	 * Tentar obter uma implementação para uma interface, utilizando convenções de nomenclatura
	 * @param interface Definição de Interface
	 * @return Classe de Implementação, se existir ou null, se nao existir.
	 */
	private Class createImplementationByNameConvention(Class interfaceDI) {
		
		Class implementationClass=null;

		// Tenta sem "I" inicial, somente, no mesmo pacote
		String implementationName = interfaceDI.getPackage().getName()+"."+interfaceDI.getSimpleName().substring(1);
		try {
			implementationClass = Class.forName(implementationName);
		} catch (ClassNotFoundException e) {
			// Tenta sem "I" e com "Impl" ao final, no mesmo pacote
			implementationName = interfaceDI.getPackage().getName()+"."+interfaceDI.getSimpleName().substring(1)+"Impl";
			try {
				implementationClass = Class.forName(implementationName);
			} catch (ClassNotFoundException e1) {
				// Tenta, se for DAO, com .jpa. ou .hibernate. como pacotes subordinados, sem o "I" inicial
				if (interfaceDI.getName().endsWith("DAO"))
					implementationName = interfaceDI.getPackage().getName()+".jpa."+interfaceDI.getSimpleName().substring(1);
				try {
					implementationClass = Class.forName(implementationName);
				} catch (ClassNotFoundException e2) {
					if (interfaceDI.getName().endsWith("DAO"))
						implementationName = interfaceDI.getPackage().getName()+".hibernate."+interfaceDI.getSimpleName().substring(1);
					try {
						implementationClass = Class.forName(implementationName);
					} catch (ClassNotFoundException e3) {
						// Não conseguiu um padrão, então deixa retornar nulo.
					}
				}
			}
		}

		
		return implementationClass;
	}

	/**
	 * jCompany 3.0 Não faz restrições a Business Components, mas pode ser especializado para tanto
	 * @param classMain Classe Principal
	 * @param dependency Classe Dependente
	 * @throws PlcException Disparar no caso do instanciamento não ser padrão
	 */
	@Override
	protected void createWithDIVerifyRestrictionsApi(Class classMain, Class dependency) 
										throws PlcException {
		
		log.debug("############### Entered in createWithDIVerifyRestrictionsApi");
		
	}


}