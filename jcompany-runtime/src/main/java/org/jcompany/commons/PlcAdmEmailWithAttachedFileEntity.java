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
 * $Id: PlcAdmEmailWithAttachedFileEntity.java,v 1.3 2006/05/17 19:53:56 joaopaulo_santos Exp $
 */
package org.jcompany.commons;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe utilizada para envio de email com anexo.
 * @author Roberto Lúcio Badaró
 * @version $Revision: 1.3 $$Date: 2006/05/17 19:53:56 $$Autho
 */
public class PlcAdmEmailWithAttachedFileEntity extends PlcAdmEmailEntity implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6448005462543250296L;

	private Set attachedFiles;
	
	private byte[] file;

	
	/**
	 * Adiciona attachedFiles a mensagem de email.
	 * <p>
	 * Para enviar attachedFiles inclua tantos quantos PlcAdmEmailWithAttachedFileEntity forem necessários
	 * ao Set attachedFiles. O assunto será o nome do anexo. Se o anexo for binário,
	 * informar o campo file (do type byte[]), caso contrário o conteúdo deve
	 * ir no campo mensagem.
	 * </p><p>
	 * Exemplo:<br><br>
	 * <pre>
	 * PlcAdmEmailWithAttachedFileEntity mail = new PlcAdmEmailWithAttachedFileEntity();
	 * 
	 * mail.setRemetente("Roberto <roberto@powerlogic.com.br>");
	 * mail.setAssunto("Envio de file anexo");
	 * mail.setMensagem("Você está recebendo um file em anexo.");
	 * 
	 * PlcAdmEmailWithAttachedFileEntity anexo = new PlcAdmEmailWithAttachedFileEntity();
	 * 
	 * byte[] figura = ... //código para recuperar o file 
	 * anexo.setAssunto("figura.gif");
	 * anexo.setArquivo(figura);
	 * anexo.setFormato("image/gif") // informe o mime type
	 * 
	 * mail.adicionaAnexo(anexo);
	 * 
	 * // envia o email
	 * log.info(mail);
	 * </pre>
	 * </p>
	 * 
	 * @param attachedFile
	 * 
	 * @since jCompany 3.0
	 */
	public void addAttachedFile(PlcAdmEmailWithAttachedFileEntity attachedFile)
	{
		if (attachedFiles == null)
			attachedFiles = new HashSet();
		
		attachedFiles.add(attachedFile);
	}
	
	/**
	 * 
	 * @since jCompany 3.0
	 */
	
	public Set getAttachedFiles()
	{
		return attachedFiles;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public byte[] getFile()
	{
		return file;
	}
	
	/**
	 * 
	 * @since jCompany 3.0
	 */

	public void setattachedFiles(Set attachedFiles)
	{
		this.attachedFiles = attachedFiles;
	}

	/**
	 * 
	 * @since jCompany 3.0
	 */
	public void setFile(byte[] file)
	{
		this.file = file;
	}

}