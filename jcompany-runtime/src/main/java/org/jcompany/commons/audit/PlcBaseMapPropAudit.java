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
package org.jcompany.commons.audit;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jcompany.commons.PlcBaseEntity;


/**
 * ENTITY Base para Auditoria Universal. Pode ser utilizado diretamente para PAC (Padrão de Aplicação Complementar) "Auditoria Completa Universal"
 * @since jCompany 3.05
 */
@MappedSuperclass
public abstract class PlcBaseMapPropAudit extends PlcBaseEntity {

	/**
	 * Nome da Propriedade modificada (incluida, alterada ou excluida)
	 */
	private String propertyName;
	
	/**
	 * Valor Anterior à modificaçao. Obrigatorio
	 */
	private String valuePrevious;
	
	/**
	 * Valor Atual após a modificação. Opcional! Como o valor atual é o que está efetivamente no modelo principal,
	 * este campo pode ter seu getter sobreposto para não ser mapeado. Sua gravação, porém, simplificará lógicas
	 * de consulta à auditoria automáticas.
	 */
	private String valueCurrent;
	
	/**
	 * Mestre contendo usuário, data/hora e outras informações gerais sobre esta modificação
	 */
	private PlcBaseMapObjectAudit plcBaseMapAudit;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "SE_AUDIT_ITENS_PLC")
	@Column (name = "ID")
	public Long getId() {
		return id;
	}

	@Column (name = "PROPERTY_NAME")
	@NotNull
	@Length (max = 50)
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Importante: Classe Abstrata deve ser sobreposta no descendente em targetEntity pela específica
	 */
	@ManyToOne (targetEntity = PlcBaseMapObjectAudit.class)
	@JoinColumn (name = "ID_AUDIT")
	public abstract PlcBaseMapObjectAudit getPlcBaseMapAuditoria();

	@Column (name = "VALUE_PREVIOUS")
	@NotNull
	@Length (max = 255)
	public String getValuePrevious() {
		return valuePrevious;
	}

	@Column (name = "VALUE_CURRRENT")
	@NotNull
	@Length (max = 255)
	public String getValueCurrent() {
		return valueCurrent;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}


	public void setValueCurrent(String valueCurrent) {
		this.valueCurrent = valueCurrent;
	}

	public void setPlcBaseMapAudit(PlcBaseMapObjectAudit plcBaseMapAudit) {
		this.plcBaseMapAudit = plcBaseMapAudit;
	}

	public void setValuePrevious(String valuePrevious) {
		this.valuePrevious = valuePrevious;
	}

	
	
}
