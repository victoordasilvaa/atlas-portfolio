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

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ENTITY;
import org.jcompany.commons.annotation.PlcPrimaryKey;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcBeanCloneHelper;
import org.jcompany.commons.helper.PlcReflectionHelper;
import org.jcompany.persistence.PlcConstantsPersistence;
import org.jcompany.persistence.hibernate.helper.PlcAnnotationPersistenceHelper;
import org.jcompany.persistence.listener.PlcBasePersistenceListener;


/**
 * jCompany 3.0 Faz logicas genéricas com base em eventos da Hibernate.
 * @since jCompany 3.0
 * @version $Id: PlcBaseHibernateListener.java,v 1.8 2006/08/24 12:47:18 tiago Exp $
 */
@SuppressWarnings("serial")
public class PlcBaseHibernateListener extends PlcBasePersistenceListener implements PreLoadEventListener, PreInsertEventListener, PreDeleteEventListener,PreUpdateEventListener,
						PostDeleteEventListener, PostInsertEventListener, PostUpdateEventListener, PostLoadEventListener{

	protected static Logger log = Logger.getLogger(PlcBaseHibernateListener.class);
	
    /**
	  * jCompany 3.0  DP Composite. Recebe o contexto do cliente para guardar em ThreadLocal no HibernateManager para
	  * ser usado em 'event listeners': PlcBaseContextVO context = HibernateManager.getContextVO();
	  * @return Devolve sessão Hibernate utilizando fábrica.
	  */
	 protected org.hibernate.Session getSession() throws PlcException {

		 log.debug("############### Entered in getSession");
		 
	     try {
	    	  
	    	PlcBaseContextVO context = getContext();
            
	    	// Captura manager apropriado e sessao correspondente
	    	return PlcHibernateManagerLocator.getInstance().getHibernateManagerClass(context.getFactoryPlc()).getSession();

       } catch (Exception e) {
           throw new PlcException("jcompany.error.generic", new Object[] {
                   "getSession", e }, e,log);
       }
	 }
	 
	 /**
	  * Devolve a configuração (dados do hibernate.cfg.xml) correspondente ao SessionFactory que iniciou este listener.
	  * @since jCompany 3.1
	  * @return Devolve configuration
	  */
	 protected Configuration getCfg() throws PlcException {

		log.debug("############### Entered in getCfg");
		 
	     try {
	    	  
	    	PlcBaseContextVO context = getContext();
            
	    	return PlcHibernateManagerLocator.getInstance().getHibernateManagerClass(context.getFactoryPlc()).getCfg();

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"getCfg", e }, e, log);
		}
	 }
	 
	 /**
	  * Verifica se há registros global (ex: hibernate.cfg.xml) para uso de otimização manyToOne Lazy ou especifico no
	  * Action-Mapping. (O definido no Action-Mapping sobrepõe a definição global
	  * @since jCompany 3.1
	  * @return true se for para  otimizar
	  */
	 protected boolean isOptimizeManyToOne() throws PlcException {

		 log.debug("############### Entered in isOptimizeManyToOne");
		 
	     try {
	    	  
	    	Configuration cfg = getCfg();
	    	
	    	return ("S".equals(cfg.getProperties().get(PlcConstantsPersistence.GLOBAL.MANY_TO_ONE_LAZY_OPTIMIZE)) &&
	    			!("N".equals(getContext().getManyToOneLazyOptimize()))) ||
	    	       "S".equals(getContext().getManyToOneLazyOptimize());

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"isOptimizeManyToOne", e }, e, log);
		}
	 }
	 
	 /**
	  * Verifica se há registros global (ex: hibernate.cfg.xml) para uso de otimização de update ou especifico no
	  * Action-Mapping. (O definido no Action-Mapping sobrepõe a definição global)
	  * @since jCompany 3.1
	  * @return true se for para  otimizar
	  */
	 protected boolean isOptimizeUpdate() throws PlcException {

		log.debug("############### Entered in isOptimizeUpdate");
		
	     try {
	    	  
	    	Configuration cfg = getCfg();
	    	
	    	return ("S".equals(cfg.getProperties().get(PlcConstantsPersistence.GLOBAL.UPDATE_OPTIMIZE)) &&
	    			!("N".equals(getContext().getUpdateOptimize()))) ||
	    	       "S".equals(getContext().getUpdateOptimize());

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"isOptimizeUpdate", e }, e, log);
		}
	 }
	 
	 /**
	  * Verifica se há registros global (ex: hibernate.cfg.xml) para uso de auditoria rígida.
	  * Somente testa auditoria se tiver registro para não ocorrer overhead em aplicações que não a
	  * utilizam. Exemplo:<p>
	  * plc.auditoriaRigida=S
	  * @since jCompany 3.1.1
	  * @return true se for para  otimizar
	  */
	 protected boolean isAuditRigidUse() throws PlcException {

		log.debug("############### Entered in isAuditRigidUse");
		
	     try {
	    	  
	    	Configuration cfg = getCfg();
	    	
	    	return "S".equals(cfg.getProperties().get(PlcConstantsPersistence.GLOBAL.AUDIT_RIGID));

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"isAuditRigidUse", e }, e, log);
		}
	 }
	 
	/* ******************************************************************************************* */
	/* ********************************  FIM SERVIÇOS APOIO *********************************** */
	/* ******************************************************************************************* */

	
	public boolean onPreInsert(PreInsertEvent event) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in ON_PRE_INSERT for class "+event.getEntity().getClass().getName()+ " value: "+event.getEntity());

		try {
	
	//		 Se detalhe estiver anotado para somenteLeitura evita sua inclusão
			if (getContext()!=null && getContext().getMainClass() != null &&
					!getContext().getMainClass().isAssignableFrom(event.getEntity().getClass()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(getContext().getLogic()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(getContext().getLogic()) &&
					PlcAnnotationHelper.getInstance().existReadonlyDetail(event.getEntity().getClass()))
					return true;
			
			if (PlcBaseEntity.class.isAssignableFrom(event.getEntity().getClass()))
				registerAuditInsertion(event);
			
			loadAggregatesWithRowid(event.getPersister(), event.getEntity());
			
			// False permite que a inserção continue
			return false;
		
		} catch (Exception e) {
			log.fatal("Error trying to insert "+e,e);
			throw new HibernateException("Error trying to insert: "+e,e);
		}
	}

	private void loadAggregatesWithRowid(EntityPersister _persister, Object entity) {
		
		try {
			if (_persister instanceof SingleTableEntityPersister){
				SingleTableEntityPersister persister = (SingleTableEntityPersister) _persister;
	
				if ((persister.getRootTableKeyColumnNames().length == 1 && persister.getRootTableKeyColumnNames()[0].equals("ROWID"))) {
	
					int c = 0;
					Session sess = persister.getFactory().getCurrentSession();
					for (Type type : persister.getPropertyTypes()) {
						// se é manytoone
						if (type instanceof ManyToOneType) {
							PlcBaseEntity manyToOneEntity = (PlcBaseEntity)PropertyUtils.getProperty(entity, persister.getPropertyNames()[c]);
							if (manyToOneEntity!=null && manyToOneEntity.getIdAux()!=null && !manyToOneEntity.getIdAux().equals("")) {
								/* Usar querySelLookup, se existir
								* FIXME A hibernate nao respeita o LAZY em manyToOne para esta situação,
								* e nao passa no otimizaManyToOne do jCompany, também.
								* Por isso, a recomendacao na versao 3.2.1 é a declaracao de uma querySelLookup
								* para as classes usadas como agregado ou combo.
								* 
								* @autor Pedro Henrique e Rogerio Baldini
								*/
							    String querySelLookup = PlcAnnotationPersistenceHelper.getInstance().getAnnotationQuerySelLookup(Class.forName(type.getName()));
							    
							    PlcBaseEntity o=null;
							    if (querySelLookup==null) {
							    	o = (PlcBaseEntity)sess.get(Class.forName(type.getName()), manyToOneEntity.getIdAux());
							    } 
							    else {
							    	Query query = sess.createQuery(querySelLookup);
	
							    	if (querySelLookup.contains("?")) {
							    		query.setString(0,manyToOneEntity.getIdAux());
							    	} else if (querySelLookup.contains(":id")) {
							    		query.setString("id", manyToOneEntity.getIdAux());
							    	}
							    	List<PlcBaseEntity> l = query.list();
							    	if (! l.isEmpty()){
							    		o = (PlcBaseEntity)l.get(0); 
							    	}
							    }	
								//PropertyUtils.setProperty(entity, persister.getPropertyNames()[c], o);
								PropertyUtils.copyProperties(PropertyUtils.getProperty(entity, persister.getPropertyNames()[c]), o);
							}	
						}
						c++;
					}
				}
			}
		} catch (Exception e) {
			throw new HibernateException(
					"Post insert listener not executed: "
							+ e, e);
		}
		
	}

	public void onPostInsert(PostInsertEvent event) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in onPostInsert for class "+event.getEntity().getClass().getName()+ " value: "+event.getEntity());

		try {
			
			if (isAuditRigidUse()) {
				getServiceAudit().insertAudit(event.getEntity(),"I");
			}
						
		
		} catch (Exception e) {
			log.fatal("Error trying to handle insert in postInsert "+e,e);
			throw new HibernateException("Post insert listener not executed: "+e,e);
		}
		
	}
	
	/**
	 * Faz tratamento de otimização de recuperação ManyToOne, desde que esta opção esteja ligada no arquivos de configurações
	 * com:<p> 
	 * <property name="plc.manyToOneLazyOtimiza">true</property>
	 * <p>
	 * E que o relacionamento ManyToOne seja 'Lazy' declarado como:<p>
	 * - @LazyToOne(LazyToOneOption.PROXY) ou <br>
	 * - @ManyToOne (fetch=FetchType.LAZY)<p>
	 * Nestes casos, evita a recuperação do grafo ManyToOne completo, utilizando a query nomeada como XXXXX.querySelLookup
	 * na classe manyToOne (devendo esta recuperar um mínimo de propriedades para este fim) ou mesmo não procedendo na
	 * recuperação, caso a classe manyToOne tenha a anotação @PlcEntityMetadata(classeLookup=true), indicando que é utilizada como "lookup" 
	 * (tipicamente exibida em combos que existem em caching na camada Controle).
	 * @since jCompany 3.1
	 * @see org.hibernate.event.PreLoadEventListener#onPreLoad(org.hibernate.event.PreLoadEvent)
	 */
	public void onPreLoad(PreLoadEvent event) {

		try {
				
			if (isOptimizeManyToOne()) {
				
				if (log.isDebugEnabled())
					log.debug("Entered in listener preload specific with entidade=" + event.getPersister());
				
				Type[] pt = event.getPersister().getClassMetadata().getPropertyTypes();
				
				for (int i = 0; i < pt.length; i++) {
					
					// Se o relacionamento for ManyToOne e estiver definido como Lazy (Objeto como Proxy)
					if (pt[i] instanceof ManyToOneType && event.getState()[i] != null &&
							HibernateProxy.class.isAssignableFrom((event.getState()[i]).getClass())) {
	
					    // Captura identificador do Proxy
						// TODO verificar se funciona ok com chaves naturais.
					    HibernateProxy lookup 	= (HibernateProxy) event.getState()[i];
					    LazyInitializer li 		= lookup.getHibernateLazyInitializer();
					    Object id 				= li.getIdentifier();

					    Class clazz = li.getPersistentClass();
					    /* Se for classe de lookup (combos em caching na camada controle) ou for o pai de um detalhe a 
					     * ser recuperado por demanda, entao evita recuperar.
					     */
					    if (PlcAnnotationHelper.getInstance().isLookupClass(clazz) || (getContext().getMainClass() != null &&
					    		getContext().getMainClass().isAssignableFrom(clazz) && !getContext().getMainClass().equals(clazz))) {
						   
					    	// ClasseLookup somente funciona com OID, nesta versao
						   PlcBaseEntity entity 		= (PlcBaseEntity) clazz.newInstance();
						   entity.setId((Long)id);
						   event.getState()[i]	=	entity;	
					    } else {
					    	// Se nao for classe de lookup e tiver namedQuery com nome padrao xxxxx.querySelLookup, entao usa.
					    	Query queryLookup = onPreLoadRetrieveNamedQueryLookup(li.getEntityName());
					    	if (queryLookup != null) {
					    		if (!(id instanceof PlcBaseEntity)){

					    			queryLookup.setParameter(0,id);
					    		} else {
					    			PlcPrimaryKey pk = (PlcPrimaryKey)clazz.getAnnotation(PlcPrimaryKey.class);
					    			if (pk != null){
					    				String[] properties = pk.properties();
					    				for (int j = 0; j < properties.length; j++) {
					    					Object valor = PropertyUtils.getProperty(id, properties[j]);
					    					queryLookup.setParameter(j, valor);
					    				}
					    			}					    		
					    		}
					    		Object obj = queryLookup.uniqueResult();
					    		event.getState()[i]	= obj;
					    	}
					   }
					}
				    else{
						/*
						 * 	Se é um componente verifica se tem algum relacionamento ManyToOne definido como Lazy (Objeto como Proxy),
						 *  se for lazy recupera o objeto HibernateProxy, e inicializa-o para colocar no componente o objeto sem proxy
						 *  Essa funcionalidade é importante para evitar o erro LazyInitializationException que ocorre quando a hibernate 
						 *  tenta recuperar um objeto lazy e a sessão está fechada 
						 *  @Autor Pedro Henrique
						 */
				    	if (pt[i] instanceof ComponentType && event.getState()[i] != null ) {
				    		
				    		Type[] ptComponent 		= ((ComponentType)pt[i]).getSubtypes();
				    		String[] propertyNames 	= ((ComponentType)pt[i]).getPropertyNames();
				    		for(int y = 0; y < ptComponent.length; y++ ){
				    			if ( ptComponent[y] instanceof ManyToOneType){
				    				Object objRelationship	= PlcReflectionHelper.getInstance().callGetter(event.getState()[i], propertyNames[y]);
				    				if (objRelationship != null && HibernateProxy.class.isAssignableFrom(objRelationship.getClass())){
				    					
				    					HibernateProxy lookup 	= (HibernateProxy) objRelationship;
				 					    LazyInitializer li 		= lookup.getHibernateLazyInitializer();
				 					    Object id 				= li.getIdentifier();
				 					    Class clazz 			= li.getPersistentClass();
									    /* Se for classe de lookup (combos em caching na camada controle) ou for o pai de um detalhe a 
									     * ser recuperado por demanda, entao evita recuperar.
									     */
									    if (PlcAnnotationHelper.getInstance().isLookupClass(clazz) || (getContext().getMainClass() != null &&
									    		getContext().getMainClass().isAssignableFrom(clazz))) {
										   
									    	// ClasseLookup somente funciona com OID, nesta versao
										   PlcBaseEntity entity 		= (PlcBaseEntity) clazz.newInstance();
										   entity.setId((Long)id);
										   PropertyUtils.setSimpleProperty(event.getState()[i], propertyNames[y], entity);
									    } else {
									    	// Se nao for classe de lookup e tiver namedQuery com nome padrao xxxxx.querySelLookup, entao usa.
									    	Query queryLookup = onPreLoadRetrieveNamedQueryLookup(li.getEntityName());
									    	if (queryLookup != null) {
									    		Object obj 			= queryLookup.setParameter(0,id).uniqueResult();
									    		PropertyUtils.setSimpleProperty(event.getState()[i], propertyNames[y], obj);
									    	}
									    }
				    				}
				    			}
				    		}
				    		

				    	}
		
				    }					
				}
			}

			
		} catch (Exception e) {
			log.fatal("Error trying to handle lazy manyToOne in optimized way "+e,e);
			throw new HibernateException("Error trying to handle lazy manyToOne in optimized way: "+e,e);
		}
	}
	
	/**
	 * Recupera uma query nomeada a partir de entidade ManyToOne. A convenção para o nome é '[nome da classe sem pacote].querySelLookup'
	 * @param entityName Nome da entidade com pacote
	 * @return Nome da query
	 */
	protected Query onPreLoadRetrieveNamedQueryLookup(String entityName) throws PlcException {
		String name = entityName.substring(entityName.lastIndexOf(".")+1)+".querySelLookup";
		try {
			Query query = getSession().getNamedQuery(name);
			return query; 
		} catch (HibernateException e) {
			//TODO criar query para chaveNatural, por enquanto pode-se anotar a query querySelLookup na entidade. 
			Query query = getSession().createQuery("from " +  entityName.substring(entityName.lastIndexOf(".")+1) + " obj where obj.id = ?");
			return query;
		}
	}

	public boolean onPreDelete(PreDeleteEvent event) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in onPreDelete for classe "+event.getEntity().getClass().getName()+ " value: "+event.getEntity());

		try {
			
	//		 Se detalhe estiver anotado para somenteLeitura evita sua exclusão
			if (getContext()!=null && getContext().getMainClass() != null &&
					!getContext().getMainClass().isAssignableFrom(event.getEntity().getClass()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(getContext().getLogic()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(getContext().getLogic()) &&
					PlcAnnotationHelper.getInstance().existReadonlyDetail(event.getEntity().getClass()))
					return true;
			
			return false;
			
		} catch (Exception e) {
			log.fatal("Error trying to insert "+e,e);
			throw new HibernateException("Error trying to insert: "+e,e);
		}

	}


	/**
	 * jCompany 3.0 Atualiza situação para inativo
	 */
	protected void onPreDeleteUpdateInactiveSituation(PlcBaseEntity vo) {
		log.debug("############### Entered in onPreDeleteUpdateInactiveSituation");
		
		try {
	
			Session sess = getSession();
					
			PlcBaseEntity entityCloned = (PlcBaseEntity) PlcBeanCloneHelper.getInstance().cloneBean(vo);
			PropertyUtils.setProperty(entityCloned,PlcConstantsCommons.ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_INACTIVE);
			sess.evict(vo);
			sess.saveOrUpdate(entityCloned);
			
		} catch (Exception e) {
			throw new HibernateException(e);
		}	
		

	}

	public void onPostDelete(PostDeleteEvent event) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in onPostDelete for classe "+event.getEntity().getClass().getName()+ " value: "+event.getEntity());

		try {
			
			if (isAuditRigidUse()) {
				getServiceAudit().insertAudit(event.getEntity(),"E");
			}
		
		} catch (Exception e) {
			log.fatal("Error trying to exclude in optimized way  "+e,e);
			throw new HibernateException("Error trying to exclude in optimized way: "+e,e);
		}
		
	}

	/**
	 * Utilizado para gravação de auditoria pauta minima (usuário e data ult alteracao) e para otimização de atualizações
	 * quando as opções estão declaradas
	 * @see org.hibernate.event.PreUpdateEventListener#onPreUpdate(org.hibernate.event.PreUpdateEvent)
	 */
	public boolean onPreUpdate(PreUpdateEvent event) {
		
		try {
			
			if (log.isDebugEnabled())
				log.debug("############### Entered in onPreUpdate for classe "+
						event.getEntity().getClass().getName()+ " value: "+event.getEntity());
			
			getContext().setUpdated(true);
			// Somente faz se está registrado para fazer e classe nao usa selectBeforeUpdate, o que significa que 
			// o desenvolvedor deseja que o Hibernate trate o update. Logicas tabulares e crud-tabulares fazem tratamento
			// já no Facade
			if (isOptimizeUpdate() && getContext()!=null && /*!PlcAnnotationPersistenceHelper.getInstance().temAnotacaoSelectBeforeUpdate(event.getEntity().getClass())*/
				!event.getPersister().isSelectBeforeUpdateRequired() && 
				!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(getContext().getLogic()) &&
				!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(getContext().getLogic())) {
				
				// Evita update em Mestre quando é lógica Mantém-Detalhe
				if (getContext().getLogic() != null &&
					getContext().getLogic().startsWith(PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL_MANTAIN_DETAIL) && 
					getContext().getMainClass()!=null && getContext().getMainClass().isAssignableFrom(event.getEntity().getClass())) {
					if (event.getPersister().isVersioned())
						mantainVersionMasterPrevious();
					getContext().setUpdated(false);
					return true;
				}
				
				//	Evita update em Mestre quando não houve modificacoes, e o padrao é masterRemember=S
				if ("S".equals(getContext().getMasterRemember()) && 
					getContext().getMainClass()!=null &&
					getContext().getMainClass().isAssignableFrom(event.getEntity().getClass()) &&
					isEqualsPrevious(event.getEntity(),getContext().getEntityPrevious())) {
					if (event.getPersister().isVersioned())
						mantainVersionMasterPrevious();
					getContext().setUpdated(false);
					return true;
				}
				
				// Evita update de Detalhes quando o detalhe está anotado como somente leitura,
				// quando não houve modificacoes, se a configuracao for detailRemember e se
				// a classe for do Mestre
				if (getContext().getMainClass()!=null && !getContext().getMainClass().isAssignableFrom(event.getEntity().getClass())) {
					
					if ("S".equals(getContext().getDetailRemember())) {
						Collection c = retrieveDetailCollection((PlcBaseEntity)event.getEntity(),(PlcBaseEntity)getContext().getEntityPrevious());
						if (isEqualsDetailPrevious((PlcBaseEntity)event.getEntity(),c)) {
							if (event.getPersister().isVersioned())
								mantainVersionDetailPrevious((PlcBaseEntity)event.getEntity(),c);
							getContext().setUpdated(false);
							return true;
						}
					}
				
				}
				
				// TODO SubDetalhes - Até a presente versao, se utilizados devem ser marcados para selectBeforeUpdate
			
			}
			
			// Se detalhe estiver anotado para somenteLeitura evita sua atualização
			if (getContext()!=null && getContext().getMainClass() != null &&
					!getContext().getMainClass().isAssignableFrom(event.getEntity().getClass()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(getContext().getLogic()) &&
					!PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(getContext().getLogic()) &&
					PlcAnnotationHelper.getInstance().existReadonlyDetail(event.getEntity().getClass())) {
					getContext().setUpdated(false);
					return true;
			}
			
			// Somente faz auditoria pauta minima se for alterar realmente
			if (PlcBaseEntity.class.isAssignableFrom(event.getEntity().getClass()))
				registerAuditAltered(event);
			
			// Mantem null em versao anterior no context quando atualiza ok 
			getContext().setVersionPrevious(null);
			
			// False permite que a alteração continue
			getContext().setUpdated(true);
			
			loadAggregatesWithRowid(event.getPersister(), event.getEntity());
			
			return false;
		
		} catch (Exception e) {
			log.fatal("Error trying to handle update in optimized way "+e,e);
			throw new HibernateException("Error trying to handle update in optimized way: "+e,e);
		}
		
	}

	/**
	 * Tem que guardar a versao original para recolocar no postUpdate, quando impedimos a gravaçao manualmente!
	 */
	protected void mantainVersionDetailPrevious(PlcBaseEntity entityDetail, Collection details) throws Exception  {
		
		for (Iterator iter = details.iterator(); iter.hasNext();) {
			PlcBaseEntity entityDetPrevious = (PlcBaseEntity) iter.next();
			if ((entityDetPrevious.getIdAux() != null && entityDetPrevious.getIdAux().equals(entityDetail.getIdAux()) ||
				entityDetPrevious.getDynamicNaturalId() != null && entityDetPrevious.getDynamicNaturalId().equals(entityDetail.getDynamicNaturalId()))) {
				getContext().setVersionPrevious((Integer) PropertyUtils.getProperty(entityDetPrevious,"versao")); 
				return;
			}
		}
		
		throw new PlcException("jcompany.errors.optimization.update.without.previous.detail.available",new Object[]{entityDetail.toString()});

	}

	/**
	 * Tem que guardar a versao original para recolocar no postUpdate, quando impedimos a gravaçao manualmente!
	 */
	protected void mantainVersionMasterPrevious() throws Exception {
		if (getContext().getEntityPrevious() != null)
			getContext().setVersionPrevious((Integer)PropertyUtils.getProperty(getContext().getEntityPrevious(),"versao"));
		
	}

	/**
	 * Verifica se duas instancias, a atual e a anterior, de um ENTITY são as mesmas, desconsiderando
	 * coleções de detalhe, mas considerando many-to-ones. A instancia anterior é recuperada da thread local.
	 * @param entity Atual
	 * @return true se forem iguais
	 */
	protected boolean isEqualsPrevious(Object entity,Object entityPrevious) throws PlcException {
		
		return entity.equals(entityPrevious);
		
	}
	
	/**
	 * Verifica se um ENTITY é igual a outro de detalhe, procurando seu par pela chave
	 * @param entityDetail ENTITY de Detalhe a ser atualizado
	 * @param voMestreAnterior ENTITY de Mestre que contém detalhes para serem comparados
	 * @return true se forem iguais
	 */
	protected boolean isEqualsDetailPrevious(PlcBaseEntity entityDetail,Collection details) throws PlcException {
		
		for (Iterator iter = details.iterator(); iter.hasNext();) {
			PlcBaseEntity entityDetPrevious = (PlcBaseEntity) iter.next();
			if ((entityDetPrevious.getIdAux() != null && entityDetPrevious.getIdAux().equals(entityDetail.getIdAux()) ||
				entityDetPrevious.getDynamicNaturalId() != null && entityDetPrevious.getDynamicNaturalId().equals(entityDetail.getDynamicNaturalId()))) {
				return entityDetail.equals(entityDetPrevious);
			}
		}
		
		throw new PlcException("jcompany.errors.optimization.update.without.previous.detail.available",new Object[]{entityDetail.toString()});
		
	}
	
	/**
	 * Verifica se um ENTITY é igual a outro de detalhe, procurando seu par pela chave
	 * @param entityDetail ENTITY de Detalhe a ser atualizado
	 * @param entityMasterPrevious ENTITY de Mestre que contém detalhes para serem comparados
	 * @return true se forem iguais
	 */
	protected Collection retrieveDetailCollection(PlcBaseEntity entityDetail,PlcBaseEntity entityMasterPrevious) throws PlcException {
		
		String detailPropertyName = entityDetail.getPropertyNamePlc();
		
		Collection details = null;
		
		try {
			details = (Collection) PropertyUtils.getProperty(entityMasterPrevious,detailPropertyName);
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveDetailCollection", e }, e, log);
		}
		
		if (details==null)
			throw new PlcException("jcompany.errors.optimization.update.without.previous.detail.available",new Object[]{entityDetail.toString()});
	
		return details;
	}
	
	/**
	 * Retorna versao para nivel anterior
	 */
	public void onPostUpdate(PostUpdateEvent event) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in onPostUpdate for classe "+event.getEntity().getClass().getName()+ " value: "+event.getEntity());
		
		try {
			
			if (Boolean.FALSE.equals(getContext().getUpdated())) {
				
				if (event.getPersister().isVersioned() && getContext().getVersionPrevious() !=null) {
					event.getPersister().setPropertyValue( 
						event.getEntity(), 
						event.getPersister().getVersionProperty(), 
						getContext().getVersionPrevious(), 
						EntityMode.POJO);
				}
				
			} else if (isAuditRigidUse()) {
				getServiceAudit().insertAudit(event.getEntity(),"A");
			}
		
		} catch (Exception e) {
			log.fatal("Error trying to handle update in optimized way "+e,e);
			throw new HibernateException("Error trying to handle update in optimized way: "+e,e);
		}
		
	}

	public void onPostLoad(PostLoadEvent event) {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in onPostLoad for classe "+event.getEntity().getClass().getName()+ " value: "+event.getEntity());

		
	}


	public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
		
		if (log.isDebugEnabled())
			log.debug("############### Entered in onSaveOrUpdate for classe "+event.getEntityName()+ " value: "+event.getEntity());
		
//		registraAuditoriaAlteracao((PlcBaseEntity)event.getEntity());
		
	}

	protected void verifyPersistenceSecurity(PlcBaseEntity baseEntity) throws HibernateException {
		log.debug("############### Entered in verifyPersistenceSecurity");
		
	}

	protected void registerAuditInsertion(PreInsertEvent event) throws HibernateException {
		
		log.debug("############### Entered in registerAuditInsertion");
		
		PlcBaseEntity baseEntity = (PlcBaseEntity) event.getEntity();
		
		PlcBaseContextVO context = getContext();
		PlcBaseUserProfileEntity baseUserProfileEntity = context.getUserProfile();
		
		String[] props = event.getPersister().getPropertyNames();
		
		Date date = new Date();
		
		for (int i = 0; i < props.length; i++) {
			
			// Data Criação
			if (PlcConstantsCommons.ENTITY.DATE_CREATION.equals(props[i])) {
				
				try {
			
					log.debug("Inserted creation date!");
					// Atualiza estado
					event.getState()[i]=date;
					// Atualiza ENTITY
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.DATE_CREATION,date);
				
				} catch (Exception e) {
					throw new HibernateException("It is not possible to insert creation date in "+baseEntity,e);
				}
			
			}
			
			// Usuário Criação
			if (PlcConstantsCommons.ENTITY.USER_CREATION.equals(props[i])) {
				
				try {
			
					if (baseUserProfileEntity==null || baseUserProfileEntity.getLogin()==null)
						throw new HibernateException("jcompany.errors.listener.auditor.without.profile");
	
					log.debug("Inserted creation user!");
					// Atualiza estado
					event.getState()[i]=baseUserProfileEntity.getLogin();
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.USER_CREATION,baseUserProfileEntity.getLogin());
				
				} catch (Exception e) {
					throw new HibernateException("It is not possible to insert creation user in "+baseEntity,e);
				}
			
			}
			
			// Data Alteração
			if (PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE.equals(props[i])) {
				try {
			
					log.debug("Inserted last update date !");
					event.getState()[i]=date;
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE,date);
				
				} catch (Exception e) {
					throw new HibernateException("It is not possible to insert last update date in "+baseEntity,e);
				}
			}
			
			// Usuario Alteração
			if (PlcConstantsCommons.ENTITY.USER_LAST_UPDATE.equals(props[i])) {
				try {
                    if (baseUserProfileEntity == null || baseUserProfileEntity.getLogin() == null){
                    	if (baseUserProfileEntity == null)
                    		baseUserProfileEntity = new PlcBaseUserProfileEntity();
                        baseUserProfileEntity.setLogin("Anonym");
                    }
                    /*
                    if (baseUsuarioPerfilVO==null || baseUsuarioPerfilVO.getLogin()==null)
						throw new HibernateException("jcompany.erros.listener.auditoria.sem.perfil");
                    */    

					log.debug("Inserted last update user!");
					event.getState()[i]=baseUserProfileEntity.getLogin();
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.USER_LAST_UPDATE,baseUserProfileEntity.getLogin());
				
				} catch (Exception e) {
					throw new HibernateException("It is not possible to insert last update user in  "+baseEntity,e);
				}
			}
		
		}	
	
		// O evento de alteracao comporta-se diferente dos demais. Nao pode ser reutilizado!
//		registraAuditoriaAlteracao(baseVO);
		
	}
	
	protected void registerAuditAltered(PreUpdateEvent event) throws HibernateException {
		
		log.debug("############### Entered in registerAuditAltered");

		PlcBaseContextVO context = getContext();
		PlcBaseUserProfileEntity baseUserProfileEntity = context.getUserProfile();
		PlcBaseEntity baseEntity = (PlcBaseEntity) event.getEntity();
		String[] props = event.getPersister().getPropertyNames();
		
		Date date = new Date();
		
		for (int i = 0; i < props.length; i++) {
		
			// Data Alteração
			if (PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE.equals(props[i])) {
				try {
			
					log.debug("Inserted last update date!");
					event.getState()[i]=date;
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE,date);
				
				} catch (Exception e) {
					throw new HibernateException("It is not possible to insert last update date in  "+baseEntity,e);
				}
			}
			
			// Usuario Alteração
			if (PlcConstantsCommons.ENTITY.USER_LAST_UPDATE.equals(props[i])) {
				try {
                    if (baseUserProfileEntity == null || baseUserProfileEntity.getLogin() == null){
                    	if (baseUserProfileEntity == null)
                    		baseUserProfileEntity = new PlcBaseUserProfileEntity();
                        baseUserProfileEntity.setLogin("Anonym");
                    }
					/*    
					if (baseUsuarioPerfilVO==null || baseUsuarioPerfilVO.getLogin()==null)
						throw new HibernateException("jcompany.erros.listener.auditoria.sem.perfil");
                    */    
					
					log.debug("Inserted last update user!");
					event.getState()[i]=baseUserProfileEntity.getLogin();
					PropertyUtils.setProperty(baseEntity,PlcConstantsCommons.ENTITY.USER_LAST_UPDATE,baseUserProfileEntity.getLogin());
				} catch (Exception e) {
					throw new HibernateException("It is not possible to insert last update user in"+baseEntity,e);
				}
			}
			
		}
		
	}

	
}
