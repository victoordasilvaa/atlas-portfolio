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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.persistence.PlcBaseConfigListener;
import org.jcompany.persistence.PlcConstantsPersistence;
import org.jcompany.persistence.hibernate.helper.PlcHibernateHelper;


/**
 * jCompany 3.0. IoC para Gerenciameto de Fábrica e Sessões Hibernate
 * Trata situação de várias fábricas, mantendo um Map cuja chave é o nome da fábrica e o valor é a referencia.
 * @since jCompany 3.0
 * @version $Id: PlcBaseHibernateManager.java,v 1.12 2006/08/17 17:06:04 alvim Exp $
 */
public abstract class PlcBaseHibernateManager extends PlcBaseConfigListener {
		
	private SessionFactory sessionFactory = null;
	private Configuration cfg = null;
	private String poolJNDI = null;
	private Boolean autoDetectDialect = true;
	protected  PlcBaseHibernateListener listener = null;  		
    protected static Logger log = Logger.getLogger(PlcBaseHibernateManager.class);
	  
	  /**
	   * @since jCompany 3.1
	   * Pseudo-ID. Classe que implementa eventos de Listener. Deve ser sobreposto para especializações do Listener
	   */
	  protected PlcBaseHibernateListener getListener() {
		  if (listener == null)
			  listener = new PlcBaseHibernateListener();
		  return listener;
	  }
	  
	  /**
	 * @param listener The listener to set.
	 */
	public void setListener(PlcBaseHibernateListener listener) {
		this.listener = listener;
	}



	/**
	   * jCompany 3.0 Recupera uma fábrica de sessões. Se não existir (não foi criada) cria com classe default PlcHibernateManager.
	   */
	  public SessionFactory getSessionFactory() {
	      return sessionFactory;
	  }
   
	  public void registerFactory() throws PlcException {
		  registerFactory(PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT);
	  }
	  
    /**
     * jCompany 3.0 Registra fábricas da Hibernate. A primeira fábrica, configurada por hibernate.cfg.xml, pode
     * ser registrado com nomes 'default' (uso clássico) ou 'hibernate', que é o 
     * uso mais lógico, seguindo a convenção: <nome-fabrica>.cfg.xml.
     * As fábricas adicionais devem seguir esta convenção.
     */
    public void registerFactory(String factory) throws PlcException {
        
    	try {
    		   		
    		autoDetectDialect = PlcAnnotationHelper.getInstance().getFactoryAutoDetectDialect(this.getClass());
    		if (factory.equals(PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT))
    		    factory = PlcAnnotationHelper.getInstance().getFactoryName(this.getClass());
    		
    	    log.info("######## Entered to configure factory : " + factory +" and autoDetectDialect: "+autoDetectDialect);
    	   
    		AnnotationConfiguration cfgA = new AnnotationConfiguration();

    		configListeners(factory,cfgA);

    		configInterceptors(cfgA);

    		String factoryName = null;
    		if (PlcConstantsPersistence.CONFIG.FACTORY_DEFAULT.equals(factory))
    			factoryName = PlcConstantsHibernate.CONFIG.FACTORY_DEFAULT_HIBERNATE+PlcConstantsHibernate.CONFIG.FILE_CFG_SUFFIX;
    		else
    			factoryName = factory + PlcConstantsHibernate.CONFIG.FILE_CFG_SUFFIX;

    		Configuration config = cfgA.configure(factoryName);

    		registerFactoryConfigDialect(factory,cfgA);
		    
			// Alteração auxiliar para convivência com modo de teste
		    alterHibernateProperties(config);


			SessionFactory sfA = config.buildSessionFactory();
			
			this.sessionFactory = sfA;
			this.cfg = cfgA;
			this.poolJNDI = cfgA.getProperty("hibernate.connection.datasource");

    		log.info("######## Configured factory: " + factory +" and autoDetectDialect: "+autoDetectDialect);

		} catch (Exception e) {
			// Incluido pois o stack do tratamento automatizado nao envia linha correta
			e.printStackTrace();
			throw new PlcException("jcompany.error.generic", new Object[] {
					"registerFactory", e }, e, log);
		}

    }

    /**
     * Registra Interceptor utilizado em logicas de profiling, para captura de SQLs,
     * já que os Listeners ainda nao disponibilizaram esta facilidade (3.2 RC2)
     * @param cfgA Configuration
     */
    protected void configInterceptors(AnnotationConfiguration cfgA) throws PlcException {
		
    	cfgA.setInterceptor(new PlcBaseInterceptor());
		
	}

	/**
     * Registra dialeto automaticamente se a anotação do gerenciador estiver com o default true e se não
     * tiver uma marcação no hibernate**.cfg.xml negando com:<p>
     *    <property name="plc.auto.detect.dialect">false</property>
     *    <p>
     * Pode ainda ser sobreposto em descendentes para especialização.
     * @since jCompany 3.1
     * @param name Nome da fábrica
     * @param cfgA Configuration Hibernate
     */
    protected void registerFactoryConfigDialect(String name,Configuration cfgA) throws PlcException {
		log.debug("############### Entered in registerFactoryConfigDialect");
		
	    if (autoDetectDialect && !("N".equals(cfgA.getProperties().get(PlcConstantsPersistence.GLOBAL.AUTO_DETECT_DIALECT))))
			PlcHibernateHelper.getInstance().configureDialectAutomatically(name, cfgA);
	}

   /** 
    * @since jCompany 3.0. Pega sessão corrente da Hibernate (ou abre uma nova se for primeira chamada), 
    * utilizando a fábrica informada<p>:
    * @return Sessão corrente no novo padrão Hibernate 3.1
    */
   public Session getSession() throws PlcException {

	   log.debug("############### Entered in getSession");
	   
	   // Pega a fábrica conforme seu nome
	   Session sess = getSessionFactory().getCurrentSession();

	   sess.setFlushMode(FlushMode.COMMIT);
	   
	   // Inicia uma transação. Se já estiver iniciada, a Hibernate reusa!
	   sess.beginTransaction();
	   
	   // Retorna a sessão com transação aberta
	   return sess;
	   
	   
  	}

   /* ************************************************************************************* */
   /* *********************** AUXILIARES PARA MODE TESTE ********************************** */
   /* ************************************************************************************* */

   /**
    * jCompany 3.0 Altera propriedades em modo de teste, dinamicamente, para reusar o mesmo arquivo 'cfg.xml', trocando
    * pool de conexões por acesso simples JDBC
    */
   protected static void alterHibernateProperties(Configuration config) {

  	 	Properties props = config.getProperties();
   		
		if(isTestMode()) {
			// Altera configurações para utilização de conexão direta
   			props.remove("hibernate.connection.provider_class");
   			props.remove("hibernate.connection.datasource");
   			props.remove("connection.provider_class");
   			props.remove("connection.datasource");
   		}else {
   			props.remove("hibernate.connection.pool_size");
   			props.remove("hibernate.connection.password");
   			props.remove("hibernate.connection.username");
   			props.remove("hibernate.connection.url");
   			props.remove("hibernate.connection.driver_class");
   			props.remove("connection.pool_size");
   			props.remove("connection.password");
   			props.remove("connection.username");
   			props.remove("connection.url");
   			props.remove("connection.driver_class");
   		}		
	}

   /**
    * jCompany 3.0 Auxiliar para modo de teste automatizado via JUnit ou JWebUnit
    * @return true se tem variável de ambiente modoTeste com valor 'true'
    */
   public static boolean isTestMode() {
	   	if(System.getProperty("testMode")!=null && System.getProperty("testMode").equals("true"))
	   		return true;
	   	return false;
   }

	/**
	 * @return Returns the autoDetectDialect.
	 */
	public Boolean getAutoDetectDialect() {
		return autoDetectDialect;
	}
	
	/**
	 * @param autoDetectDialect The autoDetectDialect to set.
	 */
	public void setAutoDetectDialect(Boolean autoDetectDialect) {
		this.autoDetectDialect = autoDetectDialect;
	}

	/**
	 * @return Returns the cfg.
	 */
	public Configuration getCfg() {
		return cfg;
	}

	/**
	 * @param cfg The cfg to set.
	 */
	public void setCfg(Configuration cfg) {
		this.cfg = cfg;
	}

	/**
	 * @return Returns the poolJNDI.
	 */
	public String getPoolJNDI() {
		return poolJNDI;
	}

	/**
	 * @param poolJNDI The poolJNDI to set.
	 */
	public void setPoolJNDI(String poolJNDI) {
		this.poolJNDI = poolJNDI;
	}

	/**
	 * @param sessionFactory The sessionFactory to set.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
}
