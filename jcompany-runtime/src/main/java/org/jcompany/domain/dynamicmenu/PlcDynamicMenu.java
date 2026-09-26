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
package org.jcompany.domain.dynamicmenu;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.domain.validate.PlcUnifiedValidation;


@MappedSuperclass
@PlcUnifiedValidation
public abstract class PlcDynamicMenu extends PlcBaseEntity {
 	
	@Id @GeneratedValue(strategy=GenerationType.AUTO, generator = "SE_PLC_DYNAMIC_MENU")
	@Column (name = "ID_PLC_DYNAMIC_MENU", nullable=false, length=5)
	private Long id;
	
	@Version
	@Column (name = "VERSION", nullable=false, length=5)
	private int version;
	
	@Column (name = "DATE_LAST_UPDATE", nullable=false, length=11)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLastUpdate = new Date();
	
	@Column (name = "USER_LAST_UPDATE", nullable=false)
	private String userLastUpdate = "";

	
	@Column (name = "NAME", nullable=false, length=30)
	private String name;
	
	@Column (name = "URL", nullable=false, length=250)
	private String url;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version=version;
	}

	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}

	public void setDateLastUpdate(Date dateLastUpdate) {
		this.dateLastUpdate=dateLastUpdate;
	}

	public String getUserLastUpdate() {
		return userLastUpdate;
	}

	public void setUserLastUpdate(String userLastUpdate) {
		this.userLastUpdate=userLastUpdate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
	
}

