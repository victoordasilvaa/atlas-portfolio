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
package org.jcompany.commons.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcAnnotationHelper;


/**
 * @since jComany 3.0.
 * Encerra transação e executa exibições de Profiling caso o Logger "performance" esteja em nível DEBUG,
 * Utilizando AOP (Esta classe é chamada como callback do serviço PlcAopService.
 */
public abstract class PlcAopBaseCallback  implements MethodInterceptor,Serializable {
	
	protected static final Logger log = Logger.getLogger(PlcAopBaseCallback.class);
	protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
	protected static PlcAopProfilingHelper helperProfiling = PlcAopProfilingHelper.getInstance();
	protected static PlcAnnotationHelper helperAnnotation = PlcAnnotationHelper.getInstance();
	
	/**
	 * @since jCompany 3.0
	 * Intercepta todos os métodos de Façade.
	 */
	public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, MethodProxy proxy) throws PlcException {
		
		if (log.isDebugEnabled()) {
			log.debug("############### Entered in intercept for method: "+method);

			 for( int i = 0; i < args.length; i++ ){
			      if( obj == args[i])
			    	  log.debug( "############### --- arg" + (i + 1) + ": "+this);
			      else
			    	  log.debug( "############### --- arg" + (i + 1) + ": "+args[i]);
			 }
		}
		
		if (logProfiling.isDebugEnabled() && helperProfiling.getLevel()>=1)
			helperProfiling.registerBeginning(null,getClassName(method),getMethod(method),helperProfiling.getDOC_GENERATE(),"");

		Object retValFromSuper = null;

        try {
        	 
        	 interceptBefore(obj,method,args,proxy);
		 
             retValFromSuper = proxy.invokeSuper(obj, args);
         
             interceptAfter(obj,method,args,proxy);
         
        } catch (PlcException ePlc) {
        	interceptException(obj,method,args,proxy);
        	if (logProfiling.isDebugEnabled() && !ePlc.isAlreadyLoggingShowed()) {
        		ePlc.setAlreadyLoggingShowed(true);
        		if (ePlc.getRootCause()!=null)
        			logProfiling.debug("++++ External Excecao : "+ePlc.getRootCause());
        		else
        			logProfiling.debug("++++ External Excecao : "+ePlc);
        	}
        	throw ePlc;
         } catch (Throwable e) {
        	 if (logProfiling.isDebugEnabled())
        		 logProfiling.debug("++++ External Excecao : "+e.toString());
        	 interceptException(obj,method,args,proxy);
			throw new PlcException("jcompany.error.generic", new Object[] {
					"intercept", e }, e, log);
        }
           
   		if (log.isDebugEnabled()) {
           if( obj == retValFromSuper)
               log.debug("return "+this);
           else 
        	   log.debug("return "+retValFromSuper);
   		}
         
		if (logProfiling.isDebugEnabled() && helperProfiling.getLevel()>=1) {
			if (helperAnnotation.getSLAMethod(method)>-1)
				helperProfiling.registerFinish(null,getClassName(method),getMethod(method),helperAnnotation.getSLAMethod(method),
						helperProfiling.getDOC_GENERATE());
			else
				helperProfiling.registerFinish(null,getClassName(method),getMethod(method),helperProfiling.getDOC_GENERATE());
		}

   		
        return retValFromSuper;
	}
	
	/**
	 * @since jCompany 3.0
	 * Template Method para interceptadores AOP. Antes de chamar o método principal. Neste caso
	 * tem implementacoes porque o Aop nao tem proposito de framework nesta versão
	 */
	protected void interceptBefore(Object obj, Method method, Object[] args, MethodProxy proxy) throws PlcException {}
	
	/**
	 * @since jCompany 3.0
	 * Template Method para interceptadores AOP.  Após de chamar o método principal
	 */
	protected void interceptAfter(Object obj, Method method, Object[] args, MethodProxy proxy) throws PlcException {}
	
	/**
	 * @since jCompany 3.0
	 * Template Method para interceptadores AOP. Em caso de excecao no método principal
	 */
	protected void interceptException(Object obj, Method method, Object[] args, MethodProxy proxy) throws PlcException {}

	/**
	 * @since jCompany 3.0
	 * Devolve o nome do Proxy apropriado para exibição
	 * @param obj Objeto cuja classe será investigada
	 * @return Nome da classe sem sufixo do proxy e somente com parte final
	 */
	protected String getClassName(Method method) {
		String strAux 	= method.toString();
		strAux = strAux.substring(0,strAux.indexOf("("));
		strAux = strAux.substring(strAux.lastIndexOf(" ")+1);
		return strAux.substring(0,strAux.lastIndexOf("."));
		
	}
	/**
	 * @since jCompany 3.0
	 */
	protected String getMethod(Method method){
		String strAux = "";
		strAux 	= method.toString().substring(0,method.toString().indexOf("("));
		strAux 	= strAux.substring(strAux.lastIndexOf(".")+1) ;
		
		return method.toString().substring(method.toString().indexOf(strAux),method.toString().indexOf(")")+1);
	}
	


}
