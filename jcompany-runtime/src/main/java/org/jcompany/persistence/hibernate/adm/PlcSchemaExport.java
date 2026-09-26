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
package org.jcompany.persistence.hibernate.adm;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.jcompany.commons.PlcException;
import org.jcompany.model.PlcBaseManager;


/**
 * Gera script para criação ou exclusão de esquema de banco de dados da aplicação
 * @author Rodrigo Magno
 * @since jCompany 2.0
 * @version $Id: PlcSchemaExport.java,v 1.5 2006/06/21 19:12:20 bruno_grossi Exp $
 */
public class PlcSchemaExport extends PlcBaseManager {

	//private String[] dropSQL;
	//private String[] createSQL;
	//private Properties connectionProperties;
	//private Dialect dialect;

	protected static Logger log = Logger.getLogger(PlcSchemaExport.class);

	/**
	 * Cria um script para criação/exclusão a partir da configuração informada
	 * @param sess Sessão da Hibernate
	 * @param delimiter Delimitador das linhas do script
	 */
	public void execute (org.hibernate.classic.Session sess, String ddl, String delimiter) throws HibernateException, PlcException {

		log.debug("Entered to execute script for database schema");

		Connection connection 	= null;
		Statement statement 	= null;
		String[] sql = StringUtils.split(ddl, delimiter);

		try {
				connection = sess.connection();
				connection.commit();
				try
				{
					connection.setAutoCommit(true);
				} catch (Exception e)
				{
					log.debug("Sybase Exception: this database doens't allow to alter autocommit to false");
				}
			statement = connection.createStatement();
			for(int i = 0; i < sql.length; i++){
				log.debug("Executing DDL: \n"+ddl);
			   statement.executeUpdate(sql[i]);
			}
		}
		catch(Exception e) {
			throw new PlcException("jcompany.schema.error.generating",new Object[]{e},e,log);
		}
	}
}
