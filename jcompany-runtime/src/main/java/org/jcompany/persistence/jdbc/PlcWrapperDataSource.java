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
package org.jcompany.persistence.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcReflectionHelper;



/**
 * Devido a experiências com o JBoss, que, dependendo de sua versão, utiliza
 * classes que não implementam javax.sql.DataSource, e prevendo que outros
 * application servers possam ter o mesmo comportamento, deve-se utilizar
 * este "wrapper" que fará o devido interfaceamento, garantido total compatibilidade
 * sem a necessidade de escrever trechos específicos para cada situação nova.
 * 
 * <p>Importante: Este DataSource não pode ser singleton porque o jCompany aceita 
 * vários! O padrão ServiceLocator garante que haverá uma única instância para cada pool!</p>
 * 
 * @author Roberto Lúcio Badaró, Alvim
 * @since jCompany 1.0
 * @version $Id: PlcWrapperDataSource.java,v 1.7 2006/06/21 19:12:20 bruno_grossi Exp $
 */
public class PlcWrapperDataSource implements DataSource {
	
	private static final Logger log = Logger.getLogger(PlcWrapperDataSource.class);
	
	private PlcReflectionHelper util = PlcReflectionHelper.getInstance();
	
    /**
     * Place-holder para o objeto DataSource.
     */
    private Object datasource = null;
    
    /**
	 * Indica que o objeto datasource informado implementa a interface
	 * DataSource, ou seja, segue o padrão, tornando desnecessária as chamadas
	 * aos métodos da interface via reflexão.
	 * 
	 * @since jCompany 3
	 */
    private boolean interfaceOK = false;


    /**
     * Alvim. DataSource tornado
     */
    public PlcWrapperDataSource() {
    }
	
    /**
     * Recebe o objeto datasource que será referenciado nas chamadas aos métodos
	 * padrão da interface javax.sql.DataSource.
     *
     * @param dataSourceObject
     */
    public void setDataSource(Object dataSourceObject) {
    	
		datasource = dataSourceObject;
    	interfaceOK = (datasource instanceof DataSource);
    	
		if (log.isDebugEnabled()) {
			log.debug("PlcWrapperDataSource received: "
					+ dataSourceObject.getClass().getName()
					+ " - Implementing interface DataSource? " + interfaceOK);
		}
	}


    /**
	 * @see javax.sql.DataSource#getConnection()
	 */
    public Connection getConnection() throws SQLException {
    	if (interfaceOK) {
    		return ((DataSource) datasource).getConnection();
    	} else {
    		return (Connection) executeMethod("getConnection", new Object[0]);
    	}
	}

    /**
	 * @see javax.sql.DataSource#getConnection(java.lang.String,
	 *      java.lang.String)
	 */
    public Connection getConnection(String username, String password)
			throws SQLException {
    	
    	if (interfaceOK) {
			return ((DataSource) datasource).getConnection(username, password);
		} else {
			return (Connection) executeMethod("getConnection", new Object[] {
					username, password });
		}
	}

    /**
	 * @see javax.sql.DataSource#getLogWriter()
	 */
    public PrintWriter getLogWriter() throws SQLException {
		if (interfaceOK) {
			return ((DataSource) datasource).getLogWriter();
		} else {
			return (PrintWriter) executeMethod("getLogWriter", new Object[0]);
		}
	}

    /**
	 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
	 */
    public void setLogWriter(PrintWriter out) throws SQLException {
		if (interfaceOK) {
			((DataSource) datasource).setLogWriter(out);
		} else {
			executeMethod("setLogWriter", new Object[] { out });
		}
	}

    /**
	 * @see javax.sql.DataSource#setLoginTimeout(int)
	 */
    public void setLoginTimeout(int seconds) throws SQLException {
		if (interfaceOK) {
			((DataSource) datasource).setLoginTimeout(seconds);
		} else {
			executeMethod("setLoginTimeout",
					new Object[] { new Integer(seconds) });
		}
	}

    /**
	 * @see javax.sql.DataSource#getLoginTimeout()
	 */
    public int getLoginTimeout() throws SQLException {
		if (interfaceOK) {
			return ((DataSource) datasource).getLoginTimeout();
		} else {
			Integer i = (Integer) executeMethod("getLoginTimeout",
					new Object[0]);
			return i.intValue();
		}
	}


    /**
	 * Recupera o método requisitado em datasource e executa-o.
	 * 
	 * @param methodName -
	 *            O nome do método a ser recuperado e executado.
	 * @param args -
	 *            Array de Object com os argumentos esperados pelo método.
	 * @return Object - Um Object com o retorno do método, se houver.
	 * @throws PlcException
	 */
    private Object executeMethod(String methodName, Object args[])
			throws SQLException {

		try {
			
			return util.executeMethod(datasource, methodName, args);
			
		} catch (PlcException e) {

			Throwable t = e.getRootCause();
			if (t instanceof SQLException) {
				throw (SQLException) t;
			} else {
				throw new SQLException("Error trying to execute method '"
						+ methodName + "': " + t.getMessage());
			}
		}
	}

    public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
        if (datasource instanceof DataSource) return ((DataSource) datasource).getParentLogger();
        throw new java.sql.SQLFeatureNotSupportedException();
    }
    public <T> T unwrap(Class<T> type) throws SQLException {
        if (type.isInstance(this)) return type.cast(this);
        if (datasource instanceof DataSource) return ((DataSource) datasource).unwrap(type);
        throw new SQLException("Unsupported unwrap: " + type);
    }
    public boolean isWrapperFor(Class<?> type) throws SQLException {
        return type.isInstance(this) || (datasource instanceof DataSource && ((DataSource) datasource).isWrapperFor(type));
    }
}
