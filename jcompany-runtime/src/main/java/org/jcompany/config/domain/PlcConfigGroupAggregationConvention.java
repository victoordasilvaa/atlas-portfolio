package org.jcompany.config.domain;



/**
 * Convenções - Versão POJO da configuracao de nome correlato, para convenção sobre a configuração
 */
public class PlcConfigGroupAggregationConvention {

    /**
     * Define a classe principal da agregação 
     */
     private Class entity;
    
    /**
     * Define o padrão(pattern) da agregação. É assumido o default "CONTROL" para casos onde nao há anotaçao de metadados.
     */
     private PlcConfigPatternConvention configPattern;
	
    /**
     * Define a lista de componentes da agregação. 
     * Importante: Deve iniciar com nulo, como indicador de que ainda não investigou a anotação (caching)
     */
    private PlcConfigComponentConvention[] components;
    
    /**
     * Define a lista de descendentes da agregação
     */
    private PlcConfigDescendentConvention[] descendents;

    /**
     * Define a lista de detalhes da agregação
     * Importante: Deve iniciar com nulo, como indicador de que ainda não investigou a anotação (caching)
     */
    private PlcConfigDetailConvention[] details;
    
    /**
     * Define as opções de segurança da agregação
     * @see PlcConfigSecurity
     */
    private PlcConfigSecurityConvention security;
    
    /**
     * Define as opções de preferência do usuário
     * @see PlcConfigUserPref
     */
    private PlcConfigUserPrefConvention userPreference;
    
    /**
     * Define as opções do arquivo anexado
     * @see org.jcompany.config.commons.PlcConfigAttachedFile 
     */
    private PlcConfigAttachedFileConvention attachedFile;
    
    /**
     * Define a lista de classes lookup da agregação
     */
    private Class[] classesLookup;
    
    /**
     * Defina a lista de classes de dominio discrte da agregacao
     */
    private Class<? extends Enum>[] classesDomainDiscreet;

	public Class entity() {
		return entity;
	}

	public void setEntity(Class entity) {
		this.entity = entity;
	}

	public PlcConfigPatternConvention configPattern() {
		return configPattern;
	}

	public PlcConfigComponentConvention[] components() {
		return components;
	}

	public void setComponents(PlcConfigComponentConvention[] components) {
		this.components = components;
	}

	public PlcConfigDescendentConvention[] descendents() {
		return descendents;
	}

	public void setDescendents(PlcConfigDescendentConvention[] descendents) {
		this.descendents = descendents;
	}

	public PlcConfigDetailConvention[] details() {
		return details;
	}

	public void setDetails(PlcConfigDetailConvention[] details) {
		this.details = details;
	}

	public PlcConfigSecurityConvention security() {
		return security;
	}

	public void setSecurity(PlcConfigSecurityConvention security) {
		this.security = security;
	}

	public PlcConfigUserPrefConvention userPreference() {
		return userPreference;
	}

	public void setUserPreference(PlcConfigUserPrefConvention userPreference) {
		this.userPreference = userPreference;
	}

	public PlcConfigAttachedFileConvention attachedFile() {
		return attachedFile;
	}

	public void setAttachedFile(PlcConfigAttachedFileConvention attachedFile) {
		this.attachedFile = attachedFile;
	}

	public Class[] getClassesLookup() {
		return classesLookup;
	}

	public void setClassesLookup(Class[] classesLookup) {
		this.classesLookup = classesLookup;
	}

	public Class<? extends Enum>[] classesDomainDiscreet() {
		return classesDomainDiscreet;
	}

	public void setClassesDomainDiscreet(
			Class<? extends Enum>[] classesDomainDiscreet) {
		this.classesDomainDiscreet = classesDomainDiscreet;
	}


	public void setConfigPattern(PlcConfigPatternConvention configPattern) {
		this.configPattern = configPattern;
	}

}
