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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Design Pattern Context Param.
 * @since jCompany 2.7.3. 
 * Classe que agrupa informações de contexto para camada modelo,
 * de forma a viabilizar o desacoplamento.
*/
public class PlcBaseContextVO implements Serializable {

	protected static final String SEPARATOR_SERVICE_PERSISTENCE = ".";
	private static final long serialVersionUID = 1L;
	
	/* Informações de contexto */
	protected Integer levelAop=0;
	protected String factoryPlc = "default";
	protected String managerClassName;
	protected PlcBaseUserProfileEntity userProfile;
	protected List avoidEquals;
	protected List avoidEqualsValMax;
	protected List<String> detailNames;
	protected Map<String,Class> detailsOnDemand;
	protected List<String> detailNamesPlc;
	protected String verticalFilter;
	protected String manyToOneLazyOptimize=null;
	protected String updateOptimize=null;
	/**
	 * jCompany 3.0 Lógica sem complexida, em minúscula. Ex: tabular, crudtabular, etc.
	 */
	protected String logic;
	/**
	 * Classe principal do grafo
	 */
	protected Class mainClass;
	protected String actionName;
	protected String excludeModeAux = "F"; // Pode ser "L"-Lógico ou "F"-Físico
	protected String historicModeAux = ""; // Pode ser ""-Sem Historico, "A"-Aprovação, "V"-Versão
	protected String approvalExcAux = ""; // Pode ser "n"-nenhum - mantém logicamente sitHistoricoPlc="I", "t"-todos. Exclui Após Aprovação e Reprovação, "r"-reprov. Excluir Somente Reprovados
	protected String historicPropsAux = ""; // Relação de colunas que, quando alteradas, geram histórico.
	protected String originalAction = ""; // Ação (botão) clicado originalmente, exceto em unespecified, onde a ação default é disponibilizada.
	private boolean approver = false; // Se usuário corrente é approver
	private boolean usesRetrieveAggregatedClass = true;
	private String primaryKeyClass;
	private String avoidChangeId = "N";
	private String apiQuerySel = null;

	/* Informações de contexto após 2.7.2 */
	private String lookupNavegationClasses;
	private boolean retrievePendent = false;
	
	/* Arquivo Anexado */
	protected IPlcFileEntity attachedFile = null;
	protected Long idAttachedFilePlc = null;
	protected String attachedFileNamePlc;
	
	protected String subDetailPropNameCollection= null;
	protected String subDetailClass= null;
	protected String subDetailParent= null;
	
	/**
	 * Otimização de updates
	 */
	protected String detailRemember = null;
	
	/**
	 * Otimização de updates
	 */
	protected String masterRemember = null;
	
	/*
	 * 
	 */
	protected String reportCurrentNamedQuery = null;
	
	/**
	 * jCompany 3.0 Nome do serviço de query da camada persistência. Se informado, indica para a
	 * camada persistencia executar dinamicamente query com este nome (Ex: CandidatoVO.minhaQuery exige declaração de "minhaQuery"
	 * em persistenceServiceName.
	 */
	protected String persistenceServiceName = "";
	
	/**
	 * jCompany 3.0 Gerenciadores da camada persistencia para casos de mais de uma fábrica
	 */
	private String persistenceServiceManagers= null;
	
	/**
	 * Sigla da aplicação.
	 * 
	 * @since jCompany 3.0
	 */
	private String applicationInitials;

	/**
	 * Mantém referencia ao grafo anterior do Vo principal, para otimizações de update
	 * na camada Modelo/Persistencia. (especialmente em Listeners)
	 */
	private Object entityPrevious;

	private Object entity;

	/**
	 * Auxiliar para otimização de atualizacao via Listeners
	 */
	private Integer versionPrevious;

	/**
     * Marca se esta utilizando o padrão de 2.x e não o padrão 3.x.
     */
    private String classicUse = "N";
    
    /**
     * Armazena o nome da propriedade de utilizado na lógica de preferência de usuário para recuperação. 
     */
    private String argPreference;
    
    /**
     * Identifica o pacote padrão da aplicação armazenado como variável de
     * contexto no web.xml, por exemplo: <code>com.empresa.app.vo</code>
     */
    private String packageEntity;
    
    /**
     * Semáforo para 'lembrar' se houve atualizacao nos listeners
     */
    private Boolean updated=null;
    
    /**
     * Array com os módulos anotados para a aplicação
     */
    private String[] modules;

    
    /**
     * @return Retorna o approver.
     * @since jCompany 3.0
     */
    public boolean isApprover() {
        return this.approver;
    }
    /**
     * @param approver O approver a ser definido
     * @since jCompany 3.0
     */
    public void setApprover(boolean approver) {
        this.approver = approver;
    }
    /**
     * @return Retorna o primaryKeyClass.
     * @since jCompany 3.0
     */
    public String getPrimaryKeyClass() {
        return this.primaryKeyClass;
    }
    /**
     * @param primaryKeyClass O primaryKeyClass a ser definido.
     * @since jCompany 3.0
     */
    public void setPrimaryKeyClass(String primaryKeyClass) {
        this.primaryKeyClass = primaryKeyClass;
    }
    /**
     * @return Retorna o avoidChangeId.
     * @since jCompany 3.0
     */
    public String getAvoidChangeId() {
        return this.avoidChangeId;
    }
    /**
     * @param avoidChangeId O avoidChangeId a ser definido.
     * @since jCompany 3.0
     */
    public void setAvoidChangeId(String avoidChangeId) {
        this.avoidChangeId = avoidChangeId;
    }
    /**
     * @return Retorna o usesRetrieveAggregatedClass.
     * @since jCompany 3.0
     */
    public boolean isUsesRetrieveAggregatedClass() {
        return this.usesRetrieveAggregatedClass;
    }
    /**
     * @param usesRetrieveAggregatedClass O usesRetrieveAggregatedClass a ser definido.
     * @since jCompany 3.0
     */
    public void setUsesRetrieveAggregatedClass(boolean usesRetrieveAggregatedClass) {
        this.usesRetrieveAggregatedClass = usesRetrieveAggregatedClass;
    }
    /**
     * @return Retorna o originalAction.
     * @since jCompany 3.0
     */
    public String getOriginalAction() {
        return this.originalAction;
    }
    /**
     * @param originalAction O originalAction a ser definido.
     * @since jCompany 3.0
     */
    public void setOriginalAction(String originalAction) {
        this.originalAction = originalAction;
    }
    /**
     * @return Retorna o approvalExcAux.
     * @since jCompany 3.0
     */
    public String getApprovalExcAux() {
        return this.approvalExcAux;
    }
    /**
     * @param approvalExcAux O approvalExcAux a ser definido.
     * @since jCompany 3.0
     */
    public void setApprovalExcAux(String approvalExcAux) {
        this.approvalExcAux = approvalExcAux;
    }
    /**
     * @return Retorna o excludeModeAux.
     * @since jCompany 3.0
     */
    public String getExcludeModeAux() {
        return this.excludeModeAux;
    }
    /**
     * @param excludeModeAux O excludeModeAux a ser definido.
     * @since jCompany 3.0
     */
    public void setExcludeModeAux(String excludeModeAux) {
        this.excludeModeAux = excludeModeAux;
    }
    /**
     * @return Retorna o factoryPlc.
     * @since jCompany 3.0
     */
    public String getFactoryPlc() {
        return this.factoryPlc;
    }
    /**
     * @param factoryPlc O factoryPlc a ser definido.
     * @since jCompany 3.0
     */
    public void setFactoryPlc(String factoryPlc) {
        this.factoryPlc = factoryPlc;
    }
    /**
     * @return Retorna o verticalFilter.
     * @since jCompany 3.0
     */
    public String getVerticalFilter() {
        return this.verticalFilter;
    }
    /**
     * @param verticalFilter O verticalFilter a ser definido.
     * @since jCompany 3.0
     */
    public void setVerticalFilter(String verticalFilter) {
        this.verticalFilter = verticalFilter;
    }
    /**
     * @return Retorna o historicModeAux.
     * @since jCompany 3.0
     */
    public String getHistoricModeAux() {
        return this.historicModeAux;
    }
    /**
     * @param historicModeAux O historicModeAux a ser definido.
     * @since jCompany 3.0
     */
    public void setHistoricModeAux(String historicModeAux) {
        this.historicModeAux = historicModeAux;
    }
    /**
     * @return Retorna o historicPropsAux.
     * @since jCompany 3.0
     */
    public String getHistoricPropsAux() {
        return this.historicPropsAux;
    }
    /**
     * @param historicPropsAux O historicPropsAux a ser definido.
     * @since jCompany 3.0
     */
    public void setHistoryPropsAux(String historicPropsAux) {
        this.historicPropsAux = historicPropsAux;
    }
    /**
     * @return Retorna o logicaPlc.
     * @since jCompany 3.0
     */
    public String getLogic() {
        return this.logic;
    }
    /**
     * @param logicaPlc O logicaPlc a ser definido.
     * @since jCompany 3.0
     */
    public void setLogic(String logicaPlc) {
        this.logic = logicaPlc;
    }
    /**
     * @return Retorna o avoidEquals.
     * @since jCompany 3.0
     */
    public List getAvoidEquals() {
        return this.avoidEquals;
    }
    /**
     * @param avoidEquals O avoidEquals a ser definido.
     * @since jCompany 3.0
     */
    public void setAvoidEquals(List avoidEquals) {
        this.avoidEquals = avoidEquals;
    }
    /**
     * @return Retorna o avoidEqualsValMax.
     * @since jCompany 3.0
     */
    public List getAvoidEqualsValMax() {
        return this.avoidEqualsValMax;
    }
    /**
     * @param avoidEqualsValMax O avoidEqualsValMax a ser definido.
     * @since jCompany 3.0
     */
    public void setAvoidEqualsValMax(List avoidEqualsValMax) {
        this.avoidEqualsValMax = avoidEqualsValMax;
    }
    /**
     * @return Retorna o actionName.
     * @since jCompany 3.0
     */
    public String getActionName() {
        return this.actionName;
    }
    /**
     * @param actionName O actionName a ser definido.
     * @since jCompany 3.0
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
    /**
     * @return Retorna o managerClassName.
     * @since jCompany 3.0
     */
    public String getManagerClassName() {
        return this.managerClassName;
    }
    /**
     * @param managerClassName O managerClassName a ser definido.
     * @since jCompany 3.0
     */
    public void setManagerClassName(String managerClassName) {
        this.managerClassName = managerClassName;
    }
    /**
     * @return Retorna uma coleçao contendo os nomes das classes com package de todos os detalhes.
     * @since jCompany 3.0
     */
    public List<String> getDetailNames() {
        return this.detailNames;
    }
    /**
     * @param detailNames O detailNames a ser definido.
     * @since jCompany 3.0
     */
    public void setDetailNames(List<String> detailNames) {
        this.detailNames = detailNames;
    }
    /**
     * @return Retorna uma coleção contendo os nomes das propriedades de coleçoes de todos os detalhes
     * @since jCompany 3.0
     */
    public List<String> getDetailNamesPlc() {
        return this.detailNamesPlc;
    }
    /**
     * @param detailNamesPlc O detailNamesPlc a ser definido.
     * @since jCompany 3.0
     */
    public void setDetailNamesPlc(List<String> detailNamesPlc) {
        this.detailNamesPlc = detailNamesPlc;
    }
    /**
     * @return Retorna o userProfile.
     * @since jCompany 3.0
     */
    public PlcBaseUserProfileEntity getUserProfile() {
        return this.userProfile;
    }
    /**
     * @param userProfile O userProfile a ser definido.
     * @since jCompany 3.0
     */
    public void setUserProfile(PlcBaseUserProfileEntity userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * @return Retorna o lookupNavegationClasses.
     * @since jCompany 3.0
     */
    public String getLookupNavigationClasses() {
        return this.lookupNavegationClasses;
    }
    /**
     * @param lookupNavegationClasses O lookupNavegationClasses a ser definido.
     * @since jCompany 3.0
     */
    public void setLookupNavigationClasses(String lookupNavegationClasses) {
        this.lookupNavegationClasses = lookupNavegationClasses;
    }
    /**
     * @return Retorna o retrievePendent.
     * @since jCompany 3.0
     */
    public boolean isRetrievePendent() {
        return this.retrievePendent;
    }
    /**
     * @param retrievePendent O retrievePendent a ser definido.
     * @since jCompany 3.0
     */
    public void setRetrievePendent(boolean retrievePendent) {
        this.retrievePendent = retrievePendent;
    }
    
    /**
     * @since jCompany 3.0
     */
	public IPlcFileEntity getAttachedFile() {
		return attachedFile;
	}
	/**
     * @since jCompany 3.0
     */
	public void setAttachedFile(IPlcFileEntity attachedFile) {
		
		this.attachedFile = attachedFile;
	}
	/**
     * @since jCompany 3.0
     */
	public Long getIdAttachedFilePlc() {
		return idAttachedFilePlc;
	}
	/**
     * @since jCompany 3.0
     */
	public void setIdAttachedFilePlc(Long idAttachedFilePlc) {
		this.idAttachedFilePlc = idAttachedFilePlc;
	}
	/**
     * @since jCompany 3.0
     */
	public String getAttachedFileNamePlc() {
		return attachedFileNamePlc;
	}
	/**
     * @since jCompany 3.0
     */
	public void setAttachedFileNamePlc(String attachedFileNamePlc) {
		this.attachedFileNamePlc = attachedFileNamePlc;
	}
	/**
	 * @return Returns the persistenceServiceName. Se for informado retorna com '_' para facilitar lógicas
	 * @since jCompany 3.0
	 */
	public String getPersistenceServiceName() {
		if (persistenceServiceName.equals(""))
			return "";
		else
			return SEPARATOR_SERVICE_PERSISTENCE+persistenceServiceName;
	}
	/**
	 * @param persistenceServiceName The persistenceServiceName to set.
	 * @since jCompany 3.0
	 */
	public void setPersistenceServiceName(String persistenceServiceName) {
		this.persistenceServiceName = persistenceServiceName;
	}
	/**
	 * @return Returns the apiQuerySel.
	 * @since jCompany 3.0
	 */
	public String getApiQuerySel() {
		return apiQuerySel;
	}
	/**
	 * @param apiQuerySel The apiQuerySel to set.
	 * @since jCompany 3.0
	 */
	public void setApiQuerySel(String apiQuerySel) {
		this.apiQuerySel = apiQuerySel;
	}
	/**
	 * @return Returns the persistenceServiceManagers.
	 * @since jCompany 3.0
	 */
	public String getPersistenceServiceManagers() {
		return persistenceServiceManagers;
	}
	/**
	 * @param persistenceServiceManagers The persistenceServiceManagers to set.
	 * @since jCompany 3.0
	 */
	public void setPersistenceServiceManagers(String persistenciaServiceManagers) {
		this.persistenceServiceManagers = persistenciaServiceManagers;
	}
	
	/**
	 * @since jCompany 3.0
	 */
	public String getReportCurrentNamedQuery() {
		return reportCurrentNamedQuery;
	}
	
	/**
	 * @since jCompany 3.0
	 */
	public void setReportCurrentNamedQuery(String reportCurrentNamedQuery) {
		this.reportCurrentNamedQuery = reportCurrentNamedQuery;
	}
	/**
	 * @return Returns the subDetailPropNameCollection.
	 * @since jCompany 3.0
	 */
	public String getSubDetailPropNameCollection() {
		return subDetailPropNameCollection;
	}
	/**
	 * @param subDetailPropNameCollection The subDetailPropNameCollection to set.
	 * @since jCompany 3.0
	 */
	public void setSubDetailPropNameCollection(String subDetailPropNameCollection) {
		this.subDetailPropNameCollection = subDetailPropNameCollection;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getApplicationInitials() {
		return applicationInitials;
	}
	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setApplicationInitials(String applicationInitials) {
		this.applicationInitials = applicationInitials;
	}
	/**
	 * @return Nível do AOP automáticao do jcompany configurado como -1 (desligado), 0 (somente Façade) ou 1 (todas as classes de primeiro nível)
	 */
	public Integer getLevelAop() {
		return levelAop;
	}
	/**
	 * @param levelAop Inclui um nível de AOP (-1, 0 ou 1) para uso na camada Modelo, obtido de configuraçoes em caching
	 */
	public void setLevelAop(Integer levelAop) {
		this.levelAop = levelAop;
	}
	/**
	 * @return Retorna subDetailClass.
	 */
	public String getSubDetailClass() {
		return subDetailClass;
	}
	/**
	 * @param subDetailClass Registra subDetailClass
	 */
	public void setSubDetailClass(String subDetailClass) {
		this.subDetailClass = subDetailClass;
	}
	/**
	 * @return Retorna detailsOnDemand.
	 */
	public Map<String, Class> getDetailsOnDemand() {
		return detailsOnDemand;
	}
	/**
	 * @param detailsOnDemand Registra detailsOnDemand
	 */
	public void setDetailsOndDemand(Map<String, Class> detailsOnDemand) {
		this.detailsOnDemand = detailsOnDemand;
	}
	
	/**
	 * Recebe o nome de uma propriedade de detalhe e testa se é por demanda
	 * @param detailColumnName nome da coluna de detalhe.
	 * @return true se for por demanda ou false (default)
	 */
	public boolean isOnDemand(String detailColumnName) {
		return detailsOnDemand!=null && detailsOnDemand.containsKey(detailColumnName);
	}
	/**
	 * @return Retorna detailRemember.
	 */
	public String getDetailRemember() {
		return detailRemember;
	}
	/**
	 * @param detailRemember Registra detailRemember
	 */
	public void setDetailRemember(String detailRemember) {
		this.detailRemember = detailRemember;
	}
	/**
	 * @return Retorna manyToOneLazyOptimize.
	 */
	public String getManyToOneLazyOptimize() {
		return manyToOneLazyOptimize;
	}
	/**
	 * @param manyToOneLazyOptimize Registra manyToOneLazyOptimize
	 */
	public void setManyToOneLazyOptimize(String manyToOneLazyOptimize) {
		this.manyToOneLazyOptimize = manyToOneLazyOptimize;
	}
	/**
	 * @return Retorna mainClass.
	 */
	public Class getMainClass() {
		return mainClass;
	}
	/**
	 * @param mainClass Registra mainClass
	 */
	public void setMainClass(Class mainClass) {
		this.mainClass = mainClass;
	}
	/**
	 * @return Retorna updateOptimize.
	 */
	public String getUpdateOptimize() {
		return updateOptimize;
	}
	/**
	 * @param updateOptimize Registra updateOptimize
	 */
	public void setUpdateOptimize(String updateOptimize) {
		this.updateOptimize = updateOptimize;
	}
	/**
	 * @return Retorna masterRemember.
	 */
	public String getMasterRemember() {
		return masterRemember;
	}
	/**
	 * @param masterRemember Registra masterRemember
	 */
	public void setMasterRemember(String masterRemember) {
		this.masterRemember = masterRemember;
	}
	/**
	 * @return Retorna entityPrevious.
	 */
	public Object getEntityPrevious() {
		return entityPrevious;
	}
	/**
	 * @param entityPrevious Registra entityPrevious
	 */
	public void setEntityPrevious(Object entityPrevious) {
		this.entityPrevious = entityPrevious;
	}
	/**
	 * @return Retorna versionPrevious.
	 */
	public Integer getVersionPrevious() {
		return versionPrevious;
	}
	/**
	 * @param versionPrevious Registra versionPrevious
	 */
	public void setVersionPrevious(Integer versionPrevious) {
		this.versionPrevious = versionPrevious;
	}

	/**
     * @return classicUse
	 */
    public String getClassicUse() {
        return classicUse;
    }
    
    /**
     * @param classicUse
     */
    public void setClassicUse(String classicUse) {
        this.classicUse = classicUse;
    }
    
	public String getArgPreference() {
		return argPreference;
	}
	
	public void setArgPreference(String argPreference) {
		this.argPreference = argPreference;
	}
    public String getPackageEntity() {
        return packageEntity;
    }
    public void setPackageEntity(String packageEntity) {
        this.packageEntity = packageEntity;
    }
	public String getSubDetailParent() {
		return subDetailParent;
	}
	public void setSubDetailParent(String subDetailParent) {
		this.subDetailParent = subDetailParent;
	}
	public Boolean getUpdated() {
		return updated;
	}
	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}
	public String[] getModules() {
		return modules;
	}
	public void setModules(String[] modules) {
		this.modules = modules;
	}
	public void setEntity(Object entity) {
		this.entity = entity;
	}
	public Object getEntity() {
		return entity;
	}


}