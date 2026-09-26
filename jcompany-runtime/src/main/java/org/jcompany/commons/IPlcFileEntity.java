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
import java.util.Date;

public interface IPlcFileEntity {

    /**
     * 
     *@since jCompany 3.0
     */
    public byte[] getImage();

    /**
     * 
     *@since jCompany 3.0
     */
    public void setImage( byte[] newImage);

    /**
     * 
     *@since jCompany 3.0
     */
    public String getType();

    /**
     * 
     *@since jCompany 3.0
     */
    public void setType( String newType );

    /**
     * 
     *@since jCompany 3.0
     */
    public Integer getSize();

    /**
     * 
     *@since jCompany 3.0
     */
    public void setSize( Integer newSize);

    /**
     * 
     *@since jCompany 3.0
     */
	public String getUrl();

	/**
     * 
     *@since jCompany 3.0
     */
	public void setUrl(String newUrl);
	


	/**
	 * @return Object
	 * @since jcompany 3.0
	 */
	public Object getObjectAuxiliar() ;

	/**
	 * @param object
	 * @since jcompany 3.0
	 */
	public void setObjectAuxiliar(Object object);
	/**
     * 
     *@since jCompany 3.0
     */
	public String getName();

	/**
     * 
     *@since jCompany 3.0
     */
	public void setName(String nome);

	/**
     * 
     *@since jCompany 3.0
     */
	public Date getDateLastUpdate() ;

	/**
     * 
     *@since jCompany 3.0
     */
	public void setDateLastUpdate(Date dateLastUpdate);

	/**
     * 
     *@since jCompany 3.0
     */
	public String getUserLastUpdate();
	/**
     * 
     *@since jCompany 3.0
     */
	public void setUserLastUpdate(String userLastUpdate);

	/**
     * 
     *@since jCompany 3.0
     */
	public int getVersion();

	/**
     * 
     *@since jCompany 3.0
     */
	public void setVersion(int version);
	
	 public java.lang.Long getId();
	 public void setIdAux(String newIdAux);
	 public void setId(java.lang.Long newId);
	 public String getIdAux();
	 public String getIndExcPlc() ;
	 
	 /**
	  * @since jCompany 3.0 
	  */
	 public void setIndExcPlc(String novoindExcPlc);

}
