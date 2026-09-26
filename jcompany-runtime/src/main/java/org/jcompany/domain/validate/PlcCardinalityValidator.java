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
package org.jcompany.domain.validate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.Validator;
import org.jcompany.commons.annotation.PlcTabular;


/**
 * Verifica restrições de cardinalidade mínimas e máximas entre classe e coleções
 * @since jCompany 3.1.1
 */
@SuppressWarnings("serial")
public class PlcCardinalityValidator implements Validator<PlcCardinality>, Serializable {
	
	private int max;
	private int min;
	
	protected static final Logger log = Logger.getLogger(PlcCardinalityValidator.class);

	public void initialize(PlcCardinality parameters) {
		max = parameters.max();
		min = parameters.min();
	}

	/**
	 * Verifica Se o total de objetos (beans) na colecao é maior ou igual a minimo e menor ou igual a máximo.
	 */
	public boolean isValid(Object value) {

		
		// Testa declaração correta
		if (value != null && !Collection.class.isAssignableFrom(value.getClass())) {
			log.error(this.getClass().getName()+ ": cardinality annotation must be used in colleciont not in : "+value.getClass());
			return false;
		}
		
		// Descobre número de válidos
		Collection c = (Collection) value;
		int totalValids=0;
		int counter=0;
		if (value != null && c.size()>0) {
			
			String propReference=null;
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				counter++;
				Object bean = (Object) iter.next();
				if (propReference==null && counter==1) {
					propReference= verifyReferencePropertyDespise(bean.getClass());
				}
				if (notMarkedToExclude(bean) &&
					 (propReference==null || informedReferencePropertyDespise(bean,propReference))) {
					totalValids++;
				}
			}
		}
		
		// Faz o teste
		return totalValids>=min && totalValids<=max;
		
	}

	/**
	 * @return Valor da propriedade de referencia para desprezar conforme anotação PlcPropReferenciaDesprezar ou null se nao encontrou anotação
	 */
	protected String verifyReferencePropertyDespise(Class<? extends Object> clazz) {
		try {
			PlcTabular tabular = clazz.getAnnotation(PlcTabular.class);
			return tabular.propReferenceDespise();
		} catch (Exception e) {
			log.error("PlcTabular annotation not found to inform a propReferenceDespise to validate cardinality in "+clazz.getName());
			return null;
		}
	}

	/**
	 * Se informou valor válido na coluna flag. Se não encontrou coluna flag gera excecao
	 * considera true (todas as linhas validas)
	 * 
	 * @param bean Bean a ser investigado
	 */
	protected boolean informedReferencePropertyDespise(Object bean,String propFlag) {
		try {
			Object value = PropertyUtils.getProperty(bean,propFlag);
			if (String.class.isAssignableFrom(value.getClass())) {
				return value != null && !("".equals(value.toString().trim()));
			} else
				return value != null;
		} catch (Exception e) {
			log.error(this.getClass().getName()+ ": annotation to propReferenceDespise referred to a unfound property: "+
					propFlag+" in class "+bean.getClass().getName());
			return false;
		}
	}
	
	/**
	 * Se o bean contem a propriedade padrão indExcPlc com valor "S", então está marcado para excluir, e portanto
	 * não será contabilizado
	 * @param bean Bean a ser investigado
	 * @return true se não está marcado para exclusão
	 */
	protected boolean notMarkedToExclude (Object bean) {
		
		try {
	
			return !("S".equals(PropertyUtils.getProperty(bean,"indExcPlc")));
		
		} catch (Exception e) {
			// se nao tem coluna indExcPlc considera valida
			return true;
		}
		
	}

		
}
