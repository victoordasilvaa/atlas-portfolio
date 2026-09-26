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
package org.jcompany.config;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.config.commons.PlcConfigBehaviour;
import org.jcompany.config.commons.PlcConfigModelTechnology;
import org.jcompany.config.commons.PlcConfigService;
import org.jcompany.config.commons.PlcConfigServices;
import org.jcompany.config.domain.PlcConfigDetail;
import org.jcompany.config.domain.PlcConfigDetailConvention;
import org.jcompany.config.domain.PlcConfigGroupAggregation;
import org.jcompany.config.domain.PlcConfigGroupAggregationConvention;


/**
 *  @since jCompany 3.2 Classe de acesso à configuração Global  (meta-dados) do jCompany, para todas as camadas MVC-P. 
 *  Os meta-dados são armazenados como anotações em pacotes, abaixo do pacote base 'org.jcompany.config'
 */
public class PlcConfigHelper {

	
	private static PlcConfigHelper INSTANCE = null;
	protected static Logger log = Logger.getLogger(PlcConfigHelper.class);

	protected boolean containerSupportEJb = true;

	/**
	 * Caching para pacomódulos
	 */ 
	protected String[] packageModules = null;
	/**
	 * Caching para configuracoes globais, após resolucao de heranca
	 */
	protected HashMap<String, Annotation> configApplicationMap = new HashMap<String, Annotation>();
	
	private PlcConfigHelper() { }
	
	public static PlcConfigHelper getInstance(){
		if (INSTANCE==null)
			INSTANCE = new PlcConfigHelper();
 		return INSTANCE;
	}

	
	/**
	 * Força a inicialização das configurações logo no início da carga
	 */
	public static void touch() {}

	
	/**
	 * Busca uma anotação de configuração informada, partindo do pacote da aplicação (app), simulando uma 'herança': busca subsequente no
	 * escopo de "bridge" (emp) ou  finalmente no "raiz" (org.jcompany.config), assumindo assim um default.<p>
	 * Todas as anotações usam valores defaults, portanto somente haverá exceção em caso de montagem errada do arquivo WAR ou EAR.<p>
	 *  Utiliza caching para as chamadas subsequentes, para evitar novas buscas em vários pacotes
	 *  @param classConfiguration Classe correspondente à anotação desejada. 
	 *  @return A anotação cuja classe é a passada por argumentos
	 *  @exception PlcException Se não encontrar o arquivo package-info.class nos pacotes org.jcompany.config,
	 *  org.jcompany.config.emp e org.jcompany.config.app.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T get(Class<T> classConfiguration) throws PlcException{
		
		return get(classConfiguration,null);
	}
	
	/**
	 * Busca uma anotação de configuração informada, partindo do pacote da aplicação (app) e pacotes ".dominio", ".comuns" , ".modelo", ".persistencia".
	 * depois no escopo de "bridge" (emp) ou  senão achou, tentar achar defaults nas diversas camadas.<p>
	 * Todas as anotações usam valores defaults, portanto somente haverá exceção em caso de montagem errada do arquivo WAR ou EAR.<p>
	 *  Utiliza caching para as chamadas subsequentes, para evitar novas buscas em vários pacotes
	 *  @param classConfiguration Classe correspondente à anotação desejada.
	 *  @param subPackage nome do action/URL. Exemplo: "departamentoman" 
	 *  @return A anotação cuja classe é a passada por argumentos
	 *  @exception PlcException Se não encontrar o arquivo package-info.class nos pacotes org.jcompany.config,
	 *  org.jcompany.config.emp e org.jcompany.config.app.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T get(Class<T> classConfiguration, String subPackage) throws PlcException{

		if ( subPackage == null )
			subPackage = "";
		else
			subPackage = subPackage.startsWith(".") || subPackage.startsWith("/") ? subPackage : "." + subPackage;
		
		subPackage = subPackage.replace('/', '.');
		String cacheKey = classConfiguration.getName() + subPackage;
		//Primeiro tenta no caching
		T config = (T)configApplicationMap.get(cacheKey);
		if (!configApplicationMap.containsKey(cacheKey)){
			
			// Pega o pacote base de anotações de pacote
			String plcPackageBase = PlcConstantsCommons.PLC_PACKAGE_CONFIG_BASE;

			final String[] generalPackages = {
					".app",					// Tenta primeiro na Aplicação
					".emp",					// Tenta depois na camada Bridge
					".commons",				// Senão achou, tentar achar defaults nas diversas camadas.
					".control.geral",		// LAYER Controle
					".model",				// LAYER Modelo	
					".persistence",		// LAYER Persistencia
					".plc" };				// Plc

			//padrão xxx/actionxxx, provavelmente é um módulo, logo adiciona a sigla do módulo 
			ArrayList<String>  listGeneralPackages = new ArrayList<String>(Arrays.asList(generalPackages));
			if (StringUtils.isNotBlank(subPackage) && subPackage.contains(".")){
				listGeneralPackages.add(0,subPackage.substring(0,subPackage.lastIndexOf(".")));
				subPackage = subPackage.substring(subPackage.lastIndexOf("."));
			}
			
			
			final String[] appPackages = {"", ".domain", ".commons" , ".model", ".persistence" }; 

			// Percorre os pacotes "Gerais" e "Pacotes da Aplicação" procurando pela anotação	
			for(String generalPackage : listGeneralPackages){
				for(String appPackage : appPackages){
					// Se o subPacote já tiver o pacoteGeral então não concatena 
					if (! subPackage.startsWith(generalPackage))
						config = getAnnotationPackage(plcPackageBase + appPackage + generalPackage + subPackage, classConfiguration);
					else
						config = getAnnotationPackage(plcPackageBase + appPackage + subPackage, classConfiguration);
					
					// Se encontrou a anotação então não procura nos outros pacotes
					if (config != null)
						break;
				}			
				// Se encontrou a anotação então não procura nos outros pacotes
				if (config != null)
					break;
			}

			if (config==null && !"inicial".equals(subPackage) ) 
				log.debug("#jCmpany Configuration Error: annotation not found "+classConfiguration+" in any application package");

			// Coloca a anotação no cache
			configApplicationMap.put(cacheKey, config);
		}
		return config;
	}
	
	/**
	 * Busca uma anotação de configuração informada, mas buscando no pacote base sem sufixos padrões.
	 * Exemplo: para URLs "/funcionarioman", procura em "funcionario"
	 * @return Anotação de Configuração para pacote com ajuste ou null, se não existir pacote com sufixo padrão
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getByUrlConvention(String subPackage,Class<T> classConfiguration) throws PlcException{

		// Considera os sufixos padrões abaixo, de manutenção e consulta
		String[] sufixos = new String[]{"slccon","edtcon","man","sel"};
		for (int i = 0; i < sufixos.length; i++) {
			if (subPackage.endsWith(sufixos[i]))
				return get(classConfiguration,subPackage.substring(0,subPackage.indexOf(sufixos[i])));
		}
		
		// Se não há URL com sufixo padrão, devolve null
		return null;
	}
	
	
	/**
	 * Método para recuperação de metadados de Açoes (sub-pacotes da aplicação) 
	 * @param subPackage nome do action/URL. Exemplo: "departamentoman"
	 * @param classConfiguration Nome da classe de anotação dos metadados. Exemplo: PlcConfigAcaoControle
	 * @return Instancia da anotacao para a classe informada.
	 */
	public <T extends Annotation> T get(String subPackage, Class<T> classConfiguration) throws PlcException {
		
		return get(classConfiguration,subPackage);
		
	}
	
	/**
	 * Verifica se um subpacote está dentro de um pacote, ou é o próprio pacote.
	 * @param subPackage
	 * @param _package
	 * @return true se é o mesmo pacote, ou começa pelo pacote.
	 */
	private boolean subPackageOfPackage(String subPackage, String _package) {
		return subPackage.equals(_package) || subPackage.startsWith(_package+'.');
	}
	
	/**
	 * Permite o registro de módulos para o utilitário de Metadados, para que seus pacotes sejam considerados em buscas
	 * @param Conjunto de siglas
	 */
	public void registraModulos(String[] moduleInitials) {
		if (moduleInitials!=null) {
			this.packageModules = new String[moduleInitials.length];
			for(int i=0; i<this.packageModules.length; i++) {
				this.packageModules[i] = "."+moduleInitials[i];
			}
		} else {
			this.packageModules = new String[0];
		}
	}
	
	/**
	 * Busca um array de subpacotes dos modulos da aplicação, para serem usados no composição do pacote de configuração.
	 * @return array com os subpacote, prefixados com ".", ou null se a aplicação não possuir modulos.
	 */
	private String[] getSubPackageModules() throws PlcException {
		return this.packageModules;
	}
	
	/**
	 * Verifica se o subpacote é um dos pacotes informados, ou pertence a algum deles.
	 * @param subPackage
	 * @param __package
	 * @return
	 * @see PlcConfigHelper#subPacoteDoPacote(String, String);
	 */
	private boolean subPackageOfPackage(String subPackage, String ... __package) {
		for (String _pacote : __package) {
			if (subPackageOfPackage(subPackage, _pacote)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Retorna uma nova instancia (new) de uma classe especificada, podendo ser um descendente. É recomendável
	 * que, após criada, a instância seja mantida em cache pelo chamador. (Os localizadores de serviço do jCompany
	 * realizam esta tarefa.
	 * @param clazz classe-base para a recuperação da instancia.
	 * @return instancia da classe, podendo ser um descendente alternativamente configurado.
	 */
	public Object createService(Class clazz) throws PlcException {
		
		try {
			PlcConfigServices cs = this.get(PlcConfigServices.class);
			// Se anotou coleção 
			if (cs != null) {
				for (int i = 0; i < cs.value().length; i++) {
					if (cs.value()[i].classReference().equals(clazz))
						return cs.value()[i].classAlternative().newInstance();
				}
			} else {
				// Se tem somente uma anotação
				PlcConfigService s = this.get(PlcConfigService.class);
				if (s!=null && s.classReference().equals(clazz))
					return s.classAlternative().newInstance();
			}
			
			// Se chegou até aqui, não encontrou nada anotado, então assume classe original
			return clazz.newInstance();
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"getService", e }, e, log);
		}
		
	}
	
	/**
	 * Retorna uma classe alternativa para uma classe especificada, podendo ser um descendente.
	 * @param clazz classe-base para a recuperação da instancia.
	 * @return classe alternativa, podendo ser um descendente alternativamente configurado, ou a própria enviada, se não encontrou configuração.
	 */
	public Class getAlternativeClass(Class clazz) throws PlcException {
		try {
			
			// Se anotou coleção 
			PlcConfigServices cs = this.get(PlcConfigServices.class);
			if (cs != null) {
				for (int i = 0; i < cs.value().length; i++) {
					if (cs.value()[i].classReference().equals(clazz))
						return cs.value()[i].classAlternative();
				}
			} else {
				// Se tem somente uma anotação
				PlcConfigService s = this.get(PlcConfigService.class);
				if (s!=null && s.classReference().equals(clazz))
					return s.classAlternative();
			}
			
			// Se chegou até aqui, não encontrou nada anotado, então assume classe original
			return clazz;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"getAlternativeClass", e }, e, log);
		}
		
	}

	/**
	 * Seta o flag que indica suporte ejb pelo container 
	 */
	public void setContainerSupportEjb(boolean b) {
		this.containerSupportEJb = b;
	}
	
	/**
	 * @throws PlcException 
	 * @since jCompany 5.0
	 * Determina se o Container irá gerenciar as transações
	 *	*/
	public boolean containerManagingTransaction() throws PlcException  {

		PlcConfigBehaviour cc = get(PlcConfigBehaviour.class);
		PlcConfigModelTechnology cmt = get(PlcConfigModelTechnology.class);
		if(cc != null && cmt != null) {
			//Só retorna verdadeiro se containerGerenciaTransacao=TRUE e modeloTecnologia=EJB3, para garantir consistência
			return (cc.containerManagingTransaction() && (cmt.technology() == PlcConfigModelTechnology.Technology.EJB3));
		}

		return false;
	}
	
	
	/**
	 * Busca um anotação de um pacote qualquer, sem nenhuma verificação.
	 * Se não encontrar a anotação no pacote, tentar pegar da classe atual (PlcConfigHelper).
	 */
	private <T extends Annotation> T getAnnotationPackage(String _package, Class<T> classAnnotation) {
		T annotationPackage = PlcAnnotationHelper.getInstance().getAnnotationPackage(_package, classAnnotation);
		return annotationPackage!=null ? annotationPackage : this.getClass().getAnnotation(classAnnotation);
	}
	
	/**
	 * @since jCompany 5 Pega diretorio de configuracao (meta-dados)
	 * @throws PlcException se nao encontrou diretorio ou URL está mal formada
	 */
	public PlcConfigGroupAggregation getConfigDomainCurrent(String actionNameWithoutBar) throws PlcException {
		
		try {
			PlcConfigGroupAggregation aggregation =  
				get(actionNameWithoutBar,PlcConfigGroupAggregation.class);
			if (aggregation == null) {
				aggregation = getByUrlConvention(actionNameWithoutBar,PlcConfigGroupAggregation.class);
				if (aggregation == null)
					return get("defaultplc",PlcConfigGroupAggregation.class);
				else
					return aggregation;
			} else
				return aggregation;
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"getConfigDomainCurrent", e }, e, log);
		}
	}
	
	/**
	 * Retorna a configuração de detalhe. Nunca retorna null, pois lança excessão caso não consiga achar a configuração.
	 * Não deve ser chamado caso detCorrPlc esteja vazio.
	 * @return configuração do detalhe de acordo com o detCorrPlc.
	 * @throws PlcException
	 */
	public PlcConfigDetailConvention getConfigurationDetail(PlcConfigGroupAggregationConvention currentConfigAction, String detailName) throws PlcException {
		
		if (currentConfigAction==null) {
			throw new PlcException("#There is no collaboration config");
		}
		PlcConfigDetailConvention[] details = currentConfigAction.details();
		if (details==null||details.length==0) {
			throw new PlcException("There is no detail conf to this use case and it's trying to add detail: "+detailName);
		}
		PlcConfigDetailConvention detail = null;
		for (PlcConfigDetailConvention _detail : details) {
			if (detailName.equals(_detail.collectionName())) {
				detail = _detail;
				break;
			}
		}
		if (detail==null) {
			throw new PlcException("Not configured detail to use case: "+detailName);
		}
		return detail;
	}
	


	
}
