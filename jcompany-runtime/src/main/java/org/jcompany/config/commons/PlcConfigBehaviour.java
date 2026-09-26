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
package org.jcompany.config.commons;

import static java.lang.annotation.ElementType.PACKAGE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @since jCompany 5.0
 * Configurações globais de definição de comportamentos padrões da aplicação
 */
public @interface PlcConfigBehaviour {

    /**
     * Enum que defini tipos de modos de histórico
     */
    public enum HistoricMode {
    	STATUS_INACTIVE,
    	STATUS_ACTIVE,
    	STATUS_PENDENT;
    }	

    /**
     * Se o jCompany deve testar, na camada controle, contra a troca de valores de chave.
     */
    boolean avoidChangeId() default  true;
    
    /**
     * Indicador para que o jCompany nao use nenhum comparator default para ordenar detalhes. 
     * Informar S para utilizar recursos de engines de persistencia, por exemplo
     */
    boolean deactivateComparatorDetails() default  false;
    
    /**
     * Indica se o teste de usuário aprovador utilizará o valor do teste realizado no BO<br>
     * ou se o teste deve ser realizado no pelo Action.<br>
     * <br>
     * S = para utilizar o valor realizado no BO.
     */
    boolean approvalVersionThree() default  true;
    
    /**
     * Indica se a aplicação deve utilizar AOP. O default é usar e somente se informado com N a AOP será desativada.
     * IMPORTANTE: Transaçoes via Annotations (rollback e commit) dependem desta funcionalidade, bem como Monitoria de Performance
     * (Profiling)
     */
    boolean aopUse() default  true;
    
    /**
     * Indica para não lembrar ultimas ediçoes em cookie (Otimiza 0,1 seg por request)
     */
    boolean rememberLastEdition() default  false;
        
    /**
     * Indica para o jCompany salvar o detalhe anterior para ser utilizado como informação adcional na lógica
     */
    boolean detailRemember() default  false;
    
    /**
     * Indica para o jCompany salvar o mestre anterior para ser utilizado como informação adcional na lógica
     */
    boolean masterRemember() default  true;
    
    /**
     * Indica para o jCompany renderizar um tooltip (title html) em tab-folders com sufixo ".ajuda", de modo
     * a permitir que balões informativos mais significativos sejam informados.
     */
    boolean tooltipDifferentiated() default  false;
    
    /**
     * Indica se a aplicação deseja utilizar os padrões da versão classic e não da versão 3.x
     * utilizada para teste de compatibilização 
     */
    boolean classicUse() default  false;
    
    /**
     * Indica se a aplicação deve informar ao usuário que uma tela editada e não gravada esta sendo "abandonada".
     */
    boolean changingAlertUse() default  true;
    
    /**
     * Indica que após a gravação de um registro, um novo registro será criado para edição
     */
    boolean massiveTyping() default  false;
    
    /**
     * Indica se os links em tab-folders e portlets usará ancoras. O default é S
     */
    boolean anchorUse() default  false;
    
    /**
     * Indica que a colaboração corrente (url) irá permitir ativação do explorador universal
     */
    boolean explorerUse() default false;
    
    /**
     * Indica se o container(Servidor de Aplicação) é compativel com JPA e é responsável pelo controle de transações.
     */
    boolean containerManagingTransaction() default false;
    
    /**
     * <b>modoJanela </b>: Indica modos para exibição da janela (layout)
     * desta lógica
     */
    String modeWindow() default "";
    
    /**
     * Indica se Exibe botão de Gravação de Versão e de Reativação ou Não Exibe
     */
    boolean versionSavePublish() default false;
    
    /**
     * Indica modo de gravação ""-Sem Historico, "A"-Aprovação, "V"-Versão
     */
    HistoricMode historicMode() default  HistoricMode.STATUS_ACTIVE;
    
    /**
     * Indica se faz validação de dados na exclusão. 
     */
    boolean exclusionValidationUse() default false;
    
    /**
     * Indica se é para utilizar form do type text/html até o último momento em logicas que usam arquivo anexado<br>
     */
    boolean attachedFileOptimize() default true;
    
    /**
     * Indica qual o layout de explorador de dados (treeview) 
     */
    String explorerLayout() default "def.componente.explorer.treeview.ancestral";
    
    /**
     * Registra filtro de segurança vertical quando o usuário é anônimo e não passa pela lógica de perfil (profiling) de usuários da autenticação.
     */
    String filterAnonym() default "#";

    /**
     * Arquivo do MS-Office de template para geração de documentos em lógicas.
     */
    String templateOffice() default "";

    /**
     * Indica usa pesquisa com URL RESTful
     */
    boolean searchRestfulUse() default  false;    
    
}
