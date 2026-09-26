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
package org.jcompany.persistence.hibernate.helper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.persistence.PlcJdbcPoolLocator;
import org.jcompany.persistence.helper.PlcPersistenceHelper;
import org.jcompany.persistence.hibernate.PlcConstantsHibernate;


/**
 * Classe de Utilitários para acesso a meta-dados e configuração da Hibernate
 * @since jCompany 3.0
 * @version $Id: PlcHibernateHelper.java,v 1.12 2006/07/31 13:46:51 bruno_grossi Exp $
 */
public class PlcHibernateHelper  extends PlcPersistenceHelper {

 	private static PlcHibernateHelper INSTANCE = new PlcHibernateHelper();
	protected static Logger log = Logger.getLogger(PlcHibernateHelper.class);
	private PlcHibernateHelper() { }
	public static PlcHibernateHelper getInstance(){
 		return INSTANCE;
	}

	/**
	 * Devolve uma referencia do bean de memoria do Hibernate que representa uma classe
	 * em uma configuração de fábrica.
	 * @param fullClassName
	 * @return referência de persistência para a classe ou nulo se não encontrar
	 */
	public org.hibernate.mapping.PersistentClass getPersistentClass(org.hibernate.cfg.Configuration cfg, String fullClassName) {

	    log.debug("######## Entered in getPersistentClass");

	    Iterator k = cfg.getClassMappings();

       while (k.hasNext()) {

           PersistentClass clazz = (PersistentClass) k.next();
           if (clazz.getClassName().equalsIgnoreCase(fullClassName))
               return clazz;
       }

       return null;

	}

    

    
    
    /**
 	 * jCompany 2.0 Método responsável por obter o driver JDBC do banco
 	 * utilizado pela hibernate para estabelecer a conexão.
 	 * 
 	 * @param jndi 
 	 * @return String contendo o driver do banco em uso pela hibernate.
 	 */
 	private String[] getDatabaseDriver(String jndi) {
 		
 		String driver = null;
 		String url = null;
 		try {
 			
 			if (log.isDebugEnabled())
 				log.debug("######### Entered to get database drive to :"+jndi);
 			
 			PlcJdbcPoolLocator services = PlcJdbcPoolLocator.getInstance();
 			Connection c = services.getConnection(jndi);
 			driver = c.getMetaData().getDriverName();
 			url = c.getMetaData().getURL();
 			if (log.isDebugEnabled())
 			    log.debug("Driver: " + driver);
 			
 		} catch (Exception e) {
 			log.error("Error trying to get database drive  : " +e,e);
 		}
 		
 		return new String[]{driver,url};
 	}

 	

 	/**
 	 * jCompany 2.0 Método responsável por configurar o dialeto da hibernate de acordo
 	 * com o driver de banco utilizado.
 	 * 
 	 * @param p Properties contendo todas as configureações para a fábrica da hibernate.
 	 * @param driver String contendo o nome do driver.
 	 * @return Properties contendo as novas configurações de driver.
 	 */
 	private Properties configureDialect(Properties p, String driver, String url) {

 		if (log.isDebugEnabled()) {
 			log.debug("###### Entered in configureDialect");
 			log.debug("###### Default dialect: " + p.get("dialect"));
 			log.debug("###### Hiberante dialect: " + p.get("hibernate.dialect"));
 		}

 		if (driver != null) {
 			log.debug("####### Entered to modify dialect as driver : " + driver );

 			// Importante: Deve estar comentado porque, se é um driver novo, pelo menos evita um erro deixando o dialeto nulo!
// 			p.remove("dialect");
// 			p.remove("hibernate.dialect");
			
 			if (url != null && url.toLowerCase().indexOf("derby")>-1) { // CloudScape/Derby
 				p.put("dialect", DerbyDialect.class.getName());				
 				p.put("hibernate.dialect", DerbyDialect.class.getName());	
 			} else if (driver.toLowerCase().trim().indexOf("ibm db2") > -1) { // DB2
 				p.put("dialect", DB2Dialect.class.getName());				
 				p.put("hibernate.dialect", DB2Dialect.class.getName());				
 			} else if (driver.toLowerCase().trim().indexOf("oracle") > -1) { // Oracle
 				p.put("dialect", Oracle9Dialect.class.getName());				
 				p.put("hibernate.dialect", Oracle9Dialect.class.getName());				
 			} else if (driver.toLowerCase().trim().indexOf("sqlserver") > -1){ //SqlServer
 				p.put("dialect", SQLServerDialect.class.getName());	
 				p.put("hibernate.dialect", SQLServerDialect.class.getName());	
 			} else if (driver.toLowerCase().trim().indexOf("mysql") > -1) { // Mysql
 				p.put("dialect", MySQLDialect.class.getName());	
 				p.put("hibernate.dialect", MySQLDialect.class.getName());	
 			} else if (driver.toLowerCase().trim().indexOf("postgresql") > -1) { // PostgreSql
 				p.put("dialect", PostgreSQLDialect.class.getName());	
 				p.put("hibernate.dialect", org.hibernate.dialect.PostgreSQLDialect.class.getName());	
 			} else if (driver.toLowerCase().trim().indexOf("sybase") > -1){ //Sybase
 				p.put("dialect", SybaseDialect.class.getName());	
 				p.put("hibernate.dialect", SybaseDialect.class.getName());	
 			}
 		}
 		
 		log.info("###### Driver: " + driver+" url: "+url);
 		log.info("###### Dialect: " + p.get("hibernate.dialect"));
 		 		
 		return p;
 	}
 	
 	/**
 	 * jCompany 3.0 Chama configuração automatica obtendo identificação do pool JDBC a partir da configuração
 	 * do datasource no arquivo de configuração, retirando o prefixo java:comp/env/
 	 * @param factoryName Nome Fábrica Hibernate
 	 * @param cfg Nome do Configuration
 	 */
	public void configureDialectAutomatically(String factoryName,Configuration cfg) {
		log.debug("############### Entered in configureDialectAutomatically");
		 if (isTestMode())
			 return;
		 
		String dataSource = cfg.getProperty(PlcConstantsHibernate.CONFIG.DATA_SOURCE);
		
		String pool = dataSource.substring(dataSource.indexOf(PlcConstantsHibernate.CONFIG.DATA_SOURCE_PREFFIX)+14);
		configureDialectAutomatically(factoryName,pool,cfg);
	}
	
 	public void configureDialectAutomatically(String factoryName,String jdbcPoolName,Configuration cfg) {
		
 		log.info("###### Configuring dialect to factory "+factoryName+" and pool "+jdbcPoolName+" for cfg"+cfg);
 		 if (isTestMode())
			 return;
		 
 		String[] ret = getDatabaseDriver(jdbcPoolName);
 		String driver = ret[0];
 		String url = ret[1];
		Properties p = configureDialect(cfg.getProperties(), driver,url);
		cfg.setProperties(p);
 	}
    
 	/**
 	 * jCompany 2.7 Configura o owner da fábrica Hibernate
 	 * 
 	 * @param p Properties contendo todas as configureações para a fábrica da hibernate.
 	 * @return Properties contendo a nova configuração de owner
 	 */
 	private Properties configureOwner(Properties p, String owner) {

 	    log.debug("######## Entered in configureOwner");

        p.put(PlcConstantsHibernate.CONFIG.DEFAULT_SCHEMA,owner);

 		return p;
 	}
 	
 	public void configureOwnerAutomatically(String factoryName,String ownerName,Configuration cfg) {
 		
		Properties p = configureOwner(cfg.getProperties(), ownerName);
		cfg.setProperties(p);
 	}
 	
	public Type[] translateTypes(String[] types) {
	
		List<Type> l = new ArrayList<Type>();
		for (int i = 0; i < types.length; i++ ) {
			
			if (types[i].equals(PlcConstantsCommons.TYPES.STRING))
				l.add(Hibernate.STRING);
			else if (types[i].equals(PlcConstantsCommons.TYPES.DATE))
				l.add(Hibernate.DATE);
			else if (types[i].equals(PlcConstantsCommons.TYPES.TIMESTAMP))
				l.add(Hibernate.TIMESTAMP);
			else if (types[i].equals(PlcConstantsCommons.TYPES.LONG))
				l.add(Hibernate.LONG);
			else if (types[i].equals(PlcConstantsCommons.TYPES.INTEGER))
				l.add(Hibernate.INTEGER);
			else if (types[i].equals(PlcConstantsCommons.TYPES.BIG_DECIMAL))
				l.add(Hibernate.BIG_DECIMAL);
			else if (types[i].equals(PlcConstantsCommons.TYPES.DOUBLE))
				l.add(Hibernate.DOUBLE);
		
		}
		
		return l.toArray(new Type[l.size()]);

	}
	
	/**
	 * Recebe uma
	 * @since jCompany 3.03
	 * @param classeMain Classe a ser investigada
	 * @param classNameProbablyReferenced Nome da classe potencialmente referenciada com many-to-one (lado 'one')
	 * @return true se a mainClass referenciar a classePotencialmenteReferenciada com relacionamento many-to-one
	 * @throws PlcException
	 */
	public boolean referenceWithManyToOne(PersistentClass classeMain,String classNameProbablyReferenced) throws PlcException {
     
		Iterator y = classeMain.getPropertyIterator();

		while (y.hasNext()) {

			Property prop = (Property) y.next();

			if (classNameProbablyReferenced.equals(prop.getType().getReturnedClass().getName()))
				return true;
			
		}
		log.debug("Returning false");
		return false;
	}
	
	

}
