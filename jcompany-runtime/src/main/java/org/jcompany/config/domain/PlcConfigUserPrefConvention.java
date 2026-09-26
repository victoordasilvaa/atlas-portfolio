package org.jcompany.config.domain;

/**
 * Convenções para definição de opções específicas para lógica Preferencia de Usuário.
 */
public class PlcConfigUserPrefConvention {
	 /**
     * Armazena o nome da propriedade de utilizado na lógica de preferência de usuário para recuperação.
     */
    private String arg="login";

	public String arg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}
}
