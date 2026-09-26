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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.DatasourceConnectionProvider;

/**
 * jCompany 3.0. Disponibiliza um pool de conexões para uma sessionFactory,
 * utilizando o dataSource definido.
 * @since jCompany 3.0
 * @version $Id: PlcConnectionProvider.java,v 1.7 2006/06/21 19:12:20 bruno_grossi Exp $
 */
public class PlcConnectionProvider implements ConnectionProvider {

	protected static Logger log = Logger.getLogger(PlcConnectionProvider.class);
	
	private boolean isHibernateConsole = false;

	private Connection connectionHibernateConsole = null;

	private DatasourceConnectionProvider dataSourceConnectionProvider;

	private String jdbcURL;

	private String name;

	private String password;
	
	public PlcConnectionProvider(){
		log.debug("######## Instanced PlcConnectionProvider");
	}
	
	
	/**
	 * O Plugin do Hibernate Console utiliza o arquivo hibernate.cfg.xml das
	 * aplicações, e neste arquivo está configurado para utilizar o
	 * "connection.provider_class". Portanto tanto as aplicações (Ex:
	 * jcurriculo) quanto o Hibernate Console, podem compartilhar o mesmo
	 * hibernate.cfg.xml ao conectar à base de dados, sendo que no ultimo caso
	 * não iremos pegar uma conexão do pool (para as aplicações), ou se é para
	 * criar uma conexão JDBC direta (para o Hibernate Console).
	 */
	public void configure(Properties props) throws HibernateException {
		// Se não estiver nulo contém o diretório dos plugins do eclipse, ou seja diretório do Hibernate Console
		/** Esta propriedade existe no WebSphere * */
		String comeFromPlugin = props.getProperty("osgi.syspath");

		if (!props.containsKey("ibm.websphere.internalClassAccessMode")
				&& (comeFromPlugin != null) && (!comeFromPlugin.equals(""))) {
			try {
				/*
				 * Cria uma conexão JDBC para o Hibernate Console.
				 */
				String driverClass = props.getProperty("connection.driver_class");
				jdbcURL = props.getProperty("connection.url");
				name = props.getProperty("connection.username");
				password = props.getProperty("connection.password");
				Class.forName(driverClass);

				connectionHibernateConsole = DriverManager.getConnection(jdbcURL, name, password);

				// Flag indicando que É hibernate console que está pegando uma conexão.
				isHibernateConsole = true;

			} catch (Exception e) {
				log.fatal("Error loading configuration file to HibernateConsole.  Error:"+ e.getMessage());
				throw new HibernateException(e);
			}
		} else {
			/*
			 * Busca uma conexão do pool, utilizado pelas aplicações desenvolvidas em jcompany. 
			 */
			dataSourceConnectionProvider = new DatasourceConnectionProvider();
			dataSourceConnectionProvider.configure(props);
			
			// Flag indicando que NÃO é hibernate console que está pegando uma conexão.
			isHibernateConsole = false;
		}

	}
	

	public Connection getConnection() throws SQLException {
		if (!this.isHibernateConsole) {
			return this.dataSourceConnectionProvider.getConnection();
		} else {
			if (this.connectionHibernateConsole.isClosed()) {
				this.connectionHibernateConsole = DriverManager.getConnection(jdbcURL, name, password);
			}
			return this.connectionHibernateConsole;
		}
	}	
	


	public void closeConnection(Connection conn) throws SQLException {
		if (!isHibernateConsole) {
			log.debug("######## Closed connection "+ (conn == null ? "" : conn.toString()));
			this.dataSourceConnectionProvider.closeConnection(conn);
		} else {
			this.connectionHibernateConsole.close();
		}
	}

	public boolean supportsAggressiveRelease() {
		return (isHibernateConsole) ? false : this.dataSourceConnectionProvider.supportsAggressiveRelease();
	}

	public void close() throws HibernateException {
		if (!isHibernateConsole) {
			this.dataSourceConnectionProvider.close();
		}
	}	
	
}
