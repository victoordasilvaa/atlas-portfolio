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
package org.jcompany.persistence.jpa.helper;

import org.apache.log4j.Logger;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.mapping.PersistentClass;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.persistence.helper.PlcPersistenceHelper;

/**
 * @since jCompnay 5.0
 * Utilitário geral para JPA
 */
public class PlcJpaHelper extends PlcPersistenceHelper {
	
	static private PlcJpaHelper INSTANCE = new PlcJpaHelper();
	protected static Logger log = Logger.getLogger(PlcJpaHelper.class);
	private PlcJpaHelper() {}
	
	static public PlcJpaHelper getInstance() {
		return INSTANCE;
	}

	public boolean referenceWithManyToOne(PersistentClass pc, String name) {
	
		return false;
	}

	/**
	 * Verifica se a classe informada está utilizando o campo reservado "sitHistoricoPlc", o que indica que possui
	 * registros "I"-Inativos, "A"-Ativos e, potencialmente  (se utilizar aprovação ou publicação), "P"-Pendente
	 * @param clazz Classe a ser investigada
	 * @return true se 'classe' contiver a propriedade 'sitHistoricoPlc' e ele estiver mapeado.
	 */
	public boolean existsStatusHistoricPlcMapped(Ejb3Configuration cfg,Class clazz) {
		
		PersistentClass pc = cfg.getClassMapping(clazz.getName());

		try {
			pc.getProperty(PlcConstantsCommons.ENTITY.STATUS_HISTORIC_PLC);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
}
