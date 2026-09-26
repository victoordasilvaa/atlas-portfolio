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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopManager;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.ioc.PlcBaseLocator;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.model.PlcBaseManager;
import org.jcompany.persistence.hibernate.PlcBaseHibernateDAO;
import org.jcompany.persistence.hibernate.service.PlcQBEHibernateService;


/**
 * jCompany 2.x. Service Locator. Singleton. Implementa este Design Pattern para
 * otimizar localização do Data Acess Object que irá atender a uma requisição,
 * baseando-se em uma classe DAO mas também podendo pegar por nome de BO ou ENTITY.
 * @author alvim
 * @since jCompany 2.0
 * @version $Id: PlcPersistenceLocator.java,v 1.2 2006/08/25 16:07:20 alvim Exp $
 */
public class PlcPersistenceLocator extends PlcBaseLocator {

	/**
	 * Area de caching de objetos em IoC
	 */
	private static Map<Class,Object> cache = new HashMap<Class,Object>();
	
	/**
	 * @return Retorna cache.
	 */
	public static Map<Class, Object> getCache() {
		return cache;
	}

	/**
	 * Limpa o HasMap de caching. Apenas para uso administrativo em tempo de desenvolvimento.
	 * Importante: Não deveria ser utilizado em produção.
	 * @since jCompany 3.02
	 */
	public void clearCache() throws PlcException {
		cache.clear();
		daoDefault = (PlcBaseDAO) new PlcBaseHibernateDAO(new PlcQBEHibernateService());
		daoDefault = getDaoDefault();
		
	}

	protected static Logger log = Logger.getLogger(PlcPersistenceLocator.class);
	private static PlcPersistenceLocator INSTANCE = new PlcPersistenceLocator();
	private PlcPersistenceLocator() { }
	public static PlcPersistenceLocator getInstance(){
   		return INSTANCE;
	}
	
	 /**
     * Serviço de IoC para DAO
     */
    private PlcPersistenceFactoryIoCService ioCService;
    /**
     * Serviço de AOP para DAO 
     */
    private PlcAopPersistenciaCallbackService aopService;
    
	/**
	 * @return  retorna o ioCService.
	 */
	private PlcPersistenceFactoryIoCService getIoCService() {
		if (ioCService==null)
			ioCService = new PlcPersistenceFactoryIoCService();
		return ioCService;
	}
	
	/**
	 * jCompany 3.0 Permite o registro de uma classe especializada de PlcBCFactoryIoCService para
	 * algoritmos de IoC e ID.
	 * @param ioCService Servico descendente de PlcBCFactoryIoCService a ser usado
	 */
	public void setIoCService(PlcPersistenceFactoryIoCService ioCService) {
		this.ioCService = ioCService;
	}
	
	/**
     * Dao utilizado como default para Hibernate.
     */
    private PlcBaseHibernateDAO daoPadraoHibernate =  new PlcBaseHibernateDAO(new PlcQBEHibernateService());
	
	public PlcBaseHibernateDAO getDaoDefaultHibernate() {
		return daoPadraoHibernate;
	}

	/**
     * Dao utilizado como default caso não seja localizado o específico.
     * TODO Reuso de instancia PlcQBEService entre DAO Padrao e especificos.
     */
    private IPlcDAO daoDefault = daoPadraoHibernate;

        
    /**
	 * jCompany 3.0 Permite que se altere o DAO Concreto Padrão, que é utilizado para recuperação
	 * de mensagens de erros e outros tratamentos genéricos da persistencia.
	 * @param daoDefault Novo DAO Padrão
	 */
	public void setDaoDefault(PlcBaseDAO daoDefault) {
		this.daoDefault = daoDefault;
	}

	public void setDaoDefault(IPlcDAO daoDefault) {
		this.daoDefault = daoDefault;
	}
	
	/**
	 * jCompany 3.0. Recebe uma classe DAO ou de BO ou de ENTITY e devolve o objeto único DAO correpondente do caching.
	 * Se receber null na classe, devolve implementação default (na 3.0, implementação de DAO para hibernate)<br>
	 * Importante: Se enviar um BO ou ENTITY, tenta usar um padrão de nomenclatura para localizar um DAO correspondente.
	 * Exemplo: Se passou "com.empresa.app.vo.TesteVO", internamente tenta achar "com.empresa.app.persistencia.TesteDAO".
	 * @return instância de DAO
	 */
	public Object get(Class classDAOorManagerorEntity) throws PlcException {

		if (logModelo.isDebugEnabled())
			logModelo.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":get("+(classDAOorManagerorEntity!=null?classDAOorManagerorEntity.getName():"null")+"):"));

		// Se achou com nome alternativo (ENTITY ou BO), devolve imediatamente.
		if (classDAOorManagerorEntity == null ||  
				PlcBaseManager.class.equals(classDAOorManagerorEntity.getClass())) {
			
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+(classDAOorManagerorEntity!=null?classDAOorManagerorEntity.getName():"null")+"):default:"));

			return getDaoDefault();

		} else if (getCache().containsKey(classDAOorManagerorEntity)) {
			
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+(classDAOorManagerorEntity!=null?classDAOorManagerorEntity.getName():"null")+"):cache:"));

			return getCache().get(classDAOorManagerorEntity);
			
		} else {
			// Verifica se existe anotação de IoC para a classe.
			classDAOorManagerorEntity = PlcConfigHelper.getInstance().getAlternativeClass(classDAOorManagerorEntity);

			Object obj = getIoCService().registerIoC(classDAOorManagerorEntity,getCache(),getDaoDefault(),getAopService());
						
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+(classDAOorManagerorEntity!=null?classDAOorManagerorEntity.getName():"null")+"=>"+obj.getClass().getName()+"):IoC:"));

			return obj;
		}
		
	}

	/**
	 * @since jCompany 3.1
	 * @return Devolve DAO padrao, ajustando para AOP se foi alterado o nivel
	 */
	public IPlcDAO getDaoDefault() throws PlcException {
	
		//		 Instancia com AOP se já não o fez e se o nível for 1
		if (PlcContextManager.getContextEntity() != null && PlcContextManager.getContextEntity().getLevelAop()>0) {	
			// Se não for proxy - instancia como proxy
			if (daoDefault.getClass().getName().equals(PlcBaseHibernateDAO.class.getName()))
				setDaoDefault((PlcBaseDAO) PlcAopManager.createProxy(PlcBaseHibernateDAO.class,getAopService()));
		}
		
		   return daoDefault;
	}

	/**
	 * jCompany 2.7. Permite que o desenvolvedor registre, para cada DAO, o ENTITY correlato como chave.
	 * Tipicamente útil em cenários de Teste de Unidade, para isolamento via Classes Mock
	 */
	public void register(Class plcEntityOrManager,PlcBaseDAO plcDAO) {

	    log.debug("######## Entered register");

	    getCache().put(plcEntityOrManager,plcDAO);

	}
	/**
	 * @return Returns the aopService.
	 */
	public PlcAopPersistenciaCallbackService getAopService() {
		if (aopService==null)
			aopService = new PlcAopPersistenciaCallbackService();
		return aopService;
	}
	/**
	 * @param aopService The aopService to set.
	 */
	public void setAopService(PlcAopPersistenciaCallbackService aopService) {
		this.aopService = aopService;
	}

}
