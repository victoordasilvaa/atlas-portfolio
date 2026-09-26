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
 * $Id: PlcAdmEmailEntity.java,v 1.3 2006/05/17 19:53:56 joaopaulo_santos Exp $
 */
package org.jcompany.commons;

import java.io.Serializable;

/**
 * Classe utilizada para envio de email. Deve ser preenchida antes de enviar para
 * o jMonitor.
 * @author Roberto Lúcio Badaró
 * @version $Revision: 1.3 $$Date: 2006/05/17 19:53:56 $$Autho
 */
public class PlcAdmEmailEntity extends PlcAdmEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8944547420772197758L;

	/** Indica que a mensagem está em format html */
	public static final String FORMAT_HTML = "H";
	
	/** Indica que a mensagem está em format plain-text (sem formatação) */ 
	public static final String FORMAT_TEXTO = "T";
	
	private String subject;
	private String sender;
	private String to;
	private String format = FORMAT_HTML;


	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getTo()
	{
		return to;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public String getSender()
	{
		return sender;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setSubject(String subject)
	{
		subject = subject;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setReceiver(String to)
	{
		to = to;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setFormat(String format)
	{
		format = format;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setSender(String sender)
	{
		this.sender = sender;
	}

}