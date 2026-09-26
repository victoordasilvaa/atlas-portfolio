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
package org.jcompany.commons;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.annotation.PlcPrimaryKey;
import org.jcompany.commons.annotation.PlcEntityMetadata;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigSuffixClass;


/**
 * jCompany 2.5.3. Value Object ancestral para todos os VOs de aplicações com
 * persistência, inclusive VOs de Chave Natural. Traz serviços de equals,
 * toString e hashCode genéricos, bem como manipuladores de identificação
 * genéricos.
 */
public class PlcBaseEntity implements Serializable, Cloneable {
	
    private static final String ERROR_ORIGINAL = ". Original Error: ";

	protected static final Logger log = Logger.getLogger(PlcBaseEntity.class);

	private static final long serialVersionUID = -6328544514859752259L;

	protected static String[] PROPS_NATURAL_KEY_PLC = new String[]{"id"};
	
	/**
	 * Mapa de propriedades das subclasses de PlcBaseEntity. É preenchida conforme a necessidade, e pode ser coletado pelo GC (usa weak reference).
	 * @since jCompany 3.0.2
	 */
	private static final WeakHashMap<Class, Set<String>> PROPS_ALL_PLC_MAP = new WeakHashMap<Class, Set<String>>();
	
    /**
     * Devolve a declaração estática de propriedades chave. Deve ser
     * especializado para permitir polimorfismo sobre declaração estática e montagem
     * de hiperlinks genéricos para recuperação
     *
     * @return String estático com relação de propriedades chave.
     * @since jCompany 3.0
	 */	
    public String[] getPropsNaturalKeyPlc() {
    	
    	PlcPrimaryKey pk = this.getClass().getAnnotation(PlcPrimaryKey.class);
    	if (pk==null || PlcBaseEntity.class.equals(pk.classe()))
    		return PROPS_NATURAL_KEY_PLC;
    	
    	//tem anotação de chave primaria.
    	return pk.properties();
    }  

    /**
     * @since 3.1
     * @param obj Objeto a ser investigado
     * @return Conjunto de propriedades para comparação completa
     */
    private static Set<String> getPropsAllPlc(PlcBaseEntity obj) {
    	Set<String> propSet = PROPS_ALL_PLC_MAP.get(obj.getClass());
    	if (propSet==null) {
    		
    		try {
    			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(obj.getClass());
    			
    			propSet = new HashSet<String>();
    			for (PropertyDescriptor desc : propertyDescriptors) {
    				String name = desc.getName();
					if (!name.endsWith("Aux") && !name.endsWith("Str") && !name.endsWith("Fon")
						&& !name.endsWith("usuarioUltAlteracao") && !name.endsWith("dataUltAlteracao")
						&& !name.endsWith("versao") && isBeanProperty(desc)) {
    					propSet.add(name);
    				}
    			}
    			
    			PROPS_ALL_PLC_MAP.put(obj.getClass(), propSet);
    		} catch (Exception e) {
    			log.fatal("jCompany. Fatal error trying to retrieve entity properties "   + obj + ERROR_ORIGINAL + e);
    			e.printStackTrace();
    		}
    	}
    	return propSet;
    }

    /**
     * Determina de uma propriedade é uma propriedade bean com leitura e escrita.
     * @param property A descrição da propriedade
     * @return true se a propriedade tem método Read e Write
     */
	private static boolean isBeanProperty(final PropertyDescriptor property) {
		Method readMethod = property.getReadMethod();
		if ((readMethod == null) &&
				(property instanceof IndexedPropertyDescriptor)) {
			readMethod = ((IndexedPropertyDescriptor) property).getIndexedReadMethod();
		}

		Method writeMethod = property.getWriteMethod();
		if ((writeMethod == null) &&
				(property instanceof IndexedPropertyDescriptor)) {
			writeMethod = ((IndexedPropertyDescriptor) property).getIndexedWriteMethod();
		}
		
		return readMethod!=null && writeMethod!=null;
	}
 
	/** Chave Object Id Genérica * */
    protected Long id = null;

    protected String idAux = "";

    /** Chave Natural Genérica */
    protected PlcBaseEntity idNatural = null;

    /** Auxiliar para gravar estados transientes * */
    protected String indExcPlc = "N";
    
    /** Guarda hashCode **/
    protected int hashCodePlc = 0;

    /**
     * Mapeamento padrão para OID, para todas as classes
     * @since jCompany 3.0
     */
    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long newId) {
        id = newId;
    }

    /**
     * OID auxiliar em forma string para entradas de dados tabulares, que não
     * possam usar o form-bean da struts
     * @since jCompany 3.0
     */
    public String getIdAux() {
        if (getId() != null)
            return (getId().toString());
        else
            return "";
    }

    /**
     * @since jCompany 3.0
     */
    public void setIdAux(String newIdAux) {
    	idAux = newIdAux;
        if (idAux != null && !idAux.equals("")){
        	this.id=new Long(idAux);
        	setId(this.id);
        } else { 
        	id = null;
        	setId(null);
        }
        	
    }

    // Mantido nos descendentes por exigencia do XDoclet
    //  public PlcBaseEntity getIdNatural()
    //  {
    //      return this.idNatural;
    //  }
    
    /**
     * Devolve o nome da propriedade padrão para este ENTITY. O padrão é seu nome final com inicial minúscula e
     * sem o ENTITY (nome abstrato). Pode ser sobreposto nos descendentes, se desejado
     * @since jCompany 3.0
     */
    public  String getPropertyNamePlc() {

    	String entityDefaultSuffix = "ENTITY";
    	try{

    		entityDefaultSuffix = PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class).entitySuffix();
    		
    	}catch(Exception e){
    		log.error(e.getMessage(),e);
    	}


    	String auxProp = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")+1);
    	if (this.getClass().getName().endsWith(entityDefaultSuffix))
    		return auxProp.substring(0,1).toLowerCase()+auxProp.substring(1,auxProp.indexOf(entityDefaultSuffix));
    	else
    		return auxProp.substring(0,1).toLowerCase()+auxProp.substring(1);
    }
  
    /**
     * Chama dinamicamente porque XDoclet exige o método getIdNatural no
     * descenente
     * @since jCompany 3.0
     */
    public PlcBaseEntity getDynamicNaturalId() {
        PlcBaseEntity idNatural = null;
        if (PropertyUtils.isReadable(this, "idNatural")) {
            try {
                idNatural = (PlcBaseEntity) PropertyUtils.getSimpleProperty(this, "idNatural");
            } catch (Exception e) {
            	log.fatal("Error trying to class getIdNatural method in "
                                + this.getClass().getName());
            }
        }
        return idNatural;
    }

    /**
     * @since jCompany 3.0 
     */
    public void setIdNatural(PlcBaseEntity idNatural) {
        this.idNatural = idNatural;
    }

    /**
     * Auxiliar que indica para cada objeto que este deverá ser excluido.
     * Utilizado em padrões Tabular ou Detalhes e preenchido em função do
     * checkbox de inclusão
     * @since jCompany 3.0
     */
    public String getIndExcPlc() {
        return indExcPlc;
    }


    /**
     * @since jCompany 3.0 
     */
    public void setIndExcPlc(String newIndExcPlc) {
        indExcPlc = newIndExcPlc;
    }
    
    /**
     * @deprecated Utilizar equalsPlc estático, passando (this,(MinhaClasse)outro,new String[]{"prop1","prop2"});
     */
    protected boolean equalsPlc(Object other, Object[] props) {
    	String[] sAux = null;
    	if (props!=null) {
    		sAux = new String[props.length];
    		for (int i = 0; i < props.length; i++) {
    			sAux[i]=props[i].toString();
    		};
    	}
    	return equalsPlc(this,(PlcBaseEntity)other, sAux);
    }

    /**
     * Processamento genérico para código "equals" em VOs. <br>
     * Testa nulidades do outro ENTITY e de valores, retornando false caso somente
     * um dos "lados" esteja nulo. Se ambos estiverem nulos, retorna true. Testa
     * genericamente para os type String, Long, Integer, Double, BigDecimal e
     * java.util.Date Todas as propriedades passadas devem estar iguais para que
     * o método retorne true.
     *
     * @param other
     *            ENTITY a ser comparado com o atual
     * @param props
     *            Object[] com nomes das propriedades.
     * @return true se todas estiverem iguais ou false em caso contrário.
     */
    protected static boolean equalsPlc(PlcBaseEntity one, PlcBaseEntity other, String ... props) {
        if (other == null || !(one.getClass().isAssignableFrom(other.getClass()) || other.getClass().isAssignableFrom(one.getClass()))) 
        	return false;
        if (one == other) {
        	return true;
        }

        try {		
        	// PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(este);
            Collection<String> propSet = props == null || props.length == 0 ? (Collection<String>)getPropsAllPlc(one) : Arrays.asList(props);
        	
            for (String oneProp : propSet) {
        		try {
        			
        			if (!propertyEqual(one,other,oneProp))
        				return false;
        			
        		} catch (Exception e) {
        			// Os VOs não utilizam log4j por padrão, por isso este envio
        			// diretamente na console.
        			log.fatal("jCompany. Fatal Error trying to compare entity  "   + one
        					+ " to "     + other   + " for property "    + oneProp + ERROR_ORIGINAL + e);
        			e.printStackTrace();
        			return false;
        		}
        	}
        	
        	return true;
        	
        } catch (Exception e) {
        	log.fatal("jCompany. Fatal Error trying to compare entity "   + one
        			+ " to "     + other   + ERROR_ORIGINAL + e.toString());
        	e.printStackTrace();
        	return false;
        }
        
    }

    /**
     * Verifica se uma propriedade é igual em dois vos diferentes.
     * Considerando que os dois são instancias do mesmo objeto
     * @since jCompany 3.0
     */
    protected static boolean propertyEqual(PlcBaseEntity one,PlcBaseEntity other, String prop) throws Exception {
    	
    	try {
    		
    		// Somente compara props com getter	
    		if (!PropertyUtils.isReadable(one,prop)) {
    			//System.out.println("Valor "+prop+" OK");
    			return true;
    		}
    		
    		//   Não considera o campo versao, de tratamento interno
    		if (prop.equals("versao"))
    			return true;
    		
    		Object valueOne = PropertyUtils.getProperty(one, prop);
    		Object valueOther = PropertyUtils.getProperty(other, prop);
    		if (prop.equals("id")) {
    			// Se for componente, o id tem que comparar como true
 				if (one.getClass().getAnnotation(Embeddable.class)!=null)
 					return true;
    			valueOther = ((PlcBaseEntity)other).getId();
    		}
    		
    		//System.out.println("Obj: "+este.getClass().getName() +" Prop: "+prop+" Vals: "+valorDeste+"="+valorOutro);
    		
    		// Se ambos forem nulos e ids, considera diferentes,
    		// senão ao adicionar em coleções somente adiciona um. Se for componente nao chega aqui
    		if ((valueOne == null && valueOther == null) && prop.equals("id")) {
    			//System.out.println("Saiu com ids nulos");
    			return false;
    		}
    		
    		if (valueOne==valueOther) { //Inclui casos de mesma referencia e quando ambos forem null
    			//System.out.println("Saiu com valores iguais");
				return true;
			}
			
			// Já que não são os dois nulos, se algum deles for nulo, são diferentes, exceto para colecoes.
    		// Coleções nulas sao consideradas como de size 0, para funcionamento adequando dos detalhes por demanda.
			if ((valueOne == null || valueOther == null) && !Collection.class.isAssignableFrom(PropertyUtils.getPropertyType(one, prop))) { 
				return false;
			} else if (Collection.class.isAssignableFrom(PropertyUtils.getPropertyType(one, prop))) {
				// Não considera coleções na igualdade da classe.
				// Coleções podem ser recuperadas por demanda, o que significa que nem todas podem estar na memória ao mesmo tempo.
				// A igualdade de um ENTITY se restringe ao dados primitivos, arrays e propriedades manyToOne (agregados)
				return true;
			} else if (valueOne.getClass().isArray()) {
				if (!valueOther.getClass().isArray()) {
					return false;
				}
				Class<?> componentType = valueOne.getClass().getComponentType();
				if (componentType.isPrimitive()) {
					if (!valueOne.getClass().getComponentType().equals(valueOther.getClass().getComponentType())) {
						return false;
					}
					if (componentType.equals(boolean.class)) {
						return Arrays.equals((boolean[])valueOne, (boolean[])valueOther);
					}
					if (componentType.equals(byte.class)) {
						return Arrays.equals((byte[])valueOne, (byte[])valueOther);
					}
					if (componentType.equals(char.class)) {
						return Arrays.equals((char[])valueOne, (char[])valueOther);
					}
					if (componentType.equals(double.class)) {
						return Arrays.equals((double[])valueOne, (double[])valueOther);
					}
					if (componentType.equals(float.class)) {
						return Arrays.equals((float[])valueOne, (float[])valueOther);
					}
					if (componentType.equals(int.class)) {
						return Arrays.equals((int[])valueOne, (int[])valueOther);
					}
					if (componentType.equals(long.class)) {
						return Arrays.equals((long[])valueOne, (long[])valueOther);
					}
					if (componentType.equals(short.class)) {
						return Arrays.equals((short[])valueOne, (short[])valueOther);
					}
				}
				return Arrays.equals((Object[])valueOne, (Object[])valueOther);
			}
			
			// Considera que VOs de lookup ManyToOne, com a mesma chave, sejam os mesmos!
			// Aqui pode testar pelos valores, porque se chegou até este ponto nao tem valores nulos!
			if (PlcBaseEntity.class.isAssignableFrom(valueOne.getClass()) &&
				PlcBaseEntity.class.isAssignableFrom(valueOther.getClass())) {
				if (((PlcBaseEntity)valueOne).getId()!=null) {
					//System.out.println("Entrou em OIDs para lookups");
					// OID
					return ((PlcBaseEntity)valueOne).getIdAux().equals(((PlcBaseEntity)valueOther).getIdAux());
				} else if (((PlcBaseEntity)valueOne).getDynamicNaturalId() != null) {
					// Chave natural
					//System.out.println("Entrou em chave natural para lookups");
					return ((PlcBaseEntity)valueOne).getDynamicNaturalId().equals(((PlcBaseEntity)valueOther).getDynamicNaturalId());
				} else {
					//considera componentes
					return valueOne.equals(valueOther);
				}
			} 
			
			//System.out.println("Chegou ao final");	
			// Se chegou aqui, sao dois tipos Java primitivos ou Wrappers
			return valueOne.equals(valueOther);
    		
    	} catch (Exception e) {
    		log.fatal("jCompany. Fatal Error trying to compare entity "   + one
    				+ " to "     + other   + " for property ="+prop+ERROR_ORIGINAL + e.toString());
    		e.printStackTrace();
    		return false;
    	}
    }

    
    /**
     * jCompany 3.0. Deve ser sobreposto no descendente para passar todas as propriedades do ENTITY,
     * de modo a possibilitar comparação por todas elas, para casos em que se
     * faça necessário. Ex: registro de auditoria de tabular.<p>
     * Ex:  return super.equalsPlc(outro,new Object[]{"codigo,descricao,tipoCliente"});
     * O método equals deve ser utilizado para comparação de identificadores (ex: OID ou Chave Natural)
     * @deprecated Utilizar o "equals" normalmente.
     * @param other Outro ENTITY a comparar
     * @return true se todas as propriedades forem iguais
     */
    public boolean apiTotEquals(Object other) {
        return equalsPlc(this,(PlcBaseEntity)other);
    }

    /**
     * jCompany 2.5.1. Override no método de java.lang.Object utilizando
     * propriedades não nulas na exibição. Pode-se sobrepor este método nos
     * descendentes para especializar a visualização.
     *
     * @return todas as propriedades não nulas do ENTITY
     * 
     * @since jCompany 3.0
     */
    public String toStringPlc(Object[] props) {

        StringBuffer sb = new StringBuffer("[");

        int cont = 0;

        if (props == null) {
            // Passou null
            PropertyDescriptor[] pds = PropertyUtils
                    .getPropertyDescriptors(this.getClass());

            if (pds != null) {

                for (int i = 0; i < pds.length; i++) {

                    PropertyDescriptor pd = (PropertyDescriptor) pds[i];
                    //System.out.println("Propriedade "+pd+" nome =
                    // "+pd.getName());
                    if (PropertyUtils.isReadable(this, pd.getName())) {
                        try {
                            Object value = PropertyUtils.getSimpleProperty(
                                    this, pd.getName());
                            if (value != null && !(value instanceof PlcBaseEntity)
                                    && !(value instanceof java.util.List)
                                    && !(value instanceof java.util.ArrayList)
                                    && !(value instanceof java.util.Set)
                                    && !(value instanceof java.util.Map)) {
                                cont++;
                                if (cont > 1)
                                    sb.append(",");
                                sb.append(pd.getName() + "=" + value);
                            }
                        } catch (Exception e) {
                            sb.append(pd.getName() + "=erro:" + e.toString());
                        }
                    }
                }
            }

        } else {
            // Forneceu lista de propriedades
            for (int i = 0; i < props.length; i++) {

                String prop = (String) props[i];

                if (PropertyUtils.isReadable(this, prop)) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty(this,
                                prop);
                        if (value != null) {
                            cont++;
                            if (cont > 1)
                                sb.append(",");
                            sb.append(prop + "=" + value);
                        }
                    } catch (Exception e) {
                        sb.append(prop + "=erro:" + e.toString());
                    }
                }
            }

        }

        sb.append("]");
        
        if (sb.toString().equals("[]"))
        	return "";
        else
        	return sb.toString();
    }

    /**
     * @since jCompany 3.1
     * @deprecated Utilizar hashCodePlc sem argumentos. 
     */
    public int hashCodePlc(String[] props) {
    	return hashCodePlc();
    }
    
    /**
     * Utilizada para montagem de hashCode recursiva, situação onde somente é montada a chave
     * @since jCompany 3.1
     */
    public int hashCodeKeyPlc() {
    	String[] props = getPropsNaturalKeyPlc();
    	int result = 17;
    	if (props != null) {
    		
    		// Não informou relação de propriedades
    		for (int i = 0; i < props.length; i++) {
    			
    			try {
    				// Se tiver getter e nao for coleção
    				if (PropertyUtils.isReadable(this,props[i]) && 
    						(!Collection.class.isAssignableFrom(PropertyUtils.getPropertyType(this,props[i])))) {
    					Object value = PropertyUtils.getNestedProperty( this, props[i]);
    					int valorAux = value == null ? 0 : value.hashCode();
    					result = result * 37 + valorAux;
    				}
    			} catch (Exception e) {
    				log.fatal("Error trying to get hashCode from property " + props[i] + " Erro:" + e);
    				e.printStackTrace();
    			}
    		}
    		
    	}  	
    	
    	return result;
    }
    
    /**
     * Implementação de hashCode em conformidade com o padrão Bloch.
     * Importante: O resultado do "hashCode" deve ser compatível com o resultado do "equals". Assim,
     * como o "equals" do jCompany percorre todas as propriedades, o mesmo deve se dar com o hashCode
     * @since jcompany 2.5.3
     */
    public int hashCodePlc() {
    	if (this.hashCodePlc==0) {
    		int result = 17;
    		Object value = null;
    		
    		try {
    			Set<String> propsAll = getPropsAllPlc(this);
    			for (String prop : propsAll) {
    				value = PropertyUtils.getProperty(this, prop);
    				//System.out.println("Vai tentar pegar hashCode de "+valor);
    				int valueAux = 0;
    				if (value!=null) {
    					if (value.getClass().isArray()) {
    						Class<?> componentType = value.getClass().getComponentType();
    						if (componentType.isPrimitive()) {
    							if (componentType.equals(boolean.class)) {
    								valueAux =  Arrays.hashCode((boolean[])value);
    							} else if (componentType.equals(byte.class)) {
    								valueAux =  Arrays.hashCode((byte[])value);
    							} else if (componentType.equals(char.class)) {
    								valueAux =  Arrays.hashCode((char[])value);
    							} else if (componentType.equals(double.class)) {
    								valueAux =  Arrays.hashCode((double[])value);
    							} else if (componentType.equals(float.class)) {
    								valueAux =  Arrays.hashCode((float[])value);
    							} else if (componentType.equals(int.class)) {
    								valueAux =  Arrays.hashCode((int[])value);
    							} else if (componentType.equals(long.class)) {
    								valueAux =  Arrays.hashCode((long[])value);
    							} else if (componentType.equals(short.class)) {
    								valueAux =  Arrays.hashCode((short[])value);
    							}
    						} else {
    							valueAux = Arrays.hashCode((Object[]) value);
    						}
    					} else if (value instanceof PlcBaseEntity) {
    						valueAux = ((PlcBaseEntity)value).hashCodeKeyPlc();
    					} else if (value instanceof Collection) {
    						//valorAux = ((Collection)valor).size();
    					} else {
    						valueAux = value.hashCode();
    					}
    				}
    				result = result * 37 + valueAux;
    			}
    		} catch (Exception e) {
    			log.fatal("Error trying to get hashCode from property "+ value + " Erro:" + e);
    			e.printStackTrace();
    		}
    		this.hashCodePlc = result;
    	}
    	
    	return this.hashCodePlc;

    }

    /**
     * @since jCompany 3.0
     * Código padronizado para equals, baseado em valores declarados como chave de negócio do ENTITY. 
     */
    public  boolean equalsPrimaryKey(PlcBaseEntity other) {
		PlcPrimaryKey pk 	= (PlcPrimaryKey)this.getClass().getAnnotation(PlcPrimaryKey.class);
		if (pk != null){
			PlcBaseEntity thisIdNatural 	= this.getDynamicNaturalId();
			PlcBaseEntity otherIdNatural 	= other.getDynamicNaturalId();
	        return equalsPlc(thisIdNatural, otherIdNatural, getPropsNaturalKeyPlc());
		}
		else
			return equalsPlc(this, other, getPropsNaturalKeyPlc());
    }

    /**
     * @since jCompany 3.0
     * Código padronizado para equals, baseado em todos os valores do ENTITY. 
     * Recomenda-se especializar fazendo teste específico ou passando propriedades suficientes
     * como array de String
     */
    public boolean equals(Object other) {
         boolean equal = other instanceof PlcBaseEntity
        	&& equalsPlc(this,(PlcBaseEntity)other);
       //  if (outro != null) System.out.println("Comparacao final: "+this.getClass().getName()+"."+this+" contra: "+outro.getClass().getName()+"."+outro+ " veredicto: "+igual);
         return equal;
    }

    /**
     * @since jCompany 3.0.
     * Código padronizado para hashCode
     */
    public int hashCode() {
        return hashCodePlc();
    }

    /**
     * @since jCompany 2.5.3.
     * Código genérico para toString
     */
    public String toString() {
        return toStringPlc(getPropsNaturalKeyPlc());
    }

    /**
     * @since jCompany 2.5.3.
     * Link de edição genérico
     */
    public String getEditionLinkPlc() {

        StringBuilder link = new StringBuilder();

        if (getId() != null ) {

            link.append("&chPlc=").append(getId());

        } else if (getIdAux()!=null && !getIdAux().trim().equals("")) {
        	
            try {
				link.append("&chPlc=").append(URLEncoder.encode(getIdAux(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				link.append("&chPlc=").append(getIdAux());
				log.fatal("jCompany. Fatal Error trying to mount edition link ENTITY "
                        + this
                        + " for property "
                        + " idAux"
                        + ERROR_ORIGINAL
                        + e.toString());
                e.printStackTrace();
			}

        } else {
    		PlcBaseEntity idNatural = this.getDynamicNaturalId();

    		if (idNatural != null){

        	link.append("&evento=y");
        	for(String property : getPropsNaturalKeyPlc()) {
				try {
                	
                	link.append("&").append(property).append("=");
                	
                    Class<?> propertyType = PropertyUtils.getPropertyDescriptor(idNatural, property).getPropertyType();
					if (propertyType!=null && propertyType.equals(java.util.Date.class)){
						Timestamp fieldValue = (Timestamp)PropertyUtils.getProperty(idNatural, property);
						if (fieldValue != null ){
							link.append(fieldValue.getTime());
						}
					}
                    else if (propertyType!=null && PlcBaseEntity.class.isAssignableFrom(propertyType)) {
                        PlcBaseEntity b = (PlcBaseEntity) PropertyUtils.getProperty(idNatural, property);
                        link.append(b.getIdAux());
                    } else
                        link.append(URLEncoder.encode(PropertyUtils.getProperty(idNatural, property).toString(), "ISO-8859-1"));

                } catch (Exception e) {
                    // Os VOs não utilizam log4j por padrão, por isso este envio
                    // diretamente na console.
                	log.fatal("jCompany. Fatal Error trying to mount edition link  ENTITY "
                                    + this
                                    + " for property "
                                    + property
                                    + ERROR_ORIGINAL
                                    + e.toString());
                    e.printStackTrace();
                }
            }
        }
    	}

        return link.toString();

    }
    
    /**
     * @since jCompany 2.5.3.
     * Link de edição que inclui todos os argumentos informados com a anotacao PlcNavigation.
     */
    public String getEditionLinkAdvancedPlc() {
    	
    	StringBuffer link = new StringBuffer("");
    	
    	if ( this.getClass().getAnnotation(PlcEntityMetadata.class) != null){
    		String props = ((PlcEntityMetadata)this.getClass().getAnnotation(PlcEntityMetadata.class)).navegacao();
    		StringTokenizer st = new StringTokenizer(props,",");
    		while (st.hasMoreElements()) {
    			
    			String prop = (String)st.nextElement();
    			
    			try {
    				if (PropertyUtils.getPropertyDescriptor(this, prop).getPropertyType().equals(java.util.Date.class))
    					link.append("&" + prop + "=" + PropertyUtils.getProperty(this, prop+ "Aux"));
    				else if (PlcBaseEntity.class.isAssignableFrom(PropertyUtils.getPropertyDescriptor(this, prop).getPropertyType())) {
    					PlcBaseEntity b = (PlcBaseEntity) PropertyUtils.getProperty(this, prop);
    					link.append("&" + prop + "=" + b.getIdAux());
    				} else if (prop.equals("id"))
    					link.append("&" + prop + "="+ getIdAux()+"&chPlc="+getIdAux());
    				else
    					link.append("&" + prop + "="+ PropertyUtils.getProperty(this, prop));
    				
    			} catch (Exception e) {
    				// Os VOs não utilizam log4j por padrão, por isso este envio
    				// diretamente na console.
    				log.fatal("jCompany. Fatal Error trying to mount advanced edition link (with navigation) do ENTITY "
    						+ this
    						+ " for property "
    						+ prop
    						+ ERROR_ORIGINAL
    						+ e.toString());
    				e.printStackTrace();
    			}
    		}
    	}
    	return link.toString();
    	
    }


    /**
     * @since jCompany 2.7
     * Auxiliar para manipulação de VOs com chave natural Se o
     * objeto tem seus identificados informados, seja OID ou chave natural
     *
     * @return true se possui todas as propriedades de identificação informadas
     */
    public boolean isIdentified() {

        boolean isIdentified = true;

        Object[] props = getPropsNaturalKeyPlc();

        if (props == null) {

            // Assume que usa OID
            if (getId() == null)
                isIdentified = false;

        } else {
            // Forneceu lista de propriedades
            for (int i = 0; i < props.length; i++) {

                String prop = (String) props[i];

                try {
                	Object value=null;
                	if (prop.equals("id"))
                		value = getId();
                	else 
                		value = PropertyUtils.getSimpleProperty(PropertyUtils.getSimpleProperty(this,"idNatural"), prop);
                	if (value == null) {
                		isIdentified = false;
                	}
                } catch (Exception e) {
                	log.fatal("Error trying to get property value "
                                    + prop + " Error:" + e);
                    e.printStackTrace();
                }

            }

        }

        return isIdentified;
    }

    public void setIndExcPlc(Boolean value){
    	if (value != null && value.equals(true))
    		this.setIndExcPlc("S");
    	else	
    		this.setIndExcPlc("N");
    }
    
	@Transient private transient PlcFileEntity attachedFile;
	public PlcFileEntity getAttachedFile() {
		return attachedFile;
	}
	public void setAttachedFile(PlcFileEntity attachedFile) {
		this.attachedFile = attachedFile;
	}

}