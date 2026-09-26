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
package org.jcompany.domain.validate.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcException;


/**
 * Serviço dinâmicos para realização de validação invariante, vinculada ao modelo de domínio e podendo
 * ser ativada em qualquer camada
 */
public class PlcInvariantValidationService {

	private Map<Class,ClassValidator> m = null;
	
	/**
	 * Realiza validação invariante no padrão do Hibernate Validator, mantendo caching de classes de validaçao conforme recomendado
	 * @param request Objet
	 * @param entity
	 * @return
	 * @throws PlcException
	 */
	public InvalidValue[] validationInvariantMergeMsgs(Locale locale, String bundleRoot, Object entity) throws PlcException {
		
		if (m == null)
			m = new HashMap<Class,ClassValidator>();
			
		ClassValidator validator=null;
		
		if (m.containsKey(entity.getClass()))
			validator = m.get(entity.getClass());
		else {
			validator = new ClassValidator( entity.getClass(), ResourceBundle.getBundle(bundleRoot, locale));
			m.put(entity.getClass(),validator);
		}
			
		// Valida efetivamente
		InvalidValue[] ivs = validator.getInvalidValues(entity);
		
		return ivs;
		
	}

}
