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
package org.jcompany.commons.file;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.jcompany.commons.PlcFileEntity;


/**
 * ENTITY Base para Arquivo Anexado Universal. 
 * Pode ser utilizado diretamente para PAC (Padrão de Aplicação Complementar) de "Arquivo Anexado Universal"
 * @since jCompany 3.05
 */
@MappedSuperclass
public abstract class PlcBaseMapFile extends PlcFileEntity {

	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "SE_FILE_PLC")
	@Column (name = "ID")
	public Long getId() {
		return id;
	}
	
	@Version
	@Column (name = "VERSION")
	public int getVersion() {
		return super.getVersion();
	}

	@Column (name = "DAT_SECURITY")
	public java.util.Date getDateLastUpdate() {
		return super.getDateLastUpdate();
	}

	@Column (name = "NAME")
	public String getName() {
		return super.getName();
	}
	
	@Column (name = "USU_SECURITY")
	public String getUserLastUpdate() {
		return super.getUserLastUpdate();
	}
	
	@Lob
	@Column (name = "BINARY_FILE")
    public byte[] getImage() {
    	return image;
    }

	@Column (name = "TYPE")
    public String getType() {
    	return type;
    }

	@Column (name = "SIZE")
    public Integer getSize() {
    	return size;
    }
}
