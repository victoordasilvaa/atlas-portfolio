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


import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;
import org.jcompany.commons.annotation.PlcIoC;

/**
 * Classe Concreta gerada a partir do assistente
 */
@Entity
@Table(name="PLC_DYNAMIC_MENU")
@SequenceGenerator(name="SE_PLC_DYNAMIC_MENU", sequenceName="SE_PLC_DYNAMIC_MENU")
@AccessType("field")

@PlcIoC(nameBCClass="org.jcompany.model.PlcBaseManager", nameDAOClass="org.jcompany.persistence.hibernate.PlcBaseHibernateDAO")
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name="PlcDynamicMenuEntity.queryMan", query="from PlcDynamicMenuEntity obj"),
	@NamedQuery(name="PlcDynamicMenuEntity.querySelLookup", query="select new PlcDynamicMenuEntity (obj.id, obj.name, obj.url) from PlcDynamicMenuEntity obj where obj.id = ? order by obj.id asc")
})
public class PlcDynamicMenuEntity extends PlcDynamicMenu {

    /*
     * Construtor padrão
     */
    public PlcDynamicMenuEntity() {
    }
	public PlcDynamicMenuEntity(Long id, String name, String url) {
		this.setId(id);
		this.setName(name);
		this.setUrl(url);
	}
	@Override
	public String toString() {
		return getName();
	}

}

