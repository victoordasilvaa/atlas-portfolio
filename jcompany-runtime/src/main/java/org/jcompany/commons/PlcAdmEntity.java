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
/*
 * $Id: PlcAdmEntity.java,v 1.3 2006/05/17 19:53:56 joaopaulo_santos Exp $
 */
package org.jcompany.commons;

import java.io.Serializable;
import java.util.Date;


/**
 * Classe ancestral para envio via JMS para o jMonitor.
 * <p> Possui as informações básicas, comuns a todos os tipos de classe de
 * monitoramento utilizadas pelo jMonitor.
 * </p>
 * @author Roberto Lúcio Badaró
 * @version $Revision: 1.3 $$Date: 2006/05/17 19:53:56 $
 */
public class PlcAdmEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7899465199245302642L;
	public static final String CATEGORY_INFO = "INFO";
	public static final String CATEGORY_DEBUG = "DEBUG";
	public static final String CATEGORY_ERROR = "ERROR";
	public static final String CATEGORY_FATAL = "FATAL";
	public static final String CATEGORY_WARN = "WARN";

	private String initialsApplication;
	private String nameApplication;
	private Date   datetimeSend;
	private String currentUser;
	private String object;
	private String serverName;

	/**
	 * Identifica o type de object: CLASSE, LINK, BUSCA, etc.
	 * @deprecated Não será necessário.
	 */
	private String typeObject;

	private String message;

	/**
	 * Guarda o resultado de Throwable.stackTrace() quando houver Exception;
	 */
	private String[] stackTrace;


	private String emailFatal;
	private String emailError;
	private String emailEnterprise;

	/**
	 * Usa a mesma definição do Log4J para idenficar o nível de log:<br>
	 * INFO
	 * DEBUG
	 * ERROR
	 * FATAL
	 * WARN
	 */
	private String category;


    /**
     * 
     * @since jCompany 3.0
     */
	public Date getDatetimeSend()
	{
		return datetimeSend;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getEmailEnterprise()
	{
		return emailEnterprise;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getEmailError()
	{
		return emailError;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getEmailFatal()
	{
		return emailFatal;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getNameApplication()
	{
		return nameApplication;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getObject()
	{
		return object;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getInitialsApplication()
	{
		return initialsApplication;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getCurrentUser()
	{
		return currentUser;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setDatetimeSend(Date datetimeSend)
	{
		this.datetimeSend = datetimeSend;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setEmailEnterprise(String emailEnterprise)
	{
		this.emailEnterprise = emailEnterprise;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setEmailError(String emailError)
	{
		this.emailError = emailError;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setEmailFatal(String emailFatal)
	{
		this.emailFatal = emailFatal;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setNameApplication(String nameApplication)
	{
		this.nameApplication = nameApplication;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setObject(String object)
	{
		this.object = object;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setInitialsApplication(String initialsApplication)
	{
		this.initialsApplication = initialsApplication;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setCurrentUser(String currentUser)
	{
		this.currentUser = currentUser;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setCategory(String string)
	{
		category = string;
	}

	
	public String getTypeObject()
	{
		return typeObject;
	}

	public void setTypeObject(String typeObject)
	{
		this.typeObject = typeObject;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String[] getStackTrace()
	{
		return stackTrace;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setStackTrace(String[] stackTrace)
	{
		this.stackTrace = stackTrace;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getServerName()
	{
		return serverName;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}



	/**
	 * Devolve String identificando o conteudo.
	 * <p>
	 * Exemplos de retorno:
	 * <ul>
	 * <li>PlcAdmEstatisticaVO: menu -> Personalizar - Layout</li>
	 * <li>PlcAdmAuditoriaVO: ciclo-de-vida -> Inicio</li>
	 * <li>PlcAdmLoggingVO: jCompany -> Aplicação iniciada</li>
	 * <li>PlcAdmEmailEntity: [assunto-do-email] </li>
	 * </ul>
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	public String toString()
	{
		String clazz = this.getClass().getName();

		int p = clazz.lastIndexOf('.');
		if (p != -1) clazz = clazz.substring(p +1);

		StringBuffer ts = new StringBuffer();

		ts.append(clazz)
		  .append(": ");

		if ( !(this instanceof PlcAdmEmailEntity) )
		{
			ts.append(object)
			  .append(" -> ")
			  .append(message);
		}
		else
		{
			ts.append( ((PlcAdmEmailEntity) this).getSubject() );
		}

		return ts.toString() ;
	}


}