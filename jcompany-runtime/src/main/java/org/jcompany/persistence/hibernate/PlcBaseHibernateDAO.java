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

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.PropertyValueException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ANNOTATION;
import org.jcompany.commons.PlcConstantsCommons.ENTITY;
import org.jcompany.commons.annotation.PlcPrimaryKey;
import org.jcompany.commons.annotation.PlcEntityMetadata;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcDateHelper;
import org.jcompany.commons.helper.PlcStringHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.persistence.PlcBaseDAO;
import org.jcompany.persistence.PlcConstantsPersistence;
import org.jcompany.persistence.helper.PlcPersistenceHelper;
import org.jcompany.persistence.hibernate.helper.PlcAnnotationPersistenceHelper;
import org.jcompany.persistence.hibernate.helper.PlcHibernateHelper;
import org.jcompany.persistence.hibernate.service.PlcQBEHibernateService;
import org.jcompany.persistence.service.PlcQBEService;



/**
 * @since jCompany 3.0
 * @version $Id: PlcBaseHibernateDAO.java,v 1.27 2006/08/22 20:06:53 bruno_grossi Exp $
 * Classe DAO com implementação para Hibernate
 *
 */
public class PlcBaseHibernateDAO extends PlcBaseDAO {

    protected Logger log = Logger.getLogger(PlcBaseHibernateDAO.class);
    protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
    
    public PlcBaseHibernateDAO(){    }
    /**
     * Construtor auxiliar com possível injecao de dependências para facilitar testes de unidade
     * Importante: o jCompany usa Setter Based Injection nesta classe! Não utiliza este construtor!
     */
    public PlcBaseHibernateDAO(PlcQBEHibernateService serviceQBE){
    	this.serviceQBE = serviceQBE;
    }
    
	/* **************************************************************************************** */
	/* **************** SERVIÇOS SIMPLE DISPONIBILIZADOS COMO SINGLETONS     ***************** */
	/* **************************************************************************************** */

 	/**
	 * @since jCompany 3.0 
	 * 
	 * Utilitários para a manipulação de Anotações específicas da camada de persistência
	 */
	protected PlcAnnotationPersistenceHelper helperAnnotation = PlcAnnotationPersistenceHelper.getInstance();

	/**
	* @since jCompany 3.0 
	*  
	* Utilitários para a manipulação de configurações do Hibernate
	 */
	protected PlcHibernateHelper helperHibernate = PlcHibernateHelper.getInstance();

	/* **************************************************************************************** */
	/* **************** SERVIÇOS IMPORTANTES DISPONIBILIZADOS COM DI             ************** */
	/* **************************************************************************************** */

    /**
	 * @since jCompany 3.0 
	 *  
	 * Utilitários para a manipulação de Query By Example com Hibernate
	 */
	private PlcQBEHibernateService serviceQBE;

	 /**
	 * @return Serviço de QBE para Hibernate
	 */
	public PlcQBEHibernateService getServiceQBE() {
		//TODO Verificar porque chega null em recuperaQBETotal
		if(serviceQBE==null)
			serviceQBE = new PlcQBEHibernateService();
		return serviceQBE;
	}
	
	
	/**
	 * Encupsula o helper p/ fatoração dos algoritmos dependentes
	 * @since jCompany 5.0
	 * @return
	 */
	@Override
	protected PlcPersistenceHelper getHelper() {
		return helperHibernate;
	}

	/**
	 * @param serviceQBE The serviceQBE to set.
	 */
	public void setServiceQBE(PlcQBEHibernateService serviceQBE) {
		this.serviceQBE = serviceQBE;
	}
	
	/* ******************************************************************************************* */
	/* ********************************  INICIO SERVIÇOS APOIO *********************************** */
	/* ******************************************************************************************* */
	
	/**
	  * @since jCompany 3.0 
	  * 
	  * DP Composite. Recebe o contexto do cliente para guardar em ThreadLocal no HibernateManager para
	  * ser usado em 'event listeners': PlcBaseContextVO context = HibernateManager.getContextVO();
	  * @return Devolve sessão Hibernate utilizando fábrica.
	  */
	 protected Session getSession() throws PlcException {

		return getSession(null);
		
	 }
	 
	 /**
	  * @since jCompany 3.0
	  * 
	  * DP Composite. Pega uma sessão utilizando a fábrica explicitada. Se for null, usa a do PlcContext.
	  * @return Devolve sessão Hibernate utilizando fábrica.
	  */
	 protected Session getSession(String factoryPlc) throws PlcException {

		 if (logPersistence.isDebugEnabled())
			 logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":getSession:"));

		 try {
			 String factoryName = factoryPlc;
			 PlcBaseContextVO context = getContext();

			 if (factoryPlc == null && context != null) 
				 factoryName = context.getFactoryPlc();
			 else if (factoryPlc == null && context == null){
				 factoryName = "default";
			 }
			 else{
				 if ( context !=null && context.getFactoryPlc() != null  && 
						 !context.getFactoryPlc().equals(factoryName)	&&
						 "default".equals(factoryName)  )

					 factoryName = context.getFactoryPlc();
			 }		


			 // Se tem mais de uma sessão
			 if (context != null && context.getPersistenceServiceManagers() != null && !PlcHibernateManagerLocator.getInstance().isInitialized(factoryName)) {
				 PlcHibernateManagerLocator.getInstance().setHibernateManagerClasses(context.getPersistenceServiceManagers());
			 }

			 Session session = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass(factoryName).getSession();

			 if (logPersistence.isDebugEnabled())
				 logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						  this.getClass().getSimpleName()+":getSession("+session+"):"));

			 return session;

		 } catch (Exception e) {
			 throw new PlcException("jcompany.error.generic", new Object[] {
					 "getSession", e }, e,log);
		 }
	 }

	 public Configuration getCfg() throws PlcException {
	       return PlcHibernateManagerLocator.getInstance().getHibernateManagerClass("default").getCfg();
	 }
	 
	 /**
	  * @return Se o jCompany esta deve otimizar a atualização das entidades persistidas.
	  */
	 @Override
	 public boolean isOptimizeUpdate() throws PlcException {
			return "S".equals(getCfg().getProperties().get(PlcConstantsPersistence.GLOBAL.UPDATE_OPTIMIZE));
	 }
	 
	/* ******************************************************************************************* */
	/* ********************************  INICIO ACESSO META-DADOS *********************************** */
	/* ******************************************************************************************* */
	 
	/**
	 * Obtém filhos possíveis da classeBase informada, investigando nos meta-dados na Hibernate
	 *  quais classes possuem relacionamento "many-to-one" com a informada. Delega para helper.
	 * @since jCompany 3.03
	 */   
	 public List<Class> retrievePossibleDescendents(Class classBase) throws PlcException{
		 
		 Configuration cfg = getCfg();
		 List<Class> l = new ArrayList<Class>();
		 Iterator i = cfg.getClassMappings();
		 try {
	
			 while (i.hasNext()) {
				 PersistentClass pc = (PersistentClass) i.next();
				 if (helperHibernate.referenceWithManyToOne(pc,classBase.getName()) &&
						 PlcAnnotationHelper.getInstance().explorerUse(pc.getClassName()))
					 l.add(Class.forName(pc.getClassName()));
			 }
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrievePossibleDescendents", e }, e, log);
		}
		 
		return l;

	 }
	 
	/* ******************************************************************************************* */
	/* ********************************  INICIO TRATAMENTO MSG *********************************** */
	/* ******************************************************************************************* */
	
	/**
	 * @since jCompany 3.0
	 * 
	 * Deve verificar o type da Exceção e devolver msg de erro aprpriada
	 * @param rootCause Exception a ser investigada
	 * @return null se não for de responsabilidade da persistencia ou String[0]: msg internacionalizada,
	 * String[1]: arg1 (opcional) e String[2]: arg2 (opcional)
	 */
    @Override
    public String[] msgExceptionHandle(Throwable rootCause) throws PlcException {

    	if (logPersistence.isDebugEnabled())
    		logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":msgExceptionHandle:"));

    	if (rootCause != null && 
    			(SQLException.class.isAssignableFrom(rootCause.getClass()))) {
    		// Se é erro de JDBC
    		return msgExcecaoHandleSQL((SQLException)rootCause);
    	}

    	if (rootCause != null && rootCause instanceof HibernateException) {
    		// Trata erros de Hibernate (não controlados)	
    		return msgExcecaoHandleHibernate((HibernateException)rootCause);
    	} 

    	String[] ret = apiHandleErrorsModelSpecific(rootCause);

    	if (logPersistence.isDebugEnabled())
    		logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":msgExceptionHandle:"));

    	return ret;

    }

	protected String[] msgExcecaoHandleConstraint (ConstraintViolationException errorSQL) {
		return new String[]{"jcompany.errors.persistence.exclude.constraint",errorSQL.getLocalizedMessage()};
	}
	/**
	 * Trata erros de JDBC e devolve msg apropriada genérica
	 */
  	protected String[] msgExcecaoHandleSQL (SQLException errorSQL) {
  		if (errorSQL.getErrorCode() == 2292) {
			return new String[]{"jcompany.errors.commit.integrity",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 1400) {
			return new String[]{"jcompany.errors.column.null",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 1401) {
			return new String[]{"jcompany.errors.value.size",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 0001) {
			return new String[]{"jcompany.errors.sql.duplicated.key",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 1407) {
			return new String[]{"jcompany.errors.sql.column.null",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == -1 && errorSQL.getMessage().contains("foreign key constraint")){
			return new String[]{"jcompany.errors.persistence.exclude.constraint",errorSQL.getLocalizedMessage()};
		}else{
			return new String[]{"jcompany.errors.sql.unexpected",errorSQL.getLocalizedMessage()};
		}
	}
  	
	
	/**
	 * Trata erros capturados pela Hibernate
	 */
	public String[] msgExcecaoHandleHibernate (HibernateException errorH) {
		
		if (errorH instanceof StaleObjectStateException) {
			return new String[]{"jcompany.errors.commit.concurrence",errorH.getLocalizedMessage()};
		} else if (errorH instanceof PropertyValueException) {
			int pos = errorH.getLocalizedMessage().lastIndexOf(".");
			String atributo = ((PropertyValueException)errorH).getPropertyName();
			return new String[]{"jcompany.errors.null.attribute.not.informed",atributo,errorH.getLocalizedMessage(),((PropertyValueException)errorH).getMessage()};
		} else if (errorH instanceof ObjectNotFoundException) {
			return new String[]{"jcompany.errors.commit.not.found",errorH.getLocalizedMessage()};
		} else if (errorH instanceof IdentifierGenerationException) {
			return new String[]{"jcompany.errors.commit.key.not.informed",errorH.getLocalizedMessage()};
		} else if (errorH.getLocalizedMessage().indexOf("Another object was associated with this id") > -1) {
			return new String[]{"jcompany.errors.sql.duplicated.key",errorH.getLocalizedMessage()};	
		} else {
			return new String[]{"jcompany.error.generic",errorH.getLocalizedMessage()};
		}
	}

	/**
     * @since jCompany 3.0
     * 
     * Template-Method para especialização do tratamento de mensagens da camada modelo
     * @param rootCause Exceçao
     * @return String[] contendo mensagem + args. Exemplo: return new String[]{"apl.errors.commit.concorrencia",causaRaiz.toString()};
     * ou null se a exceção nao form reconhecida para a camada modelo
     */
	protected String[] apiHandleErrorsModelSpecific(Throwable rootCause) throws PlcException {
		return null;
	}

	/* ******************************************************************************************* */
	/* ********************************   INICIO GER. TRANSACAO  ********************************* */
	/* ******************************************************************************************* */

	/**
	 * @since jCompany 3.0
	 * 
	 * Encerra uma transação/sessao com commit
	 * @param factory Nome da fabrica a ser utilizada
	 */
    @Override
	public void commit(String factory) throws PlcException {
		 
     	 if (log.isDebugEnabled())
    		 log.debug("########## COMMIT TO FACTORY "+factory+"!");
    	 
    	 if ((logProfiling.isDebugEnabled() && PlcAopProfilingHelper.getInstance().getLevel()>=1) || logPersistence.isDebugEnabled())
			PlcAopProfilingHelper.getInstance().transactionCount("commit");

    	 PlcBaseHibernateManager manager = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass(factory); 
		 if (manager != null)
			 manager.getSession().getTransaction().commit();
		 
	  	if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":COMMIT"));
	
	}
    
	
	/**
	 * @since jCompany 3.0
	 * 
	 * Encerra uma transação/sessao com rollback
	 * @param factory Nome da fabrica a ser utilizada
	 */
    @Override
	public void rollback(String factory) throws PlcException {

      	 if (log.isDebugEnabled())
    		 log.debug("########## ROLLBACK TO FACTORY "+factory+"!");

       	 if ((logProfiling.isDebugEnabled() && PlcAopProfilingHelper.getInstance().getLevel()>=1) || logPersistence.isDebugEnabled())
			PlcAopProfilingHelper.getInstance().transactionCount("rollback");

       	PlcBaseHibernateManager manager = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass(factory);
       	if (manager != null)
       		manager.getSession().getTransaction().rollback();
       	
	  	if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":ROLLBACK"));

	}   
	
	/**
	 * jCompany 3.0 Torna o objeto somente de consulta durante uma sessão
	 */
    @Override
	public void registerConsultationOnly(Object beanMain, Collection c) throws PlcException {
		Session sess = getSession();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Object det = (Object) iter.next();
			sess.setReadOnly(det,true);
			sess.evict(det);
		}
	}


	/**
	 * @since jCompany 3.0
	 * 
	 * Dispara os comandos em buffer gerenciados pela engine de persistencia até o momento.
	 * Importante: Não faz confirmação final (commit, por exemplo), somente envia.
	 */
    @Override
    public void sendCacheCommands(Class clazz) throws PlcException{

    	if (logPersistence.isDebugEnabled())
    		logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":sendCacheCommands:"));

    	Session sess = null;
    	try {
    		if(null != clazz) {
    			sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(clazz));
    		} else {
    			sess = getSession();
    		}

    		sess.flush();
    	} catch (Exception e) {
    		throw new PlcException("jcompany.error.generic", new Object[] {
    				"sendCacheCommands", e }, e,log);
    	}

    	if (logPersistence.isDebugEnabled())
    		logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":sendCacheCommands:"));

    }

	/* ******************************************************************************************* */
	/* ********************************      INICIO CRUD    ************************************** */
	/* ******************************************************************************************* */


	/**
	 * @since jCompany 1.0
	 * Inclui (Salva) um Value Object no Banco de Dados.
	 */
	@Override
	public Long insert(Object entity) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":insert:"));

		try {
			
			getHelper().doPhoneticsTreatment(entity);
			
			Session sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
			
			Object pk = sess.save(entity);
			// Nao precisa retornar chave natural

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":insert:"));

			if (pk instanceof Long)
				return (Long) pk;
			else
				return null;
			
		} catch (Exception e) {
			 throw new PlcException("jcompany.error.generic", new Object[] {"insert", e }, e,log);
		}

	}

	/**
	 * Recupera uma instância da classe identificada pelo id
	 * @since jCompany 5.0
	 * @param clazz Tipo do objeto a ser recuperado
	 * @param id identificacao do objeto
	 * @return instância da classe identificada pelo id
	 * 
	 */
	@Override
	public Object retrieve( Class clazz, Object id) throws PlcException{

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieve:"));

		try {

			applyFilters(clazz);

			Class classAux = getHelper().convertDynamicProxyToOriginalClass(clazz);

			String queryEdition = PlcAnnotationPersistenceHelper.getInstance().getAnnotationQueryEditionDefault(clazz);

			Object entity;

			PlcPrimaryKey pk = (PlcPrimaryKey) classAux.getAnnotation(PlcPrimaryKey.class);
			if (pk==null) {//Não tem chave primária
				queryEdition = getServiceQBE().mountFromWhere(queryEdition, classAux, id);

				entity = apiCreateQuery(clazz,queryEdition).setParameter(0,id).uniqueResult();
			} else {


				if (id != null){
					queryEdition = "from "+classAux.getName()+" obj  ";

					for (String property : pk.properties()) {
						if (!queryEdition.contains("where")) 
							queryEdition 		= queryEdition + " where obj.idNatural." + property +"= :" + property ;
						else
							queryEdition 		= queryEdition + " and obj.idNatural." + property +"= :" + property ;
					}
				}
				Query query = apiCreateQuery(clazz,queryEdition);

				for (String property : pk.properties()) {
					Object value	= PropertyUtils.getProperty(id, property); 
					query.setParameter(property, value);
				}

				entity = query.uniqueResult();
			}

			if (entity==null)
				throw new PlcException("jcompany.errors.record.not.found",new Object[]{id.toString()});

			// Garante que colecoes participantes da lógica principal, mesmo se estiverem Lazy, são carregadas
			// antes do 'detached'
			entity = retrieveDefaultGraph(entity);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieve:"));

			return entity;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"retrieve", e }, e,log);
		}

	}
	
	/**
	 * Garante que colecoes participantes da lógica principal, mesmo se estiverem Lazy, são carregadas
	 * antes do 'detached' 
	 * @since jCompany 3.02
	 * @return ENTITY recebido, caso seja um Proxy, convertido para Objeto efetivo, e incluindo coleções da lógica declaradas como lazy, recuperadas.
	 */
	protected Object retrieveDefaultGraph(Object entity) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveDefaultGraph:"));

		// Transforma proxy do objeto principal no objeto real, caso ele seja um
		transformProxyInRealObject(entity);
		
		if (getContext() != null && !"download".equals(getContext().getOriginalAction())) {
			if (getContext().getMainClass()!=null && getContext().getMainClass().isAssignableFrom(entity.getClass())) {
				// Para cada coleçao declarada na lógica, força a recuperaçao se estiver lazy
				if (getContext().getDetailNamesPlc() != null) {
					Iterator i = getContext().getDetailNamesPlc().iterator();
					while (i.hasNext()) {
						String detColumnName = (String) i.next();
						try {
							//Pode ser download ou exclusao de arquivo.
							if (PropertyUtils.isReadable(entity,detColumnName) && !getContext().isOnDemand(detColumnName))
								Hibernate.initialize(PropertyUtils.getProperty(entity,detColumnName));		
							else if (getContext().isOnDemand(detColumnName)) {
								PropertyUtils.setProperty(entity,detColumnName,null);
							}
							
							
							//Colecoes de detalhe manyToMany devem ter o outro lado anulado para evitar problemas de Lazy Initialization.
							Collection details = (Collection) PropertyUtils.getProperty(entity,detColumnName);
							if (details != null) {
								for (Iterator iter = details.iterator(); iter.hasNext();) {
									Object beanDetail = iter.next();
									
									if (PlcAnnotationHelper.getInstance().existsAnnotationManyToMany(beanDetail.getClass()))
										PropertyUtils.setProperty(beanDetail,PlcAnnotationHelper.getInstance().getEntityNameDefaultProp(entity.getClass(),null),null);
									
									// Sub-Detalhe
									if (getContext().getSubDetailParent()!=null && beanDetail.getClass().getName().equals(getContext().getSubDetailParent())) {
										if (PropertyUtils.isReadable(beanDetail,getContext().getSubDetailPropNameCollection()))
											Hibernate.initialize(PropertyUtils.getProperty(beanDetail,getContext().getSubDetailPropNameCollection()));
									}
								}
							}
							
							
						} catch (Exception e) {
							throw new PlcException("jcompany.error.generic",
									new Object[] { "retrieveDefaultGraph", e }, e, log);
						}
					}
				}
			} else {
				// neste caso é detalhe, entao inicializa a colecao de subdetalhe
				try {
					String subDetailPropNameCollection = getContext().getSubDetailPropNameCollection();
					if (subDetailPropNameCollection!=null && PropertyUtils.isReadable(entity, subDetailPropNameCollection)) {
						Hibernate.initialize(PropertyUtils.getProperty(entity,subDetailPropNameCollection));	
					}
				} catch (Exception e) {
					throw new PlcException("jcompany.error.generic", new Object[] {
							"retrieveDefaultGraph", e }, e, log);
				}
			}	
		}
		
		// Permite extensões próprias
		retrieveDefaultGraphAfter(entity);

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveDefaultGraph:"));
		
		return entity;
		
	}

	/**
	 * Permite extensões a recuperaçao de um ENTITY principal, por exemplo, para forçar recuperação com 
	 * Hibernate.inicialize(...) ou outras técnicas.
	 * @since jCompany 3.02
	 * @param entity Value Object principal real com coleções principais da lógica já recuperadas.
	 */
	protected void retrieveDefaultGraphAfter(Object entity) throws PlcException {	}

	/**
	 * Recupera coleção de detalhes do ENTITY informado
	 * @since jCompany 3.1
	 * @param entity ENTITY Mestre
	 * @param classDetail Classe do Detalhe a ser recuperado
	 * @param namePropDetail Nome da propridade (coleçao) de detalhe a ser montada
	 * @return ENTITY Mestre incluindo coleção de detalhe recuperada.
	 */
	public Object retrieveOnDemand(Object entity, String namePropDetail, Class classDetail) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveOnDemand:"));

		// Transforma proxy do objeto principal no objeto real, caso ele seja um
		transformProxyInRealObject(entity);

		try {

			Query query = apiCreateQuery(classDetail,"select obj from "+classDetail.getName()+
					" obj where obj."+
					PlcEntityHelper.getInstance().getPropertyNamePlc(entity)+" = :pai");
			query.setParameter("pai",entity);
			List l = query.list();

			if (Set.class.isAssignableFrom(PropertyUtils.getPropertyType(entity,namePropDetail))) {
				Set s = new HashSet();
				s.addAll(l);
				PropertyUtils.setProperty(entity,namePropDetail,s);
			} else {
				PropertyUtils.setProperty(entity,namePropDetail,l);
			}

			for (Iterator iter = l.iterator(); iter.hasNext();) {
				Object entityDet = iter.next();
				entityDet = retrieveDefaultGraph(entityDet);
			}


		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {	"retrieveOnDemand", e }, e, log);
		}

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveOnDemand:"));

		return entity;

	}
	
	
	
    @Override
    /**
     * @since jCompany 3.0
     *  
     * Atualiza o estado do objeto no SGBD
     */
    public void update(Object entity) throws PlcException{ 
     	
    	if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alter:"));

    	try {
    		
    		getHelper().doPhoneticsTreatment(entity);
		
    		Session sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
			
			if (log.isDebugEnabled())
				log.debug("Updating entity: "+entity);

			sess.update(entity);
			
		} catch (Exception e) {
			 throw new PlcException("jcompany.error.generic", new Object[] {
	                    "alter", e }, e,log);
		}

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alter:"));

    }
    
	/**
	 * @since jCompany 3.0
	 * 
	 * Realiza serviço de exclusão (sem commit) em persistencia Hibernate
	 */
	@Override
	public void exclude(Object entity) throws PlcException {
			
    	if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":exclude:"));

		try {
			
			Session sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
			
			if (log.isDebugEnabled())
				log.debug("Excluding entity:  "+entity);
			
			sess.delete(entity);
			sess.flush();
			
		} catch (HibernateException e1) {
			//Não é preciso enviar Logger porque o façade já o faz
			throw new PlcException("jcompany.errors.persistence.exclude",new Object[] {e1},e1,log);
		}
	
    	if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":exclude:"));

	}


	/* ******************************************************************************************* */
	/* ********************************  AGREGADO NAVEGACAO     ********************************** */
	/* ******************************************************************************************* */
	
   /**
     * @since jCompany 3.0
     * 
     * Automação de navegação em grafo de VOs
     */
    @SuppressWarnings("unchecked")
	@Override
	public List<Object> retrieveAggregateNavigation(Object entityMain, Object entityAggregateDestiny) throws PlcException {

    	if (logPersistence.isDebugEnabled())
    		logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAggregateNavigation:A:"));

    	try {

    		// Descobre por qual propriedade a classe destino é acessada a partir da origem
    		String propNavegation = getHelper().retrievePropertyManyToOne(entityAggregateDestiny.getClass(), entityMain.getClass());

    		// Recebe o vo principal
    		Object origin = PropertyUtils.getProperty(entityMain,propNavegation);

    		String querySelLookup = PlcAnnotationPersistenceHelper.getInstance().getAnnotationQuerySelLookup(entityAggregateDestiny.getClass());
    		if (querySelLookup==null)
    			querySelLookup="from "+entityAggregateDestiny.getClass().getName() +" obj where obj."+propNavegation+"=?";


    		Query query = apiCreateQuery(entityAggregateDestiny.getClass(),querySelLookup);

    		if (querySelLookup.contains("?")) {
    			query.setParameter(0,origin);
    		} else if (querySelLookup.contains(":id")) {
    			query.setParameter("id", origin);
    		} else if (querySelLookup.contains(":"+propNavegation)) {
    			query.setParameter(propNavegation, origin);
    		} else {
    			throw new PlcException("jcompany.error.querysellookup", new Object[]{entityAggregateDestiny.getClass().getName()});
    		}

    		if (logPersistence.isDebugEnabled())
    			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateNavigation:A:"));

    		return query.list();

    	} catch (Exception e) {
    		throw new PlcException("jcompany.error.generic", new Object[] {
    				"retrieveAggregateNavigation", e }, e,log);
    	}

    }
    
    /**
     * @since jCompany 3.0
     * Recupera relação de registros a partir de uma classe origem com o pk informado
     * @param pk OID ou Classe de chave composta com valores
     * @param aggregateDestiny Classe agregado de destino
     * @return Coleção de valores possíveis para destino.
     */
    public List<Object> retrieveAggregateNavigation(Class classMain, Object pk, Class aggregateDestiny) throws PlcException {

    	if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAggregateNavigation:B:"));

	    try {
	    	
	        // Descobre por qual propriedade a classe destino é acessada a partir da origem
	        String propNavegation = getHelper().retrievePropertyManyToOne(aggregateDestiny, classMain);
	        
	        /*TODO - A qrySelLooup não pode ser utilizada, pois nesta qry pode-se definir where obj.id = :id,
	        * mas o certo é obj where obj."+propNavegacao+".id=?
	        * Criar uma qrySelLookupNavegacao
	        */
	        //String querySelLookup = PlcAnnotationPersistenceHelper.getInstance().getAnotacaoQuerySelLookup(agregadoDestino);
	        String querySelLookup = null;
	        if (querySelLookup==null)
	        	querySelLookup="from "+aggregateDestiny.getName() +" obj where obj."+propNavegation+".id=?";        
	        Query query = apiCreateQuery(aggregateDestiny,querySelLookup);
	        
	        if (querySelLookup.contains("?")) {
	        	query.setParameter(0,pk);
	        } else if (querySelLookup.contains(":id")) {
	        	query.setParameter("id", pk);
	        } else {
	        	throw new PlcException("jcompany.error.querysellookup", new Object[]{aggregateDestiny.getName()});
	        }
	        
	       	if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateNavigation:B:"));

			return query.list();

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveAggregateNavigation", e }, e,log);
        }

    }
    
    /**
     * Recupera classe de lookup, somente trazendo propriedades relacionadas em getLookupPropsPlc,
     * caso o método seja declarado no ENTITY. Caso contrário, recupera com "load" (deve-se ter cuidado, neste caso,
     * com relacionamento com lazy=false, para evitar recuperações excessivas)<p>
     * Importante: somente para chaves Object-ID, nesta versão.
     * TODO Nesta versão, não respeita o filtro vertical
     * @param baseEntity ENTITY a ser recuperado
     * @param propertyName nome da propriedade que será utilizada para a pesquisa. Se null, será considerado o OID.
     * @param value valor de pesquisa da propriedade indicada.
     * @param props Relação de propriedades, com exceção das chaves, a serem recuperadas
     * @return ENTITY contendo propriedades indicadas.
 	 */
    @Override
    public Object retrieveLookupAggregate(Object baseEntity,
    		String propertyName, Object value, String[] props) throws PlcException {

    	if (logPersistence.isDebugEnabled())
    		logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregate:"));

    	try {

    		if (propertyName==null || propertyName.trim().length()==0) {
    			propertyName = "id";
    		}

    		Object baseEntityRetrieved = retrieveLookupAggregateByNamedQuery(baseEntity.getClass(), propertyName, value);
    		if (baseEntityRetrieved==null) {
    			PropertyUtils.setProperty(baseEntity, propertyName, value);
    			//baseVO.setId((Long)id);

    			// Se tem declaração do método getLookupPropsPlc, recupera somente estas propriedades
    			if (PropertyUtils.isReadable(baseEntity,"lookupPropsPlc")) {

    				Object lookupPropsPlcObj = PropertyUtils.getProperty(baseEntity,"lookupPropsPlc");

    				String[] lookupPropsPlc = (String[]) lookupPropsPlcObj;

    				baseEntityRetrieved = retrieveEntityWithoutGraph(baseEntity,propertyName,lookupPropsPlc);

    			} else {

    				// Se não tem declaração do método getLookupPropsPlc, recupera com load
    				baseEntityRetrieved = retrieveEntityWithGraph(baseEntity,propertyName);

    			}

    		}

    		if (logPersistence.isDebugEnabled())
    			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregate:"));

    		return baseEntityRetrieved;

    	} catch (Exception e) {
    		throw new PlcException("jcompany.error.generic", new Object[] {
    				"retrieveLookupAggregate", e }, e,log);
    	}

    }
    
    /**
     * Utiliza NamedQuery padrao para recuperar agregacao
     */
	protected Object retrieveLookupAggregateByNamedQuery(Class clazz, String propertyName, Object value) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregateByNamedQuery:"));

		NamedQuery namedQuery = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz, PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_LOOKUP);
		if (namedQuery!=null) {
			Session session = getSession(PlcAnnotationHelper.getInstance().getFactoryName(clazz));
			Query query = session.getNamedQuery(namedQuery.name());
			String queryString = query.getQueryString();
			if (queryString.contains("?")) {
				query.setParameter(0, value);
			} else {
				query.setParameter(propertyName, value);
			}
			List list = query.list();
			if (list.size()>0) {

				if (logPersistence.isDebugEnabled())
					logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregateByNamedQuery:"));

				return list.get(0);
			}
		}

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregateByNamedQuery:null:"));

		return null;
	}

	/**
	 * Recupera agregado utilizando query nomeada padrão
	 */
	@SuppressWarnings("unchecked")
	protected Object retrieveLookupAggregateByNamedQuery(Object entity, Map<String, Object> propertiesValues) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregateByNamedQuery:"));

		NamedQuery namedQuery = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(entity.getClass(), PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_LOOKUP);
		
		Session session = getSession(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
		Query query = null;
		if (namedQuery!=null) 
			query = session.getNamedQuery(namedQuery.name());
		else 
			query = session.createQuery("from "+entity.getClass().getSimpleName() +" obj where obj.id=?");
				
		if (query!=null) {
			String queryString 				= query.getQueryString();
			PlcPrimaryKey pk 	= entity.getClass().getAnnotation(PlcPrimaryKey.class);
			try{	
				int index=0;
				for(String property : propertiesValues.keySet()){

					if (queryString.contains("?")) {
						if (pk == null)	{
							// É OID ou então é um campo auxiliar 
							if (property != null && "id".equals(property.toLowerCase()))
								query.setParameter(index,  propertiesValues.get(property));
							else{
								//é Recuperação por propriedade auxliar Ex: cpf
								NamedQuery namedQuery2 	= PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(entity.getClass(), PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_LOOKUP + StringUtils.capitalise(property));
								if (namedQuery2 != null)
									query 	= session.getNamedQuery(namedQuery2.name());
								else{
									String queryProperty = queryString.substring(0, queryString.indexOf("where")) + " where obj." + property + "= ? ";
									query 	= session.createQuery(queryProperty);
								}
								query.setParameter(index, propertiesValues.get(property));
								 
							}
						}
						else{
							Class idNatural 	= PropertyUtils.getPropertyType(entity, "idNatural");
							Class typeOfValue 	= PropertyUtils.getPropertyType(idNatural.newInstance(), property);
							Object value		= propertiesValues.get(property);
							if (typeOfValue != null && Long.class.isAssignableFrom(typeOfValue) && value instanceof String)
								query.setParameter(index,  new Long((String)value));
							else
								if (typeOfValue != null && Date.class.isAssignableFrom(typeOfValue) && value instanceof String){
									query.setParameter(index,  PlcDateHelper.getInstance().convertStringToDate(((String)value)));
								}
								else
									if (typeOfValue != null && Integer.class.isAssignableFrom(typeOfValue) && value instanceof String)
										query.setParameter(index,  new Integer((String)value));
									else
										query.setParameter(index,  value);
						}
					} else {
						if (pk == null)	
							query.setParameter(property, propertiesValues.get(property));
						else{
							Class idNatural 	= PropertyUtils.getPropertyType(entity, "idNatural");
							Class typeOfValue 	= PropertyUtils.getPropertyType(idNatural.newInstance(), property);
							Object value		= propertiesValues.get(property);
							if (typeOfValue != null && Long.class.isAssignableFrom(typeOfValue) && value instanceof String)
								query.setParameter(property,  new Long((String)value));
							else
								if (typeOfValue != null && Date.class.isAssignableFrom(typeOfValue) && value instanceof String){
									query.setParameter(property,  PlcDateHelper.getInstance().convertStringToDate(((String)value)));
								}
								else
									if (typeOfValue != null && Integer.class.isAssignableFrom(typeOfValue) && value instanceof String)
										query.setParameter(property,  new Integer((String)value));
									else
										query.setParameter(property,  propertiesValues.get(property));
						}
					}

					index++;
				}
			}catch(Exception e){
				throw new PlcException("jcompany.error.generic", new Object[] { "retrieveLookupAggregateByNamedQuery", e }, e,log);
			}
			List list = query.list();
			if (list.size()>0) {
				if (logPersistence.isDebugEnabled())
					logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregateByNamedQuery:"));
				return list.get(0);
			}
			else {
				if (logPersistence.isDebugEnabled())
					logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregateByNamedQuery:null:"));
				return null;
			}

		}
		else
			throw new PlcException("jcompany.named.query.lookup.not.found", new Object[] { entity.getClass()});

	}

	public Object retrieveLookupAggregate(Object baseEntity, Map<String, Object> propertiesValues) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregate:"));

		try {
		
			Object baseEntityRetrieved = retrieveLookupAggregateByNamedQuery(baseEntity,propertiesValues);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregate:"));

    		return baseEntityRetrieved;
    		
    	} catch (Exception e) {
    		throw new PlcException("jcompany.error.generic", new Object[] {
    				"retrieveLookupAggregate", e }, e,log);
    	}
		 
	 }
	
	/**
     * jCompany 2.7.x Recupera um ENTITY informado e seu grafo (lazy=false),
     * a partir de relação de sua chave, que deve vir preenchida no ENTITY passado
     * @return instancia do ENTITY recuperada, contendo coleções e classes membro do grafo (lazy=false)
     * @deprecated utilize o método {@link PlcBaseHibernateDAO#retrieveEntityWithoutGraph(Object, String, String[])}
     */
    protected Object retrieveEntityWithoutGraph(Object baseEntity,String[] props) throws PlcException {
    	return retrieveEntityWithoutGraph(baseEntity, null, props);
    }
 	
    /**
     * jCompany 3.1 Recupera um ENTITY informado e seu grafo (lazy=false),
     * a partir de uma propriedade qualquer, que deve vir preenchida no ENTITY passado.
     * @param propertyName nome da propriedade pesquisada. Se for null, considera OID.
     * @return instancia do ENTITY recuperada, contendo coleções e classes membro do grafo (lazy=false)
     */
    protected Object retrieveEntityWithoutGraph(Object baseEntity,String propertyName, String[] props) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveEntityWithoutGraph:"));

        try {
        	
    		if (propertyName==null || propertyName.trim().length()==0) {
    			propertyName = "id";
    		}
    			        
	        if (!"id".equals(propertyName) && !props[0].equals("id")) {
	        	String[] _props = new String[props.length+1];
	        	_props[0] = "id";
	        	System.arraycopy(props, 0, _props, 1, props.length);
	        	props=_props;
	        }

	        String hql = "select "+getHelper().mountSelectFromArrayToString(props)
	                +" from "+baseEntity.getClass().getName()+" obj where obj."+propertyName+"=?";

	        Object value = PropertyUtils.getProperty(baseEntity, propertyName);
	        
			List l = apiCreateQuery(baseEntity.getClass(),hql).setParameter(0,value).list();

	        if (l.size()==0) {
	            if (logPersistence.isDebugEnabled())
		        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityWithoutGraph:null:"));
	        	return null;
	        }

	        if (props.length>1)
	            baseEntity = PlcEntityHelper.getInstance().
	            	fillEntityWithObjectArray(baseEntity,  props, (Object[])l.get(0));
	        else
	            PropertyUtils.setProperty(baseEntity,props[0],l.get(0));

	        if (logPersistence.isDebugEnabled())
	        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityWithoutGraph:"));

	        return baseEntity;

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveEntityWithoutGraph", e }, e,log);
        }

    }
 	
    /**
     * jCompany 2.7.x Recupera valores de propriedades de um ENTITY informado,
     * a partir de relação de propriedades e de sua chave,
     * devolvendo os valores preenchidos no próprio ENTITY
     * @deprecated utilize o método {@link PlcBaseHibernateDAO#retrieveEntityWithGraph(PlcBaseEntity, String)}
     */
    protected Object retrieveEntityWithGraph( Object baseEntity) throws PlcException {
    	//return recupera(baseVO.getClass(),baseVO.getId());
    	return retrieveEntityWithGraph(baseEntity, null);
    }
    
    /**
     * jCompany 3.1 Recupera valores de propriedades de um ENTITY informado,
     * a partir de um campo qualquer,
     * devolvendo os valores preenchidos no próprio ENTITY
     * @param nomePropriedade nome da propriedade de indexação. Se null, considera OID
     */
    @SuppressWarnings("unchecked")
    protected Object retrieveEntityWithGraph(  Map<String, Object> propertiesValues, Class classEntity) throws PlcException {

        if (logPersistence.isDebugEnabled())
        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveEntityWithGraph:"));

        try {
        	
	        String hql = "from "+classEntity.getClass().getName()+" obj ";

	        for(String property : propertiesValues.keySet()){
	        	if (! hql.contains("where"))
	        		hql = hql + " where obj."+property+"=?" ;
	        	else
	        		hql = hql + " and obj."+property+"=?" ;
			}
	        
	        Query query =  apiCreateQuery(classEntity.getClass(),hql);
	        int index=0;
	        for(String property : propertiesValues.keySet()){
	        	query.setParameter(index, propertiesValues.get(property));
	        	index++;
	        }
	        
			List<Object> l = query.list();

		     if (logPersistence.isDebugEnabled())
		        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityWithGraph:"));

			return l.size()==0 ? null : l.get(0);

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveEntityWithGraph", e }, e,log);
        }
    }
    
    protected Object retrieveEntityWithGraph( Object baseEntity, String propertyName) throws PlcException {

        if (logPersistence.isDebugEnabled())
        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveEntityWithGraph:"));

        try {
        	
    		if (propertyName==null || propertyName.trim().length()==0) {
    			propertyName = "id";
    		}
    		
	        String hql = "from "+baseEntity.getClass().getName()+" obj where obj."+propertyName+"=?";

	        Object value = PropertyUtils.getProperty(baseEntity, propertyName);
	        
			List l = apiCreateQuery(baseEntity.getClass(),hql).setParameter(0,value).list();

	       if (logPersistence.isDebugEnabled())
	        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityWithGraph:"));

			return l.size()==0 ? null : l.get(0);
			
        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveEntityWithGraph", e }, e,log);
        }
    }
    
   
    
	/* ******************************************************************************************* */
	/* ********************************  INICIO SERVIÇOS QBE E CONSULTATION ************************** */
	/* ******************************************************************************************* */
	
	
	 
    /**
     * Recupera uma lista de objetos da classe informada, utilizando queries anotadas no padrao "querySel" ou "queryTreeView", etc., em 
     * conformidade com o Application Pattern utilizado. Pode receber orderByDinamico (alterado pelo usuário ou desenvolvedor em cada
     * requisição) e também um trecho de where condition para ser dinamicamente composto (uso em lógicas dinamicas tais como
     * explorer), além de POJO de argumentos e intervalo para paginação
     * @param clazz Classe da qual será obtida a query em anotação padrao ("querySel", "queryTreeView", etc., conforme a lógica chamadora)
     * @param whereDynamic (Opcional - informe null ou "" para desconsiderar) Trecho a ser adicionado dinamicamente à query anotada. Importante: Nao informar o 'where' em si e utilizar o alias padrão 'obj.' (Ex:'obj.status=:status and obj.valor>:valor')
     * @param orderByDynamic (Opcional - informe null ou "" para desconsiderar) Trecho de orderBy a ser adicionado dinamicamente à query anotada
     * @param argQBE POJO contendo valores preenchidos para todos os argumentos existentes fixos na query ou montados dinamicamente em whereDinamico
     * @param firstLine Primeira linha para recuperação paginada ou -1 para todos
     * @param maximumLines Máximo de linhas (size da página de recuperação) ou -1 para todos
     * @since jCompany 3.03
     * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBEPaginated(java.lang.Class, java.lang.String, org.jcompany.commons.Object, int, int)
     */
	@Override
	public List retrieveListQBEPaginated(Class clazz, String whereDynamic, String orderByDynamic,Object argQBE,
			int firstLine, int maximumLines) throws PlcException {
	
       if (logPersistence.isDebugEnabled())
        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));
		
		applyFilters(clazz);
		
	    String query = getServiceQBE().getQuerySelDefault(clazz,getContext());
		   
	    try{
		
	    	// Se informado whereDinamico, inclui
	    	if (whereDynamic != null && !whereDynamic.equals(""))
	    		query =  retrieveListQBEPaginatedChangeWhere(query,whereDynamic);
	    		
	    	// Troca query para receber argumentos nulos com is null
	    	query = retrieveListQBEPaginatedChangeIsNull(query,argQBE);
	    	
	    	Query q = apiCreateQuery(clazz,query).setProperties(argQBE);
	
			if (firstLine >= 0) q.setFirstResult(firstLine);
		
			if (maximumLines == 0 )
				logWarning.debug(this.getClass().getCanonicalName()+": Maximum Lines number entered as zero!");
			else if (maximumLines >= 1)
				q.setMaxResults(maximumLines);

			List list = q.list();
		
			if (log.isDebugEnabled()) log.debug("found "+list.size()+" records(s)");

		     if (logPersistence.isDebugEnabled())
		        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));

			return list;
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListQBEPaginated", e }, e, log);
		}
		
	}
	
		

	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method. Permite que se registre um operador diferente para o argumento informado
	 * @param q Query já criada para ter a restricoes adicionadas
	 * @param voArg ENTITY contendo informaçoes sobre um argumento informado pelo usuário
	 * @return true para que o jCompany prossi
	 */
	protected boolean criteriaListQBEArgSpecificApi( Query q, PlcArgEntity entityA)throws PlcException {
		return false;
	}
	
	
	
	/**
	 * Recupera o total de registros baseando-se em NamedQuery anotada no padrao conforme a lógica: querySel, queryTreeView, etc.
	 * Permite que se acrescente trecho de where dinamicamente. Todos os valores para argumentos devem ser colocados no POJO argsQBE.
	 * IMPORTANTE: Chama o método recuperaListaQBEPaginadaTrocaWhere, neste caso, nao seguindo a convençao padrão em prol do reuso.
	 * @since jCompany 3.03
	 * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBETotal(java.lang.Class, org.jcompany.commons.Object)
	 */
	@Override
	public Integer retrieveListQBETotal( Class clazz, String whereDynamic, Object argsQBE) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBETotal:"));

		applyFilters(clazz);

		try {

			String queryCount = getServiceQBE().getQueryCount(clazz);

			if(queryCount == null) {

				String query = getServiceQBE().getQuerySelDefault(clazz,getContext());

				if (whereDynamic != null && !whereDynamic.equals(""))
					query = retrieveListQBEPaginatedChangeWhere(query,whereDynamic);

				int positionFrom = query.indexOf("from");

				if (positionFrom == -1)  
					throw new PlcException("jcompany.error.hql.without.from");

				queryCount = "select count(*) " + query.substring(positionFrom, query.length());

				// retira order by e group by
				int positionOrder = queryCount.toLowerCase().indexOf("order by");
				int positionGroup = queryCount.toLowerCase().indexOf("group by");
				int length = queryCount.length();
				int end = length;

				if (positionOrder != -1 || positionGroup != -1) {
					if (positionOrder < positionGroup)
						end = positionGroup;
					else
						end = positionOrder;
				}

				if (end != length)
					queryCount = queryCount.substring(0, end);

			}

			apiHandleArgsBeforeQuery(clazz,queryCount,argsQBE);

			Object ret = apiCreateQuery(clazz,queryCount).setProperties(argsQBE).list().get(0);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBETotal:"));

			if (ret instanceof Long) {
				return new Integer(((Long)ret).intValue());
			} else
				return (Integer) ret;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListQBETotal", e }, e, log);
		}

	}

	
    /**
	 * @since jCompany 3.0
	 * 
	 * DP Template Method.
	 * Para um argumento de filtro não registrado no perfil do usuário, retorna um valor a ser usado correntemente
	 * @param fa Anotação do filtro corrente
	 * @param f Filtro corrente
	 * @param argument Argumento esperado pelo filtro para o qual não foi encontrado valor no perfil do usuário (Map do ENTITY de perfil montado
	 * no context)
	 * @return Valor a ser utilizado ou null para que o filtro seja desabilitado (não utilizado para este usúario
	 */
	protected Object applyOneFilterInformArgOmittedApi( Class clazz, 
			Filter fa, org.hibernate.Filter f, String argument) throws PlcException {
		return null;
	}


	/**
	 * @since jCompany 3.0
	 * 
	 * DP Template Method.
	 * Retorna se deseja-se investigar este filtro para habilitá-lo ou não pelas regras padrões (todos os filtros que possuem argumentos
	 * declarados no Map de perfil).
	 * @param filter Filtro corrente
	 * @return true para que o jCompany verifique este filtro. False para que o filtro não seja habilitado
	 */
	protected boolean applyFiltersVerifyApi( Class clazz, Filter filter) throws PlcException {
		return true;
	}

	/**
	 * Recupera objetos de 'classe', filhos do objeto de 'classeBase' cujo OID seja 'idClasseBase'.
	 * @param clazz Classe para recuperação, que possui um relacionamento many-to-one com a classeBase
	 * @param classeBase Classe do lado "one" do relacionamento
	 * @param idClassBase OID de objeto da classe do lado "one" do relacionamento
	 * @return Lista contendo objetos que atendem ao critério, com as seguintes regras:<p>
	 * 1. se 'classe' tiver anotação de paginação, e posIni for -1 (primeira chamada), entao devolve o total de registros existentes na
	 * primeira posição, considerando somente Ativos se estiver utilizando "sitHistoricoPlc" e este campo estiver mapeado na classe.<br>
	 * 2. se 'classe' contiver anotação de paginação e posIni for acima de 1 (usa 1 para primeiro objeto), então recupera o numero de registros
	 * indicado a partir de posIni<br>
	 * 3. se 'classe' não contiver anotação de paginação, recupera todos os registros respeitando 'sitHistoricoPlc' também.<p>
	 * Em qualquer caso, recupera obedecendo a ordenação indicada em PlcEntityMetadata(ordenacao=....)
	 * @since jCompany 3.03
	 * @see org.jcompany.persistence.PlcBaseDAO#recuperaListaExplorer(java.lang.Class, java.lang.Class, java.lang.Object)
	 */
	public List retrieveExplorerList(Class clazz, Class classBase, Object idClassBase, long initialPosition) throws PlcException{

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveExplorerList:"));

		int numberByPage = PlcAnnotationHelper.getInstance().getEntityNumberByPage(clazz,0);

		// Ajusta para reutilizar funcoes de paginacao do jCompany que originalmente usam int
		int initialPositionInt = (int) initialPosition;

		boolean isPaginated = numberByPage >0;

		boolean mappedStatusHistoricPlc = helperHibernate.getInstance().existsStatusHistoricPlcMapped(getCfg(),clazz);

		String ordination = PlcAnnotationHelper.getInstance().getEntityOrdination(clazz,null);

		try {

			List l=null;
			Object entityArg = clazz.newInstance();
			String where=null;
			if (mappedStatusHistoricPlc) {
				PropertyUtils.setProperty(entityArg,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_ACTIVE);
				where = "obj.sitHistoricoPlc='A'";
			}

			where = retrieveListExplorerComposeArgs(clazz, classBase, idClassBase, initialPosition, entityArg, where);

			if (isPaginated && initialPosition==-1) {
				Integer tot = retrieveListQBETotal(clazz,where,entityArg);
				l = new ArrayList(0);
				l.add(tot);
				if (log.isDebugEnabled())
					log.debug("Finalized explorer with "+tot+" records");
			} else if (isPaginated && initialPosition>-1) {
				// posIniInt, se for 1, tem que iniciar do zero
				l = retrieveListQBEPaginated(clazz,where,ordination,entityArg,initialPositionInt-1,numberByPage);
			} else if (!isPaginated) {
				l = retrieveListQBEPaginated(clazz,where,ordination,entityArg,-1,-1);
			}

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveExplorerList:"));

			return l;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveExplorerList", e }, e, log);
		}	

	}


	/**
	 * Permite que se componha de forma especifica os argumentos
	 * @since jCompany 3.03
	 * @param clazz classe a ter seus objetos recuperados
	 * @param classBase classe base cujo objeto com object-id 'idClasseBase' é o pai da atual recuperação
	 * @param idClassBase object-id da classe base
	 * @param initialPosition Posiçao para recuperação (-1 para todos)
	 * @param entityArg Vo de argumentos para QBE a ser composto.
	 */
	protected String retrieveListExplorerComposeArgs(Class clazz, Class classBase, Object idClassBase,
			long initialPosition, Object entityArg,String where) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListExplorerComposeArgs:"));

		String prop = PlcAnnotationHelper.getInstance().getEntityNameDefaultProp(classBase,null);

		// Se for recursivo, permite um ajuste no nome da propriedade.
		if (clazz.getName().equals(classBase.getName())) {
			PlcEntityMetadata entity = (PlcEntityMetadata) classBase.getAnnotation(PlcEntityMetadata.class);
			if (!"".equals(entity.recursivePropName()))
				prop = entity.recursivePropName();
		}

		try {
			Object entityParent = classBase.newInstance();
			PropertyUtils.setProperty(entityParent,"id",idClassBase);
			if (log.isDebugEnabled())
				log.debug("Inserting value "+entityParent+" in property "+prop+" of Entity "+entityArg);
			PropertyUtils.setProperty(entityArg,prop,entityParent);

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListExplorerComposeArgs:"));

			if (where == null)
				return "obj."+prop+"=:"+prop;
			else
				return where + " and obj."+prop+"=:"+prop;
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListExplorerComposeArgs", e }, e, log);
		}
	}

	/* ******************************************************************************************* */
	/* ********************************      INICIO ARQUIVO ************************************** */
	/* ******************************************************************************************* */

    /**
	 * jCompany. Recupera conteudo binário do arquivo
	 * @param oid Object-Id do arquivo
	 * @param property Propriedade contendo o conteúdo binário.
	 * @return arquivo
	 */
    @Override
	public byte[] retrieveBinaryFileById( Class clazz, Long oid, String property) throws PlcException {

	     if (logPersistence.isDebugEnabled())
	        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveBinaryFileById:"));

		try {
			
			String query = "select obj."+property+" from obj in "+clazz +" where obj.id="+oid;
	
			Iterator res = apiCreateQuery(clazz,query).iterate();
	
			byte [] image = null;
	
			if (res.hasNext()) {
				image = (byte[]) res.next();
			} 	
			
		    if (logPersistence.isDebugEnabled())
		       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveBinaryFileById:"));			
			
		    return image;
		
		} catch (Exception e) {
			 throw new PlcException("jcompany.error.generic", new Object[] {"retrieveBinaryFileById", e }, e,log);
		}
	}
    

	/**
	 * Recupera um ENTITY com arquivo incluido, baseada em sua URL
	 * @param url URL para recuperar o arquivo.
	 * @return ENTITY contendo arquivo.
	 */
    @Override
	public IPlcFileEntity retrieveEntityFileByUrl( Class clazz, String url) throws PlcException {
		
	     if (logPersistence.isDebugEnabled())
	        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveEntityFileByUrl:"));
	     
		String query = "from obj in class "+clazz.getName() +
                      " where lower(obj.url)=:url";

		try {
			
			List l = apiCreateQuery(clazz,query).setParameter("url",url.toLowerCase()).list();

			if (l.size()>0) {
				
			     if (logPersistence.isDebugEnabled())
			        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityFileByUrl:"));

				return (IPlcFileEntity) l.get(0);
			} else
				throw new PlcException("jcompany.errors.retrieve.image.file",new Object[] {"Query="+query});

		} catch (HibernateException e) {
			throw new PlcException("jcompany.errors.download.url",new Object[]{url},e,log);
		}

	}
    
	/* ******************************************************************************************* */
	/* ********************************   INICIO VERSIONAMENTO/WORKFLOW ************************** */
	/* ******************************************************************************************* */

	
   
	
	/** 
	 * Trata filtros do jCompany no padrão da Hibernate 3.x. Habilita todos os filtros que não possuírem argumentos para
	 * a classe corrente e também aqueles que possuem argumentos e os tem informados no Map plcSegurancaVertical do perfil do usuário 
	 * que existe no context. Considera classes filhas, se for lógica mestre-detalhe-subdetalhe
	 * @since jCompany 3.0
	 * @param clazz Classe corrente
	 */
	protected void applyFilters(Class clazz) throws PlcException {
	
	     if (logPersistence.isDebugEnabled())
	        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":applyFilters:"));

		try {
			
			Annotation[] annotations = clazz.getDeclaredAnnotations();
			
			for (int i = 0; i < annotations.length; i++) {
				
				Annotation annotation = annotations[i];
				
				// Somente anotações de filtros da hibernate
				// Neste caso tem somente uma anotação
				if (Filter.class.isAssignableFrom(annotation.getClass()) &&	applyFiltersVerifyApi(clazz,(Filter)annotation))
					applyOneFilter(clazz,(Filter)annotation);
				else  {
					//Verifica se tem coleção de filtros
					// Neste caso tem um conjunto
					if (Filters.class.isAssignableFrom(annotation.getClass())) {
						Filters fs = (Filters) annotation;
						for (int j = 0; j < fs.value().length; j++) {
							Filter f = (Filter) fs.value()[j];
							if (applyFiltersVerifyApi(clazz,f))
								applyOneFilter(clazz,f);
						}
					}
				}
			
			}

	     if (logPersistence.isDebugEnabled())
        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":applyFilters:"));

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"applyFilters", e }, e, log);
		}
		
	}


	/**
	 * @since jCompany 3.0
	 * 
	 * Aplica um filtro, conforme 
	 * @param fA Filtro a ser considerado
	 */
	protected void applyOneFilter( Class clazz, Filter fA) throws PlcException {
			
	     if (logPersistence.isDebugEnabled())
        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":applyOneFilter:"));

		Session sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(clazz));				
		
		org.hibernate.Filter f = sess.enableFilter(fA.name());
		
		// Depois varre todos os argumentos. Se o filtro tiver todos os argumentos 
		// informados, deixa-o habilitado
		Set s = f.getFilterDefinition().getParameterNames();
		
		Iterator j = s.iterator();
		while (j.hasNext()) {
			
			String argument = (String) j.next();
			Object argumentValue = null;
			PlcBaseContextVO context = getContext();
			if (context.getUserProfile() !=null && context.getUserProfile().getVerticalSecurityPlc() != null &&
					context.getUserProfile().getVerticalSecurityPlc().containsKey(argument))
				argumentValue = context.getUserProfile().getVerticalSecurityPlc().get(argument);
		    
			if (argumentValue == null)
				argumentValue = applyOneFilterInformArgOmittedApi(clazz,fA,f,argument);
		
			if (argumentValue != null) {
				if (List.class.isAssignableFrom(argumentValue.getClass())) {
					if (log.isDebugEnabled())
						log.debug("FILTRER: inserting value "+argumentValue + " in filter argument "+argument);
					f.setParameterList(argument,(List)argumentValue);
				} else {
					if (log.isDebugEnabled())
						log.debug("FILTRER: inserting value  "+argumentValue + " int filter argument "+argument);
					// Lógica de aprovação
					if (argument.equals("sitHistoricoPlc") && "searchPendents".equals(context.getOriginalAction()))
						f.setParameter(argument,"P");
					else
						f.setParameter(argument,argumentValue);
				}
			} else {
				// Se nao encontrar um argumento advindo da lógica de profile ou método específico,
				// entao desabilita o filtro
				sess.disableFilter(fA.name());
			     if (logPersistence.isDebugEnabled())
			        	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":applyOneFilter:"));

				return;
			}
				
		}
		
		if (log.isDebugEnabled())
			log.debug("Using filter with values "+fA);

		if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":applyOneFilter:"));
		
	}

    /**
     * Cria o objeto Query a partir de um HQL enviado. Este método é chamado de todos os demais métodos do DAO, 
     * permitindo assim um ponto de sobreposição único para lógicas que necessitam alterar dinamicamente
     * o HQL, ou mesmo incluir novos parametros ou modificações quaisquer no objeto Query.
     * @param sess Sessao a ser utilizada
     * @param hql HQL a ser utilizado para criação da Query
     * @return Query, podendo ter modificações em descendentes
     */
	protected Query apiCreateQuery(Class classEntity, String hql) throws PlcException {

		if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+
	       			":apiCreateQuery("+hql+"):"));

		//pega a sessao a partir da classe de entidade recebida, porque ela  pode carregar anotações para fábricas alternativas.
		Session sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(classEntity));
		return sess.createQuery(hql);
	}
	
	/**
	 * Cria uma query, executa e retorna apenas um resultado
	 * @see jCompany 5.0 
	 */
	@Override
	protected Object apiCreateExecuteOnlyOneResult(Class classeEntity, String hql) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":apiCreateExecuteOnlyOneResult("+hql+"):"));

		Query q = apiCreateQuery(classeEntity, hql);
		try {
			return q.uniqueResult();
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"apiCreateExecuteOnlyOneResult", e }, e, log);
		}
	}
	
	/**
	 * Cria uma query, executa e retorna lista de resultados
	 * @see jCompany 5.0 
	 */	
	@Override
	protected  List apiCreateExecute(Class classEntity, String hql) throws PlcException {
	    if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+
	       			":apiCreateExecute("+hql+"):"));

		Query q = apiCreateQuery(classEntity, hql);
		try {
			return q.list();
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"apiCreateExecute", e }, e, log);
		}
	}
	
	/**
	 * Cria uma query, registra seus parametros e executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */
	@Override
	protected List apiCreateExecute(Class classEntity, String QBE, List argTypes, Object[] argValues) throws PlcException {
		
	    if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+
	       			":apiCreateExecute("+QBE+"):"));

		try {
			Query q = apiCreateQuery(classEntity, QBE);
			
			return hqlExecuteQuery(q, argTypes, argValues);			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"apiCreateExecute", e }, e, log);
		}
	}
	
	/**
	 * Cria uma query, registra seus parametros e executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */	
	@Override
	protected List apiCreateQueryWithArgsExecute(Class clazz, String query, 
		String orderByDynamic, Object[] argValues, Object[] argTypes, int firstLine, int maximumLines) throws PlcException {
		
	    if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+
	       			":apiCreateQueryWithArgsExecute("+query+"):"));

		try {
			Query q = createQueryWithArgs(clazz, query, orderByDynamic, argValues, argTypes);
			
			if (firstLine >= 0) q.setFirstResult(firstLine);
			
			if (maximumLines == 0 )
				log.warn("Maximum Lines number entered as zero!");
			else if (maximumLines >= 1)
				q.setMaxResults(maximumLines);
			
			return q.list();
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"apiCreateQueryWithArgsExecute", e }, e, log);
		}
		
	}
	
	/**
	 * Transforma o proxy de uma entidade em um objeto real,
	 * recupperando todo seu grafo. 
	 * @since jCompany 5.0
	 */
	@Override
	protected  Object transformProxyInRealObject(Object entity) {
		if (HibernateProxy.class.isAssignableFrom(entity.getClass())) {
			entity = ((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation();
		}
		return entity;
	}
	
	protected Query createQueryWithArgs( Class clazz, String query, 
			String orderByDynamic, Object[] argValues, Object[] argTypes) throws PlcException {

	    if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+
	       			":createQueryWithArgs("+query+"):"));
		
		try {
						
			if (orderByDynamic != null && !orderByDynamic.equals("") &&
					!(getContext() != null &&
					 PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TREEVIEW.equals(getContext().getLogic())))
				query = getServiceQBE().changeOrderBy(query,orderByDynamic);
			
			if (getContext() != null &&
				PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TREEVIEW.equals(getContext().getLogic()))
				query = createQueryWithArgsAdjustTreeview(query,argValues);
			
			Query q = apiCreateQuery(clazz,query);
			
			if (argTypes == null)
				return q;
			

			for (int i = 0; i < argTypes.length; i++) {
				
				//TODO Trocar o if de tipos por q.setParameter(i, valor)
				if (Type.class.isAssignableFrom((argTypes[i]).getClass())) {
				
					Type type = (Type) argTypes[i];
					Object value = argValues[i];
					
					if (type.equals(Hibernate.BIG_DECIMAL))
						q.setBigDecimal(i,(BigDecimal)value);
					else if (type.equals(Hibernate.DATE))
						q.setDate(i,(Date)value);
					else if (type.equals(Hibernate.DOUBLE))
						q.setDouble(i,(Double)value);
					else if (type.equals(Hibernate.INTEGER))
						q.setInteger(i,(Integer)value);
					else if (type.equals(Hibernate.LONG))
						q.setLong(i,(Long)value);
					else if (type.equals(Hibernate.STRING))
						q.setString(i,(String)value);
					else if (type.equals(Hibernate.TIMESTAMP))
					  q.setTimestamp(i,(Date)value);
					else if (type.equals(Hibernate.BOOLEAN))
						  q.setBoolean(i,(Boolean)value);					
					else if (type.equals(Hibernate.CLASS))
						q.setParameter(i,value);					
				} else {
					
					String type = ((String) argTypes[i]).toUpperCase();
					Object value = argValues[i];
					
					if (type.equals(PlcConstantsCommons.TYPES.BIG_DECIMAL))
						q.setBigDecimal(i,(BigDecimal)value);
					else if (type.equals(PlcConstantsCommons.TYPES.DATE))
						q.setDate(i,(Date)value);
					else if (type.equals(PlcConstantsCommons.TYPES.DOUBLE))
						q.setDouble(i,(Double)value);
					else if (type.equals(PlcConstantsCommons.TYPES.INTEGER))
						q.setInteger(i,(Integer)value);
					else if (type.equals(PlcConstantsCommons.TYPES.LONG))
						q.setLong(i,(Long)value);
					else if (type.equals(PlcConstantsCommons.TYPES.STRING))
						q.setString(i,(String)value);
					else if (type.equals(PlcConstantsCommons.TYPES.TIMESTAMP))
					  q.setTimestamp(i,(Date)value);
					else if (type.equals(Hibernate.CLASS))
						q.setParameter(i,value);					
				}
			
			}	
			
			return q;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"createQueryWithArgs", e }, e, log);
		}
	}
	
	
    /**
     * @since jCompany 3.0
     * 
     * Sobrepor para especializar queries no caminho padrão
     * @param q
     * @param argTypes
     * @param argValues
     * @throws PlcException
     */
	protected List hqlExecuteQuery( Query q, List argTypes, Object[] argValues) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+
					":hqlExecutaQuery("+q+"):"));

		try {


			for (int cont = 0; cont < argValues.length; cont++) {

				Object argValue = argValues[cont];
				Object argType = argTypes.get(cont);

				if (argType.equals(Hibernate.BIG_DECIMAL))
					q.setBigDecimal(cont,(BigDecimal)argValue);
				else if (argType.equals(Hibernate.LONG))
					q.setLong(cont,(Long)argValue);
				else if (argType.equals(Hibernate.INTEGER))
					q.setInteger(cont,(Integer)argValue);
				else if (argType.equals(Hibernate.DOUBLE))
					q.setDouble(cont,(Double)argValue);
				else if (argType.equals(Hibernate.DATE))
					q.setDate(cont,(Date)argValue);
				else if (argType.equals(Hibernate.TIMESTAMP))
					q.setTimestamp(cont,(Date)argValue);
				else if (argType.equals(Hibernate.CLASS))
					q.setParameter(cont,argValue);
				else if (argType.equals(Hibernate.BOOLEAN))
					q.setBoolean(cont,(Boolean)argValue);
				else 
					q.setString(cont,(String)argValue);		
			}

			return q.list();

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"hqlExecutaQuery", e }, e, log);
		}

	}

	/**
     * @since jCompany 3.0
     * 
     * Restringe propriedades a serem recuperadas através do critério. O padrão é:<p>
     * 1. Se houver anotação padrão .querySel, interpreta e usa (para manter compatibilidade com versões anteriores)<br> 
     * 2. Senão retorna todas as propriedades do objeto.<p>
     * Pode-se sobrepor para programação refinada da restrição
     * @param c Criteria a ser alterado
     */
    protected void criteriaRestrictionsProperties( Criteria c, Class clazz) throws PlcException {
		
    	// Esta anotação pode ser a coleção NamedQueries que contém a que queremos
    	Annotation a = PlcAnnotationHelper.getInstance().getAnnotationQueryQBEDefault(clazz);
    	
    	if (a != null && NamedQueries.class.isAssignableFrom(a.getClass()))
    		a = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryQBEDefault((NamedQueries)a);
    	
    	if (a != null) {
    		NamedQuery nq = (NamedQuery) a;
    		String props = nq.query().substring(6,nq.query().toString().indexOf("from")).trim();
    		List l = PlcStringHelper.getInstance().splitListElements(props);
    		ProjectionList pl = Projections.projectionList();
    		for (Iterator iter = l.iterator(); iter.hasNext();) {
				String property = (String) iter.next();
				if (property.indexOf("obj.")>-1)
					property = property.substring(4);
				pl.add(Projections.property(property.trim()));
			}
    		c.setProjection(pl);
    	}
    	
	}

	/**
     * @since jCompany 3.0
     * 
     * Aplica cláusula OrderBy dinâmica, porque usuários podem modificá-las.
     * @param c Criteria a ser modificado
     * @param orderByDynamic cláusula OQL contendo o "order by"
     * @throws PlcException
     */
	protected void criteriaOrderByDynamic( Criteria c, String orderByDynamic, Class clazz) throws PlcException {
			
		String orderBy = orderByDynamic;
		
		if (orderBy == null || orderBy.equals(""))
			orderBy = getOrderBySelDefault(clazz);
				
		if (orderBy != null && !orderBy.equals("")) {

			List l = PlcStringHelper.getInstance().splitListElements(orderByDynamic);
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				String oneOrdination = (String) iter.next();
				if (oneOrdination.indexOf(".")>-1)
					oneOrdination.substring(oneOrdination.indexOf(".")+1);
				int posAsc = oneOrdination.toLowerCase().indexOf("asc");
				int posDesc = oneOrdination.toLowerCase().indexOf("desc");
				boolean eAsc = (posAsc != -1) || (posDesc == -1);
				if (posAsc == -1) posAsc = oneOrdination.length();
				if (eAsc)
					c.addOrder(Order.asc(oneOrdination.substring(0,posAsc).trim()));
				else
					c.addOrder(Order.desc(oneOrdination.substring(0,posDesc).trim()));
				
			}
			
		}
		
	}
	
	/**
     * @since jCompany 3.0
     * 
     * Recebe um objeto Criteria e aplica argumentos dinamicamente.
     */
    protected List criteriaRetrieveQBE( Class clazz, 
    		String orderByDynamic,List<PlcArgEntity> argsQBE) throws PlcException {
    	
    	if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":criteriaRetrieveQBE:"));

		try {
			
			Session sess = getSession(PlcAnnotationHelper.getInstance().getFactoryName(clazz));
			
			Criteria c = sess.createCriteria(clazz);
			
			// TODO Criteria retornando VOs
			//criteriaRestricoesPropriedades(context,c,classe);
						
			criteriaArgsQBE(c,argsQBE);
			
			criteriaOrderByDynamic(c,orderByDynamic,clazz);
			
			List l = c.list();
			
			if (log.isDebugEnabled())
				log.debug("Retrieved list with found CRITERIA  "+l);

			if (logPersistence.isDebugEnabled())
		       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":criteriaRetrieveQBE:"));
			
			return l;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"criteriaRetrieveQBE", e }, e, log);
		}		
		
	}

	/**
	 * @since jCompany 3.0
	 * 
	 * Verifica se há uma cláusula order by em alguma anotação padráo [Classe].querySel ou querySelQBE declarada e a utiliza se for o caso
	 * @return critério de ordernação declarado em Anotation
	 */
	protected String getOrderBySelDefault( Class clazz) throws PlcException {
	
		String apiQuerySel = getContext().getApiQuerySel();
		if (StringUtils.isEmpty(apiQuerySel))
			apiQuerySel = ANNOTATION.SUFFIX_QUERYSEL_DEFAULT;
		
		Annotation a = PlcAnnotationHelper.getInstance().getAnnotationQueryQbeOrSelDefault(clazz, apiQuerySel);
		
	   	if (a != null && NamedQueries.class.isAssignableFrom(a.getClass()))
    		a = PlcAnnotationPersistenceHelper.getInstance().getNamedQuerySelDefault((NamedQueries)a);
		
		if (a != null) {
			NamedQuery nq = (NamedQuery)a;
			if (nq.query().indexOf("order by")>-1)
				return nq.query().substring(nq.query().indexOf("order by"));
			else
				return "";
		} else {
			return "";
		}
	}

	/**
	 * @since jCompany 3.0
	 * 
	 * Recebe um objeto Criteria e aplica argumentos dinamicamente.
	 * @param c Criteria a ter condições incluídas
	 * @param argsQBE Lista com VOs PlcArgEntity que contém argumentos informados na camada Visão
	 */
	protected void criteriaArgsQBE( Criteria c, List<PlcArgEntity> argsQBE) throws PlcException {
	
	  	if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":criteriaArgsQBE:"));

		try {
			
			if (argsQBE == null)
				return;
			
			for (Iterator<PlcArgEntity> iter = argsQBE.iterator(); iter.hasNext();) {
	
			    PlcArgEntity entityArg = iter.next();
	
				if (entityArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ARGUMENT)) {
					
					if (entityArg.getOperator() != null) {
						
						// Operator especificado pelo desenvolvedor
						if (!apiApplyArgQBESpecific(c,entityArg)) {
									
							if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_BEGIN) ||
									entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_TOTAL) ||
									entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_FINAL)) {
							
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.like(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.like(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase());
							
							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_DIFFERENT)) {
							
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.ne(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.ne(entityArg.getName(),entityArg.getFormattedValue()));
							
							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_EQUALS_TO)) {
							
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.eq(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.eq(entityArg.getName(),entityArg.getFormattedValue()));
							
							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_GREATER_THAN)) {
								
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.gt(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.gt(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase());
							
							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_GREATER_OR_EQUALS_TO)) {
							
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.ge(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.ge(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase());
							
							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LESS_THAN)) {
							
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.lt(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.lt(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase());
							
							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LESS_OR_EQUALS_TO)) {
							
								if (entityArg.isOrIsNull())
									c.add(Restrictions.or(
											Restrictions.le(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase(),
											Restrictions.isNull(entityArg.getName())
											));
								else
									c.add(Restrictions.le(entityArg.getName(),entityArg.getFormattedValue()).ignoreCase());
							
							} 
						
						}
					}
				}
			
			}
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"criteriaArgsQBE", e }, e, log);
		}

		if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":criteriaArgsQBE:"));

	}

	
	
	


	/**
     * @since jCompany 3.0
     * 
     * Retorna um critério (QBE) para uma classe. Este método deve ser sobreposto nos descendentes
     * para retornar valores de classes relacionadas ou quaisquer variações no critério
     * @return Criteria padrão
     */
    protected Criteria getCriteria( Class clazz) throws PlcException {
		
		return getSession(PlcAnnotationHelper.getInstance().getFactoryName(clazz)).createCriteria(clazz);
	
    }

	/**
	 * @since jCompany 3.0 
	 *  
	 * Template Method. Permite que se registre um operador diferente para o argumento informado
	 * @param c Criteria já criado para ter a Restrictoin adicionada
	 * @param entityArg ENTITY contendo informaçoes sobre um argumento informado pelo usuário
	 * @return true para que o jCompany prossi
	 */
	protected boolean apiApplyArgQBESpecific( Criteria c, PlcArgEntity entityArg)throws PlcException {
		return false;
	}
    
	/* ******************************************************************************************* */
	/* ********************************   INTEGRIDADE DECLARATIVA       ************************** */
	/* ******************************************************************************************* */

	/**
	 * @since jCompany 1.0
	 * Testa restrição "avoidEquals" declaradas como anotações<p>
	 *
	 * Os erros são disparados segundo uma convenção e de forma internacionalizada (I18n).
	 * Assim, no arquivo de properties, deve exitir uma mensagem com a seguinte estrutura (pressupondo um transação com url /organograma):<p>
	 *
	 * jcompany.aplicacao.naodeveexistir1.organograma=Tentativa de incluir nome/idPai do Organograma duplicado. Valor duplicado: {0}
	 * jcompany.aplicacao.naodeveexistir2.organograma=Tentativa de incluir nivel/ordem do Organograma duplicado. Valor duplicado: {0}<p>
	 *
	 * Importante: Esta lógica trata nulos, convertendo valores não informados para is null,
	 * antes de submetê-los.<P>
	 * Importante 2: A lógica também evita testar contra o próprio registro, em caso de
	 * alteração, acrescentando " and obj.id <> "+idObjetoCorrente ao teste.
	 * @param entity Value Object objeto do teste.
	 * @param mode "A" - Alteração "I" - Inclusão
	 */
    @Override
	public void	avoidEqualsExecute( Object entity, String mode) throws PlcException {

	  	if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":avoidEqualsExecute:"));

		try {
					
			// Se for inclusão de pendente ou inativo, garante que não irá fazer os testes
			if (PropertyUtils.isReadable(entity,ENTITY.STATUS_HISTORIC_PLC) &&
					!("A".equals((String)PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC)))) {
			  	if (logPersistence.isDebugEnabled())
			       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":avoidEqualsExecute:return1:"));
				return;
			}
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"avoidEqualsExecute", e }, e, log);
		}
		
		List<NamedQuery> avoidEquals = PlcAnnotationPersistenceHelper.getInstance().getAnnotationsQueryShouldntExist(entity.getClass());
		
		if (avoidEquals == null || avoidEquals.size()==0) {
		  	if (logPersistence.isDebugEnabled())
		       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":avoidEqualsExecute:return2:"));
			return;
		}

		Iterator<NamedQuery> j = avoidEquals.iterator();
	
		// TODO Ver como anotar valor máximo
		//	Iterator k = context.getNaoDeveExistirValMax().iterator();

		int counter = 0;

		String queryCount = "";
		String maxValue = "0";
		String msgValue = "";

		while (j.hasNext()) {

			counter++;
			Integer totalResults = new Integer("0");
			msgValue = "";

		//	where = (String) j.next();
		//	valMax = (String) k.next();
			NamedQuery nqShouldntExist = j.next();
			
			queryCount = nqShouldntExist.query();
			
			Object[] ret=null;

			// Se for alteração e não for chave natural, então testa o OID

		    try{
		    	
			    if (mode.equals("A") && 
			    		(!PropertyUtils.isReadable(entity,"dynamicNaturalId") ||
			    		 PropertyUtils.getProperty(entity, "dynamicNaturalId")==null))
			    	queryCount = queryCount + " and obj.id <> :id";
	
			    // jCompany 3.0 Se tiver versionamento e estiver incluindo ativo,
			    //altera automaticamente para somente considerar 'A'-Ativos
			    if (PropertyUtils.isReadable(entity,ENTITY.STATUS_HISTORIC_PLC) &&
			    		"A".equals((String)PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC)))
			    	queryCount = queryCount + " and obj.sitHistoricoPlc='A'";
			    
		    } catch (Exception e){
		    	// Exceção nao deve acontecer...
		    	log.debug("Error trying to verify sitHistoricoPlc for avoidEqualsExecute. "+e);
		    }
		     ret = verifyTotalResults(mode,queryCount,entity);

			 totalResults = (Integer) ret[0];

			 
			 try {
			
				 // Se for alteração e for chave composta, subtrai um (o que já existe)
				 if (mode.equals("A") && 
					(PropertyUtils.isReadable(entity,"dynamicNaturalId") &&
					  PropertyUtils.getProperty(entity,"dynamicNaturalId") !=null))
				     totalResults = new Integer(totalResults.intValue()-1);
				
			 } catch (Exception e) {
				throw new PlcException("jcompany.erro.generico", new Object[] {
						"avoidEqualsExecute", e }, e, log);
			 }

			 msgValue = (String) ret[1];

			if ((maxValue.equals("0") &&
				totalResults.intValue() > new Integer(maxValue).intValue()) ||
				(!maxValue.equals("0") &&
				totalResults.intValue() > (new Integer(maxValue).intValue()-1))) {

				String nameEntity = entity.getClass().getName();
				nameEntity = nameEntity.substring(nameEntity.lastIndexOf("."),nameEntity.length()).toLowerCase();

				throw new PlcException("jcompany.application."+nqShouldntExist.name(),new Object[] {msgValue});

			}

		}

	  	if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":avoidEqualsExecute:"));

	}
    
    /**
	* @since jCompany 1.0
	* Executa um "select count(*)" do type "avoidEquals"
    * @return Total de registros e valores informados
    */
    protected Object[] verifyTotalResults(String mode,String hql,Object plcBaseEntity)
    			throws PlcException {

	  	if (logPersistence.isDebugEnabled())
	       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":verifyTotalResults:"));

			try {

				Object[] ret = handleNullValues(hql,plcBaseEntity);

				hql = (String) ret[0];
				String msgValue = (String) ret[1];
				Map aggregatedProperties = (Map) ret[2];
				
				Query q = apiCreateQuery(plcBaseEntity.getClass(),hql);

				q.setProperties(plcBaseEntity);

				log.debug("After create query and setProperties");

				Set params = aggregatedProperties.keySet();
				Iterator i = params.iterator();
				String param = "";

				while (i.hasNext()) {
					param = (String) i.next();
					q.setParameter(param,aggregatedProperties.get(param));
				}

				log.debug("Before submit query");

				Iterator score = q.iterate();

				Object row = (Object) score.next();

		           if (row instanceof Long) {
		        	   row = new Integer(((Long)row).intValue());
		           }

		   	  	if (logPersistence.isDebugEnabled())
			       	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":verifyTotalResults:"));

				return  new Object[]{(Integer) row,msgValue};

		} catch (HibernateException e2) {
			throw new PlcException("jcompany.errors.hibernate.duplicity",new Object[] {e2},e2,log);
		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.duplicity",new Object[] {e},e,log);
		}

    }
    
	/**
	* @deprecated Utilizar o plugin Hibernate Console para este recurso
	* @since jCompany 1.0 Utilitário para execução genérica de HQL (uso no HQLInterativo, de forma concreta)
	* @param hql hql a ser executada
	*/
	public List executeHQL(String hql) throws PlcException {
			
		Session sess = getSession();
		
		return sess.createQuery(hql).list();
	}
	
	/**
	 * Inicia uma trasação na sessão corrente
	 * @deprecated Não deve ser invocado
	 * @since jCompany 3.0
	 */
	public void begin(String factory) throws PlcException {
	 	 PlcBaseHibernateManager manager = PlcHibernateManagerLocator.getInstance().getHibernateManagerClass(factory); 
		 if (manager != null)
			 manager.getSession().getTransaction().begin();

	}
	
	/**
	 * Configura o serviceQBE que sera utilizado
	 * @since jCompany 3.0 
	 */
	public void setServiceQBE(PlcQBEService serviceQBE) {
		if (PlcQBEHibernateService.class.isAssignableFrom(serviceQBE.getClass()))
			this.serviceQBE = (PlcQBEHibernateService)serviceQBE;
		else {
			// TODO pa: código incluido porque eventualmente evento recebe PlcQBEService sem descendente.
			log.warn("Using service PlcQBEHibernateService, "+
					serviceQBE.getClass().getName()+" is incorrect.");
			this.serviceQBE = new PlcQBEHibernateService();
		}
			
	}
	
	/**
	 * Gera um objeto Query com parametros informados 
	 * @param namedQuery Nome da Query
	 * @param parametersNames Relação de nomes dos parâmetros
	 * @param parametersValues Relação de valores dos parâmetros, na mesma ordem dos nomes.
	 * @return Objeto query criado, com parâmetros informados
	 */
	private Query retrieveNamedQuery(String namedQuery,
			String[] parametersNames, Object[] parametersValues) throws PlcException {

		Query q = getSession().getNamedQuery(namedQuery);
		
		for (int i = 0; i < parametersNames.length; i++) {
			q.setParameter(parametersNames[i], parametersValues[i]);
		}
		
		return q;
	}
	
	/**
	 * Recupera uma lista de objetos.
	 * @param namedQuery Query nomeada a ser utilizada
	 * @param parametersNames Relação de nomes dos parâmetros
	 * @param parametersValues Relação de valores dos parâmetros, na mesma ordem dos nomes.
	 * @return Lista de objetos. Deve-se fazer 'casting' utilizando generics. Ex: List minhaLista = (List<MeuObjeto>) recuperaListaComNamedQuery(...).
	 * @throws PlcException Exceção externa tratada de maneira genérica, padrão.
	 */
	protected List<?> retrieveListByNamedQuery(String namedQuery,
			String[] parametersNames, Object[] parametersValues) throws PlcException {
		
		try {
			
			return retrieveNamedQuery(namedQuery, parametersNames, parametersValues).list();
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					namedQuery, e }, e, log);
		}
	}
	
	/**
	 * Recupera um Objeto.
	 * @param namedQuery Query nomeada a ser utilizada
	 * @param parametersNames Relação de nomes dos parâmetros
	 * @param parametersValues Relação de valores dos parâmetros, na mesma ordem dos nomes.
	 * @return Objeto. Deve-se fazer 'casting' para o type esperado. Ex: Integer meuTotal = (Integer) recuperaObjetoComNamedQuery(...).
	 * @throws PlcException Exceção externa tratada de maneira genérica, padrão.
	 */
	protected Object retrieveObjectByNamedQuery(String namedQuery,
			String[] parametersNames, Object[] parametersValues) throws PlcException {
		
		try {
			
			return retrieveNamedQuery(namedQuery, parametersNames, parametersValues).uniqueResult();
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					namedQuery, e }, e, log);
		}
	}
	
}