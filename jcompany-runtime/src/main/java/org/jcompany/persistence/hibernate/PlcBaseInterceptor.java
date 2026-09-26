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
package org.jcompany.persistence.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.persistence.hibernate.util.PlcFormatterUtil;


/**
 * Utiliza Interceptor unicamente para capturar SQLs enviados, já que não foi possível encontrar este comportamento
 * nos novos Listeners (TODO Reconferir em novas versoes da Hibernate após 3.2 RC2 se este comportamento foi inserido em Listeners)
 * @since jCompany 3.1.1
 */
@SuppressWarnings("serial")
public class PlcBaseInterceptor extends EmptyInterceptor {

	private static final String LINE_END = "</td></tr>";
	private static final String LINE_BEGIN = "<tr><td>";
	protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
	protected static final Logger logDocAutomatizada = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_DOC_AUTOMATED);
	
	/**
	 * Faz logging em profiling do SQL, somente se utilizando documentacao
	 * @see org.hibernate.EmptyInterceptor#onPrepareStatement(java.lang.String)
	 */
	@Override
	public String onPrepareStatement(String sql) {
		
		if (logProfiling.isDebugEnabled() && "S".equals(PlcAopProfilingHelper.getDOC_GENERATE()))
			logDocAutomatizada.debug(LINE_BEGIN+new PlcFormatterUtil(sql).format()+LINE_END);
		
		return super.onPrepareStatement(sql);
	}

}
