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
package org.jcompany.persistence.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.PropertyValueException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcFileEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ANNOTATION;
import org.jcompany.commons.PlcConstantsCommons.ENTITY;
import org.jcompany.commons.annotation.PlcEntityMetadata;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.model.PlcModelLocator;
import org.jcompany.model.service.IPlcPhoneticService;
import org.jcompany.persistence.IPlcDAO;
import org.jcompany.persistence.PlcBaseDAO;
import org.jcompany.persistence.helper.PlcPersistenceHelper;
import org.jcompany.persistence.hibernate.helper.PlcAnnotationPersistenceHelper;
import org.jcompany.persistence.jpa.helper.PlcJpaHelper;
import org.jcompany.persistence.jpa.service.PlcQBEJpaService;
import org.jcompany.persistence.service.PlcQBEService;


@Stateless(name="PlcBaseJpaDAO")
@TransactionManagement(TransactionManagementType.CONTAINER)
/**
 * Classe DAO padrão para implementação JPA
 * @since jCompany 5.0
 */
public class PlcBaseJpaDAO extends PlcBaseDAO implements IPlcDAO {
	
	private Logger logWarningDevelop = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_WARNING_DEVELOPMENT);

	/**
	 * objeto para saida centralizada de LOG.
	 */
	protected Logger log = Logger.getLogger(PlcBaseJpaDAO.class);
	/**
	 * objeto para saida de LOG de profile.
	 */
	protected static final Logger logProfiling = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_QA_PROFILING);
    /**
     * EntityManager (equivalente ao Session hibernate) injetado pelo container.
     */
	@PersistenceContext protected EntityManager em;
	
	/**
	 * Construtor padrão
	 */
	public PlcBaseJpaDAO(){ }
    /**
     * Construtor auxiliar com possível injecao de dependências para facilitar testes de unidade
     * Importante: o jCompany usa Setter Based Injection nesta classe! Não utiliza este construtor!
     */
    public PlcBaseJpaDAO(PlcQBEJpaService serviceQBE){
    	this.serviceQBE = serviceQBE;
    }
    
    /**
     * Utilitários para a manipulação de Query By Example com Hibernate
	 * @since jCompany 5.0 
	 */
	protected PlcQBEService serviceQBE;
	
	/**
	 * Método que encapsula a obtenção do serviceQBE, gerênciando sua alocação.
	 * @since jCompany 5.0
	 */
	public PlcQBEService getServiceQBE() {
		if(serviceQBE==null)
			serviceQBE = new PlcQBEJpaService();
		return serviceQBE;
	}
	
	/**
	 * Encupsula o helper p/ fatoração dos algoritmos dependentes
	 * @since jCompany 5.0
	 */
	@Override
	protected PlcPersistenceHelper getHelper() {
		return helperJpa;
	}
	

	/**
	 * Método setter para configuração de um serviceQBE
	 * @since jCompany 5.0
	 */
	public void setServiceQBE(PlcQBEService serviceQBE) {
		this.serviceQBE = serviceQBE;
	}
	
	/**
	 * Utilitários para a manipulação de Anotações específicas da camada de persistência
	 * @since jCompany 5.0 
	 */
	protected PlcAnnotationPersistenceHelper helperAnnotation = PlcAnnotationPersistenceHelper.getInstance();
	
	 
	 //******************************************************************************************* 
	 //********************************  INICIO TRATAMENTO MSG *********************************** 
	 //******************************************************************************************* 
	
	/**
	 * @see org.jcompany.persistence.PlcBaseDAO#msgExceptionHandle(java.lang.Throwable)
	 */
    @Override
	public String[] msgExceptionHandle(Throwable rootCause) throws PlcException {
    	
		// Se é erro de JDBC
		if (rootCause != null && 
			(SQLException.class.isAssignableFrom(rootCause.getClass()))) {
			return msgExcecaoHandleSQL((SQLException)rootCause);
		} 
		if (rootCause != null && rootCause instanceof HibernateException) {
			// Trata erros de Hibernate (não controlados)	
			return msgExcecaoHandleHibernate((HibernateException)rootCause);
		} 
		
		String[] ret = apiHandleErrorsModelSpecific(rootCause);

		return ret;
		
	}
    
    
    /**
     * Trata erros de JDBC e devolve msg apropriada genérica
     * @since jCompany 5.0 
	 */
  	protected String[] msgExcecaoHandleSQL (SQLException errorSQL) {
  		if (errorSQL.getErrorCode() == 2292) {
			return new String[]{"jcompany.errors.commit.integrity",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 1400) {
			return new String[]{"jcompany.errors.column.null",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 1401) {
			return new String[]{"jcompany.errors.value.size",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 0001) {
			return new String[]{"jcompany.errors.sql.key.duplicated",errorSQL.getLocalizedMessage()};
		} else if (errorSQL.getErrorCode() == 1407) {
			return new String[]{"jcompany.errors.sql.column.null",errorSQL.getLocalizedMessage()};
		} else {
			return new String[]{"jcompany.errors.sql.unexpectedconcurrence",errorSQL.getLocalizedMessage()};
		}
	}
  	
  	/**
	 * @see org.jcompany.persistence.IPlcDAO#msgExcecaoHandleHibernate(org.hibernate.HibernateException)
	 */
	public String[] msgExcecaoHandleHibernate (HibernateException erroH) {
		//Funciona p/ Hibernate-EM
		if (erroH instanceof StaleObjectStateException) {
			return new String[]{"jcompany.errors.commit.concurrence",erroH.getLocalizedMessage()};
		} else if (erroH instanceof PropertyValueException) {
			int pos = erroH.getLocalizedMessage().lastIndexOf(".");
			String atributo = ((PropertyValueException)erroH).getPropertyName();
			return new String[]{"jcompany.errors.attribute.null.not.informed",atributo,erroH.getLocalizedMessage(),((PropertyValueException)erroH).getMessage()};
		} else if (erroH instanceof ObjectNotFoundException) {
			return new String[]{"jcompany.errors.commit.not.found",erroH.getLocalizedMessage()};
		} else if (erroH instanceof IdentifierGenerationException) {
			return new String[]{"jcompany.errors.commit.key.not.informed",erroH.getLocalizedMessage()};
		} else if (erroH.getLocalizedMessage().indexOf("Another object was associated with this id") > -1) {
			return new String[]{"jcompany.errors.sql.key.duplicated",erroH.getLocalizedMessage()};	
		} else {
			return new String[]{"jcompany.error.generic",erroH.getLocalizedMessage()};
		}
	}
	
	/**
     * @since jCompany 3.5
     * 
     * Template-Method para especialização do tratamento de mensagens da camada modelo
     * @param rootCause Exceçao
     * @return String[] contendo mensagem + args. Exemplo: return new String[]{"apl.errors.commit.concorrencia",causaRaiz.toString()};
     * ou null se a exceção nao form reconhecida para a camada modelo
     */
	protected String[] apiHandleErrorsModelSpecific(Throwable rootCause) throws PlcException {
		return null;
	}
	
	/**
	 * Utilitários para a manipulação de configurações do Hibernate
	 * @since jCompany 5.0
	 */
	protected PlcJpaHelper helperJpa = PlcJpaHelper.getInstance();

    
    
	 //============== CADIDATOS abstratos AO ANCESTRAL ======================= 
	/**
	 *  Encapsula a obtenção do EntityManager
	 *  @since jCompany 5.0 
	 *  @return Devolve EntityManager Padrão (equivalente sessão Hibernate).
	 */
	 protected EntityManager getEntityManager() throws PlcException {
		return getEntityManager("default");
	 }
	 
	 /**
	  * Encapsula a obtenção do EntityManager
	  * @since jCompany 5.0
	  * @return Devolve EntityManager(equivalente sessão Hibernate) da unidade de persistência selecionada .
	  */
	 protected EntityManager getEntityManager(String persistenceUnit) throws PlcException {

		 if (logPersistence.isDebugEnabled())
			 logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":getEntityManager:"));

		 if(em!=null) {

			 if (logPersistence.isDebugEnabled())
				   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":getEntityManager:null:"));

			 return em;
		 } else {
			 try {
				 String persistenceUnitName = persistenceUnit;
				 PlcBaseContextVO context = getContext();

				 if (persistenceUnit == null && context != null) {
					 persistenceUnitName = context.getFactoryPlc();
				 } else if (persistenceUnit == null && context == null){
					 persistenceUnitName = "default";
				 } else {
					 if ( context !=null && context.getFactoryPlc() != null  && 
							 !context.getFactoryPlc().equals(persistenceUnitName)	&&
							 "default".equals(persistenceUnitName)  ) {

						 persistenceUnitName = context.getFactoryPlc();
					 }
				 }		

				 // Se tem mais de uma sessão
				 if (context != null && context.getPersistenceServiceManagers() != null && !PlcJpaManagerLocator.getInstance().isInitialized()) {
					 PlcJpaManagerLocator.getInstance().setJpaManagerClasses(context.getPersistenceServiceManagers());
				 }

				 PlcJpaManagerLocator plcMLoc = PlcJpaManagerLocator.getInstance();
				 PlcBaseJpaManager jpaManager = plcMLoc.getJpaManagerClasse(persistenceUnitName);	

				 if (logPersistence.isDebugEnabled())
					 logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":getEntityManager:"));

				 return jpaManager.getEntityManager();

			 } catch (Exception e) {
				 throw new PlcException("jcompany.error.generic", new Object[] {
						 "getEntityManager", e }, e,log);
			 }
		 }
	 }
	 
	 
	 private Ejb3Configuration getCfg() throws PlcException {
	       return PlcJpaManagerLocator.getInstance().getJpaManagerClasse("default").getCfg();
	 }
	 
	 
	 
	/**
	 * @see org.jcompany.persistence.IPlcDAO#retrievePossibleDescendents(java.lang.Class)
	 */
	@Override
	public List<Class> retrievePossibleDescendents(Class classBase)
			throws PlcException {

		Ejb3Configuration cfg = getCfg();

		List<Class> l = new ArrayList<Class>();
		Iterator i = cfg.getClassMappings();
		try {
		
				 while (i.hasNext()) {
					 PersistentClass pc = (PersistentClass) i.next();
					 if (helperJpa.referenceWithManyToOne(pc,classBase.getName()) &&
							 PlcAnnotationHelper.getInstance().explorerUse(pc.getClassName()))
						 l.add(Class.forName(pc.getClassName()));
				 }
				
			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"retrievePossibleDescendents", e }, e, log);
			}
			 
			return l;
	}

	
	/**
	 * @see org.jcompany.persistence.IPlcDAO#begin(java.lang.String)
	 */
	public void begin(String factory) throws PlcException {
		
		 if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":BEGIN:"));

		 if (log.isDebugEnabled())
	   		 log.debug("########## Entered in BEGIN. Persistence Unit "+factory+"!");

		PlcJpaManagerLocator.getInstance().getJpaManagerClasse(factory).getEntityManager().getTransaction().begin();
	}


	/**
	 * @see org.jcompany.persistence.IPlcDAO#rollback(java.lang.String)
	 */
	@Override
	public void rollback(String factory) throws PlcException {

		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":ROLLBACK:"));

		if (log.isDebugEnabled())
   		 log.debug("########## Entered in ROLLBACK. Persistence Unit "+factory+"!");

		PlcJpaManagerLocator.getInstance().getJpaManagerClasse(factory).rollback();
	}


	/**
	 * @see org.jcompany.persistence.IPlcDAO#commit(java.lang.String)
	 */
	@Override
	public void commit(String factory) throws PlcException {

		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":COMMIT:"));

		 if (log.isDebugEnabled())
    		 log.debug("########## Entered in COMMIT. Persistence Unit "+factory+"!");
		 
		 if (logProfiling.isDebugEnabled() && PlcAopProfilingHelper.getInstance().getLevel()>=1)
				PlcAopProfilingHelper.getInstance().transactionCount("commit");
    	 
		 PlcJpaManagerLocator.getInstance().getJpaManagerClasse(factory).commit();
	
	}



	/**
	 * @see org.jcompany.persistence.PlcBaseDAO#sendCacheCommands(java.lang.Class)
	 */
	@Override
	public void sendCacheCommands(Class clazz) throws PlcException {
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":sendCacheCommands:"));

		EntityManager emLocal = null;
		try {
			if(null != clazz) {
				emLocal = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(clazz));
			} else {
				emLocal = getEntityManager();
			}
			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":sendCacheCommands:"));

			em.flush();
		} catch (Exception e) {
	            throw new PlcException("jcompany.error.generic", new Object[] {
	                    "sendCacheCommands", e }, e,log);
		}
	}

	/**
	 * @see org.jcompany.persistence.IPlcDAO#insert(org.jcompany.commons.Object)
	 */
	@Override
	public Long insert(Object entity) throws PlcException {

		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":insert:"));

		try {
			
			helperJpa.doPhoneticsTreatment(entity);
			
			EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
			
			if(PropertyUtils.getProperty(entity,"id")!=null) {
				PropertyUtils.setProperty(entity,"id",null);
			}
			
			
			em.persist(entity);
			em.flush();
			Long pk = (Long) PropertyUtils.getProperty(entity,"id");

			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":insert:"));

			return pk;
			
			
		}  catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"insert", e }, e,log);
		}

		
	}

	/**
	 * @see org.jcompany.persistence.IPlcDAO#update(org.jcompany.commons.Object)
	 */
	@Override
	public void update(Object entity) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alter:"));

		try {
    		
    		
			helperJpa.doPhoneticsTreatment(entity);
		
    		EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
			
			if (log.isDebugEnabled())
				log.debug("Altering entity "+entity);
			
			if(PropertyUtils.getProperty(entity,"id")!=null) {
			    	em.merge(entity);
			    	em.flush();
			} else {
				em.persist(entity);
			}

			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alter:"));
			
			
		}  catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
                    "alter", e }, e,log);
		}

	}

	/**
	 * @see org.jcompany.persistence.IPlcDAO#exclude(org.jcompany.commons.Object)
	 */
	@Override
	public void exclude(Object entity) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":exclude:"));

		try {
			
			EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(entity.getClass()));
			
			if (log.isDebugEnabled())
				log.debug("Excluding entity "+entity);

			em.remove(em.merge(entity));
			em.flush();
			
			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":exclude:"));

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.exclude",new Object[] {e},e,log);
		}

	}

	/**
	 * @see org.jcompany.persistence.IPlcDAO#exclui(java.lang.Class,java.lang.Object)
	 */
	@Override
	public Object retrieve( Class clazz, Object id) throws PlcException{

		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieve:"));

		try {

			applyFilters(clazz);

			Class classeAux = getHelper().convertDynamicProxyToOriginalClass(clazz);
					
			String queryEdition = PlcAnnotationPersistenceHelper.getInstance().getAnnotationQueryEditionDefault(clazz);
			
			queryEdition = getServiceQBE().mountFromWhere(queryEdition, classeAux, id);
			
			Object entity = apiCreateQuery(clazz,queryEdition).setParameter(1,id).getSingleResult();
				
			if (entity==null)
				throw new PlcException("jcompany.errors.record.not.found",new Object[]{id.toString()});
				
			// Garante que colecoes participantes da lógica principal, mesmo se estiverem Lazy, são carregadas
			// antes do 'detached'
			entity = retrieveGraphDefault(entity);

			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieve:"));

			return entity;
			
		} catch (Exception e) {
			 throw new PlcException("jcompany.error.generic", new Object[] {"retrieve", e }, e,log);
		}

	}
	
	/**
	 * Recupera grafo padrao para Entidade informada
	 */
	protected Object retrieveGraphDefault(Object entity) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveGraphDefault:"));

		//Transforma proxy do objeto principal no objeto real, caso ele seja um
		transformProxyInRealObject(entity);

		if (getContext() != null && !"download".equals(getContext().getOriginalAction())) {
			if (getContext().getMainClass()!=null && entity.getClass().getName().equals(getContext().getMainClass().getName())) {
				// Para cada coleçao declarada na lógica, força a recuperaçao se estiver lazy
				if (getContext().getDetailNamesPlc() != null) {
					Iterator i = getContext().getDetailNamesPlc().iterator();
					while (i.hasNext()) {
						String nameColDet = (String) i.next();
						try {
							//Pode ser download ou exclusao de arquivo.
							if (PropertyUtils.isReadable(entity,nameColDet) && !getContext().isOnDemand(nameColDet))
								Hibernate.initialize(PropertyUtils.getProperty(entity,nameColDet));		
							else if (getContext().isOnDemand(nameColDet)) {
								PropertyUtils.setProperty(entity,nameColDet,null);
							}


							//Colecoes de detalhe manyToMany devem ter o outro lado anulado para evitar problemas de Lazy Initialization.
							Collection details = (Collection) PropertyUtils.getProperty(entity,nameColDet);
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


						}  catch (Exception e) {
							throw new PlcException("jcompany.error.generic",
									new Object[] { "retrieveGraphDefault", e }, e, log);
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
				}  catch (Exception e) {
					throw new PlcException("jcompany.error.generic", new Object[] {
							"retrieveGraphDefault", e }, e, log);
				}
			}	
		}

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveGraphDefault:"));

		// Permite extensões próprias
		//recuperaGrafoPadraoApos(vo);

		return entity;

	}
	
	
    
	/**
	 * Recupera arquivo binário por Object-Id
	 * @see org.jcompany.persistence.jpa.IPlcJpaDAO#retrieveBinaryFileById(java.lang.Class, java.lang.Long, java.lang.String)
	 */
	@Override
	public byte[] retrieveBinaryFileById(Class clazz, Long oid,String property) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveBinaryFileById:"));

		try {
			
			String query = "select obj."+property+" from obj in "+clazz.getClass() +" where obj.id="+oid;
	
			if (log.isDebugEnabled())
					log.debug("query to get image from file="+query);
	
			EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(clazz));
			Iterator res = em.createQuery(query).getResultList().iterator();
	
			byte [] image = null;
	
			if (res.hasNext()) {
				image = (byte[]) res.next();
			} 	

			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveBinaryFileById:"));
			
			return image;
		
		}  catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"retrieveBinaryFileById", e }, e,log);
		}
	}

	/**
	 * Recupera arquivo por URL
	 * @see org.jcompany.persistence.jpa.IPlcJpaDAO#retrieveEntityFileByUrl(java.lang.Class, java.lang.String)
	 */
	@Override
	public IPlcFileEntity retrieveEntityFileByUrl(Class clazz, String url) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveEntityFileByUrl:"));

		String query = "from obj in class "+clazz.getName() +
        " where lower(obj.url)=:url";

		try {

			EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(clazz));

			List l = em.createQuery(query).setParameter("url",url.toLowerCase()).getResultList();

			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityFileByUrl:"));

			if (l.size()>0) {
				return (PlcFileEntity) l.get(0);
			} else
				throw new PlcException("jcompany.errors.retrieve.image.file",new Object[] {"Query="+query});

		}  catch (Exception e) {
			throw new PlcException("jcompany.errors.download.url",new Object[]{url},e,log);
		}
	}
	
	/**
	 * @since jCompany 3.0
	 * 
	 * Devolve query padrão, preferencialmente de anotações. Pode ser sobreposto nos descendentes
	 * para montagem específica de HQLs
	 * @param clazz Classe principal
	 * @return Annotation contendo query padrão para QBE ou um select "from [Classe] obj"
	 */
	protected String getQuerySelDefault(Class clazz) throws PlcException {
			
		String query = null;
		
		PlcBaseContextVO context = getContext();
		
		if (context != null) {
			
			// lógicas de manutenção tabulares ou crud-tabulares usam queryMan se existir.
			if (context.getApiQuerySel() != null)
				query = context.getApiQuerySel();
			else if (PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(context.getLogic()) ||
					PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(context.getLogic()))
				query = "queryMan";
			else if (PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TREEVIEW.equals(context.getLogic()))
				query = "queryTreeView";
		
		}
		
		NamedQuery nq = null;
		
		if (query != null) {
			
			nq = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz,query);
			
			// Se nao há NamedQuery especifica para treeview, procura a de seleção padrão como alternativa
			if (nq == null && query.equals("queryTreeView"))
				nq = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz,"querySel");
			
			if (nq == null) {
			
				if (query.equals("queryMan") || query.equals("queryTreeView")) {
					String nameClassWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
					return "from "+nameClassWithoutPackage+" obj";
				} else					
				  throw new PlcException("jcompany.namedquery.not.found.to",new Object[]{clazz.getName(),
						clazz.getName().substring(clazz.getName().lastIndexOf(".")+1)+"."+query});
			}
		} else {
			
			String apiQuerySel = getContext().getApiQuerySel();
			
			if (StringUtils.isEmpty(apiQuerySel))
				apiQuerySel = ANNOTATION.SUFFIX_QUERYSEL_DEFAULT;
			
			Annotation a = PlcAnnotationHelper.getInstance().getAnnotationQueryQbeOrSelDefault(clazz, apiQuerySel);
			   	
	    	if (a != null && NamedQueries.class.isAssignableFrom(a.getClass())){
	    		 /**jCompany 3.0 
				  * Se for lógica de Relatório tem que buscar a anotação querySelRel "NamedQuery"
				  * @autor - Pedro Henrique - 22/03/2006 
				  */
	    		if (( context.getLogic() != null )&&( context.getLogic().startsWith(PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_REPORT)))
		    		a = PlcAnnotationPersistenceHelper.getInstance().getNamedQuerySelReport((NamedQueries)a,context.getReportCurrentNamedQuery());
				else
		    		a = PlcAnnotationPersistenceHelper.getInstance().getNamedQuerySelDefault((NamedQueries)a);

	    	}
	
			if (a != null) {
				nq = (NamedQuery)a;
			} else {
				String nameClassWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
				return "from "+nameClassWithoutPackage+" obj";
			}
		}
		
		return nq.query();		
	}
		
	/**
	 * Coloca as chaves foneticas no ENTITY informado
	 * @param entity ENTITY antes de ser incluido ou alterado, para inclusao de valores fonéticos
	 */
	protected void doPhoneticsTreatment(Object entity)  throws Exception {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":doPhoneticsTreatment:"));

		IPlcPhoneticService phonetics = null;
		try {
			phonetics = (IPlcPhoneticService) PlcModelLocator.getInstance().get(IPlcPhoneticService.class);
		} catch (PlcException e) {
			// Erro esperado caso não tenha sido registrado uma Implementação para IPlcPhoneticService
			// Nao deve interonper a ação, apenas não sera feito nenuma tratamento fonetico
			phonetics = null;
		}

		PlcEntityMetadata annotation = (PlcEntityMetadata)entity.getClass().getAnnotation(PlcEntityMetadata.class);

		if(annotation != null) {
			if(annotation.phoneticsUsa()) {
				if (phonetics != null) {
					Field[] attributs = entity.getClass().getSuperclass().getDeclaredFields();			
					for (Field field : attributs) {
						int index = field.getName().indexOf(
								PlcConstantsCommons.CONSULTATION.QBE.QBE_ATTR_SUFFIX_PHONETICS);
						if (index != -1) {
							String fieldName = field.getName().substring(0, index);
							for (Field field2 : attributs) {
								if (field2.getName().equals(fieldName)) {
									//Nao usa o metodos set para evitar que estes alterem o valor final
									field.setAccessible(true);
									field2.setAccessible(true);
									String value = (String)field2.get(entity);
									String key = phonetics.phonetics(value);
									field.set(entity,key);

								}
							}
						}
					}
				}	
			}
		}

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":doPhoneticsTreatment:"));

	}
	
	/**
	 * @see org.jcompany.persistence.IPlcDAO#isOptimizeUpdate()
	 */
	@Override
	 public boolean isOptimizeUpdate() throws PlcException {
		//TODO fazer otimização para JPA
		return false;
	 }
	
		
	/**
	 * Recupera coleção de detalhes do ENTITY informado
	 * @since jCompany 5.0
	 * @param entity ENTITY Mestre
	 * @param classDetail Classe do Detalhe a ser recuperado
	 * @param namePropDetail Nome da propridade (coleçao) de detalhe a ser montada
	 * @return ENTITY Mestre incluindo coleção de detalhe recuperada.
	 */
	@Override
	public Object retrieveOnDemand(Object entity, String namePropDetail, Class classDetail) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":recuperaPorDemanda:"));

		//Transforma proxy do objeto principal no objeto real,
		//caso ele seja um
		entity = transformProxyInRealObject(entity);
		
		try {

			String hql = "select obj from "+classDetail.getName()+" obj where obj."+
			PlcEntityHelper.getInstance().getPropertyNamePlc(entity)+"=:pai";
						
			Query query = apiCreateQuery(classDetail,"select obj from "+classDetail.getName()+
					" obj where obj."+
					PlcEntityHelper.getInstance().getPropertyNamePlc(entity)+"=:pai");
			query.setParameter("pai",entity);
			List l = query.getResultList();
	
			if (Set.class.isAssignableFrom(PropertyUtils.getPropertyType(entity,namePropDetail))) {
				Set s = new HashSet();
				s.addAll(l);
				PropertyUtils.setProperty(entity,namePropDetail,s);
			} else {
				PropertyUtils.setProperty(entity,namePropDetail,l);
			}
			
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				Object entityDet = iter.next();
				entityDet = retrieveGraphDefault(entityDet);
			}
		
			
		}  catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {	"retrieveOnDemand", e }, e, log);
		}

		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":recuperaPorDemanda:"));
		
		return entity;
	}
	
	/**
     * @since jCompany 5.0
     * 
     * Automação de navegação em grafo de VOs
     */
    @SuppressWarnings("unchecked")
	@Override
	public List retrieveAggregateNavigation(Object entityMain, Object entityAggregateDestiny) throws PlcException {
    	
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAggregateNavigation:"));

		try {
	    	
	        // Descobre por qual propriedade a classe destino é acessada a partir da origem
	        String propNavegation = helperJpa.retrievePropertyManyToOne(entityAggregateDestiny.getClass(), entityMain.getClass());
	        
	        // Recebe o vo principal
	        Object origin = PropertyUtils.getProperty(entityMain,propNavegation);

	        String querySelLookup = PlcAnnotationPersistenceHelper.getInstance().getAnnotationQuerySelLookup(entityAggregateDestiny.getClass());
	        if (querySelLookup==null)
	        	querySelLookup="from "+entityAggregateDestiny.getClass().getName() +" obj where obj."+propNavegation+"=?";
	        
	        
	        Query query = apiCreateQuery(entityAggregateDestiny.getClass(),querySelLookup);
	        
	        if (querySelLookup.contains("?")) {
	        	query.setParameter(1,origin);
	        } else if (querySelLookup.contains(":id")) {
	        	query.setParameter("id", origin);
	        } else if (querySelLookup.contains(":"+propNavegation)) {
	        	query.setParameter(propNavegation, origin);
	        } else {
	        	throw new PlcException("jcompany.error.querysellookup", new Object[]{entityAggregateDestiny.getClass().getName()});
	        }

	    	if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateNavigation:"));

			return query.getResultList();

        }  catch (Exception e) {
        	throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveAggregateNavigation", e }, e,log);
		}
	}
	
    /**
     * @since jCompany 5.0
     * Recupera relação de registros a partir de uma classe origem com o pk informado
     * @param pk OID ou Classe de chave composta com valores
     * @param aggregateDestiny Classe agregado de destino
     * @return Coleção de valores possíveis para destino.
     */
	@Override
	public List retrieveAggregateNavigation(Class classMain, Object pk, Class aggregateDestiny) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAggregateNavigation:"));

		try {  	
	
	        // Descobre por qual propriedade a classe destino é acessada a partir da origem
	        String propNavegation = helperJpa.retrievePropertyManyToOne(aggregateDestiny, classMain);
	
	        //String querySelLookup = PlcAnnotationPersistenceHelper.getInstance().getAnotacaoQuerySelLookup(agregadoDestino);
	        String querySelLookup = null;
	        if (querySelLookup==null)
	        	querySelLookup="from "+aggregateDestiny.getName() +" obj where obj."+propNavegation+".id=?";        
	        Query query = apiCreateQuery(aggregateDestiny,querySelLookup);
	        
	        if (querySelLookup.contains("?")) {
	        	query.setParameter(1,pk);
	        } else if (querySelLookup.contains(":id")) {
	        	query.setParameter("id", pk);
	        } else {
	        	throw new PlcException("jcompany.error.querysellookup", new Object[]{aggregateDestiny.getName()});
	        }
	        
			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateNavigation:"));

			return query.getResultList();

        }  catch (Exception e) {
        	throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveAggregateNavigation", e }, e,log);
		}
	}
	
	/**
	 * @see org.jcompany.persistence.IPlcDAO#retrieveLookupAggregate(org.jcompany.commons.Object, java.lang.String, java.lang.Object, java.lang.String[])
	 */
	@Override
	public Object retrieveLookupAggregate(Object baseEntity, String propertyName, Object value, String[] props) throws PlcException {

		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregate:"));
		
		try {
    		
    		if (propertyName==null || propertyName.trim().length()==0) {
    			propertyName = "id";
    		}
    		
			Object baseEntityRetrieved = retrieveAggregateLookupByNamedQuery(baseEntity.getClass(), propertyName, value);
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
    		
    	}  catch (Exception e) {
    		throw new PlcException("jcompany.error.generic", new Object[] {
    				"retrieveLookupAggregate", e }, e,log);
		}
	}
	
	/**
     * @since jCompany 5.0
     * 
     * Sobrepor para especializar queries no caminho padrão
     * @param q
     * @param argTypes
     * @param argValues
     * @throws PlcException
     */
	protected List hqlExecuteQuery( Query q, List argTypes, Object[] argValues) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":hqlExecuteQuery:"));

		try {
	
			for (int counter = 0; counter < argValues.length; counter++) {
				
				Object argValue = argValues[counter];
				Object argType = argTypes.get(counter);
		
				if (argType.equals(Hibernate.DATE))
					q.setParameter(counter+1, (Date)argValue, TemporalType.DATE);
				else if (argType.equals(Hibernate.TIMESTAMP))
					q.setParameter(counter+1, (Date)argValue, TemporalType.TIMESTAMP);
				else
					q.setParameter(counter+1,argValue);

			}
			
			return q.getResultList();
			
		}  catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"hqlExecuteQuery", e }, e, log);
		}
	
	}
	
	/**
     * @since jCompany 5.0
     * 
     * Recebe um objeto Criteria e aplica argumentos dinamicamente.
     */
	protected List criteriaRetrieveQBE(Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE)  {
		// TODO implementar criteria utilizando jpa
		return null;
	}
	
	/**
	 * Aplica filtros associados a entidade para recuperacao das instâncias
	 * @ since jCompany 5.0
	 */
	@Override
	protected void applyFilters(Class clazz) {
		// TODO implementar os filtros para JPA 
	}
	
		
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
     * @since jCompany 5.0
     * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBEPaginated(java.lang.Class, java.lang.String, org.jcompany.commons.Object, int, int)
     */
	@Override
	public List retrieveListQBEPaginated(Class clazz, String whereDynamic, String orderByDynamic, Object argQBE, int firstLine, int maximumLines) throws PlcException {
	
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
	    	
	    	//TODO Vai funcionar apenas para treeview
		    String[] parametersNames =  getServiceQBE().distillHQLArguments(query);
		    Query q = apiCreateQuery(clazz,query);
		    for (String parameter : parametersNames) {
		    	if(null != parameter) {
		    		if(PropertyUtils.isReadable(argQBE, parameter)) {
		    			Object o = PropertyUtils.getProperty(argQBE, parameter);
		    			if (PropertyUtils.isReadable(o,"id")) {
		    				q.setParameter(parameter, PropertyUtils.getProperty(o,"id"));	
		    			} else {
		    				q.setParameter(parameter, o);
		    			}
				}
			}
					
			}
	    	
			if (firstLine >= 0) q.setFirstResult(firstLine);
		
			if (maximumLines == 0 )
				logWarningDevelop.debug(this.getClass().getCanonicalName()+": Maximum Lines number entered as zero!");
			else if (maximumLines >= 1)
				q.setMaxResults(maximumLines);

			List list = q.getResultList();
		
			if (log.isDebugEnabled()) log.debug("found "+list.size()+" record(s)");
		
			if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBEPaginated:"));

			return list;
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListQBEPaginated", e }, e, log);
		}
	}
	
	
	
	/**
     * Recupera uma lista de objetos da classe informada, utilizando queries anotadas no padrao "querySel" ou "queryTreeView", etc., em 
     * conformidade com o Application Pattern utilizado. Pode receber orderByDinamico (alterado pelo usuário ou desenvolvedor em cada
     * requisição) e também um trecho de where condition para ser dinamicamente composto (uso em lógicas dinamicas tais como
     * explorer).
     * @param clazz Classe da qual será obtida a query em anotação padrao ("querySel", "queryTreeView", etc., conforme a lógica chamadora)
     * @param whereDynamic (Opcional - informe null ou "" para desconsiderar) Trecho a ser adicionado dinamicamente à query anotada. Importante: Nao informar o 'where' em si e utilizar o alias padrão 'obj.' (Ex:'obj.status=:status and obj.valor>:valor')
     * @param orderByDinamico (Opcional - informe null ou "" para desconsiderar) Trecho de orderBy a ser adicionado dinamicamente à query anotada
     * @param argQBE POJO contendo valores preenchidos para todos os argumentos existentes fixos na query ou montados dinamicamente em whereDinamico
     * @since jCompany 5.0
     */
	@Override
	public Integer retrieveListQBETotal(Class clazz, String whereDynamic, Object argsQBE) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBETotal:"));

		applyFilters(clazz);
		
		 try {
			   String queryCount = getServiceQBE().getQueryCount(clazz);
			   
			   if(queryCount == null) {
				   String query = getServiceQBE().getQuerySelDefault(clazz,getContext());

				   if (whereDynamic != null && !whereDynamic.equals(""))
					   query = retrieveListQBEPaginatedChangeWhere(query,whereDynamic);

			       int posFrom = query.indexOf("from");
			 	
		           if (posFrom == -1)  
		        	   throw new PlcException("jcompany.error.hql.without.from");

		           queryCount = "select count(*) " + query.substring(posFrom, query.length());

		           // retira order by e group by
		           int posOrder = queryCount.toLowerCase().indexOf("order by");
		           int posGroup = queryCount.toLowerCase().indexOf("group by");
		           int size = queryCount.length();
		           int end = size;

		           if (posOrder != -1 || posGroup != -1) {
		               if (posOrder < posGroup)
		                   end = posGroup;
		               else
		                   end = posOrder;
		           }

		           if (end != size)
		               queryCount = queryCount.substring(0, end);

			   }
			   	        
	           apiHandleArgsBeforeQuery(clazz,queryCount,argsQBE);
	           
	           String namesArgs[] = getServiceQBE().distillHQLArguments(queryCount);
	           
	           Query q = apiCreateQuery(clazz,queryCount);
	           
	           
	           for(int i=0; namesArgs[i]!=null;i++) {
	        	   if(PropertyUtils.isReadable(argsQBE, namesArgs[i]))
	        		   q.setParameter(namesArgs[i],PropertyUtils.getProperty(argsQBE, namesArgs[i]));
	           }
	           
	           List l = q.getResultList();
	           Object ret = l.get(0);

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
	 * @since jCompany 5.0
	 * 
	 * Recupera objetos de 'classe', filhos do objeto de 'classeBase' cujo OID seja 'idClasseBase'.
	 * @param clazz Classe para recuperação, que possui um relacionamento many-to-one com a classeBase
	 * @param classBase Classe do lado "one" do relacionamento
	 * @param idClassBase OID de objeto da classe do lado "one" do relacionamento
	 * @return Lista contendo objetos que atendem ao critério, com as seguintes regras:<p>
	 * 1. se 'classe' tiver anotação de paginação, e posIni for -1 (primeira chamada), entao devolve o total de registros existentes na
	 * primeira posição, considerando somente Ativos se estiver utilizando "sitHistoricoPlc" e este campo estiver mapeado na classe.<br>
	 * 2. se 'classe' contiver anotação de paginação e posIni for acima de 1 (usa 1 para primeiro objeto), então recupera o numero de registros
	 * indicado a partir de posIni<br>
	 * 3. se 'classe' não contiver anotação de paginação, recupera todos os registros respeitando 'sitHistoricoPlc' também.<p>
	 * Em qualquer caso, recupera obedecendo a ordenação indicada em PlcEntityMetadata(ordenacao=....)
	 * @see org.jcompany.persistence.PlcBaseDAO#recuperaListaExplorer(java.lang.Class, java.lang.Class, java.lang.Object)
	 */
	@Override
	public List retrieveExplorerList(Class clazz, Class classBase, Object idClassBase, long initialPos) throws PlcException {
		
        if (logPersistence.isDebugEnabled())
	    	logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveExplorerList:"));

		int numberByPage = PlcAnnotationHelper.getInstance().getEntityNumberByPage(clazz,0);

		// Ajusta para reutilizar funcoes de paginacao do jCompany que originalmente usam int
		int initialPositionInt = (int) initialPos;
		
		boolean isPaginated = numberByPage >0;
		
		boolean mappedStatusHistoricPlc = helperJpa.existsStatusHistoricPlcMapped(getCfg(),clazz);
		
		String ordination = PlcAnnotationHelper.getInstance().getEntityOrdination(clazz,null);
		
		try {
	
			List l=null;
			Object entityArg = clazz.newInstance();
			String where=null;
			if (mappedStatusHistoricPlc) {
				PropertyUtils.setProperty(entityArg,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_ACTIVE);
				where = "obj.sitHistoricoPlc='A'";
			}
			
			where = retrieveListExplorerComposeArgs(clazz,classBase,idClassBase,initialPos,entityArg,where);

			if (isPaginated && initialPos==-1) {
				Integer tot = retrieveListQBETotal(clazz,where,entityArg);
				l = new ArrayList(0);
				l.add(tot);
				if (log.isDebugEnabled())
					log.debug("Finalized explorer with "+tot+" records");
			} else if (isPaginated && initialPos>-1) {
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
	    	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":retrieveListExplorerComposeArgs:"));

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
			if (where == null)
				return "obj."+prop+".id=:"+prop;
			else
				return where + " and obj."+prop+".id=:"+prop;
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveListExplorerComposeArgs", e }, e, log);
		}
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
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":apiCreateQuery:"));

		EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(classEntity));
		return em.createQuery(hql);
	}
	
	/**
	 * Cria uma query, executa e retorna apenas um resultado
	 * @see jCompany 5.0 
	 */
	@Override 
	protected Object apiCreateExecuteOnlyOneResult(Class classEntity, String hql) throws PlcException {
		
	     if (logPersistence.isDebugEnabled())
		   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":apiCreateExecuteOnlyOneResult("+hql+"):"));

		Query q = apiCreateQuery(classEntity, hql);
		try {
			return q.getSingleResult();
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
	protected  List apiCreateExecute(Class classeEntity, String hql) throws PlcException {
		
	     if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":apiCreateExecute("+hql+"):"));

		try {
			Query q = apiCreateQuery(classeEntity, hql);
		
			return q.getResultList();
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
	protected List apiCreateExecute(Class classeEntity, String QBE, List argTypes, Object[] argValues) throws PlcException {

		if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(this.getClass().getSimpleName()+":apiCreateExecute("+QBE+"):"));

		try {
			Query q = apiCreateQuery(classeEntity, QBE);
			
			return hqlExecuteQuery(q, argTypes, argValues);			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"apiCreateExecute", e }, e, log);
		}
	}
	
	/**
	 * Transforma o proxy de uma entidade em um objeto real,
	 * recupperando todo seu grafo. 
	 * @since jCompany 5.0
	 */
	@Override
	protected  Object transformProxyInRealObject(Object entity) {
		//TODO Como implementar de maneira genérica p/ JPA
		if (HibernateProxy.class.isAssignableFrom(entity.getClass())) {
			entity = ((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation();
		}
		return entity;
	}
	
	/**
	 * Torna o objeto somente de consulta durante uma sessão
	 * @since jCompany 5.0 
	 * @deprecated
	 */
	@Override
	public void registerConsultationOnly(Object beanMain, Collection collection) throws PlcException {
		//TODO não tem setReadOnly p/ EntityManager
	}

	/**
	 * Cria uma query, registra seus parametros e executa e retorna  lista de resultados
	 * @see jCompany 5.0 
	 */	
	@Override
	protected List apiCreateQueryWithArgsExecute(Class clazz, String query, 
		String orderByDynamic, Object[] argValues, Object[] argTypes, int firstLine, int maximumLines) throws PlcException {
		
	     if (logPersistence.isDebugEnabled())
			   	logPersistence.debug(PlcAopProfilingHelper.getInstance().showInternalLog(
			   			this.getClass().getSimpleName()+":apiCreateQueryWithArgsExecute("+query+"):"));

		try {
			Query q = createQueryComArgs(clazz, query, orderByDynamic, argValues, argTypes);
			
			if (firstLine >= 0) q.setFirstResult(firstLine);
			
			if (maximumLines == 0 )
				log.warn("Maximum Lines number entered as zero!");
			else if (maximumLines >= 1)
				q.setMaxResults(maximumLines);
			
			return q.getResultList();
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"apiCreateQueryWithArgsExecute", e }, e, log);
		}
		
	}
	
	/**
	 * Cria um objeto Query utilizando os parametros informados
	 * @param clazz Tipo de objeto para FROM
	 * @param query Clausula de consulta
	 * @param orderByDynamic Clausula para ordenação 
	 * @param argValues Lista de valores dos argumentos da consulta
	 * @param argTypes Lista dos Tipos de argumentos da consulta
	 * @return Query pronta monta utilizando os argumentos
	 * @throws PlcException
	 */
	protected Query createQueryComArgs( Class clazz, String query, 
		String orderByDynamic, Object[] argValues, Object[] argTypes) throws PlcException {
	   
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
				this.getClass().getSimpleName()+":createQueryComArgs("+query+"):"));

	    try {

	    	if (orderByDynamic != null && !orderByDynamic.equals("") &&
	    			!(getContext() != null &&
	    					PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TREEVIEW.equals(getContext().getLogic())))
	    		query = getServiceQBE().changeOrderBy(query,orderByDynamic);

		if (getContext() != null &&
			PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TREEVIEW.equals(getContext().getLogic()))
		    query = createQueryWithArgsAdjustTreeview(query,argValues);

		Query q = apiCreateQuery(clazz,query);

		if (argTypes == null) {
			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
					this.getClass().getSimpleName()+":createQueryComArgs("+query+"):return:"));

			return q;
		}


		for (int i = 0; i < argTypes.length; i++) {

		    if (Type.class.isAssignableFrom((argTypes[i]).getClass())) {

		    	Type type = (Type) argTypes[i];
		    	Object value = argValues[i];

		    	if (type.equals(Hibernate.DATE))
		    		q.setParameter(i+1,(Date)value,TemporalType.DATE);
		    	else if (type.equals(Hibernate.TIMESTAMP))
		    		q.setParameter(i+1,(Date)value,TemporalType.TIMESTAMP);
		    	else if (type.equals(Hibernate.TIME))
		    		q.setParameter(i+1,(Date)value,TemporalType.TIME);
		    	else
		    		q.setParameter(i+1,value);
			

		    } else {

		    	String type = ((String) argTypes[i]).toUpperCase();
		    	Object value = argValues[i];

		    	if (type.equals(PlcConstantsCommons.TYPES.DATE))
		    		q.setParameter(i+1,(Date)value,TemporalType.DATE);
		    	else if (type.equals(PlcConstantsCommons.TYPES.TIMESTAMP))
		    		q.setParameter(i+1,(Date)value,TemporalType.TIMESTAMP);
		    	else
		    		q.setParameter(i+1, value);
		    }
		}	

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
				this.getClass().getSimpleName()+":createQueryComArgs("+query+"):"));

		return q;

	    } catch (Exception e) {
	    	throw new PlcException("jcompany.error.generic", new Object[] {
			"createQueryComArgs", e }, e, log);
	    }
	}
	
	/**
	 * Ajusta primeira query inicializa, para treeview, trocando argumento pai para is null
	 */
	protected  String createQueryWithArgsAdjustTreeview(String query, Object[] argValues) {
		if ((argValues == null || argValues.length==0) && query.indexOf(":id")>-1) {
			return StringUtils.replaceOnce(StringUtils.replaceOnce(query, "= :id"," is null"), "=:id"," is null");
		}
		return query;
	}
	
	private Object retrieveAggregateLookupByNamedQuery(Class clazz, String propertyName, Object value)  throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAggregateLookupByNamedQuery:"));

		NamedQuery namedQuery = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz, PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_LOOKUP);
		if (namedQuery!=null) {
			EntityManager em = getEntityManager(PlcAnnotationHelper.getInstance().getFactoryName(clazz));
			Query query = em.createNamedQuery(namedQuery.name());
			String queryString = query.toString();
			if (queryString.contains("?")) {
				query.setParameter(1, value);
			} else {
				query.setParameter(propertyName, value);
			}
			List list = query.getResultList();
			if (list.size()>0) {
				if (logPersistence.isDebugEnabled())
					logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateLookupByNamedQuery:"));
				return list.get(0);
			}
		}
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateLookupByNamedQuery:null:"));

		return null;
	}
	
	/**
     * jCompany 3.1 Recupera valores de propriedades de um ENTITY informado,
     * a partir de um campo qualquer,
     * devolvendo os valores preenchidos no próprio ENTITY
     * @param propertyName nome da propriedade de indexação. Se null, considera OID
     */
	private Object retrieveEntityWithGraph(Object baseEntity, String propertyName) throws PlcException {
		
		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveEntityWithGraph:"));

		try {
        	
    		if (propertyName==null || propertyName.trim().length()==0) {
    			propertyName = "id";
    		}
    		
	        String hql = "from "+baseEntity.getClass().getName()+" obj where obj."+propertyName+"=?";

	        Object valor = PropertyUtils.getProperty(baseEntity, propertyName);
	        
			Query q = apiCreateQuery(baseEntity.getClass(),hql);
			List l = q.setParameter(1,valor).getResultList();

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityWithGraph:"));

			return l.size()==0 ? null : l.get(0);
        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveEntityWithGraph", e }, e,log);
        }
	}
	
	/**
     * jCompany 3.1 Recupera um ENTITY informado e seu grafo (lazy=false),
     * a partir de uma propriedade qualquer, que deve vir preenchida no ENTITY passado.
     * @param propertyName nome da propriedade pesquisada. Se for null, considera OID.
     * @return instancia do ENTITY recuperada, contendo coleções e classes membro do grafo (lazy=false)
     */
	private Object retrieveEntityWithoutGraph(Object baseEntity, String propertyName,  String[] props) throws PlcException {
		
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

	        String hql = "select "+helperJpa.mountSelectFromArrayToString(props)
	                +" from "+baseEntity.getClass().getName()+" obj where obj."+propertyName+"=?";

	        Object value = PropertyUtils.getProperty(baseEntity, propertyName);
	        
			Query q = apiCreateQuery(baseEntity.getClass(),hql);
			 
			List l = q.setParameter(1,value).getResultList();

	        if (l.size()==0) {
	        	if (logPersistence.isDebugEnabled())
	    			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveEntityWithoutGraph:"));
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
	* @since jCompany 5.0
	* Executa um "select count(*)" do type "avoidEquals"
    * @return Total de registros e valores informados
    */
	@Override
	protected Object[] verifyTotalResults(String modo, String hql, Object plcVO) throws PlcException {

		if (logPersistence.isDebugEnabled())
			logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":verifyTotalResults:"));

		try {

			Object[] ret = handleNullValues(hql,plcVO);

			hql = (String) ret[0];
			String msgValue = (String) ret[1];
			Map aggregatedProperties = (Map) ret[2];

			String argNames[] = getServiceQBE().distillHQLArguments(hql);

			Query q = apiCreateQuery(plcVO.getClass(),hql);

			for(int j=0;argNames[j]!=null;j++) {
				if(PropertyUtils.isReadable(plcVO, argNames[j]))
					q.setParameter(argNames[j], PropertyUtils.getProperty(plcVO, argNames[j]));
			}

			log.debug("After create query and setProperties");

			Set params = aggregatedProperties.keySet();
			Iterator i = params.iterator();
			String param = "";

			while (i.hasNext()) {
				param = (String) i.next();
				q.setParameter(param,aggregatedProperties.get(param));
			}

			log.debug("Before submit query");

			Iterator count = q.getResultList().iterator();

			Object row = (Object) count.next();

			if (row instanceof Long) {
				row = new Integer(((Long)row).intValue());
			}

			if (logPersistence.isDebugEnabled())
				logPersistence.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":verifyTotalResults:"));

			return  new Object[]{(Integer) row,msgValue};

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.duplicity",new Object[] {e},e,log);
		}
	}
	public Object retrieveLookupAggregate(Object baseEntity, Map<String, Object> propertiesValues) throws PlcException {
		// TODO Auto-generated method stub
		return null;
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

		Query q = getEntityManager().createNamedQuery(namedQuery);
		
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
			
			return retrieveNamedQuery(namedQuery, parametersNames, parametersValues).getResultList();
			
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
			
			return retrieveNamedQuery(namedQuery, parametersNames, parametersValues).getSingleResult();
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					namedQuery, e }, e, log);
		}
	}
	
	
	
}
