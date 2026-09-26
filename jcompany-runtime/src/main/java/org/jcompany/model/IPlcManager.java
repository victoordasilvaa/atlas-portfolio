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

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcException;


@Local
public interface IPlcManager {

	/**
	 * @since jCompany 3.0
	 * 
	 * Recupera coleção de VOs, a partir de uma classe e interpretando a lista de argumentos (PlcArgEntity).
	 * Cada instância deste objeto contém um argumento fornecido pelo usuário e operadores
	 * relacionais a serem usado.<P>
	 * Ao final, a operação deverá gerar where condition substituir o orderBy automaticamente, trazendo
	 * uma lista com registros encontrados ou disparando uma exceção caso nenhum seja achado.
	 */
	public List retrieveListQBE(Class clazz, String orderByDynamic,
			List<PlcArgEntity> argsQBE) throws PlcException;

	/**
	 * @since jCompany 3.0
	 * 
	 * Recupera lista de objetos com paginacao (ex: reg 1 a 20 de 1000) utilizando com argumentos os valores preenchidos
	 * no ENTITY argQBE.
	 */
	public List retrieveList(Class clazz, String orderyByDynamic,
			Object argQBE, int firstLine, int maximumLines)
			throws PlcException;

	/**
	 * @since jCompany 3.0
	 * 
	 * Recupera coleção de VOs paginada (ex: 1 a 20 de 200), a partir de uma classe e interpretando uma coleção de argumentos (PlcArgEntity).
	 */
	public List retrieveList(Class clazz, String orderyByDynamic,
			List<PlcArgEntity> argsQBE, int firstLine, int maximumLines)
			throws PlcException;

	/**
	 * @since jCompany 3.0. 
	 * 
	 * Variação da recuperação de listas recebendo array de valores e tipos de argumentos.
	 */
	public List retrieveList(Class clazz, String orderByDynamic,
			Object[] args, String[] types) throws PlcException;

	/**
	 * @since jCompany 3.0
	 * 
	 * Exclui um Value Object
	 * @param entity Value Object a ser excluído
	 */
	public void exclude(Object entity) throws PlcException;

	/**
	 * @since jCompany 1.0 
	 * 
	 * Altera o Value Object vo.<p>
	 *
	 * Segue o seguinte roteiro:<p>
	 * - Se opção para evitar troca de OID no ENTITY é 'S', dispara exceção se houve troca<p>
	 * - trataUploadArquivo(vo, voArq,VOAnterior.getId()): se ENTITY é de arquivo anexado, então faz upload de arquivo apropriadamente.
	 * Importante: Este método chamado pode chamar o método altera novamente, em execução recursiva, para alteração do ENTITY de Arquivo Anexado em Anexo<p>
	 * - testaNaoDeveExistir(vo,"A"): Envia a relação de queries de integridade avoidEquals (declaradas em anotações), antes de efetuar a alteração,<p>
	 * - atualizaArquivo(voArq,idArquivoAux,true): Atualiza arquivo anexado (pode gerar inclusão de arquivo ou exclusão e nova inclusão,
	 * já que arquivos anexados não são alterados verdadeiramente)<p>
	 *
	 * @param entity Value Object a ser incluído
	 * @param entityPrevious Referência ao Value Object recuperado, antes de sofrer alterações.
	 *
	 * @return PlcBaseEntity Devolve o vo após a alteração
	 */
	public Object update(Object entity, Object entityPrevious) throws PlcException;

	/**
	 * @since jCompany 1.0
	 * 
	 * Salva um novo Value Object na sessão de persistência<p>
	 *
	 * Segue o seguinte roteiro:<p>
	 * - Envia a relação de queries de integridade avoidEquals (declaradas em anotações)<p>
	 * - se tem arquivo anexado, chama este método recursivamente, para incluir o ENTITY de Arquivo, que vem agregado ao ENTITY Principal, antes de gravar o Principal.
	 * @param entity Value Object a ser incluído
	 *
	 * @return Retorna o Value Object com o OID gerado, se for auto-increment.
	 *
	 */
	public Object insert(Object entity) throws PlcException;

	/**
	 * jCompany 20. Recupera um objeto da camada de persistência<p>
	 *
	 * Após este método, no método aposRecuperaObjeto, por exemplo, outros podem ler
	 * o objeto recém-recuperado com vo.<p>
	 *
	 * Este método ainda recupera o nome de arquivos anexados ao objeto mestre, com o
	 * cuidado de não recuperar (download) o arquivo em si. Deste modo, o usuário poderá
	 * consultar o nome de um arquivo anexado e disparar o evento de download se quiser
	 * baixá-lo.
	 *
	 * @param id Chave primária (do type original Long) em formato String
	 * @throws PlcException Exceções da Hibernate são tratadas de forma diferenciada das exceções gerais
	 *               e disparadas como exceções no padrão jCompany.
	 */
	public Object[] retrieve(Class clazz, Object id) throws PlcException;

	public List retrieveApprovalModification(Object entity) throws PlcException;

	/**
	 * jCompany 3.0 Recupera total de regisro com base em argumentos de filtro para uma classe.
	 */
	public Integer retrieveTotalElementsDefault(Class clazz, Object argsQBE)
			throws PlcException;

	/**
	 * Se VOAnterior for diferente de nulo e alguma das propriedades monitoradas foram alteradas,
	 * cria um novo registro de versao a partir do VOAnterior,
	 * incluindo  sitHistoricoPlc="I" e idPai=OID do novo ENTITY
	 * @param entity Novo ENTITY
	 * @param entityPrevious ENTITY Anterior (se for nulo a operação não faz nada)
	 */
	public void versionInsert(Object entity, Object entityPrevious) throws PlcException;

	/**
	 * Inclui novo registro como pendente de aprovação (sitHistoricoPlc="P"). Através de operação
	 * usuarioAprovador() o descendente pode implementar uma verificação que dirá se o objeto será
	 * criado como definitivo (setHistoricoPlc="A") ou não (default)
	 * @param entity
	 * @param entityPrevious
	 */
	public Object pendencyInsert(Object entity, Object entityPrevious)
			throws PlcException;

	/**
	 * Verifica opções de aprovação do Usuário que estão no ENTITY e executa persistencia de acordo. Regras:<p>
	 * Se for Aprovação: Se exclusão for "todos" ou "aprovados" então somente copia o registro atual "P"-Pendente para cima do "A"-Ativo, alterando este último
	 * Senão copia e cria um novo registro "I"-Inativo com a image do registro anterior à aprovação<p>
	 * Se for Reprovação: Se exclusão for "todos" ou "reprovados" então somente exclui o registro atual
	 * Senão altera o registro atual para "I"-Inativo.
	 *
	 * @param entity ENTITY com dados alterado pelo usuário.
	 * @param entityPrevious ENTITY com dados recuperados pelo usuário, antes da aprovação
	 */
	public void approve(Object entity, Object entityPrevious) throws PlcException;

	/**
	 * Verifica qual as lógicas de histórico (versionamento ou aprovação), alterando os VOs conforme
	 * necessário.
	 * @param entity Novo ENTITY (Recém informado pelo usuário)
	 * @param entityPrevious (ENTITY anteriormente recuperado)
	 */
	public Object verifyApprovalHistoric(Object entity, Object entityPrevious)
			throws PlcException;

	/**
	 * Recupera uma coleção de objetos pendentes de aprovação (sitHistoricoPlc="P"), incluindo seu id, dataUltAlteracao, usuUltAlteracao e
	 * nome.
	 * @return List de VOs do type
	 */
	public List retrievePendents(Class clazz) throws PlcException;

	/**
	 * Recupera uma coleção de objetos inativos (sitHistoricoPlc="I"),
	 * incluindo seu id, dataUltAlteracao, usuUltAlteracao e nome.
	 * @return List de VOs do type
	 */
	public List retrieveInactives(Class clazz) throws PlcException;

	/**
	 * jCompany 3.1. Template Method que recupera uma classe agregada, utilizando preferencialmente
	 * as propriedades declaradas no ENTITY.
	 */
	public Object retrieveLookupAggregate(Object entity,
			String propertyName, Object valor) throws PlcException;

	
	/**
	 * jCompany 5.1
	 * Recupera uma classe agregada, utilizando preferencialmente as propriedades declaradas na Entidade.
	 * Utiliza um mapa de Propriedades Valores para recuperar medianete várias propriedades. Apóio à Chave Natural
	 */
	public Object retrieveLookupAggregate(Object entity, Map<String, Object> propertiesValues) throws PlcException;

	/**
	 * Recupera classes automatizando a navegação
	 * @return Coleção de VOs do type classeDestino
	 */
	public List retrieveAggregateNavigation(Class classOrigin, Object pk, Class classDestiny) throws PlcException;

	/**
	 * Recupera filhos segundo a regra:<p>
	 * 1. se tiver mais classes que possuem relacionamentos many-to-one na persistencia<br>
	 * 2. 
	 * @param classBase Class
	 * @param id
	 */
	public List retrieveExplorerList(Class classBase, Object id,
			Class descendentClass, long initialPos) throws PlcException;
	
	/**
	 * jCompany 2.7.3. Mantido para recuperação de arquivos
	 */
	public Object retrieveOnlyEntity ( Class clazz, Object id) throws PlcException ;
	public String retrieveFileName(IPlcFileEntity fileEntity, Long idFile) throws PlcException;

}