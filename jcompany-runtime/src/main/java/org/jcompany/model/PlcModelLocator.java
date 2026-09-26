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
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopManager;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.ioc.PlcBaseLocator;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.persistence.PlcContextManager;


/**
 * jCompany 3.0. Service Locator. Singleton. Implementa este Design Pattern para
 * otimizar localização do Business Object que irá atender a uma requisição,
 * baseando-se no Value Object recebido. A regra é:<p>
 *  1. Se o ENTITY possuir um nome de classe de BO explicitamente indicado, através
 *  de anotação PlcIoC(nomeClasseBC) então utiliza esta classe.<br>
 *  2. Se não possuir, verifica se existe alguma classe de BO com o mesmo nome
 *  do ENTITY, porém com sufixo BO e a utiliza. <br>
 *  3. Não ocorrendo nenhum dos casos acima, utiliza o PlcBaseConfigListener, ancestral
 *  para fazer as regras.<p>
 *  Uma vez que o BO é localizado, ele tem sua referência armazenada em um HashMap
 *  em duas entradas, com sendo o ENTITY e também o BO. Apos o registro, a regra acima
 *  não é mais averiguada!
 * Novo! Esta versão realiza injeçao de dependência com base no construtor.
 * @since jCompany 3.0
 * @version $Id: PlcModelLocator.java,v 1.2 2006/08/25 16:07:20 alvim Exp $
 */
public class PlcModelLocator extends PlcBaseLocator {

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
		managerDefault = new PlcBaseManager();
		managerDefault = getManagerDefault();
	}

	protected static Logger log = Logger.getLogger(PlcModelLocator.class);
	private static PlcModelLocator INSTANCE = new PlcModelLocator();
	private PlcModelLocator() { }
	public static PlcModelLocator getInstance(){
   		return INSTANCE;
	}
	
	 /**
     * Serviço de IoC utilizado para BCs
     */
    private PlcModelFactoryIoCService ioCService;
    /**
     * Serviço de AOP para DAO 
     */
    private PlcAopModelCallbackService aopService;
    
	/**
	 * @return  retorna o ioCService.
	 */
	public PlcModelFactoryIoCService getIoCService() {
		if (ioCService==null)
			ioCService = new PlcModelFactoryIoCService();
		return ioCService;
	}
	
	/**
	 * jCompany 3.0 Permite o registro de uma classe especializada de PlcBCFactoryIoCService para
	 * algoritmos de IoC e ID.
	 * @param ioCService Servico descendente de PlcBCFactoryIoCService a ser usado
	 */
	public void setIoCService(PlcModelFactoryIoCService ioCService) {
		this.ioCService = ioCService;
	}
    
	/**
	 * BO injetado pelo container se existir
	 */
	IPlcManager plcManager;
    
    /**
     * BO utilizado como default caso não seja localizado o específico.
     */
    private IPlcManager managerDefault = new PlcBaseManager();
    

	/**
	 * jCompany 3.0 Permite que se altere o DAO Concreto Padrão, que é utilizado para recuperação
	 * de mensagens de erros e outros tratamentos genéricos da persistencia.
	 */
	public void setManagerDefault(PlcBaseManager bcDefault) {
		this.managerDefault = bcDefault;
	}
	
	public void setManagerDefault(IPlcManager bcDefault) {
		this.plcManager = bcDefault;
	}
	
	/**
	 * Recebe uma classe de BO ou de ENTITY e devolve o objeto único BO correpondente do caching.<br>
	 * Importante: Se enviar um ENTITY, tenta usar um padrão de nomenclatura para localizar um BO correspondente.
	 * Exemplo: Se passou "com.empresa.app.vo.TesteVO", internamente tenta achar "com.empresa.app.modelo.TesteBO".
	 * @return instancia de BO
	 */
	public IPlcManager get(Class classManagerOrEntity) throws PlcException {

		if (logModelo.isDebugEnabled())
			logModelo.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":get("+(classManagerOrEntity!=null?classManagerOrEntity.getName():"null")+"):"));

		log.debug("############### Entered in getBusinessObject");

		if (classManagerOrEntity == null) {
			
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+(classManagerOrEntity!=null?classManagerOrEntity.getName():"null")+"):padrao:"));

			return getManagerDefault();
				
		} else if (getCache().containsKey(classManagerOrEntity)) {

			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+(classManagerOrEntity!=null?classManagerOrEntity.getName():"null")+"):cache:"));

			return (IPlcManager) getCache().get(classManagerOrEntity);
			
		} else {	
			
			//Verifica se existe anotação de IoC para a classe.
			classManagerOrEntity = PlcConfigHelper.getInstance().getAlternativeClass(classManagerOrEntity);
			
			IPlcManager objectFound = getIoCService().registerIoC(classManagerOrEntity,getCache(),getManagerDefault(),getAopService());
			
			if (logModelo.isDebugEnabled())
				logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":get("+classManagerOrEntity.getName()+"=>"+objectFound.getClass().getName()+"):IoC:"));

			return objectFound;
		}

	}
	
	/**
	 * Recebe uma classe de BO ou de ENTITY e devolve o objeto único BO correpondente do caching.<br>
	 * Importante: Se enviar um ENTITY, tenta usar um padrão de nomenclatura para localizar um BO correspondente.
	 * Exemplo: Se passou "com.empresa.app.vo.TesteVO", internamente tenta achar "com.empresa.app.modelo.TesteBO".
	 * @return instancia de BO
	 */
	public PlcBaseAS getAS(Class classAS) throws PlcException {

		log.debug("############### Entered in getAS");

		if (classAS == null) {
			//TODO Retornar o AS padrão
			return null;
				
		} else if (getCache().containsKey(classAS))
			return (PlcBaseAS) getCache().get(classAS);
		else {
			
			//Verifica se existe anotação de IoC para a classe.
			classAS = PlcConfigHelper.getInstance().getAlternativeClass(classAS);
			
			PlcBaseAS plcAS = getIoCService().registerIoCAS(classAS,getCache(), getManagerDefault(), getAopService());
			return plcAS;
		}

	}
	
	/**
	 * Retorna BO padrao, considerando situacao do Flag AOP
	 * @since jCompany 3.1
	 * @return Bo Padrao como proxy ou natural
	 */
	private IPlcManager getManagerDefault() throws PlcException {
		
		//Testa se o container injetou um BO
		if(null != plcManager) return plcManager;
		
		// Instancia com AOP se já não o fez e se o nível for 1
		if (PlcContextManager.getContextEntity() != null && PlcContextManager.getContextEntity().getLevelAop()>0) {
			if (managerDefault.getClass().getName().equals(PlcBaseManager.class.getName()))
				setManagerDefault((PlcBaseManager) PlcAopManager.createProxy(PlcBaseManager.class,getAopService()));
		}
			
		return managerDefault;
	}

	/**
	 * jCompany 3.0. Permite que o desenvolvedor registre, para uma dada classe (ENTITY, BO), qual o BO o atenderá ou para
	 * uma classe AS, qual AS será utilizado. 
	 * Deste modo, podendo fugir ao padrão de nomenclatura e também ser utilizado para classes Mock em tempo de teste.
	 */
	public void register(Class clazz,PlcBaseBC plcBC) {

	    log.debug("######## Entered en register");

	 	 getCache().put(clazz,plcBC);

	}
	
	/**
	 * @return Returns the aopService.
	 */
	public PlcAopModelCallbackService getAopService() {
		if (aopService==null)
			aopService = new PlcAopModelCallbackService();
		return aopService;
	}
	/**
	 * @param aopService The aopService to set.
	 */
	public void setAopService(PlcAopModelCallbackService aopService) {
		this.aopService = aopService;
	}

}
