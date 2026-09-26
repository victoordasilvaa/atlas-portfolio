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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;


/**
 * jCompany 3.0. Servigo que encapsula e disponibiliza o contexto do cliente na
 * camada de persistencia (para acesso tambim via Fagade ou Modelo), atravis do
 * Local Thread. Este objeto nco i passado nos mitodos para nco degradar a
 * assinatura dos mitodos de negscio.
 * 
 * @since jCompany 3.0
 * @version $Id: PlcContextManager.java,v 1.4 2006/06/21 19:12:20 bruno_grossi
 *          Exp $
 */
public class PlcContextManager {

	// ThreadLocal para POJO de contexto
//	private static final ThreadLocal<PlcBaseContextVO> threadContext = new ThreadLocal<PlcBaseContextVO>();
	private static final ThreadLocal<PlcBaseContextVO> threadContext = new ContextThreadLocal();
	protected static Logger log = Logger.getLogger(PlcContextManager.class);

	/**
	 * jCompany 3.0 Coloca o POJO com informagues do contexto do cliente em uma
	 * ThreadLocal para acesso de informagues dos event listeners da Hibernate
	 * 
	 * @param context
	 *            Contexto a ser 'lembrado'
	 */
	public static void setContextEntity(PlcBaseContextVO context) {
		threadContext.set(context);
	}

	/**
	 * jCompany 3.0 Recupera o POJO recim-colocado na ThreadLocal para uso em
	 * evento listeners
	 * 
	 * @return PlcBaseContextVO POJO com informagues de contexto.
	 */
	public static PlcBaseContextVO getContextEntity() {
		return threadContext.get();
	}

	/**
	 * jCompany 3.0 Remove o POJO na ThreadLocal para GC
	 * @author george
	 */
	public static void removeContext() {
		threadContext.remove();
	}
	
	
	private static class ContextThreadLocal extends InheritableThreadLocal<PlcBaseContextVO> {
		// Design Pattern "Flyweight"
		private PlcBaseContextVO flyweight;

		public ContextThreadLocal() {
			this(null);
		}
		
		public ContextThreadLocal(PlcBaseContextVO vo) {
			flyweight = vo;
		}

		
		/**
		 * Retorna o valor inicial do ThreadLocal
		 */
		@Override
		protected PlcBaseContextVO initialValue() {
			try {
				return flyweight == null ? null : (PlcBaseContextVO)BeanUtils.cloneBean(flyweight);
			} catch (Exception e) {
				throw new UnsupportedOperationException("It's not possible to clone context object");
			}
		}

	}

}
