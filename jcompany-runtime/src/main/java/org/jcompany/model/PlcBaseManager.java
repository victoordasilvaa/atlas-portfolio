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
package org.jcompany.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.collection.AbstractPersistentCollection;
import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcFileEntity;
import org.jcompany.commons.PlcConstantsCommons.ENTITY;
import org.jcompany.commons.PlcConstantsCommons.EVENT;
import org.jcompany.commons.annotation.PlcPrimaryKey;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.converters.PlcDateUtilConverter;
import org.jcompany.commons.event.PlcEventHelper;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcDateHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.commons.helper.PlcReflectionHelper;
import org.jcompany.commons.helper.PlcStringHelper;
import org.jcompany.persistence.IPlcDAO;
import org.jcompany.persistence.PlcPersistenceLocator;



/**
 * Base para objetos de negócio.<p>
 * Segundo 'Core J2EE Design Patterns': Um Businesss Object (BO) é um type de Business Component que implementa
 * as regras de granuralidade mais fina do modelo de domínio, podendo ser chamado de outros BOs, Application Services (ASs)
 * ou diretamente de Façades. Normalmente chama outros BOs ou DataAcessObjects (DAOs), para serviços de dados.
 * @since jCompany 3.0
 * @version $Id: PlcBaseManager.java,v 1.21 2006/08/22 21:15:51 rodrigo Exp $
 */
@Stateless(name="PlcBaseManager")
@TransactionManagement(TransactionManagementType.CONTAINER)

public class PlcBaseManager extends PlcBaseBC implements IPlcManager {

	 protected Logger log = Logger.getLogger(PlcBaseManager.class);
	 IPlcDAO plcDAO;
	 
	/**
	 * @since jCompany 3.0
     * 
     * Uso interno do BO genérico. Descendentes devem utilizar recebimento via Injecao de Dependencia
     * no construtor
     * @return DAO
     */
    protected IPlcDAO getDAODefault() throws PlcException {
    	if(plcDAO != null) {
    		return plcDAO;
    	}
        return (IPlcDAO) PlcPersistenceLocator.getInstance().get(this.getClass());
    }

    /**
     * @since jCompany 3.0
     * 
     * Uso interno do BO genérico. Descendentes devem utilizar recebimento via Injecao de Dependencia
     * no construtor
     * @return DAO
     */
    private IPlcDAO getDAO(Class classEntity) throws PlcException {
    	
		if(plcDAO != null) {
		    return plcDAO;
		} 
		//FIXME TIAGO data 10/07/2007 14:50 - Adicionado para tratar problema de classeEntidade ser nula.  
		else if(classEntity == null) {
			return getDAODefault();
		}
		else {
		    return (IPlcDAO) PlcPersistenceLocator.getInstance().get(classEntity);
		}
    	
    }
    


    /**
     * @since jCompany 3.0
     * 
     * DP Composite. Pega um um Business Component do type BO - Business Object.<p> 
     * BOs podem também ser
     * instanciados dinamicamente por inferência, passando-se a Entidade correlata, através da convenção de pacotes.<p>
     * Ex: meupacote.entidade.Candidato.class ou meupacote.entidade.CandidatoEntidade.class procuram instancia de meupacote.modelo.CandidatoBO.<br>
     * Esta convenção facilita inversão de controle do jCompany (e possivelmente lógicas especificas), fazendo com que
     * BOs específicos possam 'atuar' sem que seja necessário se configurar Façades, ASs e chamadas MVC monótonas/triviais.
     */
    private IPlcManager getManager(Class classManagerOrEntity) throws PlcException {
        return (IPlcManager)PlcModelLocator.getInstance().get(classManagerOrEntity);
    }

	/* **************************************************************************************** */
	/* ********************** TEMPLATE METHOD - RECUPERA LISTA QBE **************************** */
	/* **************************************************************************************** */
	 
	/* (non-Javadoc)
	 * @see org.jcompany.model.IPlcManager#recuperaListaQBE(java.lang.Class, java.lang.String, java.util.List)
	 */
	public List retrieveListQBE( Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListQBE:"));

		List l = null;
		
		try {
			
			retrieveListQBEBefore(clazz, orderByDynamic,  argsQBE);
	
			l = getDAO(clazz).retrieveWithDefaultFilter(clazz,orderByDynamic,argsQBE);
			
			retrieveListQBEAfter(clazz, orderByDynamic, argsQBE,l);
			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListQBE:"));

			return l;
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[]{"retrieveListQBE", e }, e,log);
		}

	}
	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas antes da recuperação QBE
	 */
	protected void retrieveListQBEBefore( Class clazz,String orderByDynamic, List argsQBE) throws PlcException {}
	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas após da recuperação QBE
	 */
	protected void retrieveListQBEAfter( Class clazz, String selectQBE, List argsQBE, List list) throws PlcException {}

	/* **************************************************************************************** */
	/* ********************** TEMPLATE METHOD - RECUPERA LISTA 1 ****************************** */
	/* **************************************************************************************** */

	/** 
	 * Recupera lista A
	 * @see org.jcompany.model.IPlcManager#retrieveList(java.lang.Class, java.lang.String, org.jcompany.commons.PlcBaseEntity, int, int)
	 */
	public List retrieveList(Class clazz, String orderByDynamic, Object argQBE,
				int firstLine, int maximumLines) throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveList:A:"));

		retrieveListBefore(orderByDynamic, argQBE,firstLine, maximumLines);
	
		List lista = getDAO(clazz).retrieveListQBEPaginated(clazz,orderByDynamic,argQBE,firstLine,maximumLines);
	
		retrieveListAfter( orderByDynamic, argQBE,	firstLine, maximumLines, lista);
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveList:A:"));

		return lista;
	
	}
	
	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas específicas antes da recuperação da lista paginada
	 */
	protected void retrieveListBefore( String orderByDynamic, Object argQBE, int firstLine, int maximumLines) throws PlcException {	}


	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas específicas depois da recuperação da lista paginada. Recebe a lista para complementações.
	 */
	protected void retrieveListAfter( String orderByDynamic, Object argQBE, int firstLine, 
			int maximumLines, List list) throws PlcException {}

	/* **************************************************************************************** */
	/* ********************** TEMPLATE METHOD - RECUPERA LISTA 2 ****************************** */
	/* **************************************************************************************** */
	
	/**
	 * Recupera lista B
	 * @see org.jcompany.model.IPlcManager#retrieveList(java.lang.Class, java.lang.String, java.util.List, int, int)
	 */
	public List retrieveList(Class clazz, String orderByDynamic, List<PlcArgEntity> argsQBE,
			int firstLine, int maximumLines) throws PlcException {
	
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveList:B:"));

		retrieveListBefore(orderByDynamic, argsQBE,firstLine, maximumLines);
	
		List list = getDAO(clazz).retrieveListQBEPaginated(clazz,orderByDynamic,argsQBE,firstLine,maximumLines);
	
		retrieveListAfter( orderByDynamic, argsQBE,	firstLine, maximumLines, list);

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveList:B:"));

		return list;
	
	}
	
	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas específicas antes da recuperação da lista paginada
	 */
	protected void retrieveListBefore( String orderByDynamic, List argsQBE, int firstLine, int maximumLines) throws PlcException {	}

	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas específicas após a recuperação da lista paginada
	 */
	protected void retrieveListAfter( String orderByDynamic, List argsQBE, int firstLine, 
			int maximumLines, List list) throws PlcException {}

	/* **************************************************************************************** */
	/* ********************** TEMPLATE METHOD - RECUPERA LISTA 2 ****************************** */
	/* **************************************************************************************** */
	
	/**
	 * Recupera Lista C
	 * @see org.jcompany.model.IPlcManager#retrieveList(java.lang.Class, java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	public List retrieveList( Class clazz, String orderByDynamic, Object[] args,String[] types) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveList:C:"));
		
		retrieveListBefore( clazz,args,types);

		List list = getDAO(clazz).retrieveWithDeclaredArgs(clazz,orderByDynamic,args,types);
			
		retrieveListAfter(clazz,args,types,list);

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveList:C:"));

		return list;

	}
	
	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas específicas antes da recuperação da lista.
	 */
	protected void retrieveListBefore( Class clazz, Object[] args, String[] types	) throws PlcException {}

	/**
	 * @since jCompany 3.0
	 * 
	 * Template Method para lógicas específicas depois da recuperação da lista. Recebe a lista para complementações.
	 */
	protected void retrieveListAfter(Class clazz, Object[] args, String[] types, List list) throws PlcException {}

	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - EXCLUDE ****************************** */
	/* **************************************************************************************** */
	
	/**
	 * Exclui agregações de objetos.
	 * @see org.jcompany.model.IPlcManager#exclude(org.jcompany.commons.PlcBaseEntity)
	 */
	public void exclude(Object entity) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":exclude:"));

		try {

			beforePersistence(entity, null, PlcConstantsCommons.MODES.MODE_EXCLUSION);
			excludeBefore(entity);

			if (!PlcEntityHelper.getInstance().isIdentified(entity))
				throw new PlcException("jcompany.errors.key.null.exclusion",new Object[]{entity.toString()});

			IPlcDAO baseDAO = getDAO(entity.getClass());
			
			PlcBaseContextVO context= getContext();
			retrieveDetailOnDemandBeforeExclude(entity, context.getDetailsOnDemand());
			
			baseDAO.exclude(entity);

			
			// TODO Rever se é possível se simplificar aqui
			// Na 3.0, o arquivo anexado é gerenciado no Context-Param, para manter compatibilidade 
			// na otimização (problema do download desnecessário da parte binária
			if (context.getAttachedFile() != null && context.getIdAttachedFilePlc() != null) {
				IPlcFileEntity plcFile = context.getAttachedFile();
				if (log.isDebugEnabled()) log.debug("Excluding attached file : "+plcFile);
				plcFile.setId(context.getIdAttachedFilePlc());
				try {
					baseDAO.exclude(plcFile);
					baseDAO.sendCacheCommands(plcFile.getClass());
				} catch (Exception e) {
					log.warn("Ignoring files exclusion error because FK can be cascade, as it's suggested in this case: " + e);
					// Importante:  Pode capturar outros erros, mas a probabilidade é mínima, já que há um exclui anterior.
				}

				log.debug("Excluded attached file");
			}

			excludeAfter(entity);
			afterPersistence(entity, null, PlcConstantsCommons.MODES.MODE_EXCLUSION);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":exclude:"));

		} catch (PlcException plcE) { 
			throw plcE;
		}	catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.exclude",new Object[] {e},e,log);
		}

	}
	
	/**
	 * Garante que todos os detalhes por demanda foram recuperados para evitar erro de constraint
	 */
	@SuppressWarnings("unchecked")
	protected void retrieveDetailOnDemandBeforeExclude(Object entity,  Map<String, Class> detailsOnDemand) throws PlcException{
		if (detailsOnDemand != null){
			for (String detalhe : detailsOnDemand.keySet()){
				getDAO(entity.getClass()).retrieveOnDemand(entity,detalhe,(Class)detailsOnDemand.get(detalhe));
				
			}
		}
	}
	
   /**
	* @since jCompany 2.1. 
	* 
	* Método disparado antes da exclusão de objeto. Pode ser 
	* utilizado alternativamente ao método "antesPersistencia", para lógicas que devem ser
	* disparadas somente na exclusão
    * @param vo Value Object a ser excluido
    */
   protected void excludeBefore( Object entity) throws PlcException { }
   
   /**
	 * @since jCompany 2.1.
	 * 
	 * Método disparado após a exclusão de objeto. Pode ser
	 * utilizado alternativamente ao método "aposPersistencia", para lógicas que devem ser
	 * disparadas somente na exclusão
	 * @param entity Value Object a ser excluido
	  */
   protected void excludeAfter( Object entity) throws PlcException { }

	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - ALTERA ****************************** */
	/* **************************************************************************************** */

	/**
	 * Altera agregações de objetos
	 * @see org.jcompany.model.IPlcManager#update(org.jcompany.commons.PlcBaseEntity, org.jcompany.commons.Object)
	 */
	public Object update( Object entity,Object entityPrevious) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alter:"));

		try {

			IPlcFileEntity entityFile = null;

			// Se tem opção para não alterar Id, valida
			if (!updateValidationAlterationOID(entity, entityPrevious))
				throw new PlcException("jcompany.error.altered.id ",
						new Object[]{
						(Long)PropertyUtils.getProperty(entity,"id"), entity.toString(), 
						(Long)PropertyUtils.getProperty(entityPrevious,"id"),
						entityPrevious.toString()});

			if (entity instanceof IPlcFileEntity) {

				if (log.isDebugEnabled())
					log.debug("entity "+entity.toString()+" is instance of IPlcFileEntity");
				entityFile = (IPlcFileEntity) entity;
				updateManipulateFileUpload(entity, entityFile, 
						(Long)PropertyUtils.getProperty(entityPrevious,"id"));

			} else {

				boolean alteredBeforeFileExclusion = false;

				PlcBaseContextVO context= getContext();
				  
				entityFile = context.getAttachedFile();

				if (entityFile != null) log.debug("file class="+entityFile.getClass());

				beforePersistence(entity, entityPrevious, PlcConstantsCommons.MODES.MODE_EDITION);
				updateBefore(entity, entityPrevious);

				IPlcDAO baseDAO = getDAO(entity.getClass());

				
				// Teste utilizando reflexao. Se desejado utilizar esta logic a propriedade deve existir
				if (context.getExcludeModeAux().equals(PlcConstantsCommons.DEFAULT_LOGIC.EXCLUSION.LOGICAL_MODE) &&
					"I".equals(PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC)) &&
					"A".equals(PropertyUtils.getProperty(entityPrevious,ENTITY.STATUS_HISTORIC_PLC)))
					updateToLogicalExclusionBefore(entity, entityPrevious);

				// A alteração é na verdade uma exclusão lógica, então não executa o avoidEquals
			    if (context.getExcludeModeAux().toUpperCase().startsWith("F") ||
			    	(context.getExcludeModeAux().toUpperCase().startsWith("L") &&
			    	"A".equals(PropertyUtils.getProperty(entity, ENTITY.STATUS_HISTORIC_PLC))))
			    	baseDAO.avoidEqualsExecute(entity,"A");

				if ((entityFile != null && entityFile.getImage() != null &&
					entityFile.getImage().length >0) ||
					((entityFile != null && entityFile.getIndExcPlc() != null 
							&& ( entityFile.getIndExcPlc().equals("S") || 
								 entityFile.getIndExcPlc().equals("true")))) ||
					(((PropertyUtils.isReadable(entity,"indExcPlc")
						  && PropertyUtils.getProperty(entity,"indExcPlc") != null))
						  && ( "S".equals(PropertyUtils.getProperty(entity,"indExcPlc"))
								|| "true".equals(PropertyUtils.getProperty(entity,"indExcPlc"))))) {

				      updateManipulateFile(entity, entityFile, alteredBeforeFileExclusion);
				}
				

				log.debug("Before calling Antes de chamar beforePersistenceAfterAttachedFile!");

				beforePersistenceAfterAttachedFile(entity, entityPrevious, PlcConstantsCommons.MODES.MODE_EDITION);

				registerAuditSimple(entity, false);
				
				if (context.getDetailNames() != null)
					updateExcludeCheckedDetails(entity);

				if (!alteredBeforeFileExclusion) {
					baseDAO.update(entity);
					updateMarkDetailReadonly(entity);
				}

				updateAfter(entity, entityPrevious);
				afterPersistence( entity, entityPrevious, PlcConstantsCommons.MODES.MODE_EDITION);

				if (context.getExcludeModeAux().equals(PlcConstantsCommons.DEFAULT_LOGIC.EXCLUSION.LOGICAL_MODE) &&
						"I".equals(PropertyUtils.getProperty(entity, ENTITY.STATUS_HISTORIC_PLC)) &&
						"A".equals(PropertyUtils.getProperty(entityPrevious, ENTITY.STATUS_HISTORIC_PLC)))
					updateToLogicalExclusionAfter(entity, entityPrevious);

		  }

		  if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alter:"));

		  return entity;

		 } catch (PlcException plcE) { 
			   throw plcE;
		 } catch (Exception e) { 
			throw  new PlcException("jcompany.errors.persistence.alter",new Object[] {e},e,log);
		 }
	}
	
	/**
	 * Detalhes marcados como somente leitura não são persistidos.
	 */
	protected void updateMarkDetailReadonly(Object entity) throws PlcException {
		
		try {

			if (getContext()!=null && getContext().getDetailNames()!=null) {
			
				List detailNames = getContext().getDetailNames();
				int counter=0;
				for (Iterator iter = detailNames.iterator(); iter.hasNext();) {
		
					String detailName = mountDetailClassName( (String) iter.next() ); 
					Class detailClass = Class.forName(detailName);
					if (PlcAnnotationHelper.getInstance().existReadonlyDetail(detailClass)) {
						IPlcDAO dao = getDAO(detailClass);
						String prop = getContext().getDetailNamesPlc().get(counter);
						dao.registerConsultationOnly(entity,(Collection)PropertyUtils.getProperty(entity, prop)); 
					}
					counter++;
				}
			
			}
			

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"alterMarkDetailReadonly", e }, e, log);
		}
		
		
	}

    /**
     * Completa o nome da classe detalhe com o pacote padrão de VOs, declarado
     * no web.xml, caso tenha sido informado apenas o nome simples.
     * 
     * @param className
     * @return
     */
    protected String mountDetailClassName(String className)
            throws PlcException {
        if (className.indexOf('.') == -1) {
            className = getContext().getPackageEntity() + "." + className;
        }

        return className;
    }

	/**
	 * @since jCompany 2.1
	 * 
	 * Método disparado antes da gravação de objetos em modo de alteração. Pode ser
	 * utilizado alternativamente ao método "antesPersistencia", para lógicas que devem ser
	 * disparadas somente na alteração.
    * @param voAtual Value Object com alterações
    */
   protected void updateBefore(Object entity, Object entityPrevious) throws PlcException {   }


	/**
	 * @since jCompany 3.0
	 * 
	 * Verifica se usuario tem opção de validar troca de id. se tiver não deixa trocar.
	 * Esta opção pode ser marcada no web.xml
	 * @param entity Value Object Atual
	 * @param entityPrevious Value Object Anterior, conforme recuperado
	 * @return true ou false se passou na validação ou não
	 */
	protected boolean updateValidationAlterationOID(Object entity, Object entityPrevious) throws PlcException {

		PlcBaseContextVO context = getContext();
		
		if (context.getAvoidChangeId() != null && context.getAvoidChangeId().equals("S")) {

			PlcPrimaryKey pk = entity.getClass().getAnnotation(PlcPrimaryKey.class); 
			if (pk == null){
				try {
					
				if (PropertyUtils.getProperty(entity,"id")!= null &&
					((Long)PropertyUtils.getProperty(entity,"id")).longValue()
					!=((Long)PropertyUtils.getProperty(entityPrevious,"id")).longValue())
					return false;
				} catch (Exception e) {
					throw new PlcException("jcompany.erro.generico",
							new Object[] { "alterValidationAlterationOID", e },
							e, log);
				}
				
			} else {
				// Compara para chavePrimaria
				try{
					String[] properties = pk.properties();

					for (String prop : properties) {
						Object idNaturalPrevious = PropertyUtils.getNestedProperty(entityPrevious, "idNatural");
						Object propNaturalPrevious = PropertyUtils.getNestedProperty(idNaturalPrevious, prop);
						if(propNaturalPrevious == null){
							return true;
						}

						Object idNatural = PropertyUtils.getNestedProperty(entity, "idNatural");
						Object propNatural = PropertyUtils.getNestedProperty(idNatural, prop);
				
						if (!propNatural.equals(propNaturalPrevious))
							return false;		
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}

		}

		return true;
	}
	
	/**
	 * @since jCompany 1.5.1
	 * 
	 * Método para lógicas antes da alteração para exclusão lógica.
	 * @param entity  com sitHistoricoPlc='I'
	 * @param entityPrevious com sitHistoricoPlc='A'
	 */
	protected void updateToLogicalExclusionBefore( Object entity, Object entityPrevious) throws PlcException {	}
	
	/**
	 * @since jCompany 2.0
	 *  
	 * Altera arquivo em upload em meio a lógica de alteração
	 * @param entity Value Object Atual
	 * @param entityFile Value Object de arquivo.
	 * @param alteredBeforeFileExclusion 
	 */
	protected void updateManipulateFile(Object entity,IPlcFileEntity entityFile,
			boolean alteredBeforeFileExclusion) throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alterManipulateFile:"));

		try {
			
			PlcBaseContextVO context = getContext();
	
			if  ((PropertyUtils.isReadable(entity,"indExcPlc") &&
				  PropertyUtils.getProperty(entity,"indExcPlc") != null && 
				  "S".equals(PropertyUtils.getProperty(entity,"indExcPlc"))) ||
				  (entityFile.getIndExcPlc().equals("S") ||
				   entityFile.getIndExcPlc().equals("true")) ){
				
				if (log.isDebugEnabled())
					log.debug("Excluding file with id "+ context.getAttachedFile().getId());
				Long idFileAux = context.getAttachedFile().getId();
				// transiente
				PropertyUtils.setProperty(entity,"attachedFile",new PlcFileEntity());
				// persistente
				PropertyUtils.setProperty(entity,"idAttachedFilePlc",null);
				getDAO(entity.getClass()).update(entity);
				alteredBeforeFileExclusion = true;
				entityFile = updateUpdateFile(entityFile,idFileAux,true);
	
			} else if ( (context.getAttachedFile().getSize() != null) && (!entityFile.getIndExcPlc().equals("S") && !entityFile.getIndExcPlc().equals("true"))){
	
				if ((entityFile.getUrl()==null) ||
					(entityFile.getUrl()!=null && entityFile.getUrl().equals("")))
				    	entityFile.setUrl(entityFile.getName());
				entityFile = updateUpdateFile(entityFile,context.getAttachedFile().getId(),false);
				if (entityFile != null) {
					// transiente
					PropertyUtils.setProperty(entity,"attachedFile",entityFile);
					// persistente
					PropertyUtils.setProperty(entity,"idAttachedFilePlc",entityFile.getId());
				}
			}
	
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alterManipulateFile:"));		
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"alterManipulateFile", e }, e, log);
		}
		
	}
	
	/**
	 * @since jCompany 1.0
	 * . 
	 * Método auxiliar que exclui efetivamente registros de detalhes.<p>
	 * Este método também faz alteração em cascata para arquivos anexados
	 * @param vo Referência ao Value Object mestre, cujos detalhes serão excluídos.
	 */
	protected void updateExcludeCheckedDetails(Object entity) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alterExcludeCheckedDetails:"));

		Object masterEntity = entity;

		try {

			PlcBaseContextVO context = getContext();
			if (context.getMainClass().isAssignableFrom(entity.getClass())){
				List l = context.getDetailNames();
				List listDetailsPlc = context.getDetailNamesPlc();
				List lDet = null;
				
				Iterator i = l.iterator();
				int counter = 0;
	
				// Cada interacao varre um type de detalhe
				while (i.hasNext()) {
	
					String detailNamePlc = (String)listDetailsPlc.get(counter);					
					
					// Somente garante varredura
	                String classNameDet = mountDetailClassName((String)i.next());
					Class lassDetail= Class.forName(classNameDet);
	
					counter++;
					Set sDet = null;
					
					if ( detailNamePlc.indexOf("_Det") >= 0) {
						// Padrao anterior a 3.0
						String methodName = detailNamePlc.substring(0,detailNamePlc.indexOf("_"));
						Set det = (Set)PropertyUtils.getProperty(masterEntity,methodName);
						if (det != null) 
							sDet = det;
	
					} else {
											
						if (Set.class.isAssignableFrom(PropertyUtils.getPropertyType(masterEntity,detailNamePlc)))	{
							sDet = (Set) PropertyUtils.getProperty(masterEntity,detailNamePlc);					
						
						} else {
							lDet = (List)PropertyUtils.getProperty(masterEntity,detailNamePlc);				
						}
					}
	
					Iterator j = null;
					List lAux = new ArrayList();;
					
					if (sDet != null) {
						j = sDet.iterator();
					} else if (lDet != null) {
						j = lDet.iterator();
					}
					
					if (j==null)
						return;
	
					String subdetailPropertyName = getContext().getSubDetailPropNameCollection();
					
					while (j.hasNext()) {
	
						Object detEntity = j.next();
						
						if (PropertyUtils.isReadable(detEntity,"indExcPlc")
							&& PropertyUtils.getProperty(detEntity,"indExcPlc") != null && 
							("S".equals(PropertyUtils.getProperty(detEntity,"indExcPlc")) || 
							"true".equals(PropertyUtils.getProperty(detEntity,"indExcPlc")))) {
	
							lAux.add(detEntity);
	
						} else if (subdetailPropertyName != null && !subdetailPropertyName.equals("") &&
								PropertyUtils.isReadable(detEntity,subdetailPropertyName) &&
								PropertyUtils.getProperty(detEntity,subdetailPropertyName) != null &&
								((List)PropertyUtils.getProperty(detEntity,subdetailPropertyName)).size() > 0) {
						   // Verifica sub-detalhe
						    updateExcludeMarkedSubDetails((List)PropertyUtils.getProperty(detEntity,subdetailPropertyName));
	
						}
	
					}
	
					// Exclui fora do loop, para evitar problema de concorrencia
					Iterator z = lAux.iterator();
					while (z.hasNext()) {
						Object detEntityExc = z.next();
						if (sDet != null) {
							Object entityDet=null;
							
							if (log.isDebugEnabled()) {
								log.debug("hashCode entity "+detEntityExc.hashCode());
								Iterator kk = sDet.iterator();
								while (kk.hasNext()) {
									entityDet =  kk.next();
								}
							}
							
							if (!sDet.remove(detEntityExc))
								throw new PlcException("jcompany.errors.exclusion.hashcode",new Object[]{detEntityExc});
							
						} else
							lDet.remove(detEntityExc);
				
						// Se nao for manyToMany, entao detalhes marcados devem ser explicitamente excluidos
						// Assume uma coleção no detalhe apontando para o Mestre como referencia ManyTo-Many
						if (!updateExcludeMarkedDetailsToManyToMany(detEntityExc,
							PlcEntityHelper.getInstance().getPropertyNamePlc(masterEntity))) {
							IPlcDAO baseDAO = getDAO(entity.getClass());
							baseDAO.exclude(detEntityExc);
						}
					}
				}
			}

			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alterExcludeCheckedDetails:"));

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.hibernate.update",new Object[] {e},e,log);
		}

	}
	
	/**
	 * @since jcompany 3.2 Assume uma coleçao na classe de detalhe apontando para a classe Mestre com nome de propriedade
	 * padrão como relacionamento ManyToMany. 
	 * @param classe Classe Classe de Detalhe
	 * @param propertyNamePlc nome da propriedade candidata a ManyToMany
	 * @return true se a propriedade for um List ou Set
	 */
	protected boolean updateExcludeMarkedDetailsToManyToMany(Object entityDet, String propertyNamePlc) throws PlcException {
		
		try {

			return Collection.class.isAssignableFrom(PropertyUtils.getPropertyType(entityDet,propertyNamePlc));
		
		} catch (Exception e) {
			return false;
		}
			
	}

	/**
	 * @since jCompany 2.5
	 * 
	 * Exclui sub-detalhes marcados, recursivamente inclusive. Importante notar que, nesta versao, o DAO Padrao é instanciado 
	 * neste método e não há IoC, já que a coleção pode estar vazia e o método nao tem como se basear no ENTITY corrente. Basta,
	 * no entanto, que se especialize o BO também para que o DAO atue.
     * @param subDetailPlc Coleção de sub-detalhes
     */
    protected void updateExcludeMarkedSubDetails( List subDetailPlc) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alterExcludeMarkedSubDetails:"));

        try {

		        Iterator i = subDetailPlc.iterator();
		        while (i.hasNext()) {
		        	Object entitySub =  i.next();

		            if (PropertyUtils.isReadable(entitySub,"indExcPlc") &&
		            	PropertyUtils.getProperty(entitySub,"indExcPlc") != null &&
		            	"S".equals(PropertyUtils.getProperty(entitySub,"indExcPlc"))) {

		             	i.remove();
		             	IPlcDAO baseDAO = getDAODefault();
						baseDAO.exclude(entitySub);

		            }
		        }

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alterExcludeMarkedSubDetails:"));

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "alterExcludeMarkedSubDetails", e }, e,log);
        }

    }
    
   /**
	* @since jCompany 2.1
	* 
	* Método disparado após a gravação de objetos em modo de alteração. Pode ser
	* utilizado alternativamente ao método "aposPersistencia", para lógicas que devem ser
	* disparadas somente na alteração.
    * @param entity Value Object com alterações
    * @param entityPrevious Value Object originalmente recuperado, sem alterações
    */
   protected void updateAfter( Object entity, Object entityPrevious) throws PlcException {   
	   PlcEventHelper.fireEvent(getContext(), entity);  	   
   }

	/**
	 * @since jCompany 1.5.1
	 * 
	 * Método para lógicas após a alteração para exclusão lógica.
	 * @param entityPrevious com sitHistoricoPlc='A'
	 */
	protected void updateToLogicalExclusionAfter(Object entidade,Object entityPrevious)throws PlcException  {	}

	/**
	 * @since jCompany 1.5.1
	 * 
	 * Verifica operação sobre arquivo anexado, disparando exclusão, alteração e
	 * inclusão
	 * @param entity Value Object contendo a entidade de arquivo
	 * @param plcFile Value Object de arquivo
	 * @param idPrevious id do arquivo anterior, se for para alterar
	 */
	protected void updateManipulateFileUpload( Object entity,
					IPlcFileEntity plcFile, Long idPrevious) throws PlcException {

		try {
			
			if (plcFile.getIndExcPlc() != null &&(  
			    "S".equals(PropertyUtils.getProperty(entity,"indExcPlc")) || 
			    "S".equals(PropertyUtils.getProperty(entity,"indExcPlc")))) {
				log.debug("Excluding");
				plcFile = updateUpdateFile(plcFile,plcFile.getId(),true);
			} else {
				plcFile = updateUpdateFile(plcFile,idPrevious,false);
			}
		
		} catch (Exception e) {
			throw new PlcException("jcompany.erro.generico", new Object[] {
					"alterManipulateFileUpload", e }, e, log);
		}

	}
	
	/**
	 * @since jCompany 1.5
	 * 
	 * Atualiza um arquivo, podendo incluí-lo, alterá-lo ou excluí-lo, em
	 * conformidade com o Value Object IPlcFileEntity. Esta entidade é agregada à entidade principal,
	 * descendente de Object, no momento da gravação, mas pode indicar operações
	 * diferentes da Entidade Mestre. Por exemplo: Ao alterar o mestre, o usuário pode excluir o
	 * arquivo (detalhe), ou mesmo incluí-lo ou alterá-lo. Portanto, a cada alteração
	 * no mestre, todas as operações devem ser verificadas por este método.
	 * @param fileNew Value Object contendo informações de operações para o arquivo
	 *  anexado.
	 * @param idFilePrevious Chave do Arquivo Anterior, para facilitar lógicas de alteração
	 *        e exclusão.
	 * @param exclude Se for para excluir o arquivo, este flag já vem marcado. Este é o
	 *  caso onde o usuário clicou no checkbox de exclusão.
	 *
	 * @return Retorna o Value Object com as atualizações
	 */
	protected IPlcFileEntity updateUpdateFile( IPlcFileEntity fileNew,
			Long idFilePrevious, boolean exclude)
			throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":alterUpdateFile:"));

		String oper = "";

		try {

		   if (exclude) {
				oper="E";
			} else if (idFilePrevious == null) {
				oper="I";
			} else {
				oper="A";
			}

			if (oper.equals("I")) {

				IPlcManager plcBO = (IPlcManager)getManager(fileNew.getClass());
				fileNew = (IPlcFileEntity) plcBO.insert(fileNew);
				log.debug("Including file");
				return ((IPlcFileEntity) fileNew);

			} else if (oper.equals("A")) {
				return null;
			} else if (oper.equals("E")) {
				log.debug("Entered in excluding");
				IPlcManager plcBO = (IPlcManager)getManager(fileNew.getClass());

				IPlcFileEntity filePrevious = (IPlcFileEntity)
										plcBO.retrieveOnlyEntity(fileNew.getClass(),idFilePrevious);
				IPlcDAO baseDAO = getDAO(fileNew.getClass());
				baseDAO.exclude(filePrevious);
				log.debug("Excluing file of id="+idFilePrevious);
				return ((IPlcFileEntity) filePrevious);

			}

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":alterUpdateFile:"));

		} catch (PlcException plcE) {
			throw plcE;
		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.alter",new Object[] {e},e,log);
		}

		return fileNew;

	}
  
	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - INCLUI ****************************** */
	/* **************************************************************************************** */
	
	/**
	 * Inclui uma agregacao (aciona a persistência)
	 * @see org.jcompany.model.IPlcManager#insert(org.jcompany.commons.Object)
	 */
	public Object insert( Object entity) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":insert:"));

		try {
			// TODO Implementar EBJ PlcBaseDAO baseDAO = getDAO(vo.getClass());
			IPlcDAO baseDAO = getDAO(entity.getClass());
			PlcBaseContextVO context = getContext();
			
			beforePersistence( entity, null, PlcConstantsCommons.MODES.MODE_INSERTION);
			insertBefore(entity);

			// TODO Rever este conceito de recuperaClasseAgregada
//			PlcAgregadoHelper plcBOUtil = PlcAgregadoHelper.getInstance();
	//		if (context.isUsaRecuperaClasseAgregada())
		//		plcBOUtil.recuperaClasseAgregada( vo);

			// Se nao for arquivo anexado testa 'avoidEquals' e se há arquivo anexado
			if (!IPlcFileEntity.class.isAssignableFrom(entity.getClass())) {
				
				baseDAO.avoidEqualsExecute(entity,"I");
	
				if (context.getAttachedFile() != null && context.getAttachedFile().getName() != null &&
					!context.getAttachedFile().getName().equals("")) {
					log.debug("Saving attached file");
					
					if (PropertyUtils.isWriteable(entity, "idAttachedFilePlc") && PropertyUtils.isWriteable(entity, "attachedFile")) {
					    IPlcFileEntity plcFile = context.getAttachedFile();
					    IPlcManager plcManager = (IPlcManager)getManager(plcFile.getClass());

					    // Assume URL igual ao nome
					    if ((plcFile.getUrl()==null) ||
						    (plcFile.getUrl()!=null && plcFile.getUrl().equals("")))
						plcFile.setUrl(plcFile.getName());

					    plcFile = (IPlcFileEntity) plcManager.insert(plcFile);

					    // persistente
					    PropertyUtils.setProperty(entity,"idAttachedFilePlc",plcFile.getId());
					    // transiente
					    PropertyUtils.setProperty(entity,"attachedFile",plcFile);

					    log.debug("Saved attached file");
					}
				}
			
			}

			beforePersistenceAfterAttachedFile(entity, null, PlcConstantsCommons.MODES.MODE_INSERTION);
			insertMountKeyBefore(entity);

			//incluiOneToOne(entidade);
			registerAuditSimple(entity, true);
			
			if (context.getPrimaryKeyClass() == null ||
					(context.getPrimaryKeyClass() != null && context.getPrimaryKeyClass().trim().equals(""))) {
				Long idGenerated = baseDAO.insert(entity);
				PropertyUtils.setProperty(entity,"id",idGenerated);
			} else
				baseDAO.insert(entity);

			log.debug("Entered Saved");

			insertAfter(entity);
			afterPersistence(entity, null, PlcConstantsCommons.MODES.MODE_INSERTION);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":insert:"));

			return entity;

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.insert",new Object[] {e},e,log);
		}
	}
	

	/**
	 * Inclui associações oneToOne.
	 * TODO Devemos usar anotações JPA aqui?
	 */
	protected void insertOneToOne(Object entity)  throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":insertOneToOne:"));

		try{
			//TODO Otimizar para não ter que ler todos os fields a toda inclusão.
			// Pega da classe
			Field[] fields = PlcReflectionHelper.getInstance().getAllAttributesInHierarchy(entity.getClass(), false, false);
			Method[] methods = PlcReflectionHelper.getInstance().getAllMethodsInHierarchy(entity.getClass(), false, false);
			// Pega segundo nivel da hierarquia
// // Código desnecessário.
//			for (Class<?> classe = entidade.getClass().getSuperclass();
//					classe!=null && classe.isAnnotationPresent(MappedSuperclass.class);
//					classe = classe.getSuperclass()) {
//				fields = (Field[])ArrayUtils.addAll(fields, classe.getDeclaredFields());
//				methods = (Method[])ArrayUtils.addAll(methods, classe.getDeclaredMethods());
//			}
			
			for (Field f : fields) {
				if (f.isAnnotationPresent(OneToOne.class) && Object.class.isAssignableFrom(f.getType())) {

					Object ent = PropertyUtils.getProperty(entity, f.getName());
					if (ent!=null)
						getManager(ent.getClass()).insert(ent);
				}
			}
			
			for (Method method : methods) {
				if (method.getTypeParameters().length==0 && method.isAnnotationPresent(OneToOne.class) && Object.class.isAssignableFrom(method.getReturnType())) {
					Object ent = method.invoke(entity, (Object[])null);
					if (ent!=null)
						getManager(ent.getClass()).insert(ent);
				}
			}
			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":insertOneToOne:"));

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.insert",new Object[] {e},e,log);
		}

	}

	/**
     * Registra Auditoria Simples ( Usuário e Data de Alteração/Criação) para o ENTITY, detalhes e subdetalhes
     * @param entity ENTITY que sera registrada a auditoria
     * @param insert, true se for inclusão de um novo registro
     */
    protected void registerAuditSimple(Object entity, boolean insert)  throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":registerAuditSimple:"));

		if (log.isDebugEnabled())
			log.debug("############### Entered at registerAuditSimple for:  " + entity.getClass().getSimpleName());

        //Templated Method Antes
        entity = registerAuditSimpleBefore(entity,insert);

        try {
        	
        	PlcBaseContextVO context  = getContext();
        	
        	if (context != null && Object.class.isAssignableFrom(entity.getClass())){
        	
        		Date date = new Date();
        		
        		PlcBaseUserProfileEntity baseUserProfileEntity = context.getUserProfile();
        		
        		if (baseUserProfileEntity == null || baseUserProfileEntity.getLogin() == null){
        			if (baseUserProfileEntity == null)
        				baseUserProfileEntity = new PlcBaseUserProfileEntity();
        			baseUserProfileEntity.setLogin("Anonym");
        		}

        		// Registra Auditoria para o ENTITY Principal
        		if (PropertyUtils.isReadable(entity, PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE))
        			PropertyUtils.setProperty(entity,PlcConstantsCommons.ENTITY.DATE_LAST_UPDATE, date);

        		if (PropertyUtils.isReadable(entity, PlcConstantsCommons.ENTITY.USER_LAST_UPDATE))
        			PropertyUtils.setProperty(entity,PlcConstantsCommons.ENTITY.USER_LAST_UPDATE, baseUserProfileEntity.getLogin());

        		//Se for inclusão registra usuário e data de criação
        		if (insert){

        			if (PropertyUtils.isReadable(entity, PlcConstantsCommons.ENTITY.DATE_CREATION))
        				PropertyUtils.setProperty(entity,PlcConstantsCommons.ENTITY.DATE_CREATION,date);

        			if (PropertyUtils.isReadable(entity, PlcConstantsCommons.ENTITY.USER_CREATION))
        				PropertyUtils.setProperty(entity,PlcConstantsCommons.ENTITY.USER_CREATION,baseUserProfileEntity.getLogin());
        		}

        		// if for Mestre detalhe ou Mantem Detalhe, registra para os Detalhes e ou subdetalhes
        		if ( PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL.equals(context.getLogic()) ||
        				PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL_MANTAIN_DETAIL.equals(context.getLogic()) || 
        				PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_MASTER_DETAIL_SUB_DETAIL.equals(context.getLogic())){

        			//  Recuperando os fiels, se a entidade tiver MappedSuperClass recupera da superClasse
        			Field[] fields         = PlcReflectionHelper.getInstance().getAllAttributesInHierarchy(entity.getClass(), false, false);
//      			// Código desnecessário.
//      			for (Class<?> classe = entidade.getClass().getSuperclass();
//      			classe!=null && classe.isAnnotationPresent(MappedSuperclass.class);
//      			classe = classe.getSuperclass()) {
//      			fields = (Field[])ArrayUtils.addAll(fields, classe.getDeclaredFields());
//      			}

        			// Para os fields OneToMany ( Detalhes e SubDetalhes) chama o registraAuditoriaSimples Novamente.
        			for (Field f : fields) {
        				if (f.isAnnotationPresent(OneToMany.class)) {
        					try {
        						AbstractPersistentCollection details = (AbstractPersistentCollection)PropertyUtils.getProperty(entity, f.getName());
        						if (details != null && details.wasInitialized()){
        							for(Object detail : (Collection<Object>)details)
        								// Registra a auditoria para o detalhe.
        								registerAuditSimple(detail, insert);
        						}
        					} catch (Exception e) {
        						log.warn("registerAuditSimple - It's not possible to read property: " +
        								"'"+f.getName()+"' from entity '"+entity.getClass().getName()+"': "+e.getMessage());
        					}

        				}    
        			}

        		}
        	}
        //Templated Method Apos
        entity = registerAuditSimpleAfter(entity,insert);

        if (log.isDebugEnabled())
        	log.debug("############### Registered audit for:  " + entity.getClass().getSimpleName());

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":registerAuditSimple:"));

        } catch (Exception e) {
        	throw new PlcException("jcompany.error.generic", new Object[] {"registerAuditSimple", e }, e, log);
        }
    }

	/**
     * Design Pattern: Template Method. Os métodos com prefixo "antes",
     * "apos" ou "api" são eventos (método vazios) destinados a
     * especializações nos descendentes.
     */	
	protected Object registerAuditSimpleBefore(Object entity, boolean insert)  throws PlcException {
		return entity;
	}
    /**
     * Design Pattern: Template Method. Os métodos com prefixo "antes",
     * "apos" ou "api" são eventos (método vazios) destinados a
     * especializações nos descendentes.
     */	
	protected Object registerAuditSimpleAfter(Object entity, boolean insert)  throws PlcException {
		return entity;
	}	
		
		
	/**
	* @since jCompany 2.1
	* 
	* Método disparado antes da gravação de objetos em modo de inclusão. Pode ser
	* utilizado alternativamente ao método "antesPersistencia", para lógicas que devem ser
	* disparadas somente na inclusão.
    * @param entity Value Object a ser gravado
    */
   protected void insertBefore( Object entity) throws PlcException {  }

	/**
	 * @since jCompany 1.5.1
	 * . 
	 * Método para que o descendente monte chaves compostas, se utilizado, o descendente deve instanciar a classe da chave e 
	 * montar os valores do Entidade nesta classe.
	 * @param entity
	 */
	protected void insertMountKeyBefore( Object entity) throws PlcException {}

	/**
	 * @since jCompany 2.1. 
	 * 
	 * Método disparado após a gravação de objetos em modo de inclusão. Pode ser
	 * utilizado alternativamente ao método "aposPersistencia", para lógicas que devem ser
	 * disparadas somente na inclusão.
    * @param entity Value Object a ser gravado
    */
   protected void insertAfter( Object entity) throws PlcException {  }

	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - RECUPERA **************************** */
	/* **************************************************************************************** */
   
    /**
     * Recupera um objeto
	 * @see org.jcompany.model.IPlcManager#retrieve(java.lang.Class, java.lang.Object)
	 */
	public Object[] retrieve ( Class clazz, Object id) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieve:"));

		try {
		  
			Object entity = null;
			
			retrieveBefore(clazz,id);

			PlcBaseContextVO context = getContext();
        	
			IPlcDAO baseDAO = getDAO(clazz);
			
			// Importante: Se não tiver filtro ou for download padrao de arquivos, recupera com a chave passada
			if (StringUtils.isBlank(context.getVerticalFilter()) || IPlcFileEntity.class.isAssignableFrom(clazz))
			   entity = baseDAO.retrieve(clazz, id);			
			else
			   entity = baseDAO.retrieveWithFilter(clazz,id);

			if (log.isDebugEnabled())
				log.debug("Before attached file = "+context.getAttachedFile());

			List lookupNAvigationList = null;
			if (!IPlcFileEntity.class.isAssignableFrom(clazz) &&
				PropertyUtils.isReadable(entity,"idAttachedFilePlc") &&
				PropertyUtils.getProperty(entity,"idAttachedFilePlc") != null &&
				context.getAttachedFile() != null) {

				log.debug("idFile"+context.getIdAttachedFilePlc());

				IPlcManager plcBO = getManager(context.getAttachedFile().getClass());
				Long idAttachedFilePlc = (Long)PropertyUtils.getProperty(entity,"idAttachedFilePlc");
				String fileName = plcBO.retrieveFileName(context.getAttachedFile(), idAttachedFilePlc);
				IPlcFileEntity entityFileAux = new PlcFileEntity();
				entityFileAux.setName(fileName);
				entityFileAux.setId(idAttachedFilePlc);
				PropertyUtils.setProperty(entity,"attachedFile",entityFileAux);

			}

			
			//Recupera lista de valores possíveis para classes de lookup em navegação
			if (!IPlcFileEntity.class.isAssignableFrom(clazz))
				if (context.getLookupNavigationClasses() != null)
					lookupNAvigationList = retrieveLookupNavigationList(entity);
		   
		   retrieveAfter(clazz,id,entity);

			if (log.isDebugEnabled())
				log.debug("Entity retrieved ="+entity);

			List modifications = retrieveApprovalModification(entity);
			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieve:"));

			return new Object[]{entity,modifications,lookupNAvigationList};
		
		} catch (PlcException e1) {
			throw e1;
		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.retrieve",new Object[] {e},e,log);
		}
	}
	
	/**
	 * jCompany 1.5.3. Método disparado antes de recuperação de registros, contendo uma Entidade que pode
	 * ter propriedades preenchida para a recuperação.
	 */
	protected void retrieveBefore( Class clazz,	 Object id) throws PlcException {	}

	/**
	 * jCompany 2.7.3. Mantido para recuperação de arquivos
	 */
	public Object retrieveOnlyEntity ( Class clazz, Object id) throws PlcException {

	    Object[] ret = retrieve(clazz,id);
	    return  ret[0];

	}
	
	/**
	 * jCompany. Método vazio, destinado a ser sobreposto nos descendentes para
	 * a implementação de regras do negócio posteriores á recuperação de objetos.<p>
	 *
	 * Importante: É chamado a partir do método recuperaObjeto e não dos recuperaListaXXXX,
	 * ou seja, deve ser utilizado para complementar Value Objects já recuperados, e
	 * não coleções.
	 *
	 * Tipicamente, estas regras envolvem:<P>
	 *
	 * - Complementação de informações no Value Object que não são compostas automaticamente
	 * pela persistência. Por exemplo, em alguns casos o desenvolvedor pode sentir necessidade
	 * de complementar as informações básicas a serem retornadas com regras do negócio específicas,
	 * de forma a "aprontar" a Entidade para exibição.<p>
	 *
	 * Exemplo de complementação:<p>
	 *
	 *	 TipoCurso type = (TipoCurso) entidadeAtual;<br>
	 *   type.setNome("alterado");<br>
	 * @param id 
	 * @param clazz 
	 *
	 * @throws PlcException O desenvolvedor deve tratar a exceção com o maior nível de detalhe
	 * possível, disparando uma exceção jCompany, no mínimo da seguinte forma:<p>
	 *
	 *	  } catch (Exception ex) {
	 *	   log.error("Erro ao tentar verificar o type de curso="+ex);
	 *	   throw new PlcException("aplicacao.erro.meuerro",new Object[] {ex},ex);
	 *    }
	 */
	protected void retrieveAfter(Class clazz, Object id, Object entity)
				  throws PlcException {	return;	}


	/**
	 * Recupera modificações em lógicas de aprovaçao (workflow simples)
	 * @see org.jcompany.model.IPlcManager#retrieveApprovalModification(org.jcompany.commons.Object)
	 */
	public List retrieveApprovalModification(Object entity) throws PlcException {
	
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveApprovalModification:"));

		try {
			
			 PlcBaseContextVO context= getContext();
			  
			//se for recuperação de pendente, recuperar a lista de modificações
			List modifications = null;
			if(context != null && context.isRetrievePendent()) {
			    
			    //verifica se o usário é aprovador
			    boolean approver = apiApproverUser(entity,null);
			    context.setApprover(approver);	//seta se usuário é aprovador
			    
			    if(entity != null && ENTITY.STATUS_PENDENT.equals(PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC)) 
				         && PropertyUtils.getProperty(entity,ENTITY.ID_PARENT) != null && approver){
			         modifications = retrieveModifications(entity);
			    }
			}

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveApprovalModification:"));

			return modifications;

		} catch (Exception e) {
		//	log.fatal("erro fatal ao tentar recuperar objeto"+e,e,log);
			throw new PlcException("jcompany.errors.persistence.retrieve",new Object[] {e},e,log);
		}
		
	}
	
	 /**
	  * Recupera a Entidade ativo que a Entidade pendente está relacionado, realiza a comparação entre os atributos que <br>
	  * foram modificados, gerando uma lista das modificações.<br> 
	  * Serão comparados os atributos marcados na propriedade hitoricoPropsAux da entidadePendente.<br>
	  * <br>
	  * A lista é composta pelos atributos modificados. As modificações são representadas por array de String.<br> 
	  * A posição 0 contem o nome do campo, a posição 1 o valor anterior e a posicao 2 o valor atual do campo.<br>
	  * <br>
	  * @param entityPendent
	  * @return lista das modificações, lista vazia se nenhuma modificação for encontrada.
	  */
	 protected List retrieveModifications( Object entityPendent)
           throws PlcException {

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveModifications:"));

	     List modifications = new ArrayList();		//lista com os modificações

	     try {
	    	 
	    	 //recupera o registro original
		     Object entityParent = retrieveOnlyEntity(entityPendent.getClass(),
		    		 PropertyUtils.getProperty(entityPendent,ENTITY.ID_PARENT).toString());
		
		     if(entityParent != null) {
		         //realiza a comparação entre o pendente e original
		         retrieveModificationsBefore(entityPendent,entityParent);
		         modifications = retrieveModificationsCompare(entityPendent,entityParent);
		         modifications = retrieveModificationsAfter(entityPendent,entityParent,modifications);
		     }

				if (logModel.isDebugEnabled())
					logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveModifications:"));

		     return modifications;
		     
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveModifications", e }, e,log);
		}
	    
	 }
	 
	    
    /**
     * jCompany. Método para complementação de lógica de comparação das propriedades entre pendente e ativo.<br>
     * <br>
     * @param modifications lista com das modificações entre a entidade pendente e entidade ativa.
     * @return lista das modificações entre a entidade pendente e ativa.
     */
    protected List retrieveModificationsAfter(Object entity, Object entityParent, List modifications) throws PlcException {
        return modifications;
        
    }
	   
 	/**
      * Realiza a comparação das propriedades entre as duas entidades. Serão comparadas as propriedades<br>
      * informadas no parametro propriedadeTestar.<br>
      * <b>Obs.:</b> Caso o parametro propriedadeTestar esteje vazio ou nulo, todas as propriedades serão testadas.<br>
      * <br>
      * @return lista com as modificações. Array de String [0] nome da propriedade, [1] valor anterior, [2] valor atual.
      * @throws PlcException
      */
    protected List retrieveModificationsCompare( Object entityActive, Object entityPendent)
            throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveModificationsCompare:"));

        List modifications = new ArrayList();
        PlcBaseContextVO context = getContext();
        String propertyToTest = (context != null ? context.getHistoricPropsAux() : "");
        try{
            
            //se não foi informado nenhuma propriedade, comparar todas as propriedades do ENTITY
            if(StringUtils.isBlank(propertyToTest)) {
                //recuperando todas as propriedades do ENTITY
                propertyToTest = PlcEntityHelper.getInstance().getPropertiesToString(entityPendent,context);
            }
            
            if(propertyToTest == null) {
                if (logModel.isDebugEnabled())
        			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveModificationsCompare:"));
            	return modifications;
            }
            
            //para cada campos, testar se houve modificação
            StringTokenizer stk = new StringTokenizer(propertyToTest, ",");
            while (stk.hasMoreTokens()){
                String fildToTest = (String) stk.nextToken();

                //recupera os valores dos atributos da entidade pendente e ativa
                String valuePendent = BeanUtils.getSimpleProperty(entityPendent, fildToTest);
                String valueActive = BeanUtils.getSimpleProperty(entityActive, fildToTest);

                //se forem diferentes, adiciona uma modificação
                if(!StringUtils.equals(valuePendent, valueActive)){
                    modifications.add(new String[] { fildToTest, valueActive, valuePendent });
                }
            }

        } catch (Exception e){
            throw new PlcException("jcompany.error.generic", new Object[] { "retrieveModificationsCompare", e }, e,log);
        }

        if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveModificationsCompare:"));

        return modifications;
    }
	 
	/**
	 * jCompany. Método para complementação de lógica de comparação das propriedades entre pendente e ativo. <br>
     * <br>
     */
    protected void retrieveModificationsBefore(   Object entity, Object entidadeParent) throws PlcException {  }



    /**
     * @param entity Entidade Principal
     *  que devem ter os seus valores possiveis recuperados
     * @return Conjunto de Coleções de Valores Possíveis, para cada classe, na ordem em que são declaradas
     */
    protected List retrieveLookupNavigationList(Object entity)	throws PlcException {

        if (logModel.isDebugEnabled())
        	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupNavigationList:"));

        try {
        	PlcBaseContextVO context = getContext();
        	IPlcDAO baseDAO = getDAO(entity.getClass());
        	List<Object> listSelReturn = new ArrayList<Object>();
        	List l = PlcStringHelper.getInstance().splitListElements(context.getLookupNavigationClasses());
        	Iterator i = l.iterator();
        	while (i.hasNext()) {
        		String classNavegation = (String) i.next();
        		String classDestinyPlc = null;

        		int pos = classNavegation.indexOf("->");
        		if (pos>=0) {
        			classDestinyPlc = classNavegation.substring(classNavegation.indexOf("->")+2);
        			classNavegation = classNavegation.substring(0, pos);
        		}

        		// Descobre de quem navega
        		List propertiesOrigin = PlcEntityHelper.getInstance().getAggregatePropertyForType(entity,classNavegation);
        		// Nesta versão, considera a primeira do type
        		Object entityAggregateOrigin = null;
        		if(propertiesOrigin != null && propertiesOrigin.size() > 0)
        			entityAggregateOrigin =  PropertyUtils.getProperty(entity,(String)propertiesOrigin.get(0));

        		//   String propPai = baseDAO.+".id";
        		List<Object> listSel = new ArrayList<Object>();
        		if (entityAggregateOrigin != null && classDestinyPlc!=null )
        			listSel = baseDAO.retrieveAggregateNavigation(entityAggregateOrigin.getClass(), 
        					(Long)PropertyUtils.getProperty(entityAggregateOrigin,"id"),
        					Class.forName(classDestinyPlc));

        		listSelReturn.addAll(listSel);
        	}

        	if (logModel.isDebugEnabled())
        		logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupNavigationList:"));

        	return listSelReturn;

        } catch (Exception e) {
        	throw new PlcException("jcompany.error.generic", new Object[] {
        			"retrieveLookupNavigationList", e }, e,log);
        }

    }
    
    /**
     * @deprecated Conceito revisto
     */
    protected void retrieveAggregaties( Object entity) throws PlcException {
    
        if (PropertyUtils.isReadable(entity,"aggregatesLazyPlc")) {
//            PlcAgregadoHelper aHelper = PlcAgregadoHelper.getInstance();
  //          aHelper.recuperaClasseAgregada(entidade);
        }
        
    }

	/**
	 * jCompany. Recupera somente o nome em tabela de arquivos anexados, para compor
	 * mensagem de retorno, evitando baixar o arquivo em si. Utiliza query escalar e
	 * método iterate da Hibernate.
	 *
	 * @param entityFile Value Object do type específico a ser recuperado
	 * @param idFile Chave Primária do registro a ser recuperado
	 *
	 * @return Nome do Arquivo
	 */
	public String retrieveFileName(IPlcFileEntity entityFile, Long idFile) throws PlcException{

      if (logModel.isDebugEnabled())
	        	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveFileName:"));

		if (idFile.intValue()==0) return "";

		String fileName = "File not found. "+idFile;

		try {

			IPlcDAO baseDAO = getDAO(entityFile.getClass());
			fileName = (String) baseDAO.retrieveOneValue(entityFile,idFile,"name");

		}catch(PlcException pExc ){
			throw pExc;
		} catch (Exception e){
			// Não cancela lógica principal porque não achou arquivo
			if (logWarning.isDebugEnabled()) {
				logWarning.debug("Error trying to retrieve file "+idFile+" Error:"+e+" with user: "+
			        getUserProfileEntity(),e);
				e.printStackTrace();
			}
		}

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveFileName:"));

		return fileName;

	}
	
	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - RECUPERA TOTAL REGS ***************** */
	/* **************************************************************************************** */

	/**
	 * Recupera total de registros
	 * @see org.jcompany.model.IPlcManager#retrieveTotalElementsDefault(java.lang.Class, org.jcompany.commons.Object)
	 */
   public Integer retrieveTotalElementsDefault(Class clazz,Object argsQBE) throws PlcException {

	   if (logModel.isDebugEnabled())
		   logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveTotalElementsDefault:"));

	   IPlcDAO baseDAO = getDAO(clazz);

	   retrieveTotalElementsDefaultBefore(clazz,argsQBE);

	   Integer tot = baseDAO.retrieveListQBETotal(clazz,argsQBE);

	   retrieveTotalElementsDefaultAfter(clazz,argsQBE,tot);

	   if (logModel.isDebugEnabled())
		   logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveTotalElementsDefault:"));

	   return tot;

   }  

   protected void retrieveTotalElementsDefaultBefore( Class clazz, Object argsQBE) throws PlcException{}
	
   protected void retrieveTotalElementsDefaultAfter( Class clazz, Object argsQBE, Integer tot) throws PlcException {  }

	/* **************************************************************************************** */
	/* *************************** TEMPLATE METHOD - RECUPERA LISTA SIMPLE ******************* */
	/* **************************************************************************************** */
 
	 /**
	 * jCompany 3.0. Recupera uma coleção de Objetos da camada de persistência seguindo criterio e ordenacao
	 */
	protected List retrieveListSimple(Class clazz,String orderByDynamic) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveListSimple:"));

		try {

			List list = retrieveListSimpleBefore(clazz,orderByDynamic);

			if (list == null) {
				IPlcDAO dao = getDAO(clazz);

				list = dao.retrieveAll(clazz,orderByDynamic);
			}

			retrieveListSimpleAfter(clazz,orderByDynamic,list);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveListSimple:"));

			return list;

		}catch( Exception e ){
			throw new PlcException("jcompany.erros.persistencia.recuperarListaSimples",new Object[] {e},e,log);
		}
	}
	  
	/**
	 * jCompany 3.0. Evento para codificação de lógica que reimplementa recuperação de listas simples.
	 * @param clazz Classe a ser recuperada com todas as suas instâncias
	 * @return Lista de Entidades para sobrepor à recuperação padrão (se retornada a recuperação não acontecerá.
	 * @throws PlcException Toda transação deve ser transformada em uma PlcException
	 */
	protected List retrieveListSimpleBefore( Class clazz, String orderByDynamic) throws PlcException {
		return null;
	}
	
	/**
	 * jCompany 3.0. Evento para codificação de lógica após recuperação de listas simples.
	 * @param clazz Informações de contexto da camada chamadora
	 * @param l Critério a ser obedecido, em OQL
	 */
	protected void retrieveListSimpleAfter( Class clazz, String orderByDynamic, List l) throws PlcException {}

 
	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - RECUPERA **************************** */
	/* **************************************************************************************** */
	
	/**
	 * jCompany. Recupera todos os atributos de um arquivo
	 */
	protected byte[] retrieveImageFile( Object entity, Long oid) throws PlcException {
	
      if (logModel.isDebugEnabled())
       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveImageFile:"));

		IPlcDAO baseDAO = getDAO(entity.getClass());

		byte[] imageRetrieved = baseDAO.retrieveBinaryFileById(entity.getClass(),oid,"image");
		
	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveImageFile:"));
		
		return imageRetrieved;

	}

	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - VERSION INCLUI *********************** */
	/* **************************************************************************************** */

	/**
	 * Registra uma versão do objeto
	 * @see org.jcompany.model.IPlcManager#versionInsert(org.jcompany.commons.Object, org.jcompany.commons.Object)
	 */
	public void versionInsert( Object entity, Object entityPrevious) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":versionInsert:"));

		if (entityPrevious != null) {

			if (versionInsertUpdatedPropertyMonitored( entity, entityPrevious) && versionInsertAdmitApi(entity,entityPrevious)) {

				try {

					//Object entidadeVersao =  BeanUtils.cloneBean(entidadeAnt);
					PlcBaseContextVO context = getContext();
					Object entityVersion =  PlcEntityHelper.getInstance().cloneEntityWithDetails(context.getDetailNamesPlc(),entityPrevious, true);

					PropertyUtils.setProperty(entityVersion,ENTITY.STATUS_HISTORIC_PLC,"I");
					PropertyUtils.setProperty(entityVersion,ENTITY.ID_PARENT,
							PropertyUtils.getProperty(entity,"id"));
					PropertyUtils.setProperty(entityVersion,"id",null);
					versionInsertBefore( entity, entityVersion);
					IPlcDAO baseDAO = getDAO(entity.getClass());
					baseDAO.insert(entityVersion);

					log.debug("version Updated succesfully");

				} catch (PlcException e) {
					throw e;
				} catch (Exception e2) {
					throw new PlcException("jcompany.errors.version.create.general",new Object[]{e2});
				}

			}


		}
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":versionInsert:"));
	}
	
	/**
	 * jCompany. Método para sobreposição nos descendentes, para regras do negócio que façam investigações
	 * mais avançadas em critérios para permitir ou não a geração de versões históricas para o objeto.
	 * @param entity Entidade Atual a ser incluido (sitHistoricoPlc="A")
	 * @param entityPrevious Entidade Anterior a ser incluído como versão (sitHistoricoPlc="I")
	 * @return true se permtir criar versão (default) ou false.
	 */
	protected boolean versionInsertAdmitApi( Object entity, Object entityPrevious)   throws PlcException {
		return true;
	}


	/**
	 * jCompany. Método para implementação de complementações específicas no ENTITY de versionamento, antes
	 * da gravação.
	 * @param entity nova Entidade
	 * @param entityVersion Entidade de versionamento antes de ser gravado.
	 */
	protected void versionInsertBefore( Object entity, Object entityVersion) throws PlcException {	}
	
	/**
	 * @return True se usuário alterou alguma propriedade monitorada ou False.
	 * Se nenhuma propriedade monitorada for informado, será retornado true.
	 */
	protected boolean versionInsertUpdatedPropertyMonitored( Object entity,
							Object entityPrevious) throws PlcException {

	    PlcBaseContextVO context = getContext();
		
		if (StringUtils.isBlank(context.getHistoricPropsAux()))
			return true;
		else {

			try {

				StringTokenizer s = new StringTokenizer(context.getHistoricPropsAux(),",");
				String _new="";
				String _old="";
				while (s.hasMoreTokens()) {

					String prop = s.nextToken();

					if (log.isDebugEnabled())
						log.debug("Verifying if property updated:"+prop);

					_new = BeanUtils.getProperty(entity,prop);
					_old = BeanUtils.getProperty(entityPrevious,prop);

					if (!_new.equals(_old))
						return true;

				}

				return false;

			} catch (Exception e) {
				throw new PlcException("jcompany.errors.insert.version",new Object[]{e});
			}
		}
	}

	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - VERSION INCLUI *********************** */
	/* **************************************************************************************** */

	/**
	 * Inclui uma pendência (sitHistoricoPlc="P")
	 * @see org.jcompany.model.IPlcManager#pendencyInsert(org.jcompany.commons.Object, org.jcompany.commons.Object)
	 */
	public Object pendencyInsert(Object entity,Object entityPrevious) throws PlcException {

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":pendencyInsert:"));

		PlcBaseContextVO context = getContext();
		if (context.getOriginalAction().equals("saveVersion")) {
			// É ação de inclusão de versão

			try {
					
				Long idParentAux = (Long) PropertyUtils.getProperty(entity,"id");
				if (PropertyUtils.getProperty(entity,"id") == null ||
					(PropertyUtils.getProperty(entity,"id") != null &&
					 ((Long)PropertyUtils.getProperty(entity,"id")).intValue()==0))
					idParentAux = null;
	
				Object entityVersionInactive = pendencyInsertCreateInactivePendent(entity,idParentAux,"I");
	
			    if (logModel.isDebugEnabled())
			       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":pendencyInsert:inactive:"));
	
				return entityVersionInactive;

			} catch (Exception e) {
				throw new PlcException("jcompany.erro.generico", new Object[] {
						"pendencyInsert", e }, e, log);
			}
		} else 	if (!apiApproverUser(entity, entityPrevious)) {

			// É lógica de aprovação
			log.debug("User not approver, saving with hist=P");

			try {
			
				Long idParentAux = (Long) PropertyUtils.getProperty(entity,"id");
				if (idParentAux == null ||(idParentAux != null && 
						((Long)idParentAux).longValue()==0))
					idParentAux = null;
				Object entityPendent = pendencyInsertCreateInactivePendent(entity,idParentAux,"P");
			
			    if (logModel.isDebugEnabled())
			       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":pendencyInsert:pendent:"));
	
				return entityPendent;
			
			
			} catch (Exception e) {
				throw new PlcException("jcompany.erro.generico", new Object[] {
						"pendencyInsert", e }, e, log);
			}
		}

		log.debug("User approver then returning null to admit convencional inclusion");
		
	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":pendencyInsert:null:"));

		return null;

	}

	/**
	 * Cria um novo registro inativo, baseado no ENTITY passado como argumento. Clona o registro
	 * e indica como pai o id passado como argumento.
	 *
	 * @param entityBase Value Object modelo
	 */
	protected Object pendencyInsertCreateInactivePendent( Object entityBase,
			Long idParent,String statusHistoricPlc)	throws PlcException {

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":pendencyInsertCreateInactivePendent:"));

		try {

			PlcBaseContextVO context = getContext();
			Object entityInactive =  PlcEntityHelper.getInstance().cloneEntityWithDetails(context.getDetailNamesPlc(),entityBase, true);
			Long lnulo = null;
			PropertyUtils.setProperty(entityInactive,"id",lnulo);
			PropertyUtils.setProperty(entityInactive,ENTITY.STATUS_HISTORIC_PLC,statusHistoricPlc);
			PropertyUtils.setProperty(entityInactive,ENTITY.ID_PARENT,idParent);
			PropertyUtils.setProperty(entityInactive,ENTITY.DATE_LAST_UPDATE,new java.util.Date());

			//clona cada detalhe padrão do jcompany
			//clonaDetalhePendente(entidadeBase, entidadeInativo, sitHistoricoPlc);

			if (beforeInsertInactivePendent(entityBase,entityInactive)) {

				IPlcDAO baseDAO = getDAO(entityBase.getClass());
				Long idInactive = (Long) baseDAO.insert(entityInactive);
				PropertyUtils.setProperty(entityInactive,"id",idInactive);

				afterInsertInactivePendent(entityBase,entityInactive);

			    if (logModel.isDebugEnabled())
			       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":pendencyInsertCreateInactivePendent:inactive:"));

				return entityInactive;
			}

		} catch (Exception e) {
			throw new PlcException("jcompany.historic.approval.clone",new Object[]{e},e,log);
		}

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":pendenciaIncluiCriaInativoPendente:null:"));

		return null;

	}

	/* **************************************************************************************** */
	/* ********************** TEMPLATE METHOD - APPROVE **************************************** */
	/* **************************************************************************************** */

	/**
	 * Aprovação
	 * @see org.jcompany.model.IPlcManager#approve(org.jcompany.commons.Object, org.jcompany.commons.Object)
	 */
	public void approve( Object entity,Object entityPrevious) throws PlcException {

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":approve:"));

		try {

			PlcBaseContextVO context = getContext();

			if (context.getOriginalAction().equals("approve") || context.getOriginalAction().equals("publishVersion")) {

				//PlcBaseDAO baseDAO = getDAO(vo.getClass());
				IPlcDAO baseDAO = getDAODefault();
				// Aprovação - Recupera o registro oficial válido
				if (PropertyUtils.getProperty(entity,ENTITY.ID_PARENT) != null &&
						!((context.getOriginalAction().equals("publishVersion") && !context.isApprover()))) 	{

					// Se tem id Pai vai publica, exceto se for publicação de versao de quem nao é aprovador, caso em que altera de I para P
					if (log.isDebugEnabled()) 
						log.debug("Updating parent entity - id="+PropertyUtils.getProperty(entity,ENTITY.ID_PARENT));

					Object entityActive = baseDAO.retrieve(entity.getClass(),PropertyUtils.getProperty(entity,ENTITY.ID_PARENT));

					Object entityActivePreviuos =  PlcEntityHelper.getInstance().cloneEntityWithDetails(context.getDetailNamesPlc(),
							entityActive, false);

					// Se data de alteracao do registro ativo for maior que a do registro a ser aprovado
					// recusa e sugere reprovação
					PlcDateHelper date = PlcDateHelper.getInstance();
					if (date.finalDateGreaterThanInitial((Date)PropertyUtils.getProperty(entity,ENTITY.DATE_LAST_UPDATE),
							(Date)PropertyUtils.getProperty(entityActive,ENTITY.DATE_LAST_UPDATE)))
						throw new PlcException("jcompany.errors.approve.to.new.date");

					approvePublishModifications(entity, entityActive);
					beforePersistence(entityActive, entityActivePreviuos, PlcConstantsCommons.MODES.MODE_EDITION);

					baseDAO.update(entityActive);

					afterPersistence(entityActive,entityActivePreviuos,PlcConstantsCommons.MODES.MODE_EDITION);

					// Altera anterior para inativo, somente se não for publicação
					if (!context.getOriginalAction().equals("publishVersion")) {
						PropertyUtils.setProperty(entityPrevious,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_INACTIVE);
						if (beforeInsertInactivePendent(entity,entityPrevious)) {
							baseDAO.update(entityPrevious);
						}
						afterInsertInactivePendent(entity,entityPrevious);
					}
					entity = entityActive;

				} else {

					log.debug("#### Updating only the entity");

					if (context.getOriginalAction().equals("publishVersion") && !context.isApprover()) {
						PropertyUtils.setProperty(entity,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_PENDENT);
					} else {
						PropertyUtils.setProperty(entity,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_ACTIVE);
					}
					// Nao pode ser sempre atualizado
					//entidade.setDataUltAlteracao(new java.util.Date());
					beforePersistence(entity,entityPrevious,PlcConstantsCommons.MODES.MODE_EDITION);
					baseDAO.update(entity);
					afterPersistence(entity,entityPrevious,PlcConstantsCommons.MODES.MODE_EDITION);

					if (log.isDebugEnabled())
						log.debug("Entity approved with approvalExcAux="+context.getApprovalExcAux());

					// Inclui inativo
					if ((context.getApprovalExcAux().toLowerCase().equals("n") ||
							context.getApprovalExcAux().toLowerCase().equals("r"))
							&& !context.getOriginalAction().equals("publishVersion")) {
						if (log.isDebugEnabled())
							log.debug("Entered to inactivate refering previous entity "+
									PropertyUtils.getProperty(entityPrevious,"id"));

						// Pega image anterior clona para salvar um novo como "I", inserindo como idPai o id do atual.
						//  Class.forName(entidade.getClass().getName()).newInstance();
						Object entityInactive =  PlcEntityHelper.getInstance().cloneEntityWithDetails(null,entityPrevious, true);
						Long lnull = null;
						PropertyUtils.setProperty(entityInactive,"id",lnull);
						PropertyUtils.setProperty(entityInactive,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_INACTIVE);
						PropertyUtils.setProperty(entityInactive,ENTITY.ID_PARENT,
								PropertyUtils.getProperty(entity,"id"));
						if (beforeInsertInactivePendent(entityPrevious,entityInactive)) {
							log.debug("Including inactive record: "+entityInactive);
							baseDAO.insert(entityInactive);
						}
						afterInsertInactivePendent(entityPrevious,entityInactive);
					}
				}

			} else {

				// Reprovação
				if (context.getApprovalExcAux().equals("t") || context.getApprovalExcAux().equals("r")) {
					log.debug("Reproving without keeping historical information");
					exclude(entity);
				} else {
					log.debug("Reproving keeping historical information");
					PropertyUtils.setProperty(entity,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_INACTIVE);
					if (PropertyUtils.getProperty(entity,ENTITY.OBJ_DISAPPROVAL_PLC) == null) {
						PropertyUtils.setProperty(entity,ENTITY.OBJ_DISAPPROVAL_PLC,"Not commented.");
					}
					update(entity,entityPrevious);
				}
			}

		} catch (PlcException e) {
			throw e;
		} catch (Exception e) {
			throw new PlcException("jcompany.errorr.generic", new Object[] {
					"approve", e }, e, log);
		}
		
	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":approve:"));

	}
	
	/**
	 * Verifica histórico de aprovação
	 * @see org.jcompany.model.IPlcManager#verifyApprovalHistoric(org.jcompany.commons.Object, org.jcompany.commons.Object)
	 */
	public Object verifyApprovalHistoric(Object entity, Object entityPrevious) 
	        throws PlcException {
		
	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":verifyApprovalHistoric:"));

		try {
			PlcBaseContextVO context = getContext();
			if (context.getHistoricModeAux() != null && !context.getHistoricModeAux().equals("")) {
	
				if (context.getHistoricModeAux().substring(0,1).toLowerCase().equals("v") &&
					!context.getOriginalAction().equals(EVENT.SAVE_VERSION)) {
					// Se for Versionamento e não for gravação de versão, grava versao e retorna
					// nulo para que o registro principal seja gravado também.
	
					log.debug("Historic Versioning");
	
					// Cria novo registro de versão, contendo image anterior, apontando para o principal
					versionInsert(entity,entityPrevious);
	
				} else if ((context.getHistoricModeAux().substring(0,1).toLowerCase().equals("a") &&
						!ENTITY.STATUS_PENDENT.equals(PropertyUtils.getProperty(entity,ENTITY.STATUS_HISTORIC_PLC))) ||
						context.getOriginalAction().equals(EVENT.SAVE_VERSION)) {
					// Se for lógica de aprovação ou gravação de versão
					// grava somente o registro pendente ou inativo
	
					log.debug("Historic Approval");
	
					Object entityPendentOrVersion = pendencyInsert(entity,entityPrevious);

				    if (logModel.isDebugEnabled())
				       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":verifyApprovalHistoric:pendenteOuVersao:"));

					return entityPendentOrVersion;
	
				}
	
			} else
				logWarning.debug("Entity with historicModeAux equals to null in verifyApprovalHistoric");
			
		    if (logModel.isDebugEnabled())
		       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":verifyApprovalHistoric:null:"));

			return null;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"verifyApprovalHistoric", e }, e,log);
		}

	}

	/**
	 * Publica informações da Entidade recém-aprovado para o ativo. Importante: BigDecimal nulos são convertidos
	 * para Zero nesta versão, devido a bug no ConvertUtils Apache.
	 * @param entityApproved Entidade recém aprovada
	 * @param entidadeAtivo Entidade ativa.
	 */
	protected void approvePublishModifications( Object entityApproved, Object entityActive)
				throws PlcException {

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":approvePublishModifications:"));

		try {

			PlcDateUtilConverter dc = null;
			BigDecimalConverter bdc = null;

		   approvePublishGenerateVersionToActive( entityApproved, entityActive);

		   PlcBaseContextVO context = getContext();
		   
		   if (context.getHistoricPropsAux().equals("")) {

				// Copia todas as informações do Aprovado para o Ativo, mantendo o id e situação
				log.debug("copy information from approved to active");
				Long idAux = (Long) PropertyUtils.getProperty(entityActive,"id");
				bdc = new BigDecimalConverter(new BigDecimal(0));
				ConvertUtils.register(bdc,BigDecimal.class);

		        BeanUtils.copyProperties(entityActive,entityApproved);
		        
		        PropertyUtils.setProperty(entityActive,"id",idAux);
				PropertyUtils.setProperty(entityActive,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_ACTIVE);
				PropertyUtils.setProperty(entityActive,ENTITY.DATE_LAST_UPDATE,new java.util.Date());
			
		   } else {

				String propsToPublish = context.getHistoricPropsAux()+",dateLastUpdate,userLastUpdate";

				if (!approvePublishModificationsComplementPropsApi().equals(""))
					propsToPublish = propsToPublish + ","+ approvePublishModificationsComplementPropsApi();

				if (log.isDebugEnabled())
					log.debug("Publishing content from columns:"+propsToPublish);

				StringTokenizer s = new StringTokenizer(propsToPublish,",");
				while (s.hasMoreTokens()) {
					String prop = s.nextToken();
					if (log.isDebugEnabled())
						log.debug("Publishing prop="+prop+" value="+BeanUtils.getProperty(entityApproved,prop));
						Object obj = BeanUtils.getProperty(entityApproved,prop);
						dc = new PlcDateUtilConverter(obj);
						bdc = new BigDecimalConverter(new BigDecimal(0));
						ConvertUtils.register(bdc,BigDecimal.class);
						ConvertUtils.register(dc,java.util.Date.class);
					try {
						BeanUtils.copyProperty(entityActive,prop,obj);
					} catch (Exception e ) { logWarning.debug("Date Converter is not working!");}
				}

				java.util.Date dataDefault = null;
				dc = new PlcDateUtilConverter(dataDefault);
				ConvertUtils.register(dc,java.util.Date.class);

			}

			approvePublishModificationsAfter(entityApproved,entityActive);

		    if (logModel.isDebugEnabled())
		       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":approvePublishModifications:"));

		} catch (PlcException ePlc) {
			throw ePlc;
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"approvePublishModifications", e }, e, log);
		}

	}
	
	  /**
	 * jCompany. O descendente deve relacionar lista de colunas para serem copiadas do objeto recém-aprovado para
	 * o objeto Ativo. Por default, além das propriedades monitoradas, são copiadas usuarioUltAlteracao e dataUltAlteracao.
	 * @return Relação das propriedades separadas por vírgula. Ex: return "nomeCompleto,situacao".
	 */
	protected String approvePublishModificationsComplementPropsApi() {
		return "";
	}
	

	/**
	 * jCompany. Método para complementação de lógica de publicação de informações de um registro, que pode
	 * ocorrer apos um comando explicito de "Publica Versão" ou após uma "Aprovação".
	 * Importante: Neste ponto, os dados principais já foram copiados.
	 * @param entityJustApproved Entidade que estão sendo aprovado
	 * @param entityActive Entidade para onde estão sendo publicadas as informações
	 */
	protected void approvePublishModificationsAfter( Object entityJustApproved,Object entityActive)  throws PlcException { }

  
	/**
	 * jCompany 3.0 Gera versão para registros A antes da publicação
	 */
	protected void approvePublishGenerateVersionToActive( 
			Object entityApproved, Object entityActive) throws PlcException{

	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":approvePublishGenerateVersionToActive:"));

		if(approvePublishGenerateVersionApi(entityApproved, entityActive)){
			try {
				PlcBaseContextVO context = getContext();
				log.debug("Generating version to active before publishing.");
				//	String filtroSeguranca = context.getFiltroVertical() != null ? context.getFiltroVertical() : "";
				Object entityParent =  PlcEntityHelper.getInstance().cloneEntityWithDetails(context.getDetailNamesPlc(),entityActive, true);
				PropertyUtils.setProperty(entityParent,ENTITY.STATUS_HISTORIC_PLC,ENTITY.STATUS_INACTIVE);
				PropertyUtils.setProperty(entityParent,ENTITY.ID_PARENT,
						PropertyUtils.getProperty(entityActive,"id"));
				PropertyUtils.setProperty(entityParent,ENTITY.DATE_LAST_UPDATE,new Date());
				PropertyUtils.setProperty(entityParent,"id",null);
				IPlcDAO baseDAO = getDAO(entityApproved.getClass());
				baseDAO.insert(entityParent);

			} catch (PlcException ePlc) {
				throw ePlc;
			} catch (Exception e) {
				throw new PlcException("jcompany.error.generic", new Object[] {
						"approvePublishGenerateVersionToActive", e }, e, log);
			}
		}
		
	    if (logModel.isDebugEnabled())
	       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":approvePublishGenerateVersionToActive:"));

	}

	/**
	 * jCompany. Método para sobreposição nos descendentes, para gerar versão do conteúdo inativo ao
	 * publicar nova versão.
	 * @param entity Entidade Atual a ser incluida (sitHistoricoPlc="A")
	 * @param entityPrevious Entidade Anterior a ser incluída como versão (sitHistoricoPlc="I")
	 * @return true se permtir criar versão (default) ou false.
	 */
	protected boolean approvePublishGenerateVersionApi( Object entity, Object entityPrevious) throws PlcException { return true;	}

		
	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - RECUPERA PENDENTES ****************** */
	/* **************************************************************************************** */

    /**
     * Recupera relaçao de objetos Pendentes de Aprovação
	 * @see org.jcompany.model.IPlcManager#retrievePendents(java.lang.Class)
	 */
	  public List retrievePendents( Class clazz) throws PlcException {

		  if (logModel.isDebugEnabled())
		       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrievePendents:"));

		  List listRetrieved = getDAO(clazz).retrieveListCompleteBySituation(clazz,"P");
		  
		  if (logModel.isDebugEnabled())
		       	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrievePendents:"));
		  
		  return listRetrieved;

	  }

	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - RECUPERA INATIVOS ****************** */
	/* **************************************************************************************** */

	/**
	 * Recupera lista de objetos inativos
	 * @see org.jcompany.model.IPlcManager#retrieveInactives(java.lang.Class)
	 */
	 public List retrieveInactives( Class clazz) throws PlcException {
	
		 if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveInactives:"));

		List listRetrieved = getDAO(clazz).retrieveListCompleteBySituation( clazz,"I");
		
		 if (logModel.isDebugEnabled())
			   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveInactives:"));
		 
		return listRetrieved;

	 }


	/* **************************************************************************************** */
	/* ******************************** TEMPLATE METHOD - RECUPERA AGREGADO LOOKUP ************ */
	/* **************************************************************************************** */

	/**
	 * Recupera agregado lookup A
	 * @see org.jcompany.model.IPlcManager#retrieveLookupAggregate(org.jcompany.commons.Object, java.lang.Object)
	 */
    public Object retrieveLookupAggregate( Object entity, Object id) throws PlcException {

    	if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregate:A:"));

    	Object objectRetrieved = retrieveLookupAggregate(entity, null, id);
    
       	if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregate:A:"));

    	return objectRetrieved;
    
    }
    
    
	/**
	 * Recupera objeto agregado/vinculado B
	 * @see org.jcompany.model.IPlcManager#retrieveLookupAggregate(org.jcompany.commons.Object, java.lang.String, java.lang.Object)
	 */
    public Object retrieveLookupAggregate( Object entity, String propertyName, Object value) throws PlcException {

      	if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregate:B:"));

        try {

        	// Pega instância de DAO para recuperar
        	IPlcDAO baseDAO = getDAO(entity.getClass());

        	// Recupera propriedades para lookup, se existirem declaradas no ENTITY.
        	// Se não foram declaradas, deixa nulo para que todas sejam recuperadas
        	String[] props = null;
        	try {
        		props = (String[]) PropertyUtils.getProperty(entity,"lookupPropsPlc");
        	} catch (Exception e){
        		if (logWarning.isDebugEnabled())
        			logWarning.debug("Error trying to retrieve property 'lookupPropsPlc' by reflexion in "+this.getClass().getName());
        	};

        	if (retrieveLookupAggregateBefore(entity,propertyName,props)) {

        		// Recupera
        		entity = baseDAO.retrieveLookupAggregate(entity,propertyName, value,props);

        	}

        	retrieveLookupAggregateAfter(entity,propertyName,props);

        	if (logModel.isDebugEnabled())
        		logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregate:B:"));

        	return entity;

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveLookupAggregate", e }, e,log);
        }

    }
	/**
	 * Recupera agregados/vinculados C
	 * @see org.jcompany.model.IPlcManager#retrieveLookupAggregate(org.jcompany.commons.Object, java.lang.Map)
	 */
    public Object retrieveLookupAggregate(Object entity, Map<String, Object> propertiesValues) throws PlcException{

    	if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveLookupAggregate:C:"));

    	try {

    		// Pega instância de DAO para recuperar
    		IPlcDAO baseDAO = getDAO(entity.getClass());

    		// Recupera
    		entity = baseDAO.retrieveLookupAggregate(entity,propertiesValues);

    	   	if (logModel.isDebugEnabled())
    		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveLookupAggregate:C:"));

    		return entity;

    	} catch (Exception e) {
    		throw new PlcException("jcompany.error.generic", new Object[] {
    				"retrieveLookupAggregate", e }, e,log);
    	}
    }

    
	
  /**
   * jCompany 2.7.3. Evento para implementação, ocorrendo antes da recuperação de classes agregadas (lookup)
   * Nota: no jCompany 3.1 foi adicionado o parâmetro <code>nomePropriedade</code> devido à alteração no método 
   * {@link PlcBaseConfigListener#recuperaAgregadoLookup(Object, String, Object).
   * @param entity Relação de propriedades a serem recuperadas, ou null para todas
   * @return true para recuperar de forma genérica, false para não recuperar, quando for desejado recuperar de forma específica
   */
  protected boolean retrieveLookupAggregateBefore( Object entity,String propertyName, String[] props) throws PlcException {  return true; }

  /**
   * jCompany 2.7.3. Evento para implementação, ocorrendo após a recuperação de classes agregadas (lookup)
   * Nota: no jCompany 3.1 foi adicionado o parâmetro <code>nomePropriedade</code> devido à alteração no método 
   * {@link PlcBaseConfigListener#recuperaAgregadoLookup(Object, String, Object).
   * @param entity Relação de propriedades enviadas originalmente para serem recuperadas, ou null para todas
   */
  protected void retrieveLookupAggregateAfter( Object entity,String propertyName, String[] props) throws PlcException {  }
    
  /* **************************************************************************************** */
  /* ******************************** TEMPLATE METHOD - RECUPERA AGREGADO NAVEGACAO ************ */
  /* **************************************************************************************** */

    /**
     * Recupera agregado para navegacao
	 * @see org.jcompany.model.IPlcManager#retrieveAggregateNavigation(java.lang.Class, java.lang.Object, java.lang.Class)
	 */
    public List retrieveAggregateNavigation(Class classeOrigin, Object pk,Class classDestiny) throws PlcException {

       	if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveAggregateNavigation:"));

        try {

           IPlcDAO baseDAO = getDAO(classDestiny);
           
            List l = null;

            if (retrieveAggregateNavigationBefore(classeOrigin,pk,classDestiny)) {

                // Recupera
                l = baseDAO.retrieveAggregateNavigation(classeOrigin,pk,classDestiny);

            }

            retrieveAggregateNavigationAfter(classeOrigin,pk,classDestiny,l);

            if (logModel.isDebugEnabled())
    		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveAggregateNavigation:"));

            return l;

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "retrieveAggregateNavigation", e }, e,log);
        }

    }

    

    /**
     * jCompany 3.0. Evento para implementação, ocorrendo antes da recuperação de classes via navegação automatizada
      * @param classOrigin
     * @throws PlcException
     */
    protected boolean retrieveAggregateNavigationBefore(Class classOrigin, Object pk, Class classDestiny) throws PlcException {
        return true;
    }


     /**
      * jCompany 2.7.3. Evento para implementação, ocorrendo após a recuperação de classes via navegação automatizada
      * @param classOrigin
      * @param pk
      * @param classDestiny
      * @param l
      * @throws PlcException
      */
    protected void retrieveAggregateNavigationAfter(Class classOrigin, Object pk,
            Class classDestiny,List l) throws PlcException {
    }


	 /* **************************************************************************************** */
	 /* ******************************** TEMPLATE METHOD - MÉTODOS GERAIS ************ */
	 /* **************************************************************************************** */	

	/**
	 * jCompany. Método para complementação de lógica de inclusão de inativos e pendentes de aprovação,
	 * no descendente.
	 * @param entityBase utilizado para clonagem
	 * @param entityInactive entidade inativo antes de ser incluído
	 */
	protected boolean beforeInsertInactivePendent( Object entityBase, Object entityInactive) throws PlcException {return true;}

	/**
	 * jCompany. Método para complementação de lógica de inclusão de inativos e pendentes de aprovação,
	 * no descendente, após a geração da chave do objeto
	 * @param entidadeBase utilizado para clonagem
	 */
	protected void afterInsertInactivePendent( Object entiBa, Object entityInactivePendent) 	throws PlcException {}


 	/**
 	 * jCompany. Método para ser implementado nos descendentes, com lógica 	que devolve true caso
 	 * o registro corrente não deva ser cadastrado como pendente de aprovação (sitHistoricoPlc="P") mas
 	 * definitivo (sitHistoricoPlc="A")
 	 * Por default, o retorno é recuperado do ENTITY, que contém o resultado obtido no Action.
 	 * @param entityPrevious entidade anterior
 	 * @return true se usuario for aprovador (nao grava pendência) ou false se não for aprovador (grava pendência)
 	 */
 	protected boolean apiApproverUser( Object entity, Object entityPrevious) 
 	                     throws PlcException {
 	    try{
 		    PlcBaseContextVO context = getContext();
 	    	return context.isApprover();
 		    
 	    } catch (Exception e) {
 	        throw new PlcException("jcompany.error.generic", new Object[] { "apiApproverUser", e }, e,log);
 	    }
 	}
 	


	/**
	 * jCompany. Método vazio, destinado a ser sobreposto nos descendentes para
	 * a implementação de regras do negócio anteriores à gravação do Value Object.<p>
	 *
	 * Tipicamente, estas regras envolvem:<P>
	 *
	 * - Complementação de Validação de Entrada de Dados, quando a validação necessita
	 * de recuperar dados persistentes ou necessita comparar dados
	 * recuperados com informados: nestas duas situações, a lógica de validação declarativa
	 * da Struts e complementos do jCompany não são suficientes. Importante: validações
	 * de duplicidade não precisam ser programadas (restrições "não deve existir" podem ser
	 * declaradas no struts-config.xml)
	 * ou <br>
	 * - Complementação de dados informados segundo regras do negócio: Muitas vezes, é
	 * necessário complementar o Value Object antes que seja gravado, segundo critérios
	 * do negócio.
	 *
	 * @param entity Referência ao Value Object que será objeto de persistência.
	 * @param entityPrevious Referência ao Value Object recuperado, antes de sofrer alterações.
	 * Importante 1: Se for inclusão, estará nulo.<br>
	 * * @param savingMode Indica se a operação que será chamada será de inclusão, alteração
	 * ou exclusão. Deve-se utilizar as constantes para testar. Exemplos:<p>
	 *     	  "if (modoGravacao.equals(PlcConstantes.MODOS.MODO_EXCLUSAO)) {" <br> ou
	 *  	  "if (modoGravacao.equals(PlcConstantes.MODOS.MODO_EDICAO)) {" <br> ou
	 *        "if (modoGravacao.equals(PlcConstantes.MODOS.MODO_INCLUSAO)) {"
	 * @throws PlcException O desenvolvedor deve tratar a exceção, com o maior nível de detalhe
	 * possível, disparando uma exceção jCompany, no mínimo da seguinte forma:<p>
	 *
	 *	  } catch (Exception ex) {
	 *	   log.error("Erro ao tentar verificar o type de curso="+ex);
	 *	   throw new PlcException("aplicacao.erro.meuerro",new Object[] {ex},ex);
	 *    }
	 */
	protected void beforePersistence( Object entity,
					Object entityPrevious, String savingMode) throws PlcException {
	}


	/**
	 * jCompany 1.5.3. Idêntico ao antesPersistencia, só que ocorre após o arquivo anexado
	 * ter sido criado, em lógica que fazem uso desta facilidade. Permite que se tenha o
	 * Id do arquivo idAttachedFilePlc montado na Entidade
	 */
	protected void beforePersistenceAfterAttachedFile(
				 Object entity, Object entityPrevious, String savingMode) throws PlcException {

		return;

	}

	/**
	 * jCompany. Método vazio, destinado a ser sobreposto nos descendentes para
	 * a implementação de regras do negócio posteriores à gravação do Value Object,
	 * e anteriores ao commmit.<p>
	 *
	 * Tipicamente, estas regras envolvem:<P>
	 *
	 * - Gravação de saldos e consolidados em geral, quando a complementação exige
	 * a chave primária do mestre (não disponível em "antesPersistencia")
	 *
	 * @param entity Referência ao Value Object que será objeto de persistência.
	 * @param entityPrevious Referência ao Value Object recuperado, antes de sofrer alterações.
	 * Importante 1: Se for inclusão, estará nulo.<br>
	 * @param savingMode Indica se a operação que será chamada será de inclusão, alteração
	 * ou exclusão. Deve-se utilizar as constantes para testar. Exemplos:<p>
	 *     	  "if (modoGravacao.equals(PlcConstantes.MODOS.MODO_EXCLUSAO)) {" <br> ou
	 *  	  "if (modoGravacao.equals(PlcConstantes.MODOS.MODO_EDICAO)) {" <br> ou
	 *        "if (modoGravacao.equals(PlcConstantes.MODOS.MODO_INCLUSAO)) {"
	 *
	 * @throws PlcException O desenvolvedor deve tratar a exceção com o maior nível de detalhe
	 *  possível, disparando uma exceção jCompany, no mínimo da seguinte forma:<p>
	 *
	 *	  } catch (Exception ex) {
	 *	   log.error("Erro ao tentar verificar o type de curso="+ex);
	 *	   throw new PlcException("aplicacao.erro.meuerro",new Object[] {ex},ex);
	 *    }
	 */
	protected void afterPersistence( Object entity,
					Object entityPrevious, String savingMode) throws PlcException {
		return;
	}

	/**
	 * Recupera lista para treeview do type explorer
	 * @see org.jcompany.model.IPlcManager#retrieveExplorerList(java.lang.Class, java.lang.Object, java.lang.Class, long)
	 */
	public List retrieveExplorerList(Class classBase, Object id,Class classDescendent,long initialPosition) throws PlcException {
		
      	if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(this.getClass().getSimpleName()+":retrieveExplorerList:"));

		// Lista conterá objetos ou relação de classes
		List listDescendents=null;
		
		if (classDescendent!=null) {
			// É para recuperar dados de classeFilha relacionados ao objeto da classeBase que possui o OID informado
			IPlcDAO baseDAO = getDAO(classDescendent);
			
			listDescendents = baseDAO.retrieveExplorerList(classDescendent,classBase,id,initialPosition); 
		} else {
			// É para recuperar possíveis filhos nos meta-dados, somente
			IPlcDAO baseDAO = getDAO(classBase);
			listDescendents = baseDAO.retrievePossibleDescendents(classBase); 
		}

		if (logModel.isDebugEnabled())
		   	logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(this.getClass().getSimpleName()+":retrieveExplorerList:"));

		return listDescendents;	
		
	}  	
  	
    
}
