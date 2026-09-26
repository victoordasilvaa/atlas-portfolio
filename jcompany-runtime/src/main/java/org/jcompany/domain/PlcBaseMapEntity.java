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
package org.jcompany.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.jcompany.commons.PlcBaseEntity;


/**
 * @since jCompany 3.2 Ancestral para todas as classes de Entidade (ENTITY) da aplicação, 
 * com pre-mapeamentos para OID, version e auditoria minima
 */
@MappedSuperclass
public abstract class PlcBaseMapEntity extends PlcBaseEntity {

	@Version
	@Column (name = "VERSION",nullable=false)
	private int version;	
	
	@Column (name = "DATE_LAST_UPDATE",nullable=false)
	private Date dateLastUpdate = new Date();
	
	@Column (name = "USER_LAST_UPDATE",nullable=false,length=150)
	private String userLastUpdate = "";

	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}

	public void setDateLastUpdate(Date dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}

	public String getUserLastUpdate() {
		return userLastUpdate;
	}

	public void setUserLastUpdate(String userLastUpdate) {
		this.userLastUpdate = userLastUpdate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
