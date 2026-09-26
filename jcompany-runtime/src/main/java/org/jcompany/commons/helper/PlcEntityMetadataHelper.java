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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.comparator.PlcCompareId;
import org.jcompany.config.domain.PlcConfigComponent;
import org.jcompany.config.domain.PlcConfigComponentConvention;
import org.jcompany.config.domain.PlcConfigDescendentConvention;
import org.jcompany.config.domain.PlcConfigDetail;
import org.jcompany.config.domain.PlcConfigDetailConvention;
import org.jcompany.config.domain.PlcConfigGroupAggregation;
import org.jcompany.config.domain.PlcConfigGroupAggregationConvention;
import org.jcompany.config.domain.PlcConfigPatternConvention;
import org.jcompany.config.domain.PlcConfigSubDetail;
import org.jcompany.config.domain.PlcConfigSubDetailConvention;
import org.jcompany.config.domain.PlcConfigUserPrefConvention;
import org.jcompany.config.domain.PlcConfigPattern.ExclusionMode;
import org.jcompany.config.domain.PlcConfigPattern.Logic;

/**
 * Classe utilitária para investigacao de padrões de agregação
 */
public class PlcEntityMetadataHelper  {

    private static PlcEntityMetadataHelper INSTANCE = new PlcEntityMetadataHelper();
    private PlcEntityMetadataHelper() { }
    public static PlcEntityMetadataHelper getInstance() {
        return INSTANCE;
    }
 	 /**
	 * Caching de objetos de configuração para evitar investigar a mesma ação duas vezes
	 */
	private Map<String,PlcConfigGroupAggregationConvention> aggregationMetadata =
		new HashMap<String,PlcConfigGroupAggregationConvention>();

    protected static final Logger log = Logger.getLogger(PlcEntityMetadataHelper.class);
    
    /**
     * Resolve metadados para uma agregacao de entidades investigando sua estrutura e considerando
     * anotações como prioridades.
     * @param aggregationMetadataFinal Metadados POJO alterado para conter configuracao final. Deve conter a Entidade Principal e Padrão somente
     * @param aggregationMetadataConfigured Metadados anotados (configuracao)
     */
	public PlcConfigGroupAggregationConvention verifyAggregationByConvention(
			String urlConvention,
			PlcConfigGroupAggregationConvention amf,
			PlcConfigGroupAggregation amc) throws PlcException {
		
		// Evita introspeção para obtenção de metadados de agregação
		// duas vezes para mesma Entidade Raiz de agregação
		if (aggregationMetadata.containsKey(urlConvention))
		
			return aggregationMetadata.get(urlConvention);
		
		else  {
						
			// TODO Evitar investigar mesma Entity em outro Caso de Uso Padrão novamente
			amf = new PlcConfigGroupAggregationConvention();
		
			// Coloca a entidade
			amf.setEntity(amc.entity());
			
			Logic logic = amc.configPattern().logic();
			
			
			try {
				
				BeanInfo bi = Introspector.getBeanInfo(amc.entity());
	
		        PropertyDescriptor[] pd = bi.getPropertyDescriptors();
		        
		        PlcAnnotationHelper ah = PlcAnnotationHelper.getInstance();
		        
		        boolean hasDetail=false;
		        boolean hasSubDetail=false;
		        boolean hasComponent=false;
		        boolean hasDescendent=false;
		        boolean logicalExclusion=false;
		        if (pd != null) {
		        	
		        	// Os arrays devem estar fora do loop para acumularem metadados
		        	List<PlcConfigDetailConvention> details = null;
		        	List<PlcConfigComponentConvention> components = null;
		        	
			        for (int i = 0; i < pd.length; i++){
			            
			        	if(pd[i].getReadMethod() != null) {
			                
			            	// Se for transient, despreza
			            	if (pd[i].getPropertyType().getAnnotation(Transient.class)!=null)
			            		continue;
			            	
			            	// Se for atributos especiais - exclusao logica - TODO Traduzir ingles
			            	if ("sitHistoricoPlc".equals(pd[i].getName())) {
			            		logicalExclusion = true;
			            	} else          	
			            	// Se for atributos especiais - arquivo anexado - TODO Traduzir ingles
			            	if ("idArquivoAnexadoPlc".equals(pd[i].getName())) {
			            	} else
			            	// Se for mapeamento de coluna e nao for campo reservado, salta análise
			            	if (ah.propertyHasAnnotation(pd[i].getName(),amc.entity(),Column.class) ||
			            		ah.propertyHasAnnotation(pd[i].getName(),amc.entity(),Basic.class)) {
			            	} else
			            	if (logic.equals(Logic.AUTOMATIC_CONVENTION_BASED) ||
			            		logic.equals(Logic.MASTER_DETAIL) ||
		            			logic.equals(Logic.MANTAIN_DETAIL)||
		            			logic.equals(Logic.MANTAIN_SUBDETAIL)||
		            			logic.equals(Logic.SUBDETAIL)) {
			            		int nivelDetalhe = verifyDetail(details,
			            				amc.entity(),pd[i],amf,amc,hasDetail,hasSubDetail);
			            		if (nivelDetalhe>0)
			            			hasDetail=true;
			            		if (nivelDetalhe>1)
			            			hasSubDetail=true;
			            	} else
			            	if (ah.propertyHasAnnotation(pd[i].getName(),amc.entity(),Embedded.class)) {
				            		hasComponent = verifyComponent(components,amc.entity(),
				            				             pd[i],amf,amc,hasComponent);
				            	}
			            }
			        }
		        }
		        
		        if (!hasComponent)
		        	amf.setComponents(new PlcConfigComponentConvention[]{});
		        if (!hasDetail)
		        	amf.setDetails(new PlcConfigDetailConvention[]{});
		        
		        // TODO Descendentes deve ser investigados dos filhos
		        if (!hasDescendent)
		        	amf.setDescendents(new PlcConfigDescendentConvention[]{});
	
		        // Tentar identificar o padrão caso ele tenha sido anotado como nulo
		        Logic probableMaintenancePattern;
		        if (!logic.equals(logic.AUTOMATIC_CONVENTION_BASED))
		        	probableMaintenancePattern = logic;
		        else {
		        	if (hasSubDetail)
		        		probableMaintenancePattern = Logic.SUBDETAIL;
		        	else if (hasDetail)
		        		probableMaintenancePattern = Logic.MASTER_DETAIL;
		            else {
		            	probableMaintenancePattern = Logic.CRUD;
		            }
		        }
		        
		        // TODO User Preference
		        amf.setUserPreference(new PlcConfigUserPrefConvention());
		        
				PlcConfigPatternConvention cpc = 
					verifyPatternConvention(urlConvention,amc, 
							probableMaintenancePattern);
				amf.setConfigPattern(cpc);		   
				if (logicalExclusion)
					amf.configPattern().setExclusionMode(ExclusionMode.LOGICAL);
		        // Armazena resultado em caching
		        aggregationMetadata.put(urlConvention,amf);
		        
		        return amf;

			} catch (Exception e) {
				throw new PlcException("jcompany.erro.generico", new Object[] {
						"verifyAggregationByConvention", e }, e, log);
			}
		}
        	  
	}
	
	private PlcConfigPatternConvention verifyPatternConvention(
			String urlConvention, PlcConfigGroupAggregation amc,
			Logic probableMaintenancePattern) {
		
		PlcConfigPatternConvention cpc = new PlcConfigPatternConvention();
		if (amc.configPattern()==null ||
				amc.configPattern().logic().equals(Logic.AUTOMATIC_CONVENTION_BASED)) {
			// Assume via convenção de URL
			if (urlConvention.endsWith("sel")) 
				cpc.setLogic(Logic.SELECTION);
			else if (urlConvention.endsWith("slccon")) 
				cpc.setLogic(Logic.CONSULTATION);
			else if (urlConvention.endsWith("edtcon")) 
				cpc.setLogic(Logic.CONSULTATION);
			else if (urlConvention.endsWith("man"))
				cpc.setLogic(probableMaintenancePattern);
			else
				cpc.setLogic(Logic.CONTROL);
		} else {
			if (urlConvention.endsWith("sel")) 
				cpc.setLogic(Logic.SELECTION);
			else
				cpc.setLogic(amc.configPattern().logic());
			cpc.setExclusionMode(amc.configPattern().exclusionMode());
		}
		
		return cpc;
	}
	/**
	 * Verifica se possui algum detalhe mapeado. Se tiver pelo menos um e recebeu false, devolve true.
	 * @param pd propriedade a investigar
	 * @param amf Metadados em Annotation
	 * @param amc Metadados em POJO
	 * @return true se tiver algum detalhe ou o temDetalhe recebido.
	 */
	private int verifyDetail(List<PlcConfigDetailConvention> details,
			Class mainClass,PropertyDescriptor pd, 
			PlcConfigGroupAggregationConvention amf,
			PlcConfigGroupAggregation amc,boolean temDetalhe,boolean temSubDetalhe)
	 		throws PlcException {
		
		boolean temDetalheAux = temDetalhe;
		boolean temSubDetalheAux = temSubDetalhe;
		PlcAnnotationHelper ah = PlcAnnotationHelper.getInstance();
		// Se tem configuração em anotação 
		if (!(amc.details()[0].clazz() == Object.class)) {
			temDetalheAux = true;
			// Se ainda nao configurou
			if (amf.details()==null) {
				// Tem configuração para detalhes
				PlcConfigDetail detailConfig;
				PlcConfigDetailConvention[] detailsArray = 
					new PlcConfigDetailConvention[amc.details().length];
				for (int i = 0; i < amc.details().length; i++) {
					detailConfig = amc.details()[i];
					PlcConfigDetailConvention cdc = new PlcConfigDetailConvention();
					cdc.setClazz(detailConfig.clazz());
					cdc.setCardinality(detailConfig.cardinality());
					cdc.setCollectionName(detailConfig.collectionName());
					cdc.setNumberNew(detailConfig.numberNew());
					cdc.setComparator(detailConfig.comparator());
					cdc.setOnDemand(detailConfig.onDemand());
					cdc.setOnDemandAutomatic(detailConfig.onDemandAutomatic());
					cdc.setReferencePropertyDespise(detailConfig.referencePropertyDespise());
					cdc.setSignificativeZero(detailConfig.significativeZero());
						
					PlcConfigSubDetailConvention csdc = verifySubdetail(detailConfig.clazz(),
							                                             pd,cdc,detailConfig);
					cdc.setSubDetail(csdc);
					if (csdc != null)
						temSubDetalhe = true;
					cdc.setTestDuplicity(detailConfig.testDuplicity());
					detailsArray[i] =cdc;
				}
				amf.setDetails(detailsArray);
			}
			
		} else {
			// Investiga por detalhes
			if (ah.propertyHasAnnotation(pd.getName(),mainClass,OneToMany.class)) {
				
				Class detailClass = PlcReflectionHelper.getInstance().verifyGenericType(pd,mainClass);
				
				temDetalhe=true;
				// Incluir config detalhe
				
				if (details == null)
					details = new ArrayList<PlcConfigDetailConvention>();

				PlcConfigDetailConvention cdc = new PlcConfigDetailConvention();
			
				cdc.setClazz(detailClass);
			
				cdc.setCardinality("0..*"); // TODO Pegar de anotacao no campo
				cdc.setCollectionName(pd.getName());
				cdc.setNumberNew(4); // TODO Pegar de anotacao no campo
				cdc.setComparator(PlcCompareId.class); // TODO Pegar de anotacao no campo
				cdc.setOnDemand(false); // TODO Pegar de anotacao no campo
				cdc.setOnDemandAutomatic(false); // TODO Pegar de anotacao no campo
				cdc.setReferencePropertyDespise(verifyReferencePropertyDespise(detailClass));
				cdc.setSignificativeZero(false);// TODO Pegar de anotacao no campo
				cdc.setTestDuplicity(true); // TODO Pegar de anotacao no campo
				
				PlcConfigSubDetailConvention csdc = verifySubdetail(detailClass,pd,cdc,null);
				cdc.setSubDetail(csdc);
				if (csdc != null)
					temSubDetalhe = true;
				
				details.add(cdc);

				// Registra como array nos metadados
				PlcConfigDetailConvention[] detailsArray = 
					(PlcConfigDetailConvention[])details.toArray(
							new PlcConfigDetailConvention[details.size()]);

				amf.setDetails(detailsArray);
				
			}
		}
		
		if (temSubDetalheAux)
			return 2;
		else if (temDetalhe)
			return 1;
		else
			return 0;
				
	}
	
	
	/**
	 * 
	 * @param enclosingClass
	 * @return
	 */	
	private String verifyReferencePropertyDespise(Class detailClass) throws PlcException {
		
		
		try {
			
			// TODO Considerar também de anotação no campo
			BeanInfo bi = Introspector.getBeanInfo(detailClass);
			
	        PropertyDescriptor[] pd = bi.getPropertyDescriptors();
			
	        for (int i = 0; i < pd.length; i++){
	            
	        	if(pd[i].getReadMethod() != null) {
	        		
	        		if (pd[i].getPropertyType().equals(String.class) &&
	        			(PlcAnnotationHelper.getInstance().propertyHasAnnotation(
	        					pd[i].getName(),detailClass,Column.class) ||
	        			 PlcAnnotationHelper.getInstance().propertyHasAnnotation(
	    	        			pd[i].getName(),detailClass,Basic.class)))
	        			return pd[i].getName();
	        	
	        	}
	        }
	        
		} catch (Exception e) {
			throw new PlcException("jcompany.erro.generico", new Object[] {
					"verifyReferencePropertyDespise", e }, e, log);
		}
		
		// Se chegou aqui, nao foi possivel inferir um valor 
		throw new PlcException("#Could not get a reference property to the detail class "+detailClass.getName());
		
	}
	
	private PlcConfigSubDetailConvention verifySubdetail(
			Class detailClass,
			PropertyDescriptor pd, 
			PlcConfigDetailConvention cdc,
			PlcConfigDetail detailConfig) 
			throws PlcException{
		
		// Neste caso o default da configuraçao via anotação é "null"
		PlcConfigSubDetailConvention sdf = null;		
		PlcAnnotationHelper ah = PlcAnnotationHelper.getInstance();
		// Tem anotação para sub-detalhe
		if (detailConfig != null && !(detailConfig.subDetail().clazz() == Object.class)) {

			// Tem configuração para sub-detalhe
			PlcConfigSubDetail sdc = detailConfig.subDetail();
			sdf = new PlcConfigSubDetailConvention();
			sdf.setClazz(sdc.clazz());
			sdf.setCardinality(sdc.cardinality());
			sdf.setCollectionName(sdc.collectionName());
			sdf.setNumberNew(sdc.numberNew());
			sdf.setComparator(sdc.comparator());
			sdf.setReferencePropertyDespise(sdc.referencePropertyDespise());
			sdf.setSignificativeZero(sdc.significativeZero());
			sdf.setTestDuplicity(sdc.testDuplicity());

			cdc.setSubDetail(sdf);
			
		} else {
			
			// Investiga por subdetalhe
			try {
				
				// TODO Considerar também de anotação no campo
				BeanInfo bi = Introspector.getBeanInfo(detailClass);
				
		        PropertyDescriptor[] pdDetail = bi.getPropertyDescriptors();
				
		        for (int i = 0; i < pdDetail.length; i++){
		            
		        	if(pdDetail[i].getReadMethod() != null) {
		        		
		        		if (ah.propertyHasAnnotation(pdDetail[i].getName(),detailClass,OneToMany.class)) {
		    				Class subDetailClass = PlcReflectionHelper.getInstance().verifyGenericType(pdDetail[i],detailClass);
		    				sdf = new PlcConfigSubDetailConvention();
		    				
		    				sdf.setClazz(subDetailClass);
		    				sdf.setCardinality("0..*");
		    				sdf.setCollectionName(pdDetail[i].getName());
		    				sdf.setNumberNew(2); // TODO pegar de anotacao 
		    				sdf.setComparator(PlcCompareId.class); // TODO pegar de anotacao
		    				sdf.setReferencePropertyDespise(verifyReferencePropertyDespise(subDetailClass));
		    				sdf.setSignificativeZero(false);
		    				sdf.setTestDuplicity(true);
		    				
		    			}
		        	
		        	}
		        }
		        
		        // Tem que existir um registro por default
		        if (sdf==null)
		        	sdf = new PlcConfigSubDetailConvention();
		        
			} catch (Exception e) {
				throw new PlcException("jcompany.erro.generico", new Object[] {
						"verifyReferencePropertyDespise", e }, e, log);
			}
						
		}
		
		return sdf;
	}
	
	private boolean verifyComponent(List<PlcConfigComponentConvention> components,
			Class mainClass,
			PropertyDescriptor pd, 
			PlcConfigGroupAggregationConvention amf,
			PlcConfigGroupAggregation amc, boolean temComponente)
			throws PlcException{
		
		boolean temComponenteAux = temComponente;
		PlcAnnotationHelper ah = PlcAnnotationHelper.getInstance();
		// Foi configurado explicitamente
		if (!(amc.components()[0].clazz() == Object.class)) {
			temComponenteAux=true;
			// Se ainda nao configurou
			if (amf.components()==null) {
				// Tem configuração para componentes
				PlcConfigComponent componentConfig;
				PlcConfigComponentConvention[] componentsArray = 
					new PlcConfigComponentConvention[amc.components().length];
				for (int i = 0; i < amc.details().length; i++) {
					componentConfig = amc.components()[i];
					PlcConfigComponentConvention ccc = new PlcConfigComponentConvention();
					ccc.setClazz(componentConfig.clazz());
					ccc.setProperty(componentConfig.property());
					componentsArray[i] =ccc;
				}
				amf.setComponents(componentsArray);
			}
		} else {
			// Se entrou aqui é componente
			// Investiga por componentes
			if (ah.propertyHasAnnotation(pd.getName(),mainClass,Embedded.class)) {
				Class componentClass = pd.getPropertyType();
				
			}
		
		}
		return temComponenteAux;
	}
		
}