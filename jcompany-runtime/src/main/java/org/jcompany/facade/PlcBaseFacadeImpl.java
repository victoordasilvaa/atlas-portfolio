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
package org.jcompany.facade;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.facade.IPlcBaseFacade;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.persistence.PlcConfigPersistenceIoC;
import org.jcompany.model.PlcBaseAS;
import org.jcompany.model.PlcBaseBC;
import org.jcompany.model.PlcBaseManager;
import org.jcompany.model.annotation.PlcTransactionNotApply;
import org.jcompany.persistence.PlcBaseDAO;
import org.jcompany.persistence.PlcContextManager;
import org.jcompany.persistence.PlcPersistenceLocator;


/**
 * @since jCompany 3.0
 * DP Façade. Classe base de Fachada  que contém utilitários para descendentes somente.
 * @since jCompany 3.0
 * @version $Id: PlcBaseFacadeImpl.java,v 1.9 2006/07/24 13:43:13 alvim Exp $
 */
public class PlcBaseFacadeImpl extends PlcBaseSessionFacadeImpl implements IPlcBaseFacade{


	private static final long serialVersionUID = -1696674861603617931L;

	protected static Logger log = Logger.getLogger(PlcBaseFacadeImpl.class);
	
	protected static String STATUS_EXCEPTION = "STATUS_EXCEPTION";
	protected static String STATUS_OK = "STATUS_OK";

    /**
     * Pega a referencia DAO padrão do Hibernate
     * @return DAO
     */
	@PlcTransactionNotApply
    private PlcBaseDAO getDAODefaultHibernate() throws PlcException {
        return PlcPersistenceLocator.getInstance().getDaoDefaultHibernate();
    }

	/**
     * Pega a referencia DAO padrão (base do jCompany). Tipicamente para uso genérico
     * @return DAO
     */
	@PlcTransactionNotApply
    private PlcBaseDAO getDAODefault() throws PlcException {
        return (PlcBaseDAO)PlcPersistenceLocator.getInstance().get(null);
    }
	
	/**
	 * Disponibiliza o objeto de context em local thread para uso nas camadas de negócio e persistência via getContext().
	 * O jCompany já realiza este procedimento utilizando AOP, mas este método pode ser usado em casos especiais onde
	 * este serviço porventura não esteja ativado
	 * @param context
	 */
	@PlcTransactionNotApply
	@SuppressWarnings("unchecked")
	protected void setContext(PlcBaseContextVO context) {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":setContext(@PlcTransactionNotApply):"));

		if (context!=null && context.getPersistenceServiceManagers()==null) {
			try {
				String serviceManagers = "";
				PlcConfigPersistenceIoC ioc 	= PlcConfigHelper.getInstance().get(PlcConfigPersistenceIoC.class);
				serviceManagers 				= completeServiceManagers(serviceManagers, ioc);
				
				String initialsModulo; 
				if (context.getModules() != null && context.getModules().length > 0){
					for(int i=0; i < context.getModules().length; i++ ){
						initialsModulo 	= "." + context.getModules()[i];
						ioc 			= PlcConfigHelper.getInstance().get(PlcConfigPersistenceIoC.class,initialsModulo);
						serviceManagers = completeServiceManagers(serviceManagers, ioc);
					}
				}
				context.setPersistenceServiceManagers(serviceManagers);
			} catch (PlcException e) {
				e.printStackTrace();
			}
		}

		PlcContextManager.setContextEntity(context);

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":setContext(@PlcTransactionNotApply):"));

	}

	private String completeServiceManagers(String serviceManagers, PlcConfigPersistenceIoC configIoC) {
		
		if (configIoC != null && configIoC.serviceManagers()!= null && configIoC.serviceManagers().length > 0) {
			for(int i=0; i<configIoC.serviceManagers().length; i++) {
				String classManager = ((Class)configIoC.serviceManagers()[i]).getName();
				// Se já não foi configurado 
				if (! serviceManagers.contains(classManager))
					serviceManagers	= serviceManagers + classManager;
				
				if (i+1 < configIoC.serviceManagers().length )
					serviceManagers = serviceManagers + ",";
			}
		}
		return serviceManagers;
	}
	
	 /** 
     * @since jCompany 3.0
     * Executa um método por reflexao, fechando a execução com um rollback. Deve ser utilizado somente quando as facilidades de AOP
     * do jcompany não estão habilitadas ou juntamente com a anotação @PlcTransactionNotApply
     */
  	@Override
  	@PlcTransactionNotApply
  	@Deprecated
  	protected Object executeReading(PlcBaseContextVO context, Class classAsManagerDao, String method, Object[] params) throws PlcException {
  		return executeOperation(context,classAsManagerDao,method,params,false);
  	}
  	
	 /** 
     * @since jCompany 3.0
     * Executa um método por reflexao, fechando a execução com um commit. Deve ser utilizado somente quando as facilidades de AOP
     * do jcompany não estão habilitadas ou juntamente com a anotação @PlcTransactionNotApply
     */
  	@Override
  	@PlcTransactionNotApply
  	protected Object executeOperation(PlcBaseContextVO context,Class classAsManagerDao, String method, Object[] params) throws PlcException {
  		return executeOperation(context,classAsManagerDao,method,params,true);
  	}

     /**
  	 * @since jCompany 3.0
     * Executa um método por reflexao, fechando a execução com um commit. Deve ser utilizado somente quando as facilidades de AOP
     * do jcompany não estão habilitadas ou juntamente com a anotação @PlcTransactionNotApply
  	 */
  	@PlcTransactionNotApply
 	protected Object executeOperation(PlcBaseContextVO context, Class classAsManagerDao, String method, Object[] params,
 			Boolean existUpdates) throws PlcException {

  		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":executeOperation(@PlcTransactionNotApply):"));

  		Object objectModelPersistence = null;

  		try {
  		    
  			if (PlcBaseManager.class.isAssignableFrom(classAsManagerDao))
  				objectModelPersistence = getManager(classAsManagerDao);
  			else if (PlcBaseAS.class.isAssignableFrom(classAsManagerDao))
  				objectModelPersistence = getAS(classAsManagerDao);
  			else if (PlcBaseDAO.class.isAssignableFrom(classAsManagerDao))
  				objectModelPersistence = getDAO(classAsManagerDao);
  
        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] { "executeOperation", e }, e,log);
        }


  		Object objReturn = null;

  		try {

  		    //calcula o número de parametros
  		    int numParameters = 0;
  		    if(params != null)
  		      numParameters = params.length;

  			Method[] methods = classAsManagerDao.getMethods();
  			for(int i = 0; i < methods.length; i++) {
  				if(methods[i].getName().equals(method) && numParameters == methods[i].getParameterTypes().length){
  				    if (log.isDebugEnabled())
  				        log.debug("Executing methof "+params+" in object :"+objectModelPersistence);
  					objReturn = methods[i].invoke(objectModelPersistence, params);
  					i = methods.length;
  				}
  			}

  			if (!existUpdates) {
  				if (PlcBaseBC.class.isAssignableFrom(classAsManagerDao))
  					getDAODefault().rollback(context.getFactoryPlc());
  				else
  					getDAO(classAsManagerDao).rollback(context.getFactoryPlc());
  			} else {
  				if (!classAsManagerDao.getName().equals("org.jcompany.jsecurity.modelo.PlcJSecurityBO")){
  					if (PlcBaseBC.class.isAssignableFrom(classAsManagerDao))
  						getDAODefault().commit(context.getFactoryPlc());
  					else
  						getDAO(classAsManagerDao).commit(context.getFactoryPlc());
  				} else {
  					getDAODefaultHibernate().commit(context.getFactoryPlc());
  				}
  			}

  	 		if (logModel.isDebugEnabled())
  				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":executeOperation(@PlcTransactionNotApply):"));

   			return objReturn;
   	  		
   		} catch (Exception e) {
   			getDAODefault().rollback(context.getFactoryPlc());
   			if(e.getCause().getClass().isAssignableFrom(PlcException.class)){
   				throw (PlcException)e.getCause();
   			} else {
   				throw new PlcException("jcompany.errors.persistence.execute.operation.manager",
  					new Object[] {objectModelPersistence,method,e},e,log);
   			}
  		} 

  	}
	

    /**
     *  @since jCompany 3.0 
     *  Recupera registro de mensagens tratadas de erros da camada de persistencia
     *  @param rootCause Exception
     *  @return String[0] mensagem internacionalizada, String[1]: arg1 (opcional), String[2] arg2 (opcional) ou null se não
     *  encontrou ou se ocorreu outra exceção no meio do tratamento.
     **/
  	@PlcTransactionNotApply
	public String[] retrieveMessageException(Throwable rootCause) {
		
		try{
			return ((PlcBaseDAO)PlcPersistenceLocator.getInstance().get(null)).msgExceptionHandle(rootCause);
		} catch (Exception e) {
			return null;
		}
	
	}

}
