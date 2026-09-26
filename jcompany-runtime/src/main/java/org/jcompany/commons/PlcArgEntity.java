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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * PlcArgEntity é um Value Object que encapsula argumentos informados por usuários em
 * campos declarados no struts-config, para a lógica genérica de QBE do jCompany.
 * <P> A sequência padrão para uso do QBE é:
 * <P> - Declarar campos no Action conforme o exemplo:<br>
 * <set-property property="arg" value="id_Arg,igual,0,obj,long"></set-property><br>
 * <set-property property="arg" value="nome_Arg,*%,,obj,string,N,N"></set-property><br>
 * <set-property property="arg" value="areaProfissional_Arg,igual,0,obj,long"></set-property><br>
 * <set-property property="arg" value="dataCadastro_ArgINI,maiorOuIgual,,obj,date"></set-property><br>
 * <set-property property="arg" value="dataCadastro_ArgFIM,menorOuIgual,,obj,date"></set-property><br>
 * Operadores válidos são definidos na classe
 * PlcConstantes como:<p>  String QBE_LESS_THAN = "menor"; // Gera coluna <
 * argumento<br> String QBE_GREATER_THAN = "maior"; // Gera coluna > argumento<br>
 * String QBE_GREATER_OR_EQUALS_TO = "maiorOuIgual"; // Gera coluna >= argumento<br>
 * String QBE_LESS_OR_EQUALS_TO = "menorOuIgual"; // Gera coluna <= argumento<br>
 * String QBE_LIKE_PERC_FINAL = "*%"; // Gera coluna like 'argumento%'<br> String
 * QBE_LIKE_PERC_BEGIN = "%*"; // Gera coluna like '%argumento'<br> String
 * QBE_LIKE_PERC_TOTAL = "%*%"; // Gera coluna like '%argumento%'<br>
 * @author Cláudia Seara
 * @since jCOmpany 1.0
 */
public class PlcArgEntity implements Serializable

{

	private static final long serialVersionUID = -2502592984866262159L;
	private String querySel;
	private String name;
	private String operator;
	private String value;
	private String type;
	private String alias = "obj";
	private String format;
	private String orIsNull = "N";
	private String caseSensitive = "N";
	private Object objectValue;
	
	Map<String, Object> propsNaturalKey;

    public PlcArgEntity()
    {

    }

   /**
    * 
    * @since jCompany 3.0
    */
   public String getQuerySel()
    {
    	return( querySel );
    }

   /**
    * 
    * @since jCompany 3.0
    */
    public void setQuerySel ( String querySel )
    {
        this.querySel = querySel;
    }

    /**
     * 
     * @since jCompany 3.0
     */
	public String getOrIsNull()
    {
    	return( orIsNull );
    }

	/**
	 * 
	 * @since jCompany 3.0
	 */
    public void setOrIsNull ( String orIsNull )
    {
    	this.orIsNull = orIsNull;
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public String getName()
    {
    	return( name );
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public void setName( String name )
    {
    	this.name = name;
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public String getAlias()
    {
    	return( alias );
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public void setAlias( String alias )
    {
    	this.alias = alias;
    }


    /**
     * 
     * @since jCompany 3.0
     */
    public String getOperator()
    {
    	return( operator );
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public void setOperator( String operator )
    {
    	this.operator = operator;
    }


    /**
     * @return o value como String, tal como advindo da camada View.
     * @since jCompany 3.0
     */
    public String getValue()
    {
    	return( value );
    }
    
    public Object getObjectFormattedValue() throws PlcException {
		if (getObjectValue()!=null)
			return getObjectValue();
		else
			return getFormattedValue();
    }
    
    /**
     * @return value formatado, tal como advindo da camada View.
     * @since jCompany 3.0
     */
    public Object getFormattedValue() throws PlcException    {
    	   	
    	try {

    		if (getFormat()== null)
    			setFormat(PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_STRING);
    		
    		Object obj = getValue();

	    	if (getFormat().toLowerCase().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_DATE)) {
	    		String dateValue = null;
	    		if (getOperator() != null &&
						(getOperator().equals("<=") ||
								getOperator().equals("<")))
						if (obj != null && ((String)obj).length() ==7)
							dateValue = "01/" + ((String)obj).substring(0,10) + " 23:59:59";
						else
							dateValue = ((String)obj).substring(0,10) + " 23:59:59";
					else{
						if (obj != null && ((String)obj).length() ==7)
							dateValue = "01/" + ((String)obj) + " 00:00:00";
						else
							dateValue = ((String)obj) + " 00:00:00";
					}
	    		
	
				SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss");
				obj = formatter.parse(dateValue);
	
			} else if (getFormat().toLowerCase().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_LONG)) {
				obj = new Long(value);
			} else if (getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_BEGIN))
				obj = "%"+getValue();
			else if (getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_FINAL))
				obj = getValue()+"%";
			else if (getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_TOTAL))
				obj = "%"+getValue()+"%";
			else
				obj = value+"";
	    	
	    	return obj;
	    	
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {	"getValorFormatado", e });
		}
    }
    
    /**
     * @return se o argumento pode ser nulo também.
     * @since jCompany 3.0
     */
    public boolean isOrIsNull()   {
    	   	
    	return "S".equalsIgnoreCase(getOrIsNull());
    
    }

    /**
     * 
     * @since jCompany 3.0
     */
    
    public void setValue( String value )
    {
    	this.value = value;
    }


    /**
     * 
     * @since jCompany 3.0
     */
    public String getType()
    {
    	return( type );
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public void setType( String type )
    {
    	this.type = type;
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public String getFormat()
    {
    	return( format );
    }

    /**
     * 
     * @since jCompany 3.0
     */
    public void setFormat( String format )
    {
    	this.format = format;
    }

	/**
	 * @return Returns the caseSensitive.
	 * @since jCompany 3.0
	 */
	public String getCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive The caseSensitive to set.
	 * @since jCompany 3.0
	 */
	public void setCaseSensitive(String caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Verifica se o Bean de Argumento é de chave Natural 
	 * @return
	 */
	public boolean isNaturalKey() {
		return propsNaturalKey != null;
	}

	public Map<String, Object> getPropsNaturalKey() {
		return propsNaturalKey;
	}

	public void setPropsNaturalKey(Map<String, Object> propsNaturalKey) {
		this.propsNaturalKey = propsNaturalKey;
	}

	public Object getObjectValue() {
		return objectValue;
	}

	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}

}
