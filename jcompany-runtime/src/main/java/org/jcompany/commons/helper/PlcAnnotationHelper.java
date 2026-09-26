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
package org.jcompany.commons.helper;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ANNOTATION;
import org.jcompany.commons.annotation.PlcBusinessKey;
import org.jcompany.commons.annotation.PlcDetail;
import org.jcompany.commons.annotation.PlcEntityMetadata;
import org.jcompany.commons.annotation.PlcFactory;
import org.jcompany.commons.annotation.PlcImplementation;
import org.jcompany.commons.annotation.PlcIoC;
import org.jcompany.commons.annotation.PlcLogicExclusion;
import org.jcompany.commons.annotation.PlcSlaMaximumTime;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigSuffixClass;
import org.jcompany.config.domain.PlcConfigGroupAggregation;



/**
 * @since jCompany 3.0
 * Singleton. Classe utilitária para acesso a anotações
 */
public class PlcAnnotationHelper  {

	private static PlcAnnotationHelper INSTANCE = new PlcAnnotationHelper();
    private PlcAnnotationHelper() {   }
    public static PlcAnnotationHelper getInstance() {  return INSTANCE;  }
    
    protected static Logger log = Logger.getLogger(PlcAnnotationHelper.class);

 	/**
 	 * @since jCompany 3.0
 	 * Verifica se classe tem anotacao com indicadores para recuperaçao de suas instancias
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return true se tiver ou false
 	 */
	public boolean existsAnnotationToRetrievalSimpleDefault(Class clazz) throws PlcException {
		log.debug("############### Entered in existsAnnotationToRetrievalSimpleDefault");
	
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		
		boolean existesAnnotationQueryDefault = false;
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation a = annotations[i];
			if (a.toString().indexOf(ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_QUERYSEL_DEFAULT)>-1)
				return true;
		}
		
		return existesAnnotationQueryDefault;
	}
	
	/**
 	 * @since jCompany 3.0
 	 * Verifica se classe tem anotacao com indicadores para recuperaçao de suas instancias
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return true se tiver ou false
 	 */
	public boolean existsAnnotationToComponent(Class clazz) throws PlcException {
		
		log.debug("####### Entered in existsAnnotationToComponent");
	
		Embeddable embeddable = (Embeddable) clazz.getAnnotation(Embeddable.class);
		return embeddable != null;
	}

 	/**
 	 * @since jCompany 3.0
 	 * Verifica se classe tem anotacao com indicadores para filtro no padrão jCompany
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return true se tiver ou false
 	 */
	public boolean existsAnnotationToDefaultFilter(Class clazz) throws PlcException {
		log.debug("############### Entered in existsAnnotationToDefaultFilter");

		Annotation[] annotations = clazz.getDeclaredAnnotations();
		
		boolean existsAnnotationToDefaultFilter = false;
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation a = annotations[i];
			if (a.toString().indexOf(ANNOTATION.SEPARATOR_FILTER+"filterPlc")>-1)
				return true;
		}
		
		return existsAnnotationToDefaultFilter;
		
	}
	
	/**
	 * Devolve o endereço de um bitmap de icone para a classe informada.
	 * @since jCompany 3.2
	 * @param clazz Classe a ser investigada
	 * @param imagemDefault Endereco de bitmap (Imagem) default a ser devolvida, caso nao se encontre endereco especifico
	 * @return o endereço do bitmap encontrado na anotação PlcEntityMetadata(icone=...) ou o recebido como argumento.
	 */
	public String[] getBussinessKeyProps(Class clazz) {
		
		PlcBusinessKey bk = (PlcBusinessKey) clazz.getAnnotation(PlcBusinessKey.class);
		if (bk != null && bk.props()!=null && bk.props().length>0)
			return bk.props();
		else
			return null;
	}

	
	/**
 	 * @since jCompany 3.0
 	 * Devolve anotação QBE padrão
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return Annotation chamada [ClasseSemPackage.qbePadraoPlc] ou null se não encontrada
 	 */
	public Annotation getAnnotationQuerySelDefault(Class clazz) throws PlcException {
		
		log.debug("############### Entered in getAnnotationQuerySelDefault");
		
		String classNameWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
		String keyQBEDefault = classNameWithoutPackage+ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_QUERYSEL_DEFAULT;
			
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation a = annotations[i];
			if (a.toString().indexOf(keyQBEDefault)>-1)
				return a;
		}
		
		return null;
	}
	
	/**
 	 * @since jCompany 3.0
 	 * Devolve anotação QBE padrão
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return Annotation chamada [ClasseSemPackage.qbePadraoPlc] ou null se não encontrada
 	 */
	public Annotation getAnnotationQueryQbeOrSelDefault(Class clazz, String apiQuerySel) throws PlcException {
		
		log.debug("############### Entered in getAnnotationQueryQbeOrSelDefault");
		
		String classNameWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
		
		if (StringUtils.isEmpty(apiQuerySel))
			apiQuerySel = ANNOTATION.SUFFIX_QUERYSEL_DEFAULT;
		String keyQBEDefault = classNameWithoutPackage+ANNOTATION.QUERY_SEPARATOR+apiQuerySel;
			
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation a = annotations[i];
			if (a.toString().indexOf(keyQBEDefault)>-1)
				return a;
		}
		
		return null;
	}
	
	/**
 	 * @since jCompany 3.0
 	 * Devolve anotação de relação de propriedades para criterios QBE
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return Annotation chamada [ClasseSemPackage.qbePadraoPlc] ou null se não encontrada
 	 */
	public Annotation getAnnotationQueryQBEDefault(Class clazz) throws PlcException {
		
		log.debug("############### Entered in getAnnotationQueryQBEDefault");
		
		String classNameWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
			
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		
		for (int i = 0; i < annotations.length; i++) {
			Annotation a = annotations[i];
			String aS = a.toString();
			if (aS.indexOf(classNameWithoutPackage+ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_QUERYSEL_QBE_DEFAULT)>-1 || 
					aS.indexOf(classNameWithoutPackage+ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_QUERYSEL_DEFAULT)>-1)
				return a;
		}
		
		return null;
	}
	
	/**
	 * @since jCompany 3.0
	 * Se a anotação padrão é para Criteria (querySelQBE) ou hql (querySel)
	 * @return true se for para criteria
	 * @throws PlcException se não tem nenhuma anotação padrão
	 */
	public boolean isTypeDefaultAnnotationCriteria(Class clazz, String apiQuerySel) throws PlcException {

		log.debug("############### Entered in isTypeDefaultAnnotationCriteria");
		
		Annotation a = getAnnotationQueryQbeOrSelDefault(clazz, apiQuerySel);
		
		if (a == null)
			throw new PlcException("jcompany.errors.query.default.without.annotation",new Object[]{clazz.getName()});

		return a.toString().indexOf(ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_QUERYSEL_QBE_DEFAULT)>-1;

	}
	
	/**
	 * 
	 * @param pd propriedade no qual vai procurar o OneToMany
	 * @return Um Class com a classe definida no targetEntity do OneToMany
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	
	public Class returnDetailClassByOneToMany(PropertyDescriptor pd) throws SecurityException, NoSuchFieldException {

		if (pd.getReadMethod()==null)
			return null;

		Class abstractClass = pd.getReadMethod().getDeclaringClass();

		Field field = abstractClass.getDeclaredField(pd.getName());

		OneToMany oneToMany = field.getAnnotation(OneToMany.class);
		
		if (oneToMany != null){
			return oneToMany.targetEntity();	
		}else{
			return null;
		}
		
	}
	
	/**
	 * @since jCompany 3.0
	 * Se o campo em questão é copiável. Campos Transientes não são, a menos
	 * que possuam a anotação 
	 */
	public boolean isCopyable(PropertyDescriptor pd) throws PlcException {

		log.debug("############### Entered in isCopyable");
		
		// TODO 3.5 Aceitar a anotacao PlcCopiar também em Field!
		return existsCopyableGetter(pd.getReadMethod());

	}
	
	/**
	 * @since jCompany 3.0
	 * Se propriedade tem anotacao passa como String
	 * @param annotation nome da anotação
	 * @param f Field (propriedade)
	 * @return true se tiver ou false se não tiver
	 */
	public boolean propertyHasAnnotation(Field f,String annotation) throws PlcException {
		log.debug("############### Entered in propertyHasAnnotation");
		if (f==null || f.getAnnotations()==null)
			return false;
		else if (f.getAnnotations().toString().indexOf(annotation)==-1);
			
		return false;
	}
	
	/**
	 * @since jCompany 5.2
	 * Se propriedade tem anotacao
	 * @param fieldName Name of the Property Field
	 * @param clazz Class that contains the Property Field
	 * @Param annotation Annotation class
	 * @return true or false (including field not found)
	 */
	public boolean propertyHasAnnotation(String fieldName,Class clazz,Class annotation) throws PlcException {
		
		try {
			
			if (clazz.equals(PlcBaseEntity.class))
				return false;
			
			Field field = clazz.getDeclaredField(fieldName);
					
			return field.isAnnotationPresent(annotation);
		
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @since jCompany 3.0
	 * Se o campo em questão é copiável. Campos Transientes não são, a menos
	 * que possuam a anotação @PlcCopyable
	 */
	public boolean isMethodTransient(Method m) throws PlcException {

		log.debug("############### Entered in isMethodTransient");

		if (m == null || m.getDeclaredAnnotations() == null)
			return false;
		else if (m.getDeclaredAnnotations().toString().indexOf("@Transient")>-1)
			return true;
		else 
			return false;

	}
	
	/**
	 * @since jCompany 3.0
	 * Se o campo em questão é copiável. Campos Transientes não são, a menos
	 * que possuam a anotação @PlcCopyable na propriedade ou no getter
	 */
	public boolean existsCopyableGetter(Method m) throws PlcException {

		log.debug("############### Entered in existsCopyableGetter");

		if (m == null)
			return false;
		
		if (m.getDeclaredAnnotations() == null)
			return true;
		
		if (m.getDeclaredAnnotations().toString().indexOf("@Transient")>-1 &&
				m.getDeclaredAnnotations().toString().indexOf("@PlcCopyable")>-1)
			return true;
		else if (m.getDeclaredAnnotations().toString().indexOf("@Transient")>-1)
			return false;
		else 
			return true;

	}
	
	/**
	 * @since jCompany 3.0
	 * Se o campo em questão é copiável. Campos Transientes não são, a menos
	 * que possuam a anotação @PlcCopyable na propriedade ou no getter
	 */
	public boolean existsCopyableProperty(Field f) throws PlcException {

		log.debug("############### Entered in existsCopyableProperty");

		if (f == null || f.getDeclaredAnnotations() == null)
			return false;
		else if (f.getDeclaredAnnotations().toString().indexOf("@Transient")>-1 ||
				f.getDeclaredAnnotations().toString().indexOf("@PlcCopyable")>-1)
			return true;
		else 
			return false;

	}
	
	/**
	 * @since jCompany 3.0 Se classe tem anotação para exclusão lógica
	 * @param clazz Classe
	 * @return true se tiver ou false se não tiver (default)
	 * @throws PlcException
	 */
	public boolean existAnnotationToLogicalExclusion(Class clazz) throws PlcException {
		
		log.debug("############### Entered in existAnnotationToLogicalExclusion");
		return clazz.getAnnotation(PlcLogicExclusion.class) != null;
		
	}
	/**
	 * @since jCompany 3.0 Pega anotação de fábrica gerenciada
	 * @param clazz Classe Classe com anotação opcional.
	 * @return Nome da fábrica ou 'default', sob quaisquer exceções.
	 */
	public String getFactoryName(Class clazz) throws PlcException {
		log.debug("############### Entered in getFactoryName");
		try {
			PlcFactory fabrica = (PlcFactory) clazz.getAnnotation(PlcFactory.class);
			if (fabrica!=null && fabrica.nome()!=null) {
				return fabrica.nome();
			}
		} catch (Exception e) {
			//Não faz nada, pois retornará o default.
		}
		return "default";
	}
	
	
	/**
	 * @since jCompany 3.0 Pega anotação de fábrica gerenciada
	 * @return Opção de auto deteção ou false,  sob quaisquer exceções
	 */
	public Boolean getFactoryAutoDetectDialect(Class clazz) throws PlcException {
		log.debug("############### Entered in getFactoryAutoDetectDialect");
		try {
			PlcFactory factory = (PlcFactory) clazz.getAnnotation(PlcFactory.class);
			// Se não tem anotação é porque herda o default, que é autoDetectDialect
			if (factory==null)
				return true;
			else
				return factory.autoDetectDialect();
		} catch (Exception e) {
			return false;
		}
	}
	

	/**
 	 * @since jCompany 3.0.  Devolve anotação com nome de classe para Inversão de Controle na camada modelo.
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return Nome de classe da anotação PlcIoC ou null
 	 */
	public String getAnnotationIoCNameBC(Class<?> clazz) {
		log.debug("############### Entered in getAnnotationIoCNameBC");
		try {
			PlcIoC plcIoC = (PlcIoC) clazz.getAnnotation(PlcIoC.class);
			if (plcIoC==null || "".equals(plcIoC.nameBCClass()))
				return null;
			else
				return plcIoC.nameBCClass();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
 	 * @since jCompany 3.0.  Devolve anotação com nome de classe para Inversão de Controle na camada modelo.
 	 * @param clazz Classe com anotaçoes a verificar
 	 * @return Nome de classe da anotação PlcIoC ou null
 	 */
	public String getAnnotationIoCNameDAO(Class<?> clazz) {
		log.debug("############### Entered in getAnnotationIoCNameDAO");
		try {
			PlcIoC plcIoC = (PlcIoC) clazz.getAnnotation(PlcIoC.class);
			if (plcIoC==null || "".equals(plcIoC.nameDAOClass()))
				return null;
			else
				return plcIoC.nameDAOClass();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Verifica qual a classe que será utilizada como implementação segundo a anotação
	 * {@link org.jcompany.commons.annotation.PlcImplentacao}.
	 * Retorna um array de Class, de size dois, com no máximo uma entrada diferente de zero.
	 * Se a primeira posição for diferente de nulo, é a classe usada como implementação; se a segunda
	 * posição for diferente de nulo, é a classe utilizada como Factory obter a implementação.  
	 * @param implementation
	 * @return array de Class de size dois ou Nulo se nenhuma informação for encontrada.
	 * @see org.jcompany.commons.annotation.PlcImplentacao
	 * @throws ClassNotFoundException
	 */
	public Class[] getClassImplementation(PlcImplementation implementation) throws ClassNotFoundException {
		Class classes[] = null;
		if (implementation!=null) {
			if (implementation.classe()!=null && !implementation.classe().equals(Object.class)) {
				//Facade é a classe declarada
				classes = new Class[]{implementation.classe(), null};
			} else if (StringUtils.isNotEmpty(implementation.value())) {
				//Facade é a classe declarada em string
				classes = new Class[]{Class.forName(implementation.value()), null};
			} else if (implementation.fabricaClasse()!=null && !implementation.fabricaClasse().equals(Object.class)) {
				//Facade é obtido através de fábrica declarada
				classes = new Class[]{null, implementation.fabricaClasse()};
			} else if (StringUtils.isNotEmpty(implementation.fabrica())) {
				//Facade é obtido através de fábrica declarada em string
				classes = new Class[]{null, Class.forName(implementation.fabrica())};
			}
		}
		return classes;
	}


	
	/**
	 * Procura por anotação PlcSlaMaximumTime em método
	 * @param method método a ser investigado
	 * @return tempo em milisegundos anotado
	 */
	public long getSLAMethod(Method method) {
		log.debug("############### Entered in getSLAMethod");
		PlcSlaMaximumTime sla = method.getAnnotation(PlcSlaMaximumTime.class);
		if (sla != null)
			return sla.value();
		else
			return -1;
	}
	
	/**
	 * Devolve o endereço de um bitmap de icone para a classe informada.
	 * @since jCompany 3.03
	 * @param clazz Classe a ser investigada
	 * @param imagemDefault Endereco de bitmap (Imagem) default a ser devolvida, caso nao se encontre endereco especifico
	 * @return o endereço do bitmap encontrado na anotação PlcEntityMetadata(icone=...) ou o recebido como argumento.
	 */
	public String getEntityIcon(Class clazz, String defaultIcon) {
		
		PlcEntityMetadata entity = (PlcEntityMetadata) clazz.getAnnotation(PlcEntityMetadata.class);
		if (entity != null && !entity.icone().equals(""))
			return entity.icone();
		else
			return defaultIcon;
	}
	
	/**
	 * Devolve o endereço de um bitmap de icone para a classe informada.
	 * @since jCompany 3.03
	 * @param clazz Classe a ser investigada
	 * @param imagemDefault Endereco de bitmap (Imagem) default a ser devolvida, caso nao se encontre endereco especifico
	 * @return o endereço do bitmap encontrado na anotação PlcEntityMetadata(icone=...) ou o recebido como argumento.
	 */
	public String getEntityIconSel(Class clazz, String defaultSelIcon) {
		
		PlcEntityMetadata entity = (PlcEntityMetadata) clazz.getAnnotation(PlcEntityMetadata.class);
		if (entity != null && !entity.iconeSel().equals(""))
			return entity.iconeSel();
		else
			return defaultSelIcon;
	}
	
	/**
	 * Devolve o titulo para a classe informada.
	 * @since jCompany 3.03
	 * @param classe Classe a ser investigada
	 * @param imagemDefault Endereco de bitmap (Imagem) default a ser devolvida, caso nao se encontre endereco especifico
	 * @return o titulo encontrado na anotação PlcEntityMetadata(titulo=...) ou o recebido como argumento se nao for nulo ou o nome da classe.
	 */
	public String getEntityTitle(Class classe, String defaultTitle) throws PlcException {
		
		PlcEntityMetadata entity = (PlcEntityMetadata) classe.getAnnotation(PlcEntityMetadata.class);
		if (entity != null && !entity.title().equals(""))
			return entity.title();
		else if (defaultTitle != null)
			return defaultTitle;
		else
			return PlcEntityHelper.getInstance().getSimpleClassNameWithoutEntitySuffix(classe);
	}
	
	/**
	 * Devolve o numero de registros por pagina para a classe informada.
	 * @since jCompany 3.03
	 * @param clazz Classe a ser investigada
	 * @param defaultNumberByPage Numero a ser utilizado caso a classe nao tenha a declaração. Passar 0 (zero) se nao desejar um default.
	 * @return número de registros por pagina ou zero, se nao encontrou.
	 */
	public int getEntityNumberByPage(Class clazz,int defaultNumberByPage) {
		
		PlcEntityMetadata entity = (PlcEntityMetadata) clazz.getAnnotation(PlcEntityMetadata.class);
		if (entity != null && entity.numberByPage()>0)
			return entity.numberByPage();
		else if (defaultNumberByPage >0)
			return defaultNumberByPage;
		else
			return 0;
	
	}
	
	/**
	 * Devolve cláusula de ordenação para a classe informada.
	 * @since jCompany 3.03
	 * @param clazz Classe a ser investigada
	 * @param defaultOrdination Cláusula default se nao encontrar a ordenacao declarada
	 * @return clausula orderBy se declarada ou a clausula default. Em caso contrario, devolve string vazio ("")
	 */
	public String getEntityOrdination(Class clazz,String defaultOrdination) {
		
		PlcEntityMetadata entity = (PlcEntityMetadata) clazz.getAnnotation(PlcEntityMetadata.class);
		if (entity != null && !entity.ordination().equals(""))
			return entity.ordination();
		else if (defaultOrdination != null)
			return defaultOrdination;
		else
			return "";
	
	}
	
	/**
	 * Devolve url de manutenção para a classe informada.
	 * @since jCompany 3.03
	 * @param classe Classe a ser investigada
	 * @param defaultUrlMaintenance Url default se nao encontrar a ordenacao declarada
	 * @return url de manutenção se declarada ou a url default. Em caso contrario, devolve string vazio ("")
	 */
	public String getEntityUrlMaintenance(Class classe,String defaultUrlMaintenance) {
		
		PlcEntityMetadata entidade = (PlcEntityMetadata) classe.getAnnotation(PlcEntityMetadata.class);
		if (entidade != null && !entidade.urlMaintenance().equals(""))
			return entidade.urlMaintenance();
		else if (defaultUrlMaintenance != null)
			return defaultUrlMaintenance;
		else
			return "";
	
	}
	
	/**
	 * Devolve o nome de uma propriedade padrao para a classe informada.
	 * @since jCompany 3.03
	 * @param clazz Classe a ser investigada
	 * @param nameDefaultProperty Nome da propriedade default se nao encontrar a ordenacao declarada
	 * @return nome da propriedade se declarada em annotation, ou o nome default ou a convençao: nome da classe sem package e sufixo ENTITY
	 */
	public String getEntityNameDefaultProp(Class clazz, String nameDefaultProperty) {

		String defaultEntitySuffix = "ENTITY";
		try{
			PlcConfigSuffixClass plcConfigSuffixClass = PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class);
			defaultEntitySuffix = plcConfigSuffixClass.entitySuffix();
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}

		PlcEntityMetadata entity = (PlcEntityMetadata) clazz.getAnnotation(PlcEntityMetadata.class);
		if (entity != null && !entity.nameDefaultProp().equals(""))
			return entity.nameDefaultProp();
		else if (nameDefaultProperty != null && !nameDefaultProperty.equals(""))
			return nameDefaultProperty;
		else {		
			String auxProp = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
	    	if (clazz.getName().endsWith(defaultEntitySuffix))
	    		return auxProp.substring(0,1).toLowerCase()+auxProp.substring(1,auxProp.indexOf(defaultEntitySuffix));
	    	else
	    		return auxProp.substring(0,1).toLowerCase()+auxProp.substring(1);
		}
	}
	
	/**
	 * Indica se uma classe deve ser exibida na treeview explorer
	 * @since jCompany 3.04
	 * @param className Nome da classe
	 * @return true se ela não possuir anotação PlcEntityMetadata (default é usar) e false se possui com explorerUsa=false declarado
	 */
	public boolean explorerUse(String className) throws PlcException {
		try {
			Class classe = Class.forName(className);
			PlcEntityMetadata entity =  (PlcEntityMetadata) classe.getAnnotation(PlcEntityMetadata.class);
			if (entity == null)
				return true;
			else {
				return entity.explorerUsa();
			}
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"explorerUse", e }, e, log);
		}
	}
	
	/**
	 * Se tem anotacao em PlcEntityMetadata que indica que é classe de lookup
	 * @param clazz Classe a ser investigada
	 * @return true se for de lookup
	 */
	public boolean isLookupClass(Class clazz) throws PlcException {
		PlcEntityMetadata entity = (PlcEntityMetadata) clazz.getAnnotation(PlcEntityMetadata.class);
		if (entity == null)
			return false;
		else {
			return entity.classeLookup();
		}
	}
	
	/**
	 * @since jCompany 3.2 Se tem anotação ManyToMany, baseado na anotação PlcDetail(manyToMany=true,...)
	 * @param clazz
	 * @return true se tiver anotação PlcDetail com referencia manyToMany=true
	 */
	public boolean existsAnnotationManyToMany(Class clazz) throws PlcException {
		
		PlcDetail detail = (PlcDetail) clazz.getAnnotation(PlcDetail.class);
		return detail != null && detail.manyToMany();
	}
	
	/**
	 * @param clazz Classe
	 * @return True se tem anotação PlcDetail com somenteLeitura = true;
	 */
	public boolean existReadonlyDetail(Class<?> clazz) throws PlcException {
		PlcDetail detail = (PlcDetail) clazz.getAnnotation(PlcDetail.class);
		return detail != null && detail.readonly();
	}
	
	
	/**
	 * Busca um anotação de um pacote qualquer, sem nenhuma verificação.
	 */
	public <T extends Annotation> T getAnnotationPackage(String pack, Class<T> annotationClass) {
		forcaClassloader(pack+".package-info");
		
//		Package pacoteConfig = Package.getPackage(pacote);
//		return pacoteConfig==null ? null : pacoteConfig.getAnnotation(classeAnotacao);
		
		Class c=null;
		try {
		    c = Class.forName(pack+".package-info");
		} catch (ClassNotFoundException e) {
		    Package packageConfig = Package.getPackage(pack);
		    if (packageConfig!=null) 
			return packageConfig.getAnnotation(annotationClass);
		    
		}
		
		return c==null ? null : annotationClass.cast(c.getAnnotation(annotationClass));
			
	}
	

	/**
	 * @since jCompany 5.0
	 * 
	 * workaround p/ bug presente na versão do jdk 1.5.9
	 * 
	 * Forca a recuperação manual da "classe" package-info,
	 * pois quando não tem nenhuma outra classe no pacote.
	 * O classloader não faz automaticamente.
	 * 
	 * @param className completo para a classe package-info
	 */
	private void forcaClassloader(String className) {
		try {
			Class c = Class.forName(className);
			PlcConfigGroupAggregation ann = (PlcConfigGroupAggregation) c.getAnnotation(PlcConfigGroupAggregation.class);
		} catch (ClassNotFoundException e) {
			log.debug("Warning: package '"+className+"' not found." +
					" It's not necessarily an error, jCompany assuming defaults");
		}
	}
	
	/**
	 * Retorna o Class do targetEntity de um Relacionamento ManyToOne
	 * @param entity, vo que contém o relacionamento
	 * @param nomeField, nome do Atributo
	 * @return Retorna o targetEntity
	 */
	public Class getClassRelationshipManyToOne(Class entity, String fieldName)throws PlcException {
		try {
	       	 PlcConfigSuffixClass plcConfigSuffixClass 	= PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class);
	    	 String defaultEntitySuffix 					= plcConfigSuffixClass.entitySuffix();

			ManyToOne manyToOne;
			Field field = null;
			// Se for com o padrão ENTITY, recupera o field
			if (entity.getSimpleName().endsWith(defaultEntitySuffix))
				field = PlcReflectionHelper.getInstance().retrieveField(entity.getSuperclass(), fieldName);
			else
				field = PlcReflectionHelper.getInstance().retrieveField(entity, fieldName);
			// Se existir field recupera a anotação pelo field, se for null recupera do get
			if (field != null){
				if (PlcEntityHelper.getInstance().isEntity(field)){
					manyToOne 		= field.getAnnotation(ManyToOne.class);
					if (manyToOne != null){
						if (manyToOne.targetEntity() != null)
							return  manyToOne.targetEntity();
						else
							return field.getType();
					}
					else{
						Method method 	= PropertyUtils.getPropertyDescriptor(entity.newInstance(), fieldName).getReadMethod();
						manyToOne 		= method.getAnnotation(ManyToOne.class);
						if ( manyToOne != null && manyToOne.targetEntity() != null )
							return manyToOne.targetEntity() ;
						else
							return field.getType();
					}
	    		 } else {
	    			 // Se não for entidade, deve ser id. Se for id, está referenciando a propria entidade
	    			 return entity;
				}
			}else{
				Method method 	= PropertyUtils.getPropertyDescriptor(entity.newInstance(), fieldName).getReadMethod();
				manyToOne 		= method.getAnnotation(ManyToOne.class);
				if (  manyToOne != null && manyToOne.targetEntity() != null )
					return manyToOne.targetEntity() ;
				else
					return field.getType();
			}
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] { "getClassRelationshipManyToOne", e }, e, log);
		}
		
	}

	

}