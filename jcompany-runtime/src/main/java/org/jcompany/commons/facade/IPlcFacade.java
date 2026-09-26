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
package org.jcompany.commons.facade;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcException;


/**
 * jCompany. Façade. Interface com a LAYER Modelo
 *
 * Interface que serve de "contrato" entre a camada de controle e a de modelo, deste
 * modo provendo um isolamento simples e permitindo a codificação com segurança de
 * diversas "implementações" de camada modelo para a mesma camada controle.
 *
 * O jCompany possui duas implementações desta interface, uma com gravações para a
 * framework de persistência Hibernate e outra de Simulação de Persitência, que faz
 * a gravação em memória RAM e em arquivo convencional
 * 
 * Revisão: @author Pedro Henrique
 * 	Adicionando método recuperaAgregadoLookup com Varargs para facilitar a recuperação com várias propriedades
 * 
 */
@Local
public interface IPlcFacade {

    /**
  	 * @since jCompany.
  	 * Grava um Value Object e seus detalhes, em uma única transação.
  	 * A implementação desta operação deve gravar o Value Object recebido em algum mecanismo
  	 * de persistência (SGBD, LDAP, File System, RAM, etc.), para posterior recuperação.
  	 *
  	 * @param entity Value Object a ser gravado
  	 * @param entityPrevious Value Object anterior (o jCompany mantém em sessão a última versão
  	 *                   recuperada do Value Object, para possibilitar seu envio juntamente
  	 *                   com as modificações nesta operação. Deste modo, pode-se fazer lógicas
  	 *                   que considerem a alteração efetuada ou de auditoria, com imagens de
  	 *                   dados antes e depois das alterações).<br>
  	 *                   Importante: Se for inclusão ou exclusão este objeto é enviado como NULL.
  	 *
  	 * @return PlcBaseEntity Value Object com os dados gravados, similar ao enviado para
  	 *                   gravação, porém contendo chave gerada (caso seja gerada pela persistência)
  	 *                   e informações que podem ter sido complementadas em lógicas do negócio
  	 *                   na implementação da persistência.<br>
  	 *                   Importante: Todos os dados que necessitam de ser retornados após uma
  	 *                   gravação devem estar contidos (agregados) no Value Object de retorno.
  	 *
  	 * @throws PlcException Exceções devem ser tratadas e retornadas como uma PlcException, no
  	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
  	 */
   public Object saveObject(PlcBaseContextVO context,
		   Object entity, Object entityPrevious)	throws PlcException;
   
	/**
 	 * @since jCompany.
 	 * Grava Coleção de Objetos (Lógica Tabular) em uma única transação.
 	 *
 	 * A implementação desta operação deve gravar uma coleção (List) de Value Objects
 	 * em algum mecanismo de persistência (SGBD, LDAP, File System, RAM, etc.), p
 	 * para posterior recuperação.
 	 *
 	 * Na implementação do jCompany, esta operação é implementada para lógicas de manutenção
 	 * tabular, somente, que são coleções "top-level". <br>
 	 * Importante: Muito embora lógicas mestre-detalhe contenham coleções de
 	 * detalhes para um Mestre, estas lógicas são gravadas através da operação gravaObjeto, pois
 	 * estão agregadas a um Value Object mestre.
 	 *
 	 * @param list Lista de Value Objects a serem gravados
 	 * @param listPrevious Lista de Value Objects anteriores (o jCompany mantém em sessão a última
 	 *                   versão
 	 *                   recuperada da lista de Value Objects, para possibilitar seu envio juntamente
 	 *                   com as modificações nesta operação. Deste modo, pode-se fazer lógicas
 	 *                   que considerem a alteração efetuada ou de auditoria, com imagens de
 	 *                   dados antes e depois das alterações).<br>
 	 *
 	 * @throws PlcException Exceções devem ser tratadas e retornadas como uma PlcException, no
 	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
 	 */
     public void saveTabular(PlcBaseContextVO context,Class clazz,List list,List listPrevious) throws PlcException;

     /**
  	 * @since jCompany.
  	 * Exclui um Value Object e todas as classes agregadas (detalhes) mapeados
  	 *         como cascade="true", a partir de sua chave primária <P>
  	 *
  	 * @param entity Value Object da classe específica, descendente de PlcBaseEntity, a ser excluido.
  	 *
  	 * @throws PlcException Exceções devem ser tratadas e retornadas como uma PlcException, no
  	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
  	 *                   Uma exceção deve ser disparada caso a ocorrência esperada não seja encontrada.
  	 *                 (tipicamente devido ao filtro de segurança ou concorrência entre usuários)
  	 */
   public void excludeObject(PlcBaseContextVO context, Object entity) throws PlcException;

  	/**
	 * @since jCompany.
	 * Recupera um Value Object e todas as suas classes agregadas (ex: listas
	 *        one-to-many em detalhes de lógica Mestre-Detalhe, classes many-to-one), a partir
	 *        de sua chave primária <P>
	 *
	 * @return Object[0]-ENTITY Recuperado, Object[1]-Modificações quando em aprovação,
	 * Object[2] List[] Lista de coleções para classes lookup em navegação, se existirem
	 *
	 * @throws PlcException Exceções são tratadas e retornadas como uma PlcException, no
	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
	 *                   Uma exceção deve ser disparada caso a ocorrência esperada não seja encontrada.
	 *                 (tipicamente devido ao filtro de segurança ou concorrência entre usuários)
	 */
   public Object[] retrieveObject(PlcBaseContextVO context,Class clazz, Object id) throws PlcException;
  
   public IPlcFileEntity downloadFile(PlcBaseContextVO context, Class clazz, Object id) throws PlcException;

   
 	/**
	 * @since jCompany.
	 * Recupera lista de objetos<p>
	 *
	 * Recupera baseado em uma coleção de argumentos e respectivos tipos. Este método deve ser
	 * utilizado quando for necessário passar um argumentos de uma classe agregados. Ex: "obj.tipo.id=?"<p>
	 *
	 * Exemplo:<p>
	 *  String hql = "from obj in class com.powerlogic.jbatch.vo.PlcHistorico where obj.paiPlc = ? and obj.dataHora = ?";<br>
	    *   Object[] arg = { objVO.getPaiPlc().getId(), ((PlcHistorico)objVO).getDataHora()};<br>
	    *   Object[] type = { "LONG", "TIMESTAMP" };<br>
	    *   List ret = appFac.recuperaLista(objVO, hql, arg, type, "");<br>
	 *
	 * @param args Coleção de argumentos de tipos primitivos
	 * @param types Coleção de String com nome dos tipos dos argumentos, podendo ser:
	 * STRING, LONG, TIMESTAMP, DATE
	 * @throws PlcException Exceções devem ser tratadas e retornadas como uma PlcException, no
	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
	 *                   Uma exceção deve ser disparada caso nenhuma ocorrência seja encontrada.
	 */
   public java.util.List retrieveList(PlcBaseContextVO context,Class clazz,String orderByDynamic,
		     Object[] args, String[] types) throws PlcException;

	/**
	 * @since jCompany.
	 * Recupera lista de objetos utilizando lógica QBE automática do jCompany.
	 *
	 * A implementação desta operação deve interpretar a lista de Value Object do type PlcArgEntity,
	 * sendo que cada instância deste objeto contém um argumento fornecido pelo usuário e operadores
	 * relacionais declarados na lógica Action, no struts-config, segundo o padrão do jCompany.<P>
	 * Ao final, a operação deverá gerar where condition e order by automaticamente, trazendo
	 * uma lista com registros encontrados ou disparando uma exeção caso nenhum seja achado.
	 *
	 * @throws PlcException Exceções devem ser tratadas e retornadas como uma PlcException, no
	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
	 *                   Uma exceção deve ser disparada caso nenhuma ocorrência seja encontrada.
	 */
    public List retrieveList(PlcBaseContextVO context,Class clazz,String orderByDynamic,List<PlcArgEntity> args) throws PlcException;

	/**
	 * @since jCompany 3.0
	 * Idem recuperaLista anterior, com adição de argumentos para funcionamento
	 * integrado com lógicas de seleção e consulta
	 */
	public List retrieveList(PlcBaseContextVO context,Class clazz,String orderByDynamic,
			Object argsQBE,int firstLine, int maximumLines) throws PlcException;


	/**
	 * Recupera uma lista de ENTITY's para a classe informada
	 */ 
    public List retrieveList(PlcBaseContextVO context,Class clazz,String orderByDynamic,List<PlcArgEntity> args,
    		int firstLine, int maximumLines) throws PlcException;
	
	/**
	 * @since jCompany 3.0
	 * Recupera listas simples de todos os objetos de classes lookup,
	 *           de uso abrangente (tipicamente em combos) por toda a aplicação.
	 *
	 * Esta operação pode ser chamada no início da aplicação, para as classes declaradas no web.xml
	 * como classes de lookup, ou a partir de comandos do usuario<p>
	 *
	 * Importante 1: Esta operação não deve ser utilizada para tabelas com muitos registros.<br>
	 * Importante 2: Tabelas básicas, por default, não têm filtro de segurança.<P>
	 *
	 * A implementação desta operação deve recuperar uma lista de Value Objects do type informado
	 *           concatenando a cláusula where e order by informadas.
	 *
	 * @param clazz Classe dos Value Objects a serem recuperados, incluindo package completo.<br>
	 *               Exemplo: "com.empresa.app.vo.TipoCurso"
	 * @param orderByDynamic (Opcional, "" não ordena) Cláusula de ordenação, devendo conter o alias "obj"<br>
	 *               Exemplo: "order by obj.tipo".
	 *       Importante: Como o Design Pattern do jCompany para lógicas de manutenção tabular
	 *       inclui a existência de pelo menos uma propriedade "nome", a ordenação por esta
	 *       propriedade é assumida por default nas lógicas genéricas.
	 *
	 *
	 * @throws PlcException Exceções devem ser tratadas e retornadas como uma PlcException, no
	 *                   padrão do jCompany, para tratamento genérico e exibição para usuário.
	 */
	public java.util.List retrieveSimpleList(PlcBaseContextVO contextParam,Class clazz, String orderByDynamic) throws PlcException;

	/**
	 * @since jCompany 2.5.3
	 * Devolve total de registros com base em uma cláusula HQL ou com base em IoC de objeto natural
	 * com relação ao ENTITY.
	 * @return Total de registros existentes.
	 */
	public Integer retrieveTotal(PlcBaseContextVO context,Class clazz,Object argQBE) throws PlcException;

	/**
	 * @since jCompany 3.0
	 * Idem recuperaTotal mas recebendo POJOs de argumento em lugar da classe.(Mais flexível)
	 */
	public Integer retrieveTotal(PlcBaseContextVO context,Class<Object> clazz,List<PlcArgEntity> argsQBE) throws PlcException;

	/**
	 * @since jCompany 3.1
	 * Recupera classes de lookup many-to-one (agregados), desde que utilizando qualquer propriedade do ENTITY
	 * @param id Objeto contendo o identificador. Na versão 2.7.3, somente aceita OID, mas a assinatura está
	 * mantido genérica para evoluções utilizando chave natural
	 * @param propertyName nome da propriedade que será utilizada para a pesquisa. Se null, será considerado o OID.
	 * @param value valor de pesquisa da propriedade indicada.
	 * @return Instância do ENTITY de lookup
	 */
	public Object retrieveLookupAggregate(PlcBaseContextVO context,Object baseEntity,String propertyName, Object value) throws PlcException;

	/**
	 * @since jCompany 5.1
	 * Recupera classes de lookup many-to-one (agregados), utilizando qualquer propriedade da Entidade
	 * Utiliza um mapa de Propriedades Valores para recuperar medianete várias propriedades. Apóio à Chave Natural
	 * @return Instância da Entidade de lookup
	 */
	public Object retrieveLookupAggregate(PlcBaseContextVO context,Object baseEntity, Map<String, Object> propertiesValues) throws PlcException;


    /**
     * @since jCompany 2.7.3
     * Recupera objetos de classes com ligação many-to-one com uma classe origem informada,
     * desde que a identificacao da classe origem seja OID
     * @param classDestinyPlc classe dos objetos a serem recuperados
     * @param classOriginPlc classe cuja classeDestinoPlc enxerga como many-to-one
     * @param pk Identificador da instência da classe Origem que servirá de base para a recuperação
     * @param context Parâmetros gerais da sessão e contexto de importância para a camada Modelo.
     * @return Coleção de instancias do type de classeDestino que se ligam à instância de classeOrigem com OID "oid".
     **/
    public List retrieveNavigation(PlcBaseContextVO context,Class classOriginPlc, Object pk,Class classDestinyPlc) throws PlcException;
    
    /**
     *  @since jCompany 3.0
     *  Recupera relação de registros para auditoria, de forma genérica
     *  @param id Objeto podendo conter Long (oid) ou Classe da PK (Na versão 3.0, somente OID funciona)
     **/
    public List<Object> retrieveAudit(PlcBaseContextVO context,Class<Object> clazz, Object id) throws PlcException;

    
    public int retrieveSubReportsAmount(PlcBaseContextVO context,Class clazz) throws PlcException ;

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
    public PlcBaseUserProfileEntity loadJSecurityProfile( PlcBaseContextVO context) throws PlcException;
			
    /**
     * Se a aplicação estiver configurada para utilizar o jSecurity, recupera o
     * cadastro básico do usuário.
     * 
     * @since jCompany 3.x
     * @param context
     * @return
     * @throws PlcException
     */
    public Serializable retrieveJSecurityUser(PlcBaseContextVO context)
			throws PlcException;


    /**
     * Recupera lista de exploração a partir de uma classe e OID
     * @since jCompany 3.03
     * @param classe Classe-Base 
     * @param id Identificador (até esta versão somente OID)
     * @return Lista valores de detalhamento para o objeto
     */
	public List retrieveExplorerList(PlcBaseContextVO context, Class baseClass, Object id,
			Class descendentClass,long initialPos) throws PlcException;
	
	/**
	 * Transforma uma string em sua representação fonetica se existir uma
	 * implementação para o servico de fonetização.
	 * @since jCompany 3.1
	 * @param pattern String que será fonetizada
	 * @return retorna a representação fonetica da string, caso não exista uma
	 * implementação do serviço de fonetização registrado returno null
	 * @throws PlcException
	 */
	public String phonetics(String pattern) throws PlcException;
	
	 
	/**
	 * @since jCompany 3.1
	 * Recupera um detalhe por demanda
	 * 
	 * @param entity ENTITY Principal que já deve ter sido recuperado anteriormente (conter OID ou chave natural, etc.), exceto a coleçao do detalhe
	 * informada em nomePropDetalhe.
	 * @param propertyNameDetail Nome da propriedade que contém a coleçao de detalhes a serem recuperados
	 * @return ENTITY com coleçao de detalhes recuperados
	 *
	 */
   public Object retrieveOnDemand(PlcBaseContextVO context,Object entity, String propertyNameDetail,
		   Class classDetail) throws PlcException;

   /**
    *  @since jCompany 3.0
    *  Recupera registro de mensagens tratadas de erros da camada de persistencia
    *  @param rootCause Exception
    *  @return String[0] mensagem internacionalizada, String[1]: arg1 (opcional), String[2] arg2 (opcional)
    **/
	public String[] retrieveMessageException(Throwable rootCause);
	
	/**
	 * @since jCompany 5.0
	 * Configura como BO padrao da aplicação o BO EBJ que é injetado pelo container
	 * dentre da PlcFacadeImpl. Caso o Container não tenha injetado lança PlcException.
	 * 
	 * @throws PlcException
	 */
	public void setDefaultEJBManager() throws PlcException;
}
