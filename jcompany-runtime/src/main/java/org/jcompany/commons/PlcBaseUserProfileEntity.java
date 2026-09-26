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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * jCompany. Value Object. Encapsula dados de Perfil do usuário correntemente
 * autenticado na sessão.
 * 
 * @version $Id: PlcBaseUserProfileEntity.java,v 1.7 2006/08/09 20:34:02 roberto Exp $
 */
public class PlcBaseUserProfileEntity implements Serializable {

	private static final long serialVersionUID = -472267141861819026L;
	
	/**
	 * Login do usuário autenticado
	 */
	private String login;
	
	/**
	 * Nome completo do usuário autenticado
	 */
	private String name;
	
	/**
     * Apelido ou name pelo qual o usuário é conhecido.<br>
     * O método <code>getDisplayName()</code> retorna o nickname, se
     * preenchido, se não, o name.
     * 
     * @since jCompany 3.x
     */
	private String nickname;
	
	/**
     * Valor para timeout da sessão do usuário. Se informado, sobrepõe a
     * informação do web.xml.
     * 
     * @since jCompany 3.x
     */
	private Long timeout;
	
	/**
	 * Email do usuário autenticado
	 */
	private String email;
	
	/**
	 * Identificador de enterprise corrente do usuário autenticado em formato String
	 */
	private String enterprise;
	
	/**
	 * Identificador de sub-enterprise (subdivisão) corrente do usuário autenticado em formato String
	 */
	private String subEnterprise;
	
	private Long idEnterprise;
    private Long idSubEnterprise;
	private String enterpriseInitials;
    private String enterpriseName;
    private String subEnterpriseName;
	private String visSkin;
	private String visLayout;
	/**
	 * Url que, se informada, desvia o usuario na entrada da aplicação
	 */
	private String initialUrl;
	private Map verticalSecurityPlc;

	/**
	 * IP de Origem
	 */
	private String ip;
	
	/**
	 * Grupos (de usuários) dos quais o usuário é membro.
	 */
	private List<String> groups = new ArrayList<String>();

	/**
	 * Roles do usuário.
	 */
	private List<String> roles = new ArrayList<String>();

	/**
	 * Mapa com as configurações de acesso aos resources da aplicação.
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private Map<String, Object> resources = new HashMap<String, Object>();

	/**
	 * O acesso geral do usuário na aplicação foi negado?
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private boolean accessDenied = false;
	
	/**
	 * O usuário autenticou com certificado?
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private boolean authenticatedCertification;
	
	/**
	 * O usuário autenticou com certificado único?
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private boolean personalCertification;
	
	/**
	 * O usuário é obrigado a estar autenticado com certificado para acessar a
	 * aplicação?
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private boolean mandatoryCertification;

	/**
	 * O usuário é obrigado a estar autenticado com certificado único para
	 * acessar a aplicação?
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private boolean uniqueMandatoryCertification;
	
	/**
	 * O certificado atende às necessidades para liberar acesso à aplicação?
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private boolean sufficientCertification;
	
	/**
	 * Expressão regular para encontrar e extrair identificador no Certificado.
	 * Default = "CN=(.+),OU".
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private String searchPatternCertification = "CN=(.+),OU";
	
	/**
	 * Dos groups retornados pela execução da expressão regular, qual utilizar?
	 * Default = 1.
	 * <p>
	 * Esta propriedade não é populada automaticamente pelo jCompany, a não ser
	 * que o JSecurity esteja configurado, ficando a cargo do desenvolvedor
	 * optar por utilizá-la ou não.
	 * </p>
	 * 
	 * @since jCompany 3.0
	 */
	private int searchGroupCertification = 1;
	
	/**
	 * Flag indicando se o profile do usuário foi carregado com sucesso. Esse
	 * controle serve para evitar que o usuário tenha acesso total à aplicação
	 * se ocorrer alguma pane durante a configuração do profile. Qualquer
	 * tentativa de acesso a qualquer recurso será negada.
	 * 
	 * @since jCompany 3.0
	 */
	private boolean successLoadingProfile;
	
	/**
	 * Inicializa mapa de filtros
	 */
	public PlcBaseUserProfileEntity(){
		verticalSecurityPlc = new HashMap();
	}
	
	/**
     * Retorna o nickname, se preenchido, se não, o name.
     * 
     * @since jCompany 3.x
     * 
     * @return O nickname, se preenchido, se não, o name.
     */
	public String getDisplayName() {
		if (!StringUtils.isBlank(nickname)) {
			return nickname;
		} else {
			return name;
		}
	}
	
	public void setDisplayName() {
		// mock - não deve ser retirado.
	}
	
	/**
	 * Verifica se o usuário possui a role informada. Procura nos groups do
	 * usuário e nas roles.
	 * 
	 * @param role
	 * @return
	 */
	public boolean isUserInRole(String role) {

		if (groups != null ) {
			Iterator<String> i = groups.iterator();
			while (i.hasNext()) {
				String g = i.next();
				if (g.equalsIgnoreCase(role)) {
					return true;
				}
			}
		}
		
		if (roles != null) {
			Iterator<String> i = roles.iterator();
			while (i.hasNext()) {
				String g = i.next();
				if (g.equalsIgnoreCase(role)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Map<String, Object> getResources() {
		return resources;
	}
	public void setResources(Map<String, Object> resources) {
		this.resources = resources;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/**
	* @deprecated Utiliar getEmpresa()
	*/ 
	public String getLoginEnterprise(){
		return getEnterprise();
	}

	/**
	* @deprecated Utilizar getSubEmpresa()
	*/
	public String getLoginSubEnterprise(){
		return getSubEnterprise();
	}

	/**
	* @deprecated Utilizar getLogin()
	*/
	public String getLoginUser(){
		return getLogin();
	}

	public String getVisLayout(){
		return visLayout;
	}

	public String getVisSkin(){
		return visSkin;
	}


	/**
	 * @deprecated Utiliar setEmpresa()
	 * @param newVal
	 */
	public void setLoginEnterprise(String enterprise){
		setEnterprise(enterprise);
	}

	/**
	 * @deprecated Utilizar setSubEmpresa()
	 * @param newVal
	 */
	public void setLoginSubEnterprise(String enterprise){
		setSubEnterprise(enterprise);
	}

	/**
	 * @deprecated Utilizar setLogin()
	 * @param newVal
	 */
	public void setLoginUser(String login){
		setLogin(login);
	}

	public void setFilter(String filter,String className){
		// Fazer
	}

	public void setVisLayout(String visLayout){
		this.visLayout=visLayout;
	}

	public void setVisSkin(String visSkin){
		this.visSkin=visSkin;
	}

	/**
	 * Maps com registro de classes e segurança vertival para o usuários
	 * nestas classes, conforme padrão descrito no @see PlcPerfilUsuarioBO
	 */
	public java.util.Map getVerticalSecurityPlc(){
		return verticalSecurityPlc;
	}

	public void setVerticalSecurityPlc(java.util.Map verticalSecurityPlc){
		this.verticalSecurityPlc=verticalSecurityPlc;
	}

	/**
	 * @return Long com o Id da Empresa
	 */
	public Long getIdEnterprise() {
		return idEnterprise;
	}

	/**
	 * @return Long com o Id da Sub Empresa
	 */
	public Long getIdSubEnterprise() {
		return idSubEnterprise;
	}

	/**
	 * @param long1
	 */
	public void setIdEnterprise(Long idEnterprise) {
		this.idEnterprise = idEnterprise;
	}

	/**
	 * @param long1
	 */
	public void setIdSubEnterprise(Long idSubEnterprise) {
		idSubEnterprise = idSubEnterprise;
	}

	/**
	 * @deprecated Utilizar getEmpresaNome()
	 */
	public String getNameEnterprise() {
		return getEnterpriseName();
	}

	/**
	 * @deprecated Utilizar getSubEmpresaNome()
	 */
	public String getNameSubEnterprise() {
		return getSubEnterpriseName();
	}

	/**
	 * @return String com a sigla da enterprise
	 */
	public String getEnterpriseInitials() {
		return enterpriseInitials;
	}

	/**
	 * @deprecated Utilizar setEmpresaNome()
	 */
	public void setNameEnterprise(String nameEnterprise) {
		setEnterpriseName(nameEnterprise);
	}

	/**
	 * @deprecated Utilizar setSubEmpresaNome()
	 */
	public void setNameSubEnterprise(String nameEnterprise) {
		setSubEmpresaName(nameEnterprise);
	}

	/**
	 * @param string
	 */
	public void setEnterpriseInitials(String enterpriseInitials) {
		this.enterpriseInitials = enterpriseInitials;
	}

	public List<String> getGrupos() {
		return groups;
	}

	/**
	 * @param newVal
	 */
	public void setGroups(List<String> groups) {
		this.groups=groups;
	}

	public String getInitialUrl() {
		return initialUrl;
	}
	
	public void setInitialUrl(String initialUrl) {
		this.initialUrl = initialUrl;
	}

	/**
	 * jCompany 20.  Exibe login do usuário se não for nulo
	 * @return login
	 */
	public String toString() {
		if (getLogin() != null || getIp() !=null)
			return getLogin()+" e IP: "+getIp();
		else if (getLogin() != null) {
		   return getLogin()+" e IP unknown";
		} else
		   return "anonyms";
	}

    /**
     * @return Retorna o ip.
     */
    public String getIp() {
        return this.ip;
    }
    /**
     * @param ip O ip a ser definido.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Returns the enterprise.
	 */
	public String getEnterprise() {
		return enterprise;
	}

	/**
	 * @param enterprise The enterprise to set.
	 */
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	/**
	 * @return Returns the enterpriseName.
	 */
	public String getEnterpriseName() {
		return enterpriseName;
	}

	/**
	 * @param enterpriseName The enterpriseName to set.
	 */
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	/**
	 * @return Returns the login.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login The login to set.
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the subEnterprise.
	 */
	public String getSubEnterprise() {
		return subEnterprise;
	}

	/**
	 * @param subEnterprise The subEnterprise to set.
	 */
	public void setSubEnterprise(String subEnterprise) {
		this.subEnterprise = subEnterprise;
	}

	/**
	 * @return Returns the subEnterpriseName.
	 */
	public String getSubEnterpriseName() {
		return subEnterpriseName;
	}

	/**
	 * @param subEnterpriseName The subEnterpriseName to set.
	 */
	public void setSubEmpresaName(String subEnterpriseName) {
		this.subEnterpriseName = subEnterpriseName;
	}

	public boolean isSuccessLoadingProfile() {
		return successLoadingProfile;
	}

	public void setSuccessLoadingProfile(boolean successLoadingProfile) {
		this.successLoadingProfile = successLoadingProfile;
	}

	public boolean isAuthenticatedCertification() {
		return authenticatedCertification;
	}

	public void setAuthenticatedCertification(boolean authenticatedCertification) {
		this.authenticatedCertification = authenticatedCertification;
	}

	public boolean isPersonalCertification() {
		return personalCertification;
	}

	public void setPersonalCertification(boolean personalCertification) {
		this.personalCertification = personalCertification;
	}

	public boolean isMandatoryCertification() {
		return mandatoryCertification;
	}

	public void setMandatoryCertification(boolean mandatoryCertification) {
		this.mandatoryCertification = mandatoryCertification;
	}

	/**
	 * @see {@link org.jcompany.commons.PlcBaseUserProfileEntity#uniqueMandatoryCertification}
	 */
	public boolean isUniqueMandatoryCertification() {
		return uniqueMandatoryCertification;
	}

	/**
	 * @see org.jcompany.commons.PlcBaseUserProfileEntity#uniqueMandatoryCertification
	 */
	public void setUniqueMandatoryCertification(boolean uniqueMandatoryCertification) {
		this.uniqueMandatoryCertification = uniqueMandatoryCertification;
	}

	public boolean isSufficientCertification() {
		return sufficientCertification;
	}

	public void setSufficientCertification(boolean sufficientCertification) {
		this.sufficientCertification = sufficientCertification;
	}

	public boolean isAccessDenied() {
		return accessDenied;
	}

	public void setAccessDenied(boolean accessDenied) {
		this.accessDenied = accessDenied;
	}

	public int getSearchGroupCertification() {
		return searchGroupCertification;
	}

	public void setSearchGroupCertification(int searchGroupCertification) {
		this.searchGroupCertification = searchGroupCertification;
	}

	public String getSearchPatternCertification() {
		return searchPatternCertification;
	}

	public void setSearchPatternCertification(String searchPatternCertification) {
		this.searchPatternCertification = searchPatternCertification;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
}