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
import java.sql.Connection;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.persistence.jdbc.PlcWrapperDataSource;


/**
 * jCompany 1.5.x. Service Locator. Singleton. Pega a referencia de um pool de conexões com o 
 * SGBD configurado do App Server. Evita lookup em cada request.
 * @since jCompany 1.5
 * @version $Id: PlcJdbcPoolLocator.java,v 1.4 2006/06/21 19:12:20 bruno_grossi Exp $
 */
public class PlcJdbcPoolLocator {

	private static PlcJdbcPoolLocator INSTANCE = new PlcJdbcPoolLocator();
	protected static Logger log = Logger.getLogger(PlcJdbcPoolLocator.class);
	protected static Logger logF = Logger.getLogger("flow");
	public static final String JDBC_POOL_JNDINAME = "java:comp/env";
	private static HashMap dataSourceCache = new HashMap();
	private PlcJdbcPoolLocator() { }
	public static PlcJdbcPoolLocator getInstance(){
   		return INSTANCE;
	}
	
	/**
	 * Recebe "jdbc/meupool" e devolve referência ao pool de conexões pegando via JNDI
	 */
	public Connection getConnection(String jndiDataSource) throws PlcException {
		
		if (log.isDebugEnabled())
			logF.debug("###### Entered to get  JDBC Connection to address = "+jndiDataSource);
		
		PlcWrapperDataSource datasource = null;
		
		try {
		
			if (dataSourceCache.containsKey(jndiDataSource)) {
				
				if (log.isDebugEnabled())
					log.debug("Caching of connections for "+jndiDataSource);
	
				datasource = (PlcWrapperDataSource) dataSourceCache.get(jndiDataSource);
				Connection con = datasource.getConnection();
				return con; 
				
			} else {
	
				if (log.isDebugEnabled())
					log.debug("Not Using caching of connections for :"+jndiDataSource);
	
				Context ic = new InitialContext();

				Context env = (Context) ic.lookup("java:comp/env");
	
				log.debug("Context lookup");
		
				datasource = new PlcWrapperDataSource();
				try {
				    datasource.setDataSource(env.lookup(jndiDataSource));
				} catch (NameNotFoundException nnfe) {
				    datasource.setDataSource(ic.lookup(jndiDataSource));
				}
	
				dataSourceCache.put(jndiDataSource,datasource);
					
				if (log.isDebugEnabled()) 
					log.debug("datasource creation");
	
				Connection con = datasource.getConnection();
				try{
				    con.setAutoCommit(false);
				} catch(Exception e){
					log.debug("It's not possible to configure autoCommit to false");				    
				}    
								
				return con;

			}
		} catch (Exception e) {
			throw new PlcException("jcompany.errors.reconnect",new Object[]{e},e,log);
		}
		
	}

   
}
