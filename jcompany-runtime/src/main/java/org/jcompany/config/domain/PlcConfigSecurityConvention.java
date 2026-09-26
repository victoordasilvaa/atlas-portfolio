package org.jcompany.config.domain;

/**
 * Convenções para definição de segurança na ação
 */
public class PlcConfigSecurityConvention {

	/**
     * Define a relação de papéis (roles) que podem fazer manutenção e consulta em uma Agregação
     */
    String[] maintenanceRoles;

    /**
     * Define a relação de papéis (roles) que podem fazer somente consulta em uma Agregação
     */
    String[] onlyConsultationRoles;

	public String[] getMaintenanceRoles() {
		return maintenanceRoles;
	}

	public void setMaintenanceRoles(String[] maintenanceRoles) {
		this.maintenanceRoles = maintenanceRoles;
	}

	public String[] getOnlyConsultationRoles() {
		return onlyConsultationRoles;
	}

	public void setOnlyConsultationRoles(String[] onlyConsultationRoles) {
		this.onlyConsultationRoles = onlyConsultationRoles;
	}
}
