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

import javax.persistence.MappedSuperclass;

import java.util.Date;
import javax.persistence.Version;
import javax.persistence.Id;
import javax.persistence.Column;
import org.hibernate.validator.Length;
import javax.persistence.GeneratedValue;
import org.hibernate.validator.NotNull;
import org.jcompany.commons.PlcBaseEntity;

/**
 * Ancestral para aplicacoes que desejem controlar variáveis de classe de forma elegante e padronizada
 * @since jCompany 3.2
 */
@MappedSuperclass
public class PlcBaseMapClassProp extends PlcBaseEntity  {

	private Long id;
	
	private int version;
	
	private Date dateLastUpdate = new Date();
	
	private String userLastUpdate = "";

	/**
	 * Nome da Classe sem pacote, para protecao quanto a refactorings. Pode ser especializado
	 */
	protected String simpleClass;
	
	/**
	 * Nome da Propriedade modificada (incluida, alterada ou excluida)
	 */
	private String property;
	
	/**
	 * Valor Atual após a modificação. Opcional! Como o value atual é o que está efetivamente no modelo principal,
	 * este campo pode ter seu getter sobreposto para não ser mapeado. Sua gravação, porém, simplificará lógicas
	 * de consulta à auditoria automáticas.
	 */
	private String value;

	@Id @GeneratedValue(strategy=javax.persistence.GenerationType.AUTO, generator = "SE_PLC_PROP_CLASS")
	@Column (name = "ID_PLC_PROP_CLASS")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}

	@Column (name = "SIMPLE_CLASS")
	@NotNull
	@Length (max = 100)
	public String getSimpleClass() {
		return simpleClass;
	}

	public void setSimpleClass(String simpleClass) {
		this.simpleClass=simpleClass;
	}

	@Column (name = "PROPERTY")
	@NotNull
	@Length (max = 100)
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property=property;
	}

	@Column (name = "VALUE")
	@Length (max = 255)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value=value;
	}

	@Version
	@Column (name = "VERSION")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version=version;
	}
	
	@Column (name = "DATE_LAST_UPDATE")
	@NotNull
	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}

	public void setDateLastUpdate(Date dateLastUpdate) {
		this.dateLastUpdate=dateLastUpdate;
	}

	@Column (name = "USER_LAST_UPDATE")
	@NotNull
	public String getUserLastUpdate() {
		return userLastUpdate;
	}

	public void setUserLastUpdate(String userLastUpdate) {
		this.userLastUpdate=userLastUpdate;
	}

}

