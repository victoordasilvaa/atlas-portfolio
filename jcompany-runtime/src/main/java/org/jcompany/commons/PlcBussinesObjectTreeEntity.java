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


/**
 * jCompany. Value Object. Utilizada pela lógica de Tree-View, para encapsular
 * dados a serem exibidos em uma árvore.
 * @version 1.0
 */
public class PlcBussinesObjectTreeEntity extends PlcBaseEntity{

	private static final long serialVersionUID = 2779016442564904358L;

	/**
	 * Guarda, na inclusão a chave do pai.
	 */
	private int idParentPlc;

	/**
	 * Guarda a posição do pai do registro na coleção
	 */
	private int idParentPosAux;

	private int levelPlc;

	/**
	* F-Fechado, A-Aberto, M-Minimizado
	*/
	private String situationPlc;
	private String linkPlc;
	private String iconPlc;
	private String iconSelectedPlc;
	private String idNamePlc;
	private String nameHierarchyPlc;
	/**
	 * Tipo para lógicas customizáveis (ex: O-Objeto, P-Pagina e C-Classe para explorer)
	 */
	private String typePlc="O";

	/**
	 * 1001,1002, ..., 10011001, 10011002,...,etc.
	 */
	private String orderPlc;

	/**
	 * C-Concatena ao link default, S-Substitui Link Default, J-Javascript, "#"-Não Exibir
	 */
	private String linkMode = "C";
	
	private String clazz;

	/**
	 * @since jcompany 3.0
	 */
	public PlcBussinesObjectTreeEntity(){

	}

	/**
	 * @since jcompany 3.0
	 */
	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getIconPlc(){
		return iconPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getSelectedIconPlc(){
		return iconSelectedPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getIdNamePlc(){
		return idNamePlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public int getIdParentPlc(){
		return idParentPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getLinkPlc(){
		return linkPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public int getLevelPlc(){
		return levelPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getNameHierarchyPlc(){
		return nameHierarchyPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getSituationPlc(){
		return situationPlc;
	}

	/**
	 * @since jcompany 3.0
	 */
	public String getOrderPlc(){
		return orderPlc;
	}

	/**
	 * @since jcompany 3.0
	 * @param    newVal
	 */
	public void setIconPlc(String iconPlc){
		this.iconPlc=iconPlc;
	}

	/**
	 * @since jcompany 3.0
	 * @param    newVal
	 */
	public void setIconSelectedPlc(String iconSelectedPlc){
		this.iconSelectedPlc=iconSelectedPlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setIdNamePlc(String idNamePlc){
		this.idNamePlc=idNamePlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setIdParentPlc(int idParentPlc){
		this.idParentPlc=idParentPlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setLinkPlc(String linkPlc){
		this.linkPlc=linkPlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setLevelPlc(int levelPlc){
		this.levelPlc=levelPlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setNameHierarchyPlc(String nameHierarchyPlc){
		this.nameHierarchyPlc=nameHierarchyPlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setSituationPlc(String situationPlc){
		this.situationPlc=situationPlc;
	}

	/**
	 * @since jcompany 3.0
	* @param    newVal
	*/
	public void setOrderPlc(String orderPlc){
		this.orderPlc=orderPlc;
	}


	/**
	 * @since jcompany 3.0
	 * @return int
	 */
	public int getIdParentPosAux() {
		return idParentPosAux;
	}

	/**
	 * @since jcompany 3.0
	 * @param i
	 */
	public void setIdParentPosAux(int idParentPosAux) {
		this.idParentPosAux = idParentPosAux;
	}

	/**
	 * @since jcompany 3.0
	 * @return String
	 */
	public String getLinkMode() {
		return linkMode;
	}

	/**
	 * @since jcompany 3.0
	 * @param string
	 */
	public void setLinkMode(String linkMode) {
		this.linkMode = linkMode;
	}

	/**
	 * @return Returns the clazz.
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz The clazz to set.
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return Retorna typePlc.
	 */
	public String getTypePlc() {
		return typePlc;
	}

	/**
	 * @param typePlc Registra typePlc
	 */
	public void setTypePlc(String typePlc) {
		this.typePlc = typePlc;
	}

}