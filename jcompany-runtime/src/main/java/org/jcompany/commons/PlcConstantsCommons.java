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
package org.jcompany.commons;


/**
 * @since jCompany 3.0.
 * Constantes de Uso Global do jCompany. Constantes são
 * organizadas em inner classes.
 */
public interface PlcConstantsCommons  {
	
	/**
	 * Nome para registros JNDI de EJBs
	 */
	public static String PLC_FACADE_JNDI_NAME = "ejb/PlcFacade";
	public static String PLC_FACADE_JNDI_NAME_REMOTO = "ejb/PlcFacade";
	public static String PLC_JPADAO_JNDI_NAME = "ejb/PlcJpaDAO";
	public static String PLC_MANAGER_JNDI_NAME = "ejb/PlcBO";
	public static String PLC_DEFAULT_PREFIX_JNDI_NAME = "java:comp/env/";
	public static String PLC_DEFAULT_SUFFIX_JNDI_NAME_REMOTE = "Remote";
	
	/**
	 * Chave para recuperar Entidade raiz das Agregações - nome. Usa 'valueObject' para manter compatibilidade com versões anteriores à 5.0
	 */
	String ENTITY_ROOT = "valueObject";
	/**
	 * Chave para recuperar Entidade raiz das Agregações - classe. Usa 'valueObjectClass' para manter compatibilidade com versões anteriores à 5.0
	 */
	String ENTITY_ROOT_CLASS = "valueObjectClass";
	
	/**
	 * Chave para recuperar o nome da propriedade associada a um ENTITY
	 */
	String ENTITY_ROOT_PROP = "valueObjectProp";
	/**
	 * Chave para recuperar Padrão de Colaboração. Usa 'logic' para manter compatibilidade com versões anteriores à 5.0
	 */
	String STEREOTYPE_COLLABORATION = "logic";
	
	/**
	 * Chave para recuperar pai de SubDetalhe
	 */
	String SUB_DETAIL_PARENT_ENTITY = "subDetalhePaiVO";
	
	/**
	 * Chave para recuperar nome da colecao
	 */
	String SUB_DETAIL_PROP_NAME_COLLECTION = "subDetailPropNameCollection";
	
	/**
	 * Chave para recuperar ENTITY do SubDetalhe
	 */
	String SUB_DETAIL_ENTITY = "subDetalheVO";
	
	/**
	 * Chave para recuperar flagDesprezar do SubDetalhe
	 */
	String SUB_DETAIL_FLAG_DESPISE = "subDetalheFlagDesprezar";
	
	/**
	 * Chave para recuperar flagDescZero do SubDetalhe
	 */
	String SUB_DETAIL_FLAG_DESC_ZERO = "subDetalheFlagDescZero";
	
	/**
	 * Chave para recuperar subDetalheNumNovos do SubDetalhe
	 */
	String SUB_DETAIL_NUM_NEW = "subDetalheNumNovos";
	
	/**
	 * Nome do pocote raiz das Anotações de pacote
	 */
	String PLC_PACKAGE_CONFIG_BASE = "org.jcompany.config";

	Class PLC_CLASS_ENTITY = PlcBaseEntity.class;
	
    /**
     * Usar "S" diretamente
     */
    String YES = "S";

    /**
     * Usar "N" diretamente
     */
    String NO = "N";
    
    /**
     * Token a ser usado para retornar valor quando um tratamento de erro não precisa ser feito
     */
    String SILENCER_ERROR_PLC = "erroSilPlc";

    /**
     * Nome da propriedade que tem o getter com 
     * o método que devolve classes Agregadas para lógica de automação otimizada do jCOmpany
     */
    String ATTRIBUT_AGGREGATE_LAZY = "agregadosLazyPlc";
    
    String TOTAL_DETAILS = "totalDetalhes";

    String LIST_ARGUMENTS = "listaArgumentos";
    
    /**
     * Valores possíveis para eventos originários da camada controle (Ação Original)
     */
    public interface EVENT {

        /**
         * Ação foi de aprovação
         */
        String APPROVE = "aprova";

        /**
         * Ação foi de gravação
         */
        String SAVE = "grava";
        
        /**
         * Ação foi de exclusão
         */
        String EXCLUDE = "exclui";

        /**
         * Ação foi de reprovação
         */
        String REPROVE = "reprova";

        /**
         * Ação foi de inclusão de detalhe
         */
        String INSERT_DET = "incluiDet";

        /**
         * Ação foi de edição
         */
        String EDIT = "edita";

        /**
         * Ação foi de edição de crud tabular
         */
        String EDIT_CRUD_TABULAR = "editaCrudTabular";

        /**
         * Ação foi para publicar versão
         */
        String PUBLISH_VERSION = "publicaVersao";
        
        /**
         * Ação de cancelamento do assistente
         */
        String ASSISTANT_CANCEL = "assistenteCancela";
        
        /**
         * Ação de inicialização do assistente
         */
        String ASSISTANT_INITIALIZE = "assistenteInicializa";
        
        /**
         * Ação de próximo passo do assistente
         */
        String ASSISTANT_NEXT = "assistenteProximo";
        
        /**
         * Ação de  passo anterior do assistente
         */
        String ASSISTANT_PREVIOUS = "assistenteAnterior";
        
        /**
         * Ação de  refresh de combos
         */
        String REFRESH = "refresh";
        
        /**
         * Ação de  pesquisa
         */
        String SEARCH = "pesquisa";
        
        /**
         * Ação de download
         */
        String DOWNLOAD = "download";
        
        /**
         * Desconecta
         */
        String DESCONECT = "desconecta";
        
        /**
         * Abre
         */
        String OPEN = "abre";

        /**
         * Gravação de versão
         */
		String SAVE_VERSION = "gravaVersao";

		/**
		 * Recuperação de agregados (tipicamente para apresentação)
		 */
		String RETRIEVE_OBJECT_AGGREGATE = "recuperaAgregado";

		/**
		 * Inclusão de um item de sub-detalhe
		 */
		String INSERT_SUB_DETAIL = "incluiSubDet";

		/**
		 * Clonagem
		 */
		Object CLONE = "clona";

		/**
		 * Pesquisa de registros inativos (sitHistoricoPlc=I)
		 */
		Object SEARCH_INACTIVES = "pesquisaInativos";

		/**
		 * Pesquisa de registros pendentes (sitHistoricoPlc=P)
		 */
		Object SEARCH_PENDENTS = "pesquisaPendentes";


    }
    
    /**
     * jCompany 2.7. Constantes utilizadas em lógicas de fluxo de aprovação
     */
    public interface WORKFLOW {

        /**
         * Indica, no request, a ação original (nome do método que inicialmente
         * recebeu a requisição)
         */
        String IND_ORIGINAL_ACTION = "indAcaoOriginal";

        /**
         * Indica, no request, o evento origem
         *
        String IND_EVENTO_ORIGEM = "eventoOrigem";
         */
        /**
         * Indica que uma operação de exclusão ocorreu ok, no request
         */
        String EXCLUDE_FINISHED_OK = "encerrouExcluiOk";
        
        /**
         * Indica que uma operação de gravação/aprovação ocorreu ok, no request
         */
        String SAVE_APPROVAL_FINISHED_OK = "encerrouGravaAprovOk";
        
        /**
         * Indica que uma operação de pequisa ocorreu ok, no request
         */
		String SEARCH_FINISHED_OK = "encerrouPesquisaOk";
		
		 /**
         * Indica que uma operação de pequisa ocorreu ok, no request
         */
		String RETRIEVE_ON_DEMAND_FINISHED_OK = "encerrouRecuperaPorDemandaOk";

        /**
         * Indica que uma ocorreu ok, no request, para qualquer evento
         */
        String EVENT_FINISHED_OK = "encerrouEventoOk";
        
        /**
         * Auxiliar para atualização de caching
         */
        String ALREADY_UPDATED_SAVE_SIMPLE = "jaAtualizouNoGravaSimples";
        
        /**
         * Valores possíveis para STATUS_HISTORIC_PLC
         */
        public interface STATUS_HISTORIC_PLC_VALUES {

            /**
             * Objetos ativos/válidos
             */
            String ACTIVE = "A";
            /**
             * Objetos pendentes de aprovação
             */
            String APPROVAL_PENDENT = "P";
            /**
             * Objetos inativos/excluidos logicamente
             */
            String INACTIVE = "I";
        }


        /**
         * Variável de request que mantém registros de auditoria
         */
        String AUDIT_ITEMS = "itensAuditoriaPlc";

        /**
         * Indicador do portlet de auditoria na sessão
         */
        String AUDIT = "auditoriaPlc";
        
        /**
         * Flag do request que indica se um registro tem sitHistoricoPlc="A",
         * com S ou N
         */
        String IND_ACTIVE = "indAtivoPlc";

        /**
         * Coloca S ou nada no request conforme usuário corrente seja ou não
         * aprovador de um registro recém-editado
         */
        String IND_APPROVER_USER = "usuApvPlc";

        /**
         * Indicador de sitHistoricoPlc para logicas de workflow ou
         * versionamento
         */
        String IND_STATUS_HISTORIC_PLC = "sitHistoricoPlc";

        /**
         * Indicador de edição de registro para aprovação.
         */
        String IND_EDITION_APPROVAL = "apvPlc";

        /**
         * Campo no form-bean que mantém se usuário é aprovador de registro corrente.
         */
		String APPROVER_USER = "usuarioAprovadorPlc";

    }




    /**
     * jCompany 2.7. Constantes utilizadas em lógicas de manipulação de Value
     * Objects
     */
    public interface ENTITY {

    	/**
    	 * Propriedade que deve ser criada para conter "A"-Ativo, "I"-Invativo ou "P"-Pendente em lógicas
    	 * de aprovação, exclusão lógica ou versionamento.
    	 */
    	String STATUS_HISTORIC_PLC = "sitHistoricoPlc";
   		String STATUS_INACTIVE = "I";
       	String STATUS_ACTIVE = "A";
       	String STATUS_PENDENT = "P";

    	String ID_PARENT = "idPai";
    	
    	/**
    	 *@deprecated utilizar nome significativo nas coleções
    	 */
    	String PARENT_PLC = "paiPlc";
    	
    	String OBJ_DISAPPROVAL_PLC = "objReprovacaoPlc";	
    	String API_QUERY_SEL = "apiQuerySel";
    	
    	/**
    	 * Propriedades de auditoria
    	 */
    	public static final String DATE_LAST_UPDATE = "dataUltAlteracao";
    	public static final String DATE_CREATION = "dataCriacao";
      	public static final String USER_CREATION = "usuarioCriacao";
    	public static final String USER_CREATION_NAME = "usuarioCriacaoNome";
      	public static final String USER_LAST_UPDATE = "usuarioUltAlteracao";
    	public static final String USER_LAST_UPDATE_NAME = "usuarioUltAlteracaoNome";
        
        /**
         * Propriedade utilizada para controle de concorrencia otimista e versionamento
         */
		String VERSION = "versao";
    	
        /**
         * Utilizado como prefixo para guardar estado de objetos entre leitura e
         * gravação. O padrão é PREFIX_OBJ+ <nome da classe sem package>"
         */
        String PREFIX_OBJ = "objeto";

        /**
         * Identifica o pacote padrão da aplicação armazenado como variável de
         * contexto no web.xml, por exemplo com.empresa.app.vo
         */
        String PACKAGE_ENTITY_KEY = "entityPackage";

		/**
		 * Coleção de subDetalhe. Padrão anterior à 3.0
		 *
		String SUB_DETALHE_PLC = "subDetalhePlc";
		*/
        
		/**
		 * Mensagem de advertencia no ENTITY. Padrão anterior à 3.0
		 */
		String MSG_WARNING = "msgAdvertencia";
		
		/**
		 * @deprecated Utilizar ENTERPRISE
		 */
		String LOGIN_ENTERPRISE = "loginEmpresa";
		/**
		 * @deprecated Utilizar SUB_EMPRESA
		 */
		String LOGIN_SUB_ENTERPRISE = "loginSubEmpresa";
		
		String ENTERPRISE = "empresaPlc";
		String SUB_EMPRESA = "subEmpresaPlc";
		
		String ID_ENTERPRISE = "idEmpresaPlc";
		String ID_SUB_ENTERPRISE = "idSubEmpresaPlc";

    }

    
    
    /**
     * jCompany 2.7. Declarações utilizadas em lógicas padrões nos
     * Action-Mappings
     */
    public interface DEFAULT_LOGIC {

        /**
         * Logic corrente conforme declarado no action-mapping, no request
         */
        String LOGIC_PLC = "logicaPlc";
        

        /**
         * Design Patterns disponíveis no jCompany. Aparecem em "logicPlc",
         * seguidos do separador "#" e da complexidade. Ex: crud#simples
         */
        public interface DP {

            /**
             * Identifica a lógica Tabular declarada no action-mapping
             */
            String PATTERN_TABULAR = "tabular";

            /**
             * Identifica a lógica Crud declarada no action-mapping
             */
            String PATTERN_CRUD = "crud";

            /**
             * Identifica a lógica Crud-Tabular declarada no action-mapping
             */
            String PATTERN_CRUD_TABULAR = "crudtabular";

            /**
             * Identifica a lógica Preferencia de Aplicacao (Um Registro) declarada no action-mapping
             */
            String PATTERN_PREF_APPLICATION = "prefaplicacao";

            /**
             * Identifica a lógica de Preferência declarada no action-mapping
             */
            String PATTERN_USER_PREF = "prefusuario";

            /**
             * Identifica a lógica Mestre-Detalhe declarada no action-mapping
             */
            String PATTERN_MASTER_DETAIL = "mestredetalhe";

            /**
             * Identifica a lógica Mántem-Detalhe declarada no action-mapping
             */
            String PATTERN_MASTER_DETAIL_MANTAIN_DETAIL = "mandetalhe";

            /**
             * Identifica a lógica Mestre-Detalhe-Subdetalhe no action-mapping
             */
            String PATTERN_MASTER_DETAIL_SUB_DETAIL = "subdetalhe";

            /**
             * Identifica a lógica Seleção declarada no action-mapping
             */
            String PATTERN_SELECTION = "selecao";

            /**
             * Identifica a lógica de Seleção Mántem-Detalhe declarada no action-mapping
             */
            String PATTERN_SELECTION_MANTAIN_DETAIL = "selecaomandet";

            /**
             * Identifica a lógica Consulta declarada no action-mapping
             */
            String PATTERN_CONSULTATION = "consulta";

            /**
             * Identifica a lógica TreeView declarada no action-mapping
             */
            String PATTERN_CONSULTATION_TREEVIEW = "consultatreeview";

            /**
             * Identifica a lógica Relatório declarada no action-mapping
             */
            String PATTERN_REPORT = "relatorio";

            /**
             * Identifica a lógica de Controle declarada no action-mapping
             */
            String PATTERN_CONTROL = "controle";

            /**
             * Identifica a lógica de Upload declarada no action-mapping
             */
            String PATTERN_UPLOAD = "upload";

            /**
             * Identifica a lógica de Aprovoção declarada no action-mapping
             */
            String PATTERN_APPROVAL = "aprovacao";

			String PATTERN_TREEVIEW = "treeview";

			/**
			 * @deprecated Utilizar PREF_APLICACAO, novo padrao da 3.0
			 */
			String PATTERN_ONE_RECORD_OLD = "umregistro";
        }

        /**
         * Nìveis de complexidade. Aparecem em "logicaPlc", após a lógica padrão
         * e do separador "#" Ex: crud#simples
         */
        public interface COMPLEXITY {

            /**
             * Lógicas que não possuem classes especializadas, totalmente
             * generalizadas pelo jCompany. IE: Alta produtividade obtida do
             * framework (com geração 100%)
             */
            String COMPLEXITY_SIMPLE = "simples";

            /**
             * Lógicas que possuem classes especializadas de Controle ou Modelo,
             * utilizando Template Methods da forma previsata, ou seja,
             * especializando métodos com prefixo "antes", "apos" ou "api". IE:
             * Boa produtividade obtida do framework
             */
            String COMPLEXITY_MEDIUM = "media";

            /**
             * Lógicas que possuem classes especializadas de Controle ou Modelo,
             * utilizando Template Methods da forma prevista mas também com
             * sobreposições de métodos que alteram o comportamento do
             * framework. IE: Pouca produtividade obtida do framework
             */
            String COMPLEXITY_HIGH = "complexa";

            /**
             * Lógicas que possuem uma complexidade do negócio considerável,
             * independente do modo como especializam o framework IE: Pouca
             * produtividade obtida do framework
             */
            String COMPLEXITY_HIGH_BUSINESS = "complexaNegocio";

        }
    
     
        /**
         * Declarações utilizadas em mecanismos genéricos de exclusão lógica e física
         */
        public interface EXCLUSION {

            /**
             * Se exclusão for fisicamente realizada (default)
             */
            String PHYSICAL_MODE = "fisica";
            
            /**
             * Se exclusão for logicamente realizada (sitHistoricoPlc='I')
             */
            String LOGICAL_MODE = "logic";
            
        }
  
    }

    /**
     * jCompany 2.7 Constantes para modos diversos
     */
    public interface MODES {
        /**
         * Chave utilizada para lembrar o modo corrente. através de campos
         * hidden nos layouts. Este campo deve ser declarado em todo form-bean.
         */
        String MODE = "modePlc";

        /**
         * Valor que indica que o modo corrente é de consulta
         */
        String MODE_CONSULTATION = "consultaPlc";

        /**
         * Valor que indica que o modo corrente é de edicão (ou alteração) de
         * objetos
         */
        String MODE_EDITION = "alteracaoPlc";

        /**
         * Valor que indica que o modo corrente é de criação de novos objetos
         */
        String MODE_INSERTION = "inclusaoPlc";

        /**
         * Valor que indica que o modo corrente é de exclusão de objetos
         */
        String MODE_EXCLUSION = "exclusaoPlc";

        /**
         * Valor que indica que o modo corrente é de consulta para seleção de
         * registros para a manutenção
         */
        String MODE_SELECION_MAINTENANCE = "selmanutencao";

        /**
         * Valor que indica que o modo corrente é de impressão
         */
        String MODE_PRINT = "impressao";

        String MODE_DOCUMENTATION = "documentacao";

        String MODE_ASSISTANT = "assistente";

        /**
         * Valor que indica que o modo corrente é de consulta para seleção de
         * registros
         */
        String MODE_SELECTION_SIMPLE = "selsimples";
    }
    
    /**
     * Declarações utilizadas em lógicas padrões de consulta e seleção
     */
    public interface CONSULTATION {

        /**
         * Declarações utilizadas em lógicas de Query By Example
         */
        public interface QBE {

            /**
             * Terminação dos argumentos de pesquisa para montagem automática.
             * Por exemplo, _ArgINI e _ArgFIM_ são utilizados com intervalo de
             * valores, como data inicial e final.
             */
            String[] QBE_IDT_ARG = { "_Arg", "_ArgINI", "_ArgFIM", "_ArgFON" , "INI_ArgINI", "FIM_ArgFIM"};
            
            /**
             * Sufixo padrão para atributos foneticos referentes a atributos de mesmo prefixo
             */
            String QBE_ATTR_SUFFIX_PHONETICS = "Fon";

            /**
             * TODO: Documentar
             */
            String QBE_IDT_ARG_DESCONSIDER_VALUE = "DesconsiderarValor";

            /**
             * TODO: Documentar
             */
            String QBE_IDT_ARG_OPERATOR = "Operator";

            /**
             * Identifica que o type do argumento é simples, ou seja,
             * deverá ser adicionado a cláusula where.
             */
            String QBE_TYPE_ARGUMENT = "Argumento";

            /**
             * Identifica que o type do argumento é um OrderBy. Utilizando
             * para montagem dinâmico do HQL
             */
            String QBE_TYPE_ORDER_BY = "OrderBy";

            /**
             * Identifica que o type do argumento é uma query, ou seja,
             * contém a parte select e from.
             */
            String QBE_TYPE_SQL = "SQL";

            /**
             * Representa o operador "<" para montagem de HQL dinâmico
             */
            String QBE_LESS_THAN = "menor";

            /**
             * Representa o operador "<>" para montagem de HQL dinâmico
             */
            String QBE_DIFFERENT = "diferente";

            /**
             * Representa o operador "=" para montagem de HQL dinâmico
             */
            String QBE_EQUALS_TO = "igual";

            /**
             * Representa o operador ">" para montagem de HQL dinâmico
             */
            String QBE_GREATER_THAN = "maior";

            /**
             * Representa o operador ">=" para montagem de HQL dinâmico
             */
            String QBE_GREATER_OR_EQUALS_TO = "maiorOuIgual";

            /**
             * Representa o operador "<=" para montagem de HQL dinâmico
             */
            String QBE_LESS_OR_EQUALS_TO = "menorOuIgual";

            /**
             * Representa o operador "%" no final da string de pesquisa, por exemplo Maria%
             */
            String QBE_LIKE_PERC_FINAL = "*%";

            /**
             * Representa o operador "%" no início da string de pesquisa, por exemplo %aria
             */
            String QBE_LIKE_PERC_BEGIN = "%*"; // Gera coluna like

            /**
             * Representa o operador "%" no início e no final da string de pesquisa, por exemplo %Maria%
             */
            String QBE_LIKE_PERC_TOTAL = "%*%"; // Gera coluna like

            /**
             * Format alfanumérico, como deve ser declarado no
             * struts-config.xml, atributo arg.
             */
            String QBE_FORMAT_STRING = "string";

            /**
             * Format data, como deve ser declarado no struts-config.xml,
             * atributo arg
             */
            String QBE_FORMAT_DATE = "date";

            /**
             * Format numérico, como deve ser declarado no
             * struts-config.xml, atributo arg
             */
            String QBE_FORMAT_LONG = "long";
            
            /**
             * Format decimal, como deve ser declarado no
             * struts-config.xml, atributo arg
             */
            String QBE_FORMAT_BIGDECIMAL = "bigdecimal";

            /**
             * Format decimal, como deve ser declarado no
             * struts-config.xml, atributo arg
             */
            String QBE_FORMAT_BOOLEAN = "boolean";
            

            /**
             * Serviço (método) padrão para serviços de seleção na camada DAO
             */
			String API_QUERY_SEL = "apiQuerySel";
        }

    }
    
    /**
     * jCompany 3. Tipos genéricos para logicas de QBE desacoplada de engine de persistência
     */
    public interface TYPES {

        String STRING = "STRING";
        String BIG_DECIMAL = "BIG_DECIMAL";
        String LONG = "LONG";
        String INTEGER = "INTEGER";
        String DATE = "DATE";
        String TIMESTAMP = "TIMESTAMP";
        String DOUBLE = "DOUBLE";

    }
    
    /**
     * jCompany 3. Anotações Padrões
     */
    public interface ANNOTATION {

        String QUERY_SEPARATOR = ".";

        String SUFFIX_QUERYSEL_DEFAULT = "querySel";
        String SUFFIX_QUERYTREEVIEW_DEFAULT = "queryTreeView";
        String SUFFIX_QUERYSEL_QBE_DEFAULT = "querySelQBE";
        String SUFFIX_AVOID_EQUALS_DEFAULT = "avoidEquals";
        String SUFFIX_REPORT_DEFAULT = "querySelRel";
        String SUFFIX_QUERYSEL_LOOKUP = "querySelLookup";

        String SEPARATOR_FILTER = "_";
        String SUFFIX_FILTER_DEFAULT = "filtroPlc";
        
        /**
         * Anotation utilizada para NamedQuery de sub relatórios.
         */
        String SUFFIX_QUERYSELREL_SUBREPORT = "querySelRel_SubReporte_";

		String SUFFIX_QUERYEDITION_DEFAULT = "queryEdita";

		String SUFFIX_QUERYSELLOOKUP_DEFAULT = "querySelLookup";
    	
    }
    
    /**
     * 
     */
    public interface LOGGERs {

    	String JCOMPANY_QA_PROFILING 		= "org.jcompany_qa.profiling";
    	String JCOMPANY_DOC_AUTOMATED	= "org.jcompany_doc.automatizada.profiling";
    	String JCOMPANY_VIEW				= "org.jcompany.log.view";
    	String JCOMPANY_CONTROL			= "org.jcompany.log.control";
    	String JCOMPANY_MODEL				= "org.jcompany.log.model";
    	String JCOMPANY_PERSISTENCE		= "orgjcompany.log.persistence";
    	String JCOMPANY_WARNING_DEVELOPMENT = "org.jcompany.advertencia";
    
    }
    
    /**
     * jCompany 3. Tipos genéricos para logicas de QBE desacoplada de engine de persistência
     */
    public interface MSG {

        String PROP_TITLE_AUTOMATIC = "###propAutomaticTitlePlc###";

    }

}