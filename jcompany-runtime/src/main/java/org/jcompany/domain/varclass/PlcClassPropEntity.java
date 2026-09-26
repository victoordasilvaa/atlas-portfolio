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
package org.jcompany.domain.varclass;


import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
/**
 * Classe Concreta gerada a partir do assistente
 */
@Entity
@Table(name="PLC_PROP_CLASS")
@SequenceGenerator(name="SE_PLC_PROP_CLASS", sequenceName="SE_PROP_CLASS")
@SuppressWarnings("serial")
public class PlcClassPropEntity extends PlcBaseMapClassProp {
 	
    /*
     * Construtor padrão
     */
    public PlcClassPropEntity() {
    }
	public String toString() {
		return getSimpleClass()+"-"+getProperty();
	}

	/** Relação de colunas para recuperação em lookups many-to-one. Pode diferir das exibidas, quando
	 * necessário trazer mais informações do que as exibidas **/
	@javax.persistence.Transient
	public String[] getLookupPropsPlc() {
		return new String[]{"simpleClass","propriedade"};
	}

}

