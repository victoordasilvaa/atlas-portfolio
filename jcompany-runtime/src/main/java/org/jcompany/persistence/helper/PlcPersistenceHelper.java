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
package org.jcompany.persistence.helper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.annotation.PlcEntityMetadata;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.model.PlcModelLocator;
import org.jcompany.model.service.IPlcPhoneticService;


public abstract class PlcPersistenceHelper {
	
	protected static Logger log = Logger.getLogger(PlcPersistenceHelper.class);
	
	/**
     * Monta parte select de um HQL com alias "obj" para a relação de propriedades informada
     * @since jCompany 2.7
     * @param props String[] contendo relação de propriedades a serem montadas (Ex: {id,nome})
     * @return String contendo parte HQL (Ex: "obj.id, obj.nome")
     */
    public String mountSelectFromArrayToString(String[] props) throws PlcException {

        log.debug("######## Entered in mountSelectFromArrayToString");

        try {

            if (props.length==0)
                return "";

            StringBuffer sb = new StringBuffer("");

            for (int i=0;i<props.length;i++) {

                sb.append("obj."+props[i]);

                if ((i+1)<props.length)
                    sb.append(",");
            }

            return sb.toString();

        } catch (Exception e) {
            throw new PlcException("jcompany.error.generic", new Object[] {
                    "mountSelectFromArrayToString", e }, e,log);
        }

    }
    
    /**
     * Recupera a primeira propriedade dos meta-dados da Hibernate que possui o type classeAgregada,
     * na classeBase. Importante: Para caso de dois relacionamentos entre classes do mesmo type, esta
     * lógica pode não atender e deve ser especializada.
     * Importante: Somente funciona para Object-IDs
     * @since jCompany 3.0
     * @return Nome da propriedade da classeBase que é do type classeAgregada.
     */
    public String retrievePropertyManyToOne(Class clazz, Class classSearched) throws PlcException {

        log.debug("######## Entered in retrievePropertyManyToOne");
        
        // TODO 3.5 Recuperar nome padrão sem instanciar
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
        try {
	
        	for (int i = 0; i < pds.length; i++) {
				if (PlcEntityHelper.getInstance().isEntity(pds[i].getPropertyType())
					 && pds[i].getPropertyType().isAssignableFrom(classSearched)
						&& !pds[i].getName().equals("naturalDynamicId"))
					return pds[i].getName();
			}
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"retrievePropertyManyToOne", e }, e, log);
		}
		throw new PlcException("jcompany.errors.not.found.origin.attribut", new Object[] {
				"retrievePropertyManyToOne", clazz });
    }
    
    /**
     * jCompany 3.0 Auxiliar para modo de teste automatizado via JUnit ou JWebUnit
     * @return true se tem variável de ambiente modoTeste com valor 'true'
     */
    public static boolean isTestMode() {
 	   	if(System.getProperty("testMode")!=null && System.getProperty("testMode").equals("true"))
 	   		return true;
 	   	return false;
    }
    
    /**
	 * Coloca as chaves foneticas no ENTITY informado
	 * @param entity ENTITY antes de ser incluido ou alterado, para inclusao de valores fonéticos
	 */
	public void doPhoneticsTreatment(Object entity)  throws Exception {
		// Codigo original: entrando e cancelando a cada inclusao de todos os objetos!!!
		//IPlcPhoneticService fon = null;
	//	try {
	//		fon = (IPlcPhoneticService) PlcModelLocator.getInstance().get(IPlcPhoneticService.class);
	//	} catch (PlcException e) {
			// Erro esperado caso não tenha sido registrado uma Implementação para IPlcPhoneticService
	//		fon = null;
	//	}
		
		PlcEntityMetadata annotation = (PlcEntityMetadata)entity.getClass().getAnnotation(PlcEntityMetadata.class);
		
		if(annotation != null) {
			if(annotation.phoneticsUsa()) {
				
				// Codigo reposicionado para somente testar se tem anotacao no ENTITY!
				IPlcPhoneticService phonetics = null;
				try {
					phonetics = (IPlcPhoneticService) PlcModelLocator.getInstance().get(IPlcPhoneticService.class);
				} catch (PlcException e) {
					// Erro esperado caso não tenha sido registrado uma Implementação para IPlcPhoneticService
					phonetics = null;
				}
				
				if (phonetics != null) {
					Field[] attributs = entity.getClass().getSuperclass().getDeclaredFields();			
					for (Field field : attributs) {
						int index = field.getName().indexOf(PlcConstantsCommons.CONSULTATION.QBE.QBE_ATTR_SUFFIX_PHONETICS);
						if (index != -1) {
							String fielName = field.getName().substring(0, index);
							for (Field field2 : attributs) {
								if (field2.getName().equals(fielName)) {
									//Nao usa o metodos set para evitar que estes alterem o valor final
									field.setAccessible(true);
									field2.setAccessible(true);
									String value = (String)field2.get(entity);
									String key = phonetics.phonetics(value);
									field.set(entity,key);

								}
							}
						}
					}
				}
				
				
			}
		}
		
	}
    
	/**
	 * Verifica se a classe informada está utilizando o campo reservado "sitHistoricoPlc", o que indica que possui
	 * registros "I"-Inativos, "A"-Ativos e, potencialmente  (se utilizar aprovação ou publicação), "P"-Pendente
	 * @param clazz Classe a ser investigada
	 * @return true se 'classe' contiver a propriedade 'sitHistoricoPlc' e ele estiver mapeado.
	 */
	public boolean existsStatusHistoricPlcMapped(Configuration cfg,Class clazz) {
		
		PersistentClass pc = cfg.getClassMapping(clazz.getName());

		try {
			pc.getProperty(PlcConstantsCommons.ENTITY.STATUS_HISTORIC_PLC);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	/**
	 * Testa se uma classe é um proxy dinâmico da GCLIG e converte para a
	 * classe original
	 * 
	 * @since jCompnay 5.0
	 * 
	 * @param clazz Possivel proxy dinâmico
	 * @return classe original
	 * @throws PlcException causa raiz ClassNotFoundException
	 */
	public Class convertDynamicProxyToOriginalClass(Class clazz) throws PlcException {
		Class classAux = null;
		
		if (clazz.getName().indexOf("$")>-1) {
			String classS = clazz.getName();
			classS = classS.substring(0,classS.indexOf("$"));
			try {
				classAux = Class.forName(classS);
			} catch (Exception e) {
				throw new PlcException("jcompany.error.proxy.to.class", new Object[] {"convertDynamicProxyToOriginalClass", e }, e, log);
			}
			
			return classAux;
		}
		
		return clazz;
	}

}


