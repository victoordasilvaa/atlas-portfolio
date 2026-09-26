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
 * $Id: PlcAdmEmailFullEntity.java,v 1.3 2006/05/17 19:53:56 joaopaulo_santos Exp $
 */
package org.jcompany.commons;

/**
 * Classe para envio de email com todas as opções de cabeçalho:<br>
 * <ul>
 * <li>remetente</li>
 * <li>destinatario</li>
 * <li>toCc</li>
 * <li>toBcc</li>
 * </ul>
 * @author Roberto Lúcio Badaró
 * @version $Revision: 1.3 $$Date: 2006/05/17 19:53:56 $
 */
public class PlcAdmEmailFullEntity extends PlcAdmEmailWithAttachedFileEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9202042322239681193L;
	private String toCc;
	private String toBcc;
	private String sender;
	private String replyTo;



	/**
	 * @return Endereços para envio com cópia oculta.
	 * 
	 * @since jCompany 3.0
	 */
	public String getToBcc()
	{
		return toBcc;
	}

	/**
	 * @return Endereços para envio com cópia.
	 * 
	 * @since jCompany 3.0
	 */
	public String getToCc()
	{
		return toCc;
	}

	/**
	 * @param string Endereços para envio com cópia oculta.
	 * 
	 * @since jCompany 3.0
	 */
	public void setToBcc(String toBcc)
	{
		this.toBcc = toBcc;
	}

	/**
	 * @param string Endereços para envio com cópia.
	 * 
	 * @since jCompany 3.0
	 */
	public void setToCc(String toCc)
	{
		this.toCc = toCc;
	}

	/**
	 * @return Endereços para o reply to.
	 * 
	 * @since jCompany 3.0
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * @return Endereço do Sender.
	 * 
	 * @since jCompany 3.0
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param string Endereços para o reply to.
	 * 
	 * @since jCompany 3.0
	 */
	public void setReplyTo(String string) {
		replyTo = string;
	}

	/**
	 * @param string Endereço do Sender.
	 * 
	 * @since jCompany 3.0
	 */
	public void setSender(String string) {
		sender = string;
	}

}