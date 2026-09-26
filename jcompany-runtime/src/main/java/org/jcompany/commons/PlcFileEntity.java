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
import java.util.Date;

/**
 * jCompany. Value Object. Encapsula dados de arquivos anexados para lógicas de
 * upload/download do arquivo vinculados a transações <p>
 */
public class PlcFileEntity extends PlcBaseEntity implements IPlcFileEntity,Serializable {

	private String userLastUpdate;
	private Date dateLastUpdate;
	private int version;
	private String name;
	protected byte[] image;
    protected String type;
    protected Integer size;
    protected String url;
    protected Object objectAux;

    /**
     * 
     *@since jCompany 3.0
     */
    public PlcFileEntity()
    {
    }

    /**
     * 
     *@since jCompany 3.0
     */
    public byte[] getImage()
    {
    	return( image );
    }

    /**
     * 
     *@since jCompany 3.0
     */
    public void setImage( byte[] newImage)
    {
        image = newImage;
    }

    /**
     * 
     *@since jCompany 3.0
     */
    public String getType()
    {
    	return( type );
    }

    /**
     * 
     *@since jCompany 3.0
     */
    public void setType( String newType )
    {
        type = newType;
    }

    /**
     * 
     *@since jCompany 3.0
     */
    public Integer getSize()
    {
    	return( size );
    }

    /**
     * 
     *@since jCompany 3.0
     */
    public void setSize( Integer newSize)
    {
        size = newSize;
    }

    /**
     * 
     *@since jCompany 3.0
     */
	public String getUrl(){
		return url;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public void setUrl(String newUrl){
		url = newUrl;
	}

	/**
	 * @return Object
	 * @since jcompany 3.0
	 */
	public Object getObjectAuxiliar() {
		return objectAux;
	}

	/**
	 * @param object
	 * @since jcompany 3.0
	 */
	public void setObjectAuxiliar(Object object) {
		objectAux = object;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public String getName() {
		return name;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public void setDateLastUpdate(Date dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public String getUserLastUpdate() {
		return userLastUpdate;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public void setUserLastUpdate(String userLastUpdate) {
		this.userLastUpdate = userLastUpdate;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public int getVersion() {
		return version;
	}

	/**
     * 
     *@since jCompany 3.0
     */
	public void setVersion(int version) {
		this.version = version;
	}

}
