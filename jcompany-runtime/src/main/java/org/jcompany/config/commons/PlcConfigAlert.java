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

import org.jcompany.config.metadata.PlcMetaConfig;
import org.jcompany.config.metadata.PlcMetaEditor;
import org.jcompany.config.metadata.PlcMetaEditorParameter;
import org.jcompany.config.metadata.PlcMetaConfig.LAYER;
import org.jcompany.config.metadata.PlcMetaConfig.SCOPE;


@Documented
@Target(PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@PlcMetaConfig(root=true, scope=SCOPE.APP, layer=LAYER.COMMONS)
@PlcMetaEditor(label="Alerta de Erros", description="Configurações globais de definição de sistema de alerta de erros e envio de email")
/**
 * @since jCompany 3.2.1 Configurações globais de definição de sistema de alerta de erros e envio de email.
 */
public @interface PlcConfigAlert {

	/**
	 * Indica com qual tecnologia o Appender Log4j que envia emails proverá a saída do email
	 */
	public enum SendMailTechnology {
		/**
		 * Envio via JMS. Exige um endereço JMS em jmsInitialContextProviderURL que contenha uma versão do eCompany Monitor.
		 */
		JMS,
		/**
		 * Envio via Javamail. Exige um endereço de servidor de email que contenha uma versão do eCompany Monitor.
		 */
		JAVAMAIL,
		/**
		 * Não usa 
		 */
		NONE
	}
	
	/**
	 * Indica com qual tecnologia o Appender Log4j que envia emails proverá a saída do email
	 */
	@PlcMetaEditorParameter(label="Technology Envio de E-mail", description="Indica com qual tecnologia o Appender Log4j que envia emails proverá a saída do email")
	SendMailTechnology sendMailTechnology() default SendMailTechnology.NONE;
	
	/**
	 * Conta de email que será utilizada como rementete das mensagens de erro fatais.
	 */
	@PlcMetaEditorParameter(label="E-mail do Remetente", description="Conta de email que será utilizada como rementete das mensagens de erro fatais", size=20)
	String emailSender() default "#";
	
	/**
	 * Email que receberá mensagens de erros de nível FATAL (default # não envia).
	 */
	@PlcMetaEditorParameter(label="E-mails para Fatal", description="Email que receberá mensagens de erros de nível FATAL", size=20)
	String[] emailFatal() default "#";
	
	/**
	 * Email que receberá mensagens de erros de nível ERROR (default # não envia).
	 */
	@PlcMetaEditorParameter(label="E-mails para Fatal", description="Email que receberá mensagens de erros de nível ERROR", size=20)
	String[] emailError() default "#";
	
	/**
	 * java.naming.provider.url - Endereço do provider JMS. Ex: jnp://localhost:1099
	 */
	@PlcMetaEditorParameter(label="Endereço do provider JMS", description="java.naming.provider.url - Endereço do provider JMS. Ex: jnp://localhost:1099", size=20)
	String jmsInitialContextProviderURL() default "";
	
	/**
	 * Indica que a aplicação utiliza o jMonitor.
	 */
	@PlcMetaEditorParameter(label="Usa?", description="Indica que a aplicação utiliza o jMonitor")
	boolean monitorUse() default  false;
    	
	
	@PlcMetaEditorParameter(label="Endereço SMTP", description="Endereço do servidor de SMTP", size=20)
	String mailSmtpHost() default "";
	
	@PlcMetaEditorParameter(label="Pseudo Produção", description="Simula Modo de Produção")
	boolean pseudoProduction() default false;
	
	/**
	 * Se o jCompany sempre exigirá uma mensagem declarada no arquivo de bundle para cada Foreign Key que possa disparar
	 * erro de integridade referencial. O padrão é não exigir (caso não encontre uma mensagem, exibe a mensagem de erro padrão)
	 */
	@PlcMetaEditorParameter(label="Sempre Traduz Integridade Referencial", description="Se o jCompany sempre exigirá uma mensagem declarada no arquivo de bundle para cada Foreign Key que possa disparar erro de integridade referencial.")
	boolean alwaysTranslateReferentialIntegrity() default false;
	
}
