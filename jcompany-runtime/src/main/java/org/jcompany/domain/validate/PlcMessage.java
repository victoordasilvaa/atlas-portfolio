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
package org.jcompany.domain.validate;


/**
 * Classe que encapsula mensagens de validação de entrada de dados, das
 * várias camadas, para apresentação unificada.
 * @since jCompany 3.2
 */
public class PlcMessage {

	public enum Color {
		msgBluePlc,
		msgRedPlc,
		msgYellowPlc,
		msgGreenPlc;
	}
	
	public enum PresentAt {
		TOP,
		FIELD;
	}
	
	private Color color = Color.msgBluePlc;
	private PresentAt presentAt = PresentAt.TOP;

	/**
	 * Mensagem, já montada no idioma correto, sem nenhum token a ser traduzido
	 */
	private String message;
	
	/**
	 * Propriedade. Se não informado considera validação em nível de classe
	 */
	private String property;

	/**
	 * Se for um erro inesperado, message pode conter o stack trace
	 */
	private String stackTrace;
	
	public String toString() {
		return getMessage();
	}
	
	public PlcMessage(String message,Color color) {
		this.message=message;
		this.color=color;
	}
	

	public PlcMessage(String message,String property) {
		this.message=message;
		this.property=property;
	}
	
	public PlcMessage(String message,String property,Color color) {
		this.message=message;
		this.color=color;
		this.property=property;
	}
	
	public PlcMessage(String message,String property,Color color,PresentAt presentAt,String stackTrace) {
		this.message=message;
		this.presentAt=presentAt;
		this.color=color;
		this.property=property;
		this.stackTrace=stackTrace;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public PresentAt getPresentAt() {
		return presentAt;
	}

	public void setPresentAt(PresentAt presentAt) {
		this.presentAt = presentAt;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PlcMessage
				&& ((PlcMessage)obj).color==this.color
				&& ((PlcMessage)obj).presentAt==this.presentAt
				&& equals(((PlcMessage)obj).message,this.message)
				&& equals(((PlcMessage)obj).property,this.property)
				&& equals(((PlcMessage)obj).stackTrace,this.stackTrace);
	}
	
	/**
	 * Método auxiliar para verificação de igualdade.
	 */
	private boolean equals(Object o1, Object o2) {
		return (o1==null && o2==null) || (o1!=null && o2!=null && o1.equals(o2));
	}

	
}
