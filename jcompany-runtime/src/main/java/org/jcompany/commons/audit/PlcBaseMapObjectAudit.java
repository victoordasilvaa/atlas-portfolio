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

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jcompany.commons.PlcBaseEntity;


/**
 * ENTITY Base de Mestre para Auditoria Universal. Pode ser utilizado diretamente por um descendente com @Entity declarado, para atender ao
 * PAC (Padrão de Aplicação Complementar) "Auditoria Completa Universal"
 * @since jCompany 3.05
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class PlcBaseMapObjectAudit extends PlcBaseEntity {

	/**
	 * Nome da Classe modificada (incluida, alterada ou excluida), sem pacote. 
	 * Para diferenciar possíveis homonimas, utilizar a anotacao PlcAudit(nomeClasseAuditoria="MeuNomeAlternativo")
	 */
	protected String simpleClassName;
	
	/**
	 * Guarda o OID Objeto auditado, ou toString da chaveNatural.
	 */
	protected String idObject;
	
	/**
	 * Guarda o "toString" do objeto auditado
	 */
	protected String nameObject;
	
	
	/**
	 * Data/Hora da ocorrência
	 */
	protected Date dateOccurrence=new Date();
	
	/**
	 * Usuário responsável pela ocorrência
	 */
	protected String userOccurrence="anonym ";
	
	/**
	 * I-Inclusão, A-Alteração, E-Exclusão
	 */
	protected String defaultOperation="I";
	
	/**
	 * Propriedade customizável para indicar um type especial de operação, tal como "aprovação" ou outras do negócio.
	 */
	protected String specificOperation;
	
	/**
	 * Observações gerais sobre a operação, devendo ser incluida de forma especifica.
	 */
	protected String specificObservation;
	
	/**
	 * Coleção de modificações em propriedades auditadas
	 */
	protected Set<PlcBaseMapPropAudit> plcBaseMapAuditItens;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "SE_AUDIT_PLC")
	@Column (name = "ID")
	public Long getId() {
		return id;
	}

	@Column (name = "CLASS_NAME")
	@NotNull
	@Length (max = 50)
	public String getSimpleClassName() {
		return simpleClassName;
	}

	@Column (name = "OBJECT_ID")
	@NotNull
	@Length (max = 100)
	public String getIdObject() {
		return idObject;
	}

	@Column (name = "OBJECT_NAME")
	@NotNull
	@Length (max = 150)
	public String getNameObject() {
		return nameObject;
	}
	
	
	@Column (name = "OCCURRENCE_DATE")
	@NotNull
	public Date getDateOccurrence() {
		return dateOccurrence;
	}

	@Column (name = "OCCURRENCE_OPERATION")
	@NotNull
	@Length (max = 1)
	public String getDefaultOperation() {
		return defaultOperation;
	}

	@Column (name = "OCCURRENCE_LOGIN")
	@NotNull
	@Length (max = 50)
	public String getUserOccurrence() {
		return userOccurrence;
	}
	
	@Column (name = "SPECIFIC_OPERATION")
	@Length (max = 1)
	public String getSpecificOperation() {
		return specificOperation;
	}
	
	@Column (name = "SPECIFIC_OBS")
	@Length (max = 150)
	public String getSpecificObservation() {
		return specificObservation;
	}
	

	/**
	 * Importante: O descendente concreto deve especializar o targetEntity para classe concreta!
	 */
	@OneToMany (targetEntity = PlcBaseMapPropAudit.class, fetch = javax.persistence.FetchType.EAGER, 
			cascade=javax.persistence.CascadeType.ALL)
	@JoinColumn (name = "ID_AUDIT")
	public abstract Set<PlcBaseMapPropAudit> getPlcBaseMapAuditItens();


	public void setDateOccurrence(Date dateOccurrence) {
		this.dateOccurrence = dateOccurrence;
	}

	public void setSpecificObservation(String specificObservation) {
		this.specificObservation = specificObservation;
	}
	public void setSpecificOperation(String specificOperation) {
		this.specificOperation = specificOperation;
	}

	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}

	public void setDefaultOperation(String defaultOperation) {
		this.defaultOperation = defaultOperation;
	}
	
	public void setUserOccurrence(String userOccurrence) {
		this.userOccurrence = userOccurrence;
	}
	
	public void setPlcBaseMapAuditItens(
			Set<PlcBaseMapPropAudit> plcBaseMapAuditItens) {
		this.plcBaseMapAuditItens = plcBaseMapAuditItens;
	}

	public void setIdObject(String idObject) {
		this.idObject = idObject;
	}
	
	public void setNameObject(String nameObject) {
		this.nameObject = nameObject;
	}
	
	
}
