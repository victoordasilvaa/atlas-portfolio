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
package org.jcompany.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.hibernate.HibernateException;
import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcException;
import org.jcompany.persistence.hibernate.PlcBaseHibernateDAO;
import org.jcompany.persistence.service.PlcQBEService;


@Local
public interface IPlcDAO {

	/**
	 * @since jCompany 3.5
	 * @return Serviço de QBE para Hibernate
	 */
	public PlcQBEService getServiceQBE();

	/**
	 * @since jCompany 3.5
	 * @param serviceQBE The serviceQBE to set.
	 */
	public void setServiceQBE(PlcQBEService serviceQBE);

	/**
	 * @since jCompany 3.5
	 * 
	 * Deve verificar o type da Exceção e devolver msg de erro aprpriada
	 * @param rootCause Exception a ser investigada
	 * @return null se não for de responsabilidade da persistencia ou String[0]: msg internacionalizada,
	 * String[1]: arg1 (opcional) e String[2]: arg2 (opcional)
	 */
	public String[] msgExceptionHandle(Throwable rootCause) throws PlcException;

	/**
	 * @since jCompany 3.5
	 * 
	 * Trata erros capturados pela Hibernate
	 */
	public String[] msgExcecaoHandleHibernate(HibernateException errorH);

	/**
	 * @since jCompany 3.0
	 * @param classBase
	 * @return Lista de classe relacionadas com a classe base
	 * @throws PlcException
	 */
	public List<Class> retrievePossibleDescendents(Class classBase)
			throws PlcException;

	/**
	 * Inicia uma transação de persistência
	 * @param factory
	 * @throws PlcException
	 */
	public void begin(String factory) throws PlcException;

	/** 
	 * Encerra uma transação/sessao com rollback
	 * @param factory Nome da fabrica a ser utilizada
	 */
	public void rollback(String factory) throws PlcException;
	/** 
	 * Encerra uma transação/sessao com rollback
	 */
	public void rollback() throws PlcException;

	/**
	 * @since jCompany 3.0
	 * 
	 * Encerra uma transação/sessao com commit
	 * @param factory Nome da fabrica a ser utilizada
	 */
	public void commit(String factory) throws PlcException;
	/**
	 * @since jCompany 3.0
	 * 
	 * Encerra uma transação/sessao com commit
	 */
	public void commit() throws PlcException;

	/**
	 * @since jCompany 3.0
	 * 
	 * Dispara os comandos em buffer gerenciados pela engine de persistencia até o momento.
	 * Importante: Não faz confirmação final (commit, por exemplo), somente envia.
	 */
	public void sendCacheCommands(Class clazz) throws PlcException;

	/**
	 * @since jCompany 3.0
	 * 
	 * Dispara os comandos em buffer gerenciados pela engine de persistencia até o momento.
	 * Importante: Não faz confirmação final (commit, por exemplo), somente envia.
	 */
	public void sendCacheCommands() throws PlcException;

	/**
	 * Persiste um objeto no SGBD
	 * @param entity Entidade a ser incluida
	 * @return Identificador do objeto
	 * @throws PlcException
	 */
	public Long insert(Object entity) throws PlcException;

	/**
	 * Altera os dados e uma entidade persistida no SGBD
	 * @param entity Entidade a ser alterada
	 * @throws PlcException
	 */
	public void update(Object entity) throws PlcException;

	/**
	 * Remove uma entidade persistida do SGBD
	 * @param entity Entidade a ser removida
	 * @throws PlcException
	 */
	public void exclude(Object entity) throws PlcException;

	/**
	 * Recupera do SGBD uma entidade
	 * @param clazz Classe o objeto a ser recuperado
	 * @param id Identificador do Objeto (chave primária)
	 * @return Entidade Persistida
	 * @throws PlcException
	 */
	public Object retrieve(Class clazz, Object id) throws PlcException;

	/**
	 * Recupera apenas o valor da propriedade selecionada
	 * @param entity Entidade Modelo para recuperação
	 * @param oid Identificador do objeto (chave primária)
	 * @param propertyName propriedade a ser recuperada
	 * @return Valor da propriedade selecionada
	 * @throws PlcException
	 */
	public Object retrieveOneValue(Object entity, Long oid, String propertyName)
			throws PlcException;

	/**
	 * Recupera todos objetos de uma classe selecionada
	 * @param clazz Tipo dos objetos a serem recuperados
	 * @param orderByDynamic clausula de ordenação
	 * @return Lista de objetos da classe selecionada
	 * @throws PlcException
	 */
	public List retrieveAll(Class clazz, String orderByDynamic)
			throws PlcException;

	/**
	 * jCompany. Recupera conteudo binário do arquivo
	 * @param oid Object-Id do arquivo
	 * @param property Propriedade contendo o conteúdo binário.
	 * @return arquivo
	 */
	public byte[] retrieveBinaryFileById(Class clazz, Long oid,
			String property) throws PlcException;

	/**
	 * Recupera um ENTITY com arquivo incluido, baseada em sua URL
	 * @param url URL para recuperar o arquivo.
	 * @return ENTITY contendo arquivo.
	 */
	public IPlcFileEntity retrieveEntityFileByUrl(Class clazz, String url)
			throws PlcException;

	/**
	  * @return Se o jCompany esta deve otimizar a atualização das entidades persistidas.
	  */
	public boolean isOptimizeUpdate() throws PlcException;

	/**
	 * Recupera um grafo de  Value Object a partir do OID (id), aplicando filtros padrões.
	 * @param id Object-id
	 * @return ENTITY recuperado ou null se não encontrou.
	 */

	public Object retrieveWithFilter(Class clazz, Object id)
			throws PlcException;

	/**
	 * Recupera coleção de detalhes do ENTITY informado
	 * @since jCompany 3.3
	 * @param entity ENTITY Mestre
	 * @param classDetail Classe do Detalhe a ser recuperado
	 * @param ropertyNameDetail Nome da propridade (coleçao) de detalhe a ser montada
	 * @return ENTITY Mestre incluindo coleção de detalhe recuperada.
	 */
	public Object retrieveOnDemand(Object entity, String ropertyNameDetail,
			Class classDetail) throws PlcException;

	/**
	 * @since jCompany 3.3
	 * 
	 * Automação de navegação em grafo de VOs
	 */
	@SuppressWarnings("unchecked")
	public List<Object> retrieveAggregateNavigation(Object entityMain,
			Object entityAggregateDestiny) throws PlcException;

	/**
     * @since jCompany 3.0
     * Recupera relação de registros a partir de uma classe origem com o pk informado
     * @param pk OID ou Classe de chave composta com valores
     * @param aggregateDestiny Classe agregado de destino
     * @return Coleção de valores possíveis para destino.
     */
	public List<Object> retrieveAggregateNavigation(Class classMain,
			Object pk, Class aggregateDestiny) throws PlcException;

	/**
	 * Recupera classe de lookup, somente trazendo propriedades relacionadas em getLookupPropsPlc,
	 * caso o método seja declarado no ENTITY. Caso contrário, recupera com "load" (deve-se ter cuidado, neste caso,
	 * com relacionamento com lazy=false, para evitar recuperações excessivas)<p>
	 * Importante: somente para chaves Object-ID, nesta versão.
	 * @param baseEntity ENTITY a ser recuperado
	 * @param propertyName nome da propriedade que será utilizada para a pesquisa. Se null, será considerado o OID.
	 * @param valor valor de pesquisa da propriedade indicada.
	 * @param props Relação de propriedades, com exceção das chaves, a serem recuperadas
	 * @return ENTITY contendo propriedades indicadas.
	 * * TODO Nesta versão, não respeita o filtro vertical
	 */
	public Object retrieveLookupAggregate(Object baseEntity,String propertyName, Object value, String[] props) throws PlcException;

	public Object retrieveLookupAggregate(Object baseEntity, Map<String, Object> propertiesValues) throws PlcException;

	/**
	 * Recupera lista de objetos aplicando o filtro padrão na recuperação dos mesmos.
	 * @param clazz Tipo dos objetos a serem recuperados
	 * @param orderByDynamic clausula de ordenação
	 * @param argsQBE argumentos para recuperação
	 * @return Lista de objetos da classe selecionada
	 * @throws PlcException
	 */
	public List retrieveWithDefaultFilter(Class clazz, String orderByDynamic,
			List<PlcArgEntity> argsQBE) throws PlcException;

	/**
	 * Recupera lista de objetos aplicando o filtro programado recuperação dos mesmos.
	 * @param clazz Tipo dos objetos a serem recuperados
	 * @param orderByDynamic clausula de ordenação
	 * @return Lista de objetos da classe selecionada
	 * @throws PlcException
	 */
	public List retrieveWithProgrammedFilter(Class clazz, String orderByDynamic)
			throws PlcException;

	/**
	 * @since jCompany 5.0
	 * 
	 * Implementação deve recuperar listas com argumentos.
	 * @param clazz Classe a ser recuperada
	 */
	public List retrieveWithDeclaredArgs(Class clazz, String orderByDynamic,
			Object[] valueArgs, String[] typeArgs) throws PlcException;

	/**
     * Recupera uma lista de objetos da classe informada, utilizando queries anotadas no padrao "querySel" ou "queryTreeView", etc., em 
     * conformidade com o Application Pattern utilizado. Pode receber orderByDinamico (alterado pelo usuário ou desenvolvedor em cada
     * requisição), um POJO de argumentos (todos existentes na query anotada devem ser composto neste objeto) e intervalo para paginação	
     * @param clazz Classe da qual será obtida a query em anotação padrao ("querySel", "queryTreeView", etc., conforme a lógica chamadora)
     * @param orderByDynamic (Opcional - informe null ou "" para desconsiderar) Trecho de orderBy a ser adicionado dinamicamente à query anotada
     * @param argQBE POJO contendo valores preenchidos para todos os argumentos existentes fixos na query ou montados dinamicamente em whereDinamico
     * @param firstLine Primeira linha para recuperação paginada ou -1 para todos
     * @param maximumLines Máximo de linhas (size da página de recuperação) ou -1 para todos
     * @since jCompany 5.0
     * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBEPaginated(java.lang.Class, java.lang.String, org.jcompany.commons.PlcBaseEntity, int, int)
     */
	public List retrieveListQBEPaginated(Class clazz, String orderByDynamic,
			Object argsQBE, int firstLine, int maximumLines)
			throws PlcException;

	public List retrieveListQBEPaginated(Class clazz, String whereDynamic,
			String orderByDynamic, Object argQBE, int firstLine,
			int maximumLines) throws PlcException;

	/**
     * Recupera uma lista de objetos da classe informada, utilizando queries anotadas no padrao "querySel" ou "queryTreeView", etc., em 
     * conformidade com o Application Pattern utilizado. Pode receber orderByDinamico (alterado pelo usuário ou desenvolvedor em cada
     * requisição) e também um trecho de where condition para ser dinamicamente composto (uso em lógicas dinamicas tais como
     * explorer), além de POJO de argumentos e intervalo para paginação
     * @param clazz Classe da qual será obtida a query em anotação padrao ("querySel", "queryTreeView", etc., conforme a lógica chamadora)
     * @param whereDinamico (Opcional - informe null ou "" para desconsiderar) Trecho a ser adicionado dinamicamente à query anotada. Importante: Nao informar o 'where' em si e utilizar o alias padrão 'obj.' (Ex:'obj.status=:status and obj.valor>:valor')
     * @param orderByDynamic (Opcional - informe null ou "" para desconsiderar) Trecho de orderBy a ser adicionado dinamicamente à query anotada
     * @param argQBE POJO contendo valores preenchidos para todos os argumentos existentes fixos na query ou montados dinamicamente em whereDinamico
     * @param firstLine Primeira linha para recuperação paginada ou -1 para todos
     * @param maximumLines Máximo de linhas (size da página de recuperação) ou -1 para todos
     * @since jCompany 5.0
     * @see org.jcompany.persistence.PlcBaseDAO#retrieveListQBEPaginated(java.lang.Class, java.lang.String, org.jcompany.commons.PlcBaseEntity, int, int)
     */
	public List retrieveListQBEPaginated(Class clazz, String orderByDynamic,
			List<PlcArgEntity> argsQBE, int firstLine, int maximumLines)
			throws PlcException;

	/**
	 * Devolve total de registros considerando argumentos
	 * @since jCompany  5.0 
	 */
	public Integer retrieveListQBETotal(Class clazz, Object argsQBE)
			throws PlcException;

	/**
	 * Devolve total de registros considerando argumentos
	 * @since jCompany  5.0 
	 */
	public Integer retrieveListQBETotal(Class clazz, String whereDynamic,
			Object argsQBE) throws PlcException;

	/**
	 * Devolve total de registros considerando argumentos
	 * @since jCompany  5.0 
	 */
	public Integer retrieveListQBETotal(Class clazz, List<PlcArgEntity> argsQBE)
			throws PlcException;

	/**
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
	 * @since jCompany 3.03
	 * @see org.jcompany.persistence.PlcBaseDAO#recuperaListaExplorer(java.lang.Class, java.lang.Class, java.lang.Object)
	 */
	public List retrieveExplorerList(Class clazz, Class classBase,
			Object idClassBase, long initialPosition) throws PlcException;

	/**
	  * Recupera a lista de registros inativos e pendentes de aprovação para um id de registro ativo
	 * @since jCompany 5.0 
	 * @param clazz Classe principal
	 * @param pk OID do registro Ativo
	 * @return Coleção de VOs com id,dataUltAlteracao, usuarioUltAlteracao, versao e nome populados
	 */
	public List retrieveListCompleteAudit(Class clazz, Object pkActive)
			throws PlcException;

	/**
	 * @since jCompany 5.0
	 * Recupera um historico recebendo o type
	 * @param statusHistoricPlc "I" ou "P" para recuperar inativo ou pendente de aprovação
	 * @return List de VOs do type desejado
	 */
	public List retrieveListCompleteBySituation(Class clazz,
			String statusHistoricPlc) throws PlcException;

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
	public void avoidEqualsExecute(Object entity, String mode)
			throws PlcException;

	/**
	* Recupera a quantidade de Sub-Relatórios, baseia-se nas anotações (anotations)
	* Colocadas no ENTITY do relatório (Classe do Parâmetro). As NamedQuery's tem que seguir o padrão de nomes:  
	* ' PlcConstantsCommons.ANNOTATION.SUFIXO_QUERYSELREL_SUBREPORTE_'  e o número do subreport
	*/
	public int retrieveSubReportsAmount(Class clazz) throws PlcException;

	/**
	 * jCompany 3.0 Torna o objeto somente de consulta durante uma sessão
	 * @param det Coleção de Detalhe a não ser considerada.
	 * @param bean Objeto Mestre
	 */
	public void registerConsultationOnly(Object beanMain, Collection c)
			throws PlcException;

}