package org.jcompany.config.domain;

/**
 * Convenções para definição de componentes
 */
public class PlcConfigComponentConvention {

	 /**
     * Configuração do Value Object ancestral para componente 
     */
    private Class clazz;

    /**
     * Nome da propriedade do componente na Entidade principal 
     */
    private String property;

	public Class clazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String property() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}
