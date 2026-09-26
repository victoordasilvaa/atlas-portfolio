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

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcAopException;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.aop.PlcAopBaseCallback;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.facade.PlcBaseFacadeImpl;
import org.jcompany.persistence.PlcBaseDAO;
import org.jcompany.persistence.PlcContextManager;
import org.jcompany.persistence.PlcPersistenceLocator;
import org.jcompany.persistence.hibernate.helper.PlcAnnotationPersistenceHelper;

import net.sf.cglib.proxy.MethodProxy;


/**
 * Serviços de AOP para camada de modelo
 * @since jCompany 3.0
 * @version $Id: PlcAopModelCallbackService.java,v 1.6 2006/08/25 16:07:20 alvim Exp $
 */
public class PlcAopModelCallbackService extends PlcAopBaseCallback {
	
	protected static final Logger logModelo = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_MODEL);

	PlcAnnotationPersistenceHelper  anotationPersistenceHelper = PlcAnnotationPersistenceHelper.getInstance();

	/**
	 * Se for Facade inclui context em Thread Local.
	 * @see org.jcompany.commons.aop.PlcAopBaseCallback#interceptBefore(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	protected void interceptBefore(Object obj, Method method, Object[] args, MethodProxy proxy) throws PlcException {

		super.interceptBefore(obj, method, args, proxy);
		
		// Facade sendo um proxy nao encerra com "Facade" ou "FacadeImpl", mas contém $... (como continuação)!
		/*
		 * Esta inclusão do Context para uma thread local esta sendo feita explicitamenta desde o jCompany 5.0
		 * para aumentar a legibilidade do fluxo do código.
		 * 
		 * if (obj != null && 
				(obj.getClass().getName().indexOf("Facade")>-1 || obj.getClass().getName().indexOf("FacadeImpl")>-1))
			contextIncluiThreadLocal(args);
		*/
	}

	/**
	 * Encerra Transação Declarativa via AOP. Se tiver anotação em qualquer método de persistência ou 
	 * se for método do Façade padrão, descendente de PlcBaseFacadeImpl, entao executa o fechamento.
	 * @since jCompany 3.0
	 * @see org.jcompany.commons.aop.PlcAopBaseCallback#interceptAfter(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	protected void interceptAfter(Object obj, Method method, Object[] args, MethodProxy proxy) throws PlcException {
		
		if (logModelo.isDebugEnabled())
			logModelo.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":interceptAfter:"));

		String factory = anotationPersistenceHelper.existsAnnotationTransactionRead(method);
		if (factory != null) {
			try{
				//Só vai fazer rollback aqui se o container não estiver gerenciando
				if(!PlcConfigHelper.getInstance().containerManagingTransaction()) { 
					PlcBaseContextVO context = PlcContextManager.getContextEntity();
					// Troca fábrica se o contexto não usa a padrão.
					if (context != null && context.getFactoryPlc() != null && !context.getFactoryPlc().equals("default"))
						factory = context.getFactoryPlc();
					PlcBaseDAO dao = (PlcBaseDAO)PlcPersistenceLocator.getInstance().get(context == null ? null : context.getMainClass());
					dao.rollback(factory);
				}
				// Emite um logging de fechamento, o que permite a auditoria de unidade logic de transacao (ULT) corretamente.
		//		if (logProfiling.isDebugEnabled() && helperProfiling.getNivel()>=1)
			//		helperProfiling.registraFimTransacao("rollback");
					
				if (obj != null && 
					(obj.getClass().getName().indexOf("Facade")>-1 || obj.getClass().getName().indexOf("FacadeImpl")>-1))
					contextRemoveThreadLocal(args);
				
			} catch (Exception e) {
				throw new PlcAopException("jcompany.erro.generic",
						new Object[] { "interceptAfter.rollback", e }, e, log);
			}
		} else {
			factory = anotationPersistenceHelper.existsAnnotationTransactionPersist(method);
			if (factory != null || (PlcBaseFacadeImpl.class.isAssignableFrom(obj.getClass()) &&
					 !anotationPersistenceHelper.existsAnnotationTransactionNotApply(method))) {
				if (factory == null)
					factory = "default";
				// Importante: Se for descendente do Facade, faz commit de qualquer forma.
				try {
//					Só vai fazer commit aqui se o container não estiver gerenciando
					if(!PlcConfigHelper.getInstance().containerManagingTransaction()) { 
					    PlcBaseContextVO context = PlcContextManager.getContextEntity();
					    // Troca fábrica se o contexto não usa a padrão.
					    if (context != null && context.getFactoryPlc() != null
							   && !context.getFactoryPlc().equals("default"))
						   factory = context.getFactoryPlc();
					   PlcBaseDAO dao = (PlcBaseDAO)PlcPersistenceLocator.getInstance().get(null);

					   // Pode ter sido chamado de um método da versão 2.7.4 
					   if (factory !=null)
					      dao.commit(factory);	
					}
//					 Emite um logging de fechamento, o que permite a auditoria de unidade logic de transacao (ULT) corretamente.
			//		if (logProfiling.isDebugEnabled() && helperProfiling.getNivel()>=1)
				//		helperProfiling.registraFimTransacao("commit");

					if (obj != null && 
					(obj.getClass().getName().indexOf("Facade")>-1 || obj.getClass().getName().indexOf("FacadeImpl")>-1))
						contextRemoveThreadLocal(args);
					
				} catch (Exception e) {
					throw new PlcAopException("jcompany.erro.generic",
							new Object[] { "interceptAfter.commit", e }, e, log);
				}
			}
		}
		
		if (logModelo.isDebugEnabled())
			logModelo.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":interceptAfter:"));


	}
	
	/**
	 * @since jCompany 3.0 
	 * Template Method para interceptadores AOP. Em caso de excecao no método principal
	 */
	protected void interceptException(Object obj, Method method, Object[] args, MethodProxy proxy) throws PlcException {
		String factory = anotationPersistenceHelper.existsAnnotationTransactionRead(method);
		if (factory==null)
			factory =  anotationPersistenceHelper.existsAnnotationTransactionPersist(method);
		if (factory!=null || (PlcBaseFacadeImpl.class.isAssignableFrom(obj.getClass()) &&
				 !anotationPersistenceHelper.existsAnnotationTransactionNotApply(method))) {
			try {
				//Só vai fazer rollback aqui se o container não estiver gerenciando
				if(!PlcConfigHelper.getInstance().containerManagingTransaction()) { 
					PlcBaseContextVO context = PlcContextManager.getContextEntity();
				
					// Troca fábrica se o contexto não usa a padrão.
					if (context != null && context.getFactoryPlc() != null && !context.getFactoryPlc().equals("default"))
						factory = context.getFactoryPlc();
				
					PlcBaseDAO dao = (PlcBaseDAO)PlcPersistenceLocator.getInstance().get(null);
					dao.rollback(factory);		
				}
//				 Emite um logging de fechamento, o que permite a auditoria de unidade logic de transacao (ULT) corretamente.
		//		if (logProfiling.isDebugEnabled() && helperProfiling.getNivel()>=1)
			//		helperProfiling.registraFimTransacao("rollback");

				if (obj != null && 
						(obj.getClass().getName().indexOf("Facade") >-1 || obj.getClass().getName().indexOf("FacadeImpl")>-1))
							contextRemoveThreadLocal(args);
				
			} catch (Exception e) {
				log.fatal("Error trying to rollback by AOP "+e,e);
			}
		
		}
	
	}

	/**
	 * Inclui Context em Thread Local
	 * @since jCompany 3.05
	 * @param args argumentos do método
	 */
	private void contextInsertThreadLocal(Object[] args) throws PlcException {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg != null && PlcBaseContextVO.class.isAssignableFrom(arg.getClass()))
					PlcContextManager.setContextEntity((PlcBaseContextVO)arg);
				
			}
		}

		
	}
	
	/**
	 * Remove Context da Thread Local
	 * @since jCompany 3.05
	 * @param args argumentos do método
	 */
	private void contextRemoveThreadLocal(Object[] args) throws PlcException {		

		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg != null && PlcBaseContextVO.class.isAssignableFrom(arg.getClass()))
					PlcContextManager.removeContext();
				
			}
		}
		
	}
	
}
