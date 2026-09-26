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
package org.jcompany.facade;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ENTITY;
import org.jcompany.commons.PlcConstantsCommons.DEFAULT_LOGIC.EXCLUSION;
import org.jcompany.commons.aop.PlcAopProfilingHelper;
import org.jcompany.commons.facade.IPlcAdmFacade;
import org.jcompany.commons.facade.IPlcFacade;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.commons.helper.PlcReflectionHelper;
import org.jcompany.model.IPlcManager;
import org.jcompany.model.PlcBaseManager;
import org.jcompany.model.PlcModelLocator;
import org.jcompany.model.annotation.PlcTransactionNotApply;
import org.jcompany.model.annotation.PlcTransactionPersist;
import org.jcompany.model.annotation.PlcTransactionRead;
import org.jcompany.model.service.IPlcPhoneticService;
import org.jcompany.persistence.IPlcDAO;
import org.jcompany.persistence.PlcPersistenceLocator;
import org.jcompany.persistence.hibernate.PlcBaseHibernateDAO;
import org.jcompany.persistence.hibernate.adm.PlcSchemaUpdate;
import org.jcompany.persistence.hibernate.service.PlcQBEHibernateService;


/**
 * DP Façade. Implementação da Interface IPlcFacade
 * <p>
 * Importante: Deve fechar sessões de persistencia antes de encerrar
 * <p>
 * 
 * Importante 2: Descendentes desta classe não devem conter regras do negócio.
 * Esta classe somente serve de adapter entre a camada de controle e as lógicas
 * do negócio dos Business Componentes: Business Objects (BOs) ou Application
 * Services (ASs).
 * 
 * @since jCompany 3.0
 * @version $Id: PlcFacadeImpl.java,v 1.4 2006/05/17 20:47:40 rogerio_baldini
 *          Exp $
 */

@Stateless(name="PlcFacadeImpl")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PlcFacadeImpl extends PlcBaseFacadeImpl implements IPlcFacade, IPlcAdmFacade {

	protected static Logger log = Logger.getLogger(PlcFacadeImpl.class);
	
	
	IPlcDAO plcDAO;
	IPlcManager plcManagerDefault;
	
	@Override
	protected IPlcDAO getDAO(Class classDAOorEntity) throws PlcException {
		// Testa se o DAO foi injetado pelo container
		if(plcDAO  != null) {
			return plcDAO;
		}
		//Caso não tenha sido injetado, usa o código do ansestral para localizar uma implementação
		return super.getDAO(classDAOorEntity);
	}
	
	/**
	 * Placeholder para a classe PlcJSecurityBO. Se a aplicação estiver
	 * configurada para utilizar o JSecurity, será armazenada a classe nesssa
	 * propriedade para evitar chamadas repetidas ao Class.forName().
	 * 
	 * @since jCompany 3.0
	 */
	private static Class classPlcJSecurityManager;
	
	public PlcFacadeImpl() {}

	/**
	 * Limpa os cachings dos Service Locators para BC e DAO
	 * @since jCompany 3.02
	 */
	@PlcTransactionNotApply
	public void clearCacheIoC() throws PlcException {
		PlcModelLocator.getInstance().clearCache();
		PlcPersistenceLocator.getInstance().clearCache();

	}
	
	/**
	 * Gera um esquema DDL comparando com o SGBD
	 * @since jCompany 3.0 
	 */
	@PlcTransactionRead
	public String generateSchema(PlcBaseContextVO context, String typeAction,
			String objTable, String objConstraint, String objSequence,String objIndex,
			String delimiter) throws PlcException {
		PlcSchemaUpdate managerSchema = (PlcSchemaUpdate) getManager(PlcSchemaUpdate.class);
		PlcPersistenceLocator pl = PlcPersistenceLocator.getInstance();
		IPlcDAO daoPrevious = pl.getDaoDefault();
		pl.setDaoDefault((IPlcDAO) new PlcBaseHibernateDAO(new PlcQBEHibernateService()));
		String ddl = managerSchema.generate(typeAction, objTable, objConstraint,objSequence,objIndex, delimiter);
		pl.setDaoDefault(daoPrevious);
		return ddl;

	}

	/**
	 * Submete um esquema DDL para o SGBD
	 * @since jCompany 3.0 
	 */
	@PlcTransactionPersist
	public void executeSchema(PlcBaseContextVO context, String schema,
			String delimiter) throws PlcException {
		PlcSchemaUpdate managerSchema = (PlcSchemaUpdate) getManager(PlcSchemaUpdate.class);
		PlcPersistenceLocator pl = PlcPersistenceLocator.getInstance();
		IPlcDAO daoPrevious = pl.getDaoDefault();
		pl.setDaoDefault((IPlcDAO) new PlcBaseHibernateDAO(new PlcQBEHibernateService()));
		managerSchema.execute(schema, delimiter);
		pl.setDaoDefault(daoPrevious);
	}

	/**
	 * jCompany 3.0 Recupera relação de registros para auditoria, de forma
	 * genérica TODO Atualizar utilitário auditoria
	 * 
	 * @param id
	 *            Objeto podendo conter Long (oid) ou Classe da PK (Na versão
	 *            3.0, somente OID funciona)
	 * @param clazz
	 *            classe para a qual a auditoria será recuperada
	 */
	@PlcTransactionNotApply
	public List<Object> retrieveAudit(PlcBaseContextVO context, Class<Object> clazz,
			Object id) throws PlcException {

		return (List<Object>) executeOperation(context, PlcBaseManager.class,
				"retrieveAudit",
				new Object[] { (Long) id, clazz.getName() }, false);
	}

	/**
	 * Design Pattern: Template Method. Os métodos com prefixo "antes", "apos"
	 * ou "api" são eventos (método vazios) destinados a especializações nos
	 * descendentes.
	 * <p>
	 * Grava um Value Object e seus detalhes, gerenciando transação segundo com
	 * utilização da Hibernate. Segue o seguinte roteiro:
	 * <p> - beginTransaction(vo.getFabricaPlc()): se estiver usando transação
	 * via Hibernate, inicializa Transação e Sessão.
	 * <p> - verificaHistoricoAprovacao(sess,baseBO, vo, voAnt): se gravação não
	 * for simples (caso de versionamento, aprovação ou reprovação, em lógicas
	 * de workflow) então verifica se irá gerar registros inativos
	 * (sitHistoricoPlc='I') e outros comportamentos pertinentes a estas
	 * operações de gravação especiais.
	 * <p> - se não for gravação de pendência (registro pendente de aprovação ou
	 * reprovação), então entra para verificar qual operação de gravação
	 * realizar:
	 * <p> - baseBO.verificaAcaoAprovacao(sess,vo,voAnt): se for operação de
	 * aprovação ou reprovação em workflow, chama método específico para
	 * considerar as ações<br> - baseBO.inclui(sess,vo): Se for operação de
	 * gravação simples e voAnt for nulo, então assume inclusão<br> -
	 * baseBO.altera(sess,vo,voAnt): Se for operação de gravação simples e voAnt
	 * for informado, então assume alteração
	 * <p> - commit(sess,tx): se tudo ocorreu ok (nenhuma exceção), efetua
	 * fechamento com commit, encerramento de sessão Hibernate e também
	 * devolução de conexão JDBC para pool
	 * <p> - rollback(sess,tx): se alguma exceção foi dispara, efetua fechamento
	 * com rollback, encerramento de sessão Hibernate e também devolução de
	 * conexão JDBC para pool
	 * 
	 * @param entity
	 *            Value Object a ser gravado
	 * @param entityPrevious
	 *            Value Object anterior (o jCompany mantém em sessão a última
	 *            versão recuperada do Value Object, para possibilitar seu envio
	 *            juntamente com as modificações nesta operação. Deste modo,
	 *            pode-se fazer lógicas que considerem a alteração efetuada ou
	 *            de auditoria, com imagens de dados antes e depois das
	 *            alterações).<br>
	 *            Importante: Se for inclusão ou exclusão este objeto é enviado
	 *            como NULL.
	 * 
	 * @throws PlcException
	 *             Exceções devem ser tratadas e retornadas como uma
	 *             PlcException, no padrão do jCompany, para tratamento genérico
	 *             e exibição para usuário.
	 */
	@PlcTransactionPersist
	public Object saveObject(PlcBaseContextVO context, Object entity,
			Object entityPrevious) throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":saveObjeto(@PlcTransactionPersist):"));

		setContext(context);
		
		IPlcManager baseManager = getManager(entity.getClass());

		try {

			Object entityPendent = null;

			// Se for gravação convencional e tiver modo historico ou
			// Se for publicação de inativo ou
			// Se for gravação de versão
			if ((context.getOriginalAction().equals(
					PlcConstantsCommons.EVENT.SAVE)
					&& !context.getHistoricModeAux().equals("") && !ENTITY.STATUS_INACTIVE
					.equals(PropertyUtils.getProperty(entity, ENTITY.STATUS_HISTORIC_PLC)))
					|| (context.getOriginalAction().equals(
							PlcConstantsCommons.EVENT.PUBLISH_VERSION)
							&& ENTITY.STATUS_INACTIVE.equals(PropertyUtils.getProperty(
									entity, ENTITY.STATUS_HISTORIC_PLC)) && context.isApprover())
					|| context.getOriginalAction().equals(PlcConstantsCommons.EVENT.SAVE_VERSION)) {
				// Grava pendencias
				log.debug("Vai verificar historico de aprovacao");
				entityPendent = baseManager.verifyApprovalHistoric(entity, entityPrevious);
			}

			// Somente entra se não for gravação de pendência de aprovação ou
			// gravação de versão,
			// caso em que já houve gravação da pendência
			if (entityPendent == null) {

				if (PropertyUtils.isReadable(entity, ENTITY.ID_PARENT)
						&& PropertyUtils.getProperty(entity, ENTITY.ID_PARENT) != null
						&& ((Long) PropertyUtils.getProperty(entity, ENTITY.ID_PARENT) == 0)) {
					PropertyUtils.setProperty(entity, ENTITY.ID_PARENT, null);
				}

				// Inclusão e Alteração Padrões
				if (entityPrevious == null) {

					baseManager.insert(entity);

				} else {

					if (!context.getOriginalAction().equals(PlcConstantsCommons.EVENT.APPROVE)
							&& !context.getOriginalAction().equals(PlcConstantsCommons.EVENT.REPROVE)
							&& !context.getOriginalAction().equals(PlcConstantsCommons.EVENT.PUBLISH_VERSION)) {

						entity = baseManager.update(entity, entityPrevious);

					} else if (ENTITY.STATUS_PENDENT.equals(PropertyUtils.getProperty(entity, ENTITY.STATUS_HISTORIC_PLC))
							|| ENTITY.STATUS_INACTIVE.equals(PropertyUtils.getProperty(entity, ENTITY.STATUS_HISTORIC_PLC))) {

						baseManager.approve(entity, entityPrevious);

					} else {
						throw new PlcException("jcompany.errors.approve");
					}

				}

			} else {
				// Troca o registro oficial pelo de Pendencia, para alterações
				// finais pelo usuário
				entity = entityPendent;
			}

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":saveObjeto(@PlcTransactionPersist):"));

			return entity;

		} catch (Exception e1) {
			throw new PlcException("jcompany.errors.persistence.save",
					new Object[] { e1 }, e1, log);
		}

	}



	/**
	 *  Configura todo o contexto EJB setando o BOpadrao
	 */
	@PlcTransactionNotApply
	public void setDefaultEJBManager() throws PlcException {
		//Testa se um BO EBJ e um AppEjbLocator foram injetados pelo container
		if( (null != plcManagerDefault)) { 
			//Se foi vamos registra-lo como padrao
			PlcModelLocator.getInstance().setManagerDefault(plcManagerDefault);
			PlcPersistenceLocator.getInstance().setDaoDefault(plcDAO);
		} else {
			throw new PlcException("jcompany.registry.error.managerejb");
		}
	}

	/**
	 * @since jCompany 3.0
	 * 
	 * Grava Coleção de Objetos (Lógica Tabular) em uma única transação.
	 * <p>
	 * 
	 * Grava uma coleção (top-level collection) de Value Objects segundo o
	 * mapeamento OO x SGBD-R da Hibernate
	 * 
	 * É utilizada em lógicas de manutenção tabular, somente, que são coleções
	 * "top-level". Importante: Muito embora lógicas mestre-detalhe contenham
	 * coleções de detalhes para um Mestre, estas lógicas são gravadas através
	 * da operação gravaObjeto, pois estão agregadas a um Value Object mestre.
	 * 
	 * @param list
	 *            Lista de Value Objects a serem gravados
	 * @param listPrevious
	 *            Lista de Value Objects anteriores (o jCompany mantém em sessão
	 *            a última versão recuperada da lista de Value Objects, para
	 *            possibilitar seu envio juntamente com as modificações nesta
	 *            operação. Deste modo, pode-se fazer lógicas que considerem a
	 *            alteração efetuada ou de auditoria, com imagens de dados antes
	 *            e depois das alterações).<br>
	 * 
	 * @throws PlcException
	 *             Exceções devem ser tratadas e retornadas como uma
	 *             PlcException, no padrão do jCompany, para tratamento genérico
	 *             e exibição para usuário.
	 */
	@PlcTransactionPersist
	public void saveTabular(PlcBaseContextVO context, Class clazz,
			List list, List listPrevious) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":saveTabular(@PlcTransactionPersist):"));

		setContext(context);
		
		IPlcManager baseManager = (IPlcManager) getManager(clazz);

		Object entity = null;
		try {

			Iterator i = list.iterator();

			// se lista não está vazia
			while (i.hasNext()) {

					entity = (Object) i.next();

					Object[] ret = verifyOperation(entity, listPrevious);
					String oper = (String) ret[0];
					Object plcentityPrevious = null;

					if (log.isDebugEnabled())
						log.debug("before receive preview entity with operation=" + oper);
					if (ret[1] != null)
						plcentityPrevious = ret[1];

					if (oper.equals("E")) {
						if (log.isDebugEnabled())
							log.debug("Decided to exclude for " + entity);
						baseManager.exclude(entity);
					} else if (oper.equals("I")) {
						if (log.isDebugEnabled())
							log.debug("Decided to insert for " + entity);
						baseManager.insert(entity);
					} else if (oper.equals("A")) {
						if (log.isDebugEnabled())
							log.debug("Decided to alter for " + entity);
					
						/* TODO Rever uma configuração para utilizar a otimização. Atenção: antes ficava no hiberante.cfg.xml
						return "S".equals(getCfg().getProperties().get(PlcConstantsPersistence.GLOBAL.UPDATE_OPTIMIZE));
						if (!isOtimizaUpdate(vo) || !vo.equals(plcVoAnt))
						 inclui no anterior para lógica de otimizaçao de  
						getContext().setVoAnterior(plcVoAnt); */
							baseManager.update(entity, plcentityPrevious);
					}

					log.debug("passes saving iteration");

			}

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":saveTabular(@PlcTransactionPersist):"));

		} catch (Exception e1) {
			throw new PlcException("jcompany.errors.persistence.save",
					new Object[] { e1 }, e1, log);
		}

	}

	/**
	 * Verifica se tem opção de otimização de update e lembraDetalhes, global ou específico.
	 * @param entity ENTITY principal
	 */
	@PlcTransactionNotApply
	protected boolean isOptimizeUpdate(Object entity) throws PlcException {
		return (getDAO(entity.getClass()).isOptimizeUpdate() && "S".equals(getContext().getDetailRemember()));
	}

	/**
	 * @since jCompany 3.0 Recupera Lista A
	 * 
	 * Recupera lista de objetos utilizando lógica QBE automática do jCompany.
	 * Recebe orderByDinamico pois usuário pode alterá-lo. A lista de argumentos
	 * contém VOs com valores informados na entrada de dados e operadores.
	 */
	@PlcTransactionRead
	public List retrieveList(PlcBaseContextVO context, Class clazz,
			String orderByDynamic, List<PlcArgEntity> args) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)A:"));

		try {
			
			setContext(context);

			List listRetrieved = getManager(clazz).retrieveListQBE(clazz, orderByDynamic, args);
			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)A:"));
			
			return listRetrieved;

		} catch (Exception e1) {
			throw new PlcException(
					"jcompany.errors.persistence.retrieve.list",
					new Object[] { e1 }, e1, log);
		}
	}

	/**
	 * @since jCompany 1.0 . Recupera listas simples, que são tabelas básicas
	 *        com poucos registros e de uso abrangente (tipicamente combos) por
	 *        toda a aplicação. A implementação deve conter lógica de caching
	 *        para estas tabelas, "thread-safe" e homologada para clusters
	 *        <P>
	 * 
	 * Esta operação é chamada no início de cada sessão, para as classes
	 * declaradas no web.xml como classes de lookup.
	 * <p>
	 * 
	 * Importante: Esta operação não deve ser utilizada para tabelas com muitos
	 * registros.<br>
	 */
	@PlcTransactionRead
	public List retrieveSimpleList(PlcBaseContextVO context, Class clazz,
			String orderByDynamic) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveSimpleList(@PlcTransactionRead):"));

		try {
			
			setContext(context);
			
			List listRetrieved = getDAO(clazz).retrieveAll(clazz, orderByDynamic);
			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveSimpleList(@PlcTransactionRead):"));

			return listRetrieved;

		} catch (Exception e) {
			throw new PlcException(
					"jcompany.errors.persistence.retrieve.simple.list",
					new Object[] { clazz.getName(), e }, e, log);
		}

	}

	/**
	 * @since jCompany 3.0 Recupera Lista B (paginada - argumentos por objeto)
	 * 
	 * Recupera lista de objetos
	 */
	@PlcTransactionRead
	public List retrieveList(PlcBaseContextVO context, Class clazz,
			String orderByDynamic, Object argsQBE, int firstLine,
			int maximumLines) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)B:"));

		try {
			
			setContext(context);
			
			List listRetrieved = getManager(argsQBE.getClass()).retrieveList(clazz, orderByDynamic, argsQBE,
					firstLine, maximumLines);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)B:"));

			return listRetrieved;

		} catch (Exception e) {
			throw new PlcException(
					"jcompany.errors.persistence.retrieve.list.query",
					new Object[] { argsQBE, e }, e, log);
		}

	}

	/**
	 * Recupera Lista de VOs trazendo páginas, a partir de argumentos informados
	 * 
	 * @since jCompany 3.0 Recupera Lista C (paginada - argumentos por Lista)
	 * 
	 */
	@PlcTransactionRead
	public List retrieveList(PlcBaseContextVO context, Class clazz,
			String orderByDynamic, List<PlcArgEntity> args, int firstLine,
			int maximumLines) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)C:"));
		
		List list = null;

		try {
			
			setContext(context);
			
			list = getManager(clazz).retrieveList(clazz, orderByDynamic, args,
					firstLine, maximumLines);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)C:"));

			return list;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveList", e }, e, log);
		}

	}

	/**
	 * jCompany. Recupera lista de objetos D
	 * <p>
	 * 
	 * Recupera baseado em uma coleção de argumentos e respectivos tipos. Este
	 * método deve ser utilizado quando for necessário passar um argumentos de
	 * uma classe agregados. Ex: "obj.tipo.id=?"
	 * <p>
	 * 
	 * Exemplo:
	 * <p>
	 * String oqlDeclarado em anotaçoes = "from obj in class
	 * com.powerlogic.jbatch.vo.PlcHistorico where obj.paiPlc = ? and
	 * obj.dataHora = ?";<br>
	 * Object[] arg = { objVO.getPaiPlc().getId(),
	 * ((PlcHistorico)objVO).getDataHora()};<br>
	 * Object[] type = { "LONG", "TIMESTAMP" };<br>
	 * List ret = appFac.recuperaLista(objVO, hql, arg, type, "");<br>
	 * 
	 * @param args
	 *            Coleção de argumentos de tipos primitivos
	 * @param types
	 *            Coleção de String com nome dos tipos dos argumentos, podendo
	 *            ser: STRING, LONG, TIMESTAMP, DATE, INTEGER
	 * @throws PlcException
	 *             Exceções devem ser tratadas e retornadas como uma
	 *             PlcException, no padrão do jCompany, para tratamento genérico
	 *             e exibição para usuário. Uma exceção deve ser disparada caso
	 *             nenhuma ocorrência seja encontrada.
	 */
	@PlcTransactionRead
	public List retrieveList(PlcBaseContextVO context, Class clazz,
			String orderByDynamic, Object[] args, String[] types)
			throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)D:"));
		
		setContext(context);
		
		IPlcManager baseManager = getManager(clazz);

		try {

			List listRetrieved = baseManager.retrieveList(clazz, orderByDynamic, args, types);
			
			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveList(@PlcTransactionRead)D:"));
			
			return listRetrieved;

		} catch (Exception e) {
			throw new PlcException(
					"jcompany.errors.persistence.retrieve.list.query",
					new Object[] { clazz.getName(), e }, e, log);
		}

	}

	/**
	 * jCompany. Recupera um Value Object e todas as suas classes agregadas (ex:
	 * listas one-to-many em detalhes de lógica Mestre-Detalhe, classes
	 * many-to-one), a partir de sua chave primária
	 * <P>
	 * 
	 * @throws PlcException
	 *             Exceções são tratadas e retornadas como uma PlcException, no
	 *             padrão do jCompany, para tratamento genérico e exibição para
	 *             usuário. Uma exceção deve ser disparada caso a ocorrência
	 *             esperada não seja encontrada. (tipicamente devido ao filtro
	 *             de segurança ou concorrência entre usuários)
	 */
	@PlcTransactionRead
	public Object[] retrieveObject(PlcBaseContextVO context, Class clazz,
			Object id) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retriveObject(@PlcTransactionRead):"));

		setContext(context);
			
		try {
			
			Object[]  ret = getManager(clazz).retrieve(clazz, id); 

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retriveObject(@PlcTransactionRead):"));

			return ret;

		} catch (Exception e) {
			throw new PlcException(
					"jcompany.errors.persistence.retrieveObject",
					new Object[] { e }, e, log);
		}

	}

	/**
	 * jCompany. Exclui um Value Object e todas as classes agregadas (detalhes)
	 * mapeados como cascade="true", a partir de sua identificação em id. Deve
	 * receber os VOs atual e anterior, porque a exclusão pode ser lógica
	 * (alteração para sitHistoricoPlc='I')
	 * <P>
	 * 
	 * @param entity
	 *            Value Object da classe específica, descendente de PlcBaseEntity, a
	 *            ser excluido.
	 * 
	 * @throws PlcException
	 *             Exceções Tratada e retornadas como uma PlcException, no
	 *             padrão do jCompany, para tratamento genérico e exibição para
	 *             usuário. Uma exceção deve ser disparada caso a ocorrência
	 *             esperada não seja encontrada. (tipicamente devido ao filtro
	 *             de segurança ou concorrência entre usuários)
	 */
	@PlcTransactionPersist
	public void excludeObject(PlcBaseContextVO context, Object entity)
			throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":excludeObject(@PlcTransactionPersist):"));
		
		Object entityPrevious = null;

		try {

			setContext(context);
			
			IPlcManager baseManager = (IPlcManager) getManager(entity.getClass());

			if (EXCLUSION.LOGICAL_MODE.equals(context.getExcludeModeAux())
					|| PlcAnnotationHelper.getInstance()
							.existAnnotationToLogicalExclusion(entity.getClass())) {
				log.debug("Entered to logical exclusion");
				entityPrevious = BeanUtils.cloneBean(entity);
				PlcEntityHelper.getInstance().updateStatusAll(context, entity,ENTITY.STATUS_INACTIVE);
				baseManager.update(entity, entityPrevious);
			} else
				baseManager.exclude(entity);


			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":excludeObject(@PlcTransactionPersist):"));

		} catch (Exception e) {
			throw new PlcException(
					"jcompany.errors.persistence.excludeObject",
					new Object[] { e }, e, log);
		}

	}

	/**
	 * jCompany. Baixa um arquivo anexado gravado em SGBD, via Hibernate.
	 * 
	 * Importante: O upload é realizado automaticamente em um conceito
	 * "mestre-detalhe", ou seja, com o arquivo agregado a um Value Object
	 * principal, no mesmo evento de gravação (botão Grava). Já o evento de
	 * download deve ser feito á parte, para evitar recuperação desnecessária de
	 * arquivos.
	 * 
	 * @throws PlcException
	 *             Exceções são retornadas como uma PlcException, no padrão do
	 *             jCompany, para tratamento genérico e exibição para usuário.
	 */
	@PlcTransactionRead
	public IPlcFileEntity downloadFile(PlcBaseContextVO context, Class clazz,
			Object id) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":downloadFile(@PlcTransactionRead):"));

		IPlcFileEntity vo = null;

		try {
			
			setContext(context);
			
			IPlcManager baseManager = getManager(clazz);

			if (id instanceof Long) {
				Object[] ret = baseManager.retrieve(clazz, id);
				vo = (IPlcFileEntity) ret[0];
			} else {
				// Se for url, dispensa BO para download
				IPlcDAO baseDAO = getDAO(clazz);
				vo = (IPlcFileEntity) baseDAO.retrieveEntityFileByUrl(clazz,(String) id);
			}	

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":downloadFile(@PlcTransactionRead):"));

			return vo;

		} catch (Exception e) {
			throw new PlcException("jcompany.errors.persistence.download",
					new Object[] { e }, e, log);
		}

	}

	/**
	 * Recupera Total A
	 * @return Devolve o total de registros para uma consulta paginada
	 */
	@PlcTransactionRead
	public Integer retrieveTotal(PlcBaseContextVO context, Class clazz,
			Object argsQBE) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveTotal(@PlcTransactionRead)A:"));

		try {
		  	
			setContext(context);
		  	
			Integer total = getDAO(argsQBE.getClass()).retrieveListQBETotal(clazz, argsQBE);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveTotal(@PlcTransactionRead)A:"));

			return total;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"recuperaTotalRegistros", e }, e, log);
		}

	}

	/**
	 * Recupera Total B
	 * @return Recupera total de registros para consulta paginada
	 */
	@PlcTransactionRead
	public Integer retrieveTotal(PlcBaseContextVO context, Class<Object> clazz,
			List<PlcArgEntity> argsQBE) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveTotal(@PlcTransactionRead)B:"));

		try {
		    	setContext(context);

		    	
		    	Integer total = getDAO(clazz).retrieveListQBETotal(clazz, argsQBE);

				if (logModel.isDebugEnabled())
					logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
							this.getClass().getSimpleName()+":retrieveTotal(@PlcTransactionRead)B:"));

				return total;
				
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveTotal", e }, e, log);
		}

	}

	
	/**
	 * Recupera agregado lookup B
	 */
	@PlcTransactionRead
	public Object retrieveLookupAggregate(PlcBaseContextVO context,
			Object entity, String propertyName, Object value) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retriveLookupAggregate(@PlcTransactionRead)B:"));

		try {
			
			setContext(context);
			
			// Chama template method
			Object entityRetrieved = getManager(entity.getClass()).retrieveLookupAggregate(entity, propertyName, value);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retriveLookupAggregate(@PlcTransactionRead)B:"));

			return entityRetrieved;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"retriveLookupAggregate", e }, e, log);
		}

	}
	
	/**
	 * Recupera agregado lookup C
	 */
	@PlcTransactionRead
	public Object retrieveLookupAggregate(PlcBaseContextVO context, Object entity, Map<String, Object> propertiesValues) throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retriveLookupAggregate(@PlcTransactionRead)C:"));
		
		try {
			
			setContext(context);
			
			// Chama template method
			 Object entityRetrieved = getManager(entity.getClass()).retrieveLookupAggregate(entity, propertiesValues);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retriveLookupAggregate(@PlcTransactionRead)C:"));
			
			return entityRetrieved;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retriveLookupAggregate", e }, e, log);
		}
		
	}
	
	/**
	 * @since jCompany 2.7.3.
	 * 
	 * Recupera por navegação de um Vo para outro
	 */
	@PlcTransactionRead
	public List retrieveNavigation(PlcBaseContextVO context,
			Class classOriginPlc, Object pk, Class classDestinyPlc)
			throws PlcException {
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveNavigation(@PlcTransactionRead):"));

		try {
			setContext(context);

			if (!(pk instanceof Long))
				throw new PlcException("jcompany.aggregate.lookup.only.oid");

			// Chama template method
			List l = getManager(classDestinyPlc).retrieveAggregateNavigation(classOriginPlc, pk,
					classDestinyPlc);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":retrieveNavigation(@PlcTransactionRead):"));

			return l;

		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrieveNavigation", e }, e, log);
		}
	}

	/**
	 * Recupera Anotações contendo número de sub-reports
	 * TODO Rever esta prática
	 */
	@PlcTransactionNotApply
	public int retrieveSubReportsAmount(PlcBaseContextVO context,Class clazz) throws PlcException {
	    	setContext(context);
	    	
		return getDAO(PlcBaseHibernateDAO.class).retrieveSubReportsAmount(
				clazz);
	}

    /**
	 * Se a aplicação estiver configurada para utilizar o jSecurity, carrega o
	 * profile do usuário.
	 * 
	 * @since jCompany 3.0
	 * 
	 * @param context
	 * @return Perfil do usuário configurado.
	 * @throws PlcException
	 */
	@PlcTransactionPersist
	public PlcBaseUserProfileEntity loadJSecurityProfile(PlcBaseContextVO context) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":loadJSecurityProfile(@PlcTransactionPersist):"));

		if (context==null) {
			context = getContext();
			if (context==null) {
				throw new NullPointerException("It's not possible retrieve aplication context!");
			}
		} else {
			setContext(context);
		}

		Class classeManager = getClassPlcJSecurityManager();

		PlcBaseUserProfileEntity profile = (PlcBaseUserProfileEntity) PlcReflectionHelper.getInstance().
												executeMethod(getManager(classeManager), "loadUserProfile",
				new Object[] { context },
				new Class[] { PlcBaseContextVO.class });

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
					this.getClass().getSimpleName()+":loadJSecurityProfile(@PlcTransactionPersist):"));

		return profile;
	}

	/**
	 * Se a aplicação estiver configurada para utilizar o jSecurity, recupera o
	 * cadastro básico do usuário.
	 * 
	 * @since jCompany 3.x
	 * @param context
	 * @return
	 * @throws PlcException
	 */
	@PlcTransactionRead
	public Serializable retrieveJSecurityUser(PlcBaseContextVO context) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveJSecurityUser(@PlcTransactionRead):"));
		
		if (context==null) {
			context = getContext();
			if (context==null) {
				throw new NullPointerException("It's not possible retrieve aplication context!");
			}
		} else {
			setContext(context);
		}

		Class classManager = getClassPlcJSecurityManager();
		Object jsBO = getManager(classManager);

		Serializable usuario = (Serializable)  PlcReflectionHelper.getInstance().
							executeMethod(jsBO, "retrieveUser",
											new Object[] { context },
											new Class[] { PlcBaseContextVO.class });	
	    		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
					this.getClass().getSimpleName()+":retrieveJSecurityUser(@PlcTransactionRead):"));

	    return usuario;
	}
	
	/**
	 * Recupera a classe BO do jSecurity, caso esteja configurado.
	 * 
	 * @return
	 * @throws PlcException
	 */
	@PlcTransactionNotApply
	private Class getClassPlcJSecurityManager() throws PlcException {
    	if (classPlcJSecurityManager == null) {
			try {
				classPlcJSecurityManager = Class.forName("org.jcompany.jsecurity.modelo.PlcJSecurityBO");
			} catch (ClassNotFoundException e) {
				throw new PlcException(e);
			}
		}
    	return classPlcJSecurityManager;
	}
    	
    /**
     * Recupera lista de exploração a partir de uma classe e OID
     * @since jCompany 3.03
     * @param classe Classe-Base 
     * @param id Identificador (até esta versão somente OID)
     * @return Lista valores de detalhamento para o objeto
     */
    @PlcTransactionRead
	public List retrieveExplorerList(PlcBaseContextVO context, Class classBase, Object id,
			Class classDescendent, long initialPosition) throws PlcException {

    	if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveExplorerList(@PlcTransactionRead):"));
    	
   		setContext(context);

		List listRetrieved = getManager(classBase).retrieveExplorerList(classBase,id,classDescendent,initialPosition);
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
					this.getClass().getSimpleName()+":retrieveExplorerList(@PlcTransactionRead):"));

		return listRetrieved;
	}
    
    /**
	 * Transforma uma string em sua representação fonetica se existir uma
	 * implementação para o servico de fonetização.
	 * @since jCompany 3.1
	 * @param pattern String que será fonetizada
	 * @return retorna a representação fonetica da string, caso não exista uma
	 * implementação do serviço de fonetização registrado returno null
	 * @throws PlcException
	 */
    @PlcTransactionRead
	public String phonetics(String pattern) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":phonetics(@PlcTransactionRead):"));

    	IPlcPhoneticService fon = null;
		
		try {
			
			fon = (IPlcPhoneticService)PlcModelLocator.getInstance().get(IPlcPhoneticService.class);
			
			String phoneticsValue = fon.phonetics(pattern);

			if (logModel.isDebugEnabled())
				logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
						this.getClass().getSimpleName()+":phonetics(@PlcTransactionRead):"));
			
			return phoneticsValue;
			
		} catch (Exception e) {
			// tratamento esperado caso não encontre uma implementacao
			if (logAdvertencia.isDebugEnabled()) {
				logAdvertencia.debug("Error on trying finding phonetics routine: "+e);
				e.printStackTrace();
			}
			return pattern;
		}
		
	}

	/**
	 * @see org.jcompany.commons.facade.IPlcFacade#retrieveObject(org.jcompany.commons.PlcBaseContextVO, org.jcompany.commons.PlcBaseEntity, java.lang.String)
	 */
	@PlcTransactionRead
	public Object retrieveOnDemand(PlcBaseContextVO context, Object entity, 
			String propertyNameDetail,Class classDetail) throws PlcException {

		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogInitial(
					this.getClass().getSimpleName()+":retrieveOnDemand(@PlcTransactionRead):"));

		setContext(context);

		Object entityWithDetail = getDAO(entity.getClass()).retrieveOnDemand(entity,propertyNameDetail,classDetail);
		
		if (logModel.isDebugEnabled())
			logModel.debug(PlcAopProfilingHelper.getInstance().showLogFinal(
					this.getClass().getSimpleName()+":retrieveOnDemand(@PlcTransactionRead):"));
		
		return entityWithDetail;
		
	}




}