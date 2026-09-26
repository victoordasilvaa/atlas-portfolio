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

import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.jcompany.commons.PlcException;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigAudit;
import org.jcompany.model.PlcBaseManager;
import org.jcompany.persistence.IPlcDAO;
import org.jcompany.persistence.PlcPersistenceLocator;
import org.jcompany.persistence.hibernate.PlcHibernateManagerLocator;
import org.jcompany.persistence.jpa.PlcBaseJpaDAO;


/**
 * Gera script para atualização de esquema de banco de dados da aplicação
 * @author Rodrigo Magno
 * @since jCompany 2.0
 * @version $Id: PlcSchemaUpdate.java,v 1.7 2006/08/14 17:42:57 alvim Exp $
 */

public class PlcSchemaUpdate extends PlcBaseManager {

	protected static Logger log = Logger.getLogger(PlcSchemaUpdate.class);

	/**
	 * TODO Fazer gerador de esquema na camada modelo
	 * Cria um script para atualizacao a partir da configuração informada
	 */
	public String generate (String actionType,String objTable, 
			String objConstraint, 
			String objSequence, 
			String objIndex, 
			String delimiter) throws PlcException {

		String[] dropSQL;
		String[] createSQL;
		String[] updateSQL;
		Configuration cfg = null;
		
		IPlcDAO dao = PlcPersistenceLocator.getInstance().getDaoDefault();
		
		if (dao instanceof PlcBaseJpaDAO) {
			//TODO Descobrir uma maneira de suportar a geração interativa das DDL com JPA
			throw new PlcException("jcompany.errors.functional.notsupported");
		} else {
			cfg = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getCfg();
		}
		

		if (cfg == null)
			throw new PlcException("jcompany.errors.cfg.null");
		
		Properties connectionProperties = cfg.getProperties();
		Dialect dialect = Dialect.getDialect(connectionProperties);	
			
		if (cfg != null){
				
			if(actionType.equals("C")){
				log.debug("Entered to call mathod to execute database creation script");
				createSQL = getCreateSQL(cfg, dialect);
				String ddlFormated = formatDDL(objTable, objConstraint, objSequence, objIndex,delimiter, createSQL);
				return ddlFormated;
			
			} else if(actionType.equals("D")){
				
				log.debug("Entered to call mathod to execute database exclusion script");
				dropSQL = getDropSQL(cfg, dialect);
				String ddlFormated = formatDDL(objTable, objConstraint, objSequence, objIndex,delimiter, dropSQL);
				return ddlFormated;
			
			} else if(actionType.equals("U")){
				
				log.debug("Entered to call mathod to execute database update script");
				updateSQL = getUpdateSQL(cfg,dialect);		
				String ddlFormated = formatDDL(objTable, objConstraint, objSequence, objIndex,delimiter, updateSQL);
				return ddlFormated;
			}
			else
				throw new PlcException ("jcompany.schema.error.type");
			
		} else
			throw new PlcException ("jcompany.schema.error.type");

	}
	
    /**
     * @return Returns the createSQL.
     * @throws HibernateException
     */
    public String[] getCreateSQL(Configuration cfg, Dialect dialect) throws HibernateException {
        return 	cfg.generateSchemaCreationScript(dialect);
    }
    
    /**
     * @return Returns the dropSQL.
     * @throws HibernateException
     */
    public String[] getDropSQL(Configuration cfg, Dialect dialect) throws HibernateException {
        return cfg.generateDropSchemaScript(dialect);
    }
    
    /**
     * @return Returns the updateSQL.
     * @throws HibernateException
     */
    public String[] getUpdateSQL(Configuration cfg, Dialect dialect) throws HibernateException, PlcException {
    	try {
			Session sess = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getSession();
    		DatabaseMetadata  meta = new DatabaseMetadata(sess.connection(), dialect);
    		return cfg.generateSchemaUpdateScript(dialect,meta);
    	
    	} catch (Exception e) {
			throw new PlcException("jcompany.error.update.script",new Object[]{e},e,log);
		}
    }

    /**
	 * Format an SQL statement using simple rules:
	 *  a) Insert newline after each comma;
	 *  b) Indent three spaces after each inserted newline;
	 * If the statement contains single/double quotes return unchanged,
	 * it is too complex and could be broken by simple formatting.
	 */
	private static String format(String sql) {
		if ( sql.toLowerCase().startsWith("create table") ) {
			return formatCreateTable( sql );
		}
		else if ( sql.toLowerCase().startsWith("alter table") ) {
			return formatAlterTable( sql );
		}
		else if ( sql.toLowerCase().startsWith("comment on") ) {
			return formatCommentOn( sql );
		}
		else {
			return sql;
		}
	}
	private static String formatCommentOn(String sql) {
		StringBuffer result = new StringBuffer(60);
		StringTokenizer tokens = new StringTokenizer( sql, " '[]\"", true );

		boolean quoted = false;
		while ( tokens.hasMoreTokens() ) {
			String token = tokens.nextToken();
			result.append(token);
			if ( isQuote(token) ) {
				quoted = !quoted;
			}
			else if (!quoted) {
				if ( "is".equals(token) ) {
					result.append("\n   ");
				}
			}
		}
		
		return result.toString();
	}

	private static String formatAlterTable(String sql) {
		StringBuffer result = new StringBuffer(60);
		StringTokenizer tokens = new StringTokenizer( sql, " (,)'[]\"", true );

		boolean quoted = false;
		while ( tokens.hasMoreTokens() ) {
			String token = tokens.nextToken();
			if ( isQuote(token) ) {
				quoted = !quoted;
			}
			else if (!quoted) {
				if ( isBreak(token) ) {
					result.append("\n    ");
				}
			}
			result.append(token);
		}
		
		return result.toString();
	}

	private static String formatCreateTable(String sql) {
		StringBuffer result = new StringBuffer(60);
		StringTokenizer tokens = new StringTokenizer( sql, "(,)'[]\"", true );

		int depth = 0;
		boolean quoted = false;
		while ( tokens.hasMoreTokens() ) {
			String token = tokens.nextToken();
			if ( isQuote(token) ) {
				quoted = !quoted;
				result.append(token);
			}
			else if (quoted) {
				result.append(token);
			}
			else {
				if ( ")".equals(token) ) {
					depth--;
					if (depth==0) result.append("\n");
				}
				result.append(token);
				if ( ",".equals(token) && depth==1 ) result.append("\n   ");
				if ( "(".equals(token) ) {
					depth++;
					if (depth==1) result.append("\n    ");
				}
			}
		}
		
		return result.toString();
	}

	private static boolean isBreak(String token) {
		return "add".equals(token) || 
			"references".equals(token) || 
			"foreign".equals(token) ||
			"on".equals(token);
	}

	private static boolean isQuote(String tok) {
		return "\"".equals(tok) || 
				"`".equals(tok) || 
				"]".equals(tok) || 
				"[".equals(tok) ||
				"'".equals(tok);
	}


	/**
	 * Método responsável por obter o dialeto da hibernate e acionar o modificador
	 * de esquema especifico para o banco corrente
	 * @param schema String contendo o esquema do banco original
	 * @return String contendo o esquema do banco modificado
	 */
	public static String apiAdjustSchema(String schema) throws PlcException {

		log.debug("###### Entered to generate specific database schema");

		Configuration cfg 	= PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getCfg();

		String dialect = (String) cfg.getProperties().get("hibernate.dialect");

		if (log.isDebugEnabled())
			log.debug("###### Current dialiect: " + dialect);

		if (dialect.trim().equalsIgnoreCase(DerbyDialect.class.getName())) {
			schema = generateSpecificCloudscapeSchema(schema);

		}

		return schema;
	}

	/**
	 * Método responsável por realizar a substituição de todas as strings
	 * informadas para o banco de dados cloudscape.
	 * @param schema String contendo o esquema de banco original
	 * @return String contendo o esquema de banco modificado
	 */
	private static String generateSpecificCloudscapeSchema(String schema) {

		log.debug("###### Entered to generate specific cloudscape database schema");

		schema = StringUtils.replace(schema,"generated by default as identity", "generated always as identity");

		return schema;
	}
	
	/** Trata o script DDL. Formata para visualização e/ou grava para arquivo quando requisitado.
	 * @throws IOException
     */
    protected String formatDDL(String objTable, 
    		String objConstraint, 
    		String objSequence, 
    		String objIndex,
    		String delimitater, 
    		String[] ddl)
    		throws PlcException {
	    
    	String ddlFormated = "";
    	
    	for(int j = 0; j < ddl.length; j++) {
		    	if(ddlValidate(ddl[j], objTable, objConstraint, objSequence,objIndex)){
		    		String oneDDLClause = formatDDLCustomize(ddlFormated,ddl[j]);
		       	    ddlFormated = ddlFormated + format (oneDDLClause);
		        	if (delimitater!=null)
		        	    ddlFormated += delimitater + "\n ";
		        	ddlFormated = apiAdjustSchema(ddlFormated);	    	    	
		        //   return ddlFormatada;
		    	}
	    }
	    
    	return ddlFormated;

    }


    /**
     * @since jCompany 3.2.3 Customiza DDL aprimorando nomenclatura e fazendo outros ajustes
     * @param oneDDLClause Uma cláusulo DDL, podendo ser um Create Table, um Alter Table ou outros.
     * @return clausula modificada
     */
    protected String formatDDLCustomize(String ddlFull, String oneDDLClause) throws PlcException {
    	
    	// Correções
    	oneDDLClause = formataDDLCorrect(oneDDLClause);
    	
		return formatDDLCustomizeApi(oneDDLClause);
	}
    
    protected String formataDDLCorrect(String oneDDLClause) {
    	
    	// Corrige geraçao errada para Oracle Exemplo: varchar2(2 char); para varchar2(2); e variacoes com virgula ou espaco apos
    	return StringUtils.replace(StringUtils.replace(StringUtils.replace(oneDDLClause," char),", "),")," char);", ");")," char) ", ") ");
    }

	/**
     * @since jCompany 3.2.3 Permite extensões na customização de DDL aprimorando nomenclatura e fazendo outros ajustes
     * @param oneDDLClause Uma cláusulo DDL, podendo ser um Create Table, um Alter Table ou outros.
     * @return clausula modificada
     */
    protected String formatDDLCustomizeApi(String oneDDLClause) throws PlcException {
		return oneDDLClause;
	}

	/** Valida a ddl quanto à geração de algum objeto específico
     * @param ddl DDL a ser validada
     * @param objTable Indica que ddl deve ser de tabela
     * @param objConstraint Indica que ddl deve ser de constraint
     * @param objSequence Indica que ddl deve ser de sequence
     */
    protected boolean ddlValidate(String ddl, String objTable, String objConstraint, String objSequence,String objIndex) throws PlcException {

    	if(((objTable.equals("N") && objConstraint.equals("N") && objSequence.equals("N") && objIndex.equals("N")) ||
    	   (objTable.equals("S") && (ddl.indexOf("create table") >= 0 || ddl.indexOf("drop table") >= 0
    	           || (ddl.indexOf("alter table") >= 0 && ddl.indexOf("constraint") == -1))) ||
           (objConstraint.equals("S") && (ddl.indexOf("constraint") >= 0)) ||
           (objIndex.equals("S") && (ddl.indexOf("index") >= 0)) ||
           (ddl.startsWith("insert into")) ||
           (objSequence.equals("S") && (ddl.indexOf("sequence") >= 0))) &&
           !eFKAudit(ddl))
    	    return true;
    	else
    	    return false;
    }
    
    /**
     * @param ddl Trecho de DDL para análise
     * @return true se for um DDL para FK em tabela de auditoria
     */
    protected boolean eFKAudit(String ddl)throws PlcException {
		return ddl != null && ddl.toLowerCase().indexOf("references ")>-1 && 
				ddl.toLowerCase().indexOf("_"+
				PlcConfigHelper.getInstance().get(PlcConfigAudit.class).classAuditSuffix().toLowerCase())>-1;
	}

	/**
	 * Cria um script para criação/exclusão a partir da configuração informada
	 * @param delimiter Delimitador das linhas do script
	 */
	public void execute (String ddl, String delimiter) throws HibernateException, PlcException {

		log.debug("Entered to execute database schema script");

		String[] sql = StringUtils.split(ddl, delimiter);

		try {
				Session sess = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getSession();

				if (sess.isConnected()) {
					
					sess.connection().commit();
				
					try		{
						PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getSession().connection().setAutoCommit(true);
					} catch (Exception e)
					{
						log.debug("Sybase Exception: this database doens't allow to alter autocommit to false");
					}
				}
			java.sql.Statement statement = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getSession().connection().createStatement();
			for(int i = 0; i < sql.length; i++){
				log.debug("Executing DDL: \n"+ddl);
			   statement.executeUpdate(sql[i]);
			}
	
		} catch(Exception e) {
			// Evita exceção que é disparada indevidamente
			if (e.getMessage() != null && e.getMessage().indexOf("SQL passed with no tokens")>-1)
				return;
			throw new PlcException("jcompany.schema.error.generating",new Object[]{e},e,log);
		}
	}

}