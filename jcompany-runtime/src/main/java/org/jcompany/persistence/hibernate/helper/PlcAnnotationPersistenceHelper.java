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
package org.jcompany.persistence.hibernate.helper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Entity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ANNOTATION;
import org.jcompany.model.annotation.PlcTransactionPersist;
import org.jcompany.model.annotation.PlcTransactionRead;
import org.jcompany.model.annotation.PlcTransactionNotApply;


/**
 * Classe de Utilitários para acesso a meta-dados de anotação EJB3 e Hibernate
 * @since jCompany 3.0
 * @version $Id: PlcAnnotationPersistenceHelper.java,v 1.9 2006/08/17 17:06:04 alvim Exp $
 */
public class PlcAnnotationPersistenceHelper  implements Serializable{
	

	private static final long serialVersionUID = 8535697015353850994L;
	private static PlcAnnotationPersistenceHelper INSTANCE = new PlcAnnotationPersistenceHelper();
	protected static Logger log = Logger.getLogger(PlcAnnotationPersistenceHelper.class);
	private PlcAnnotationPersistenceHelper() { }
	public static PlcAnnotationPersistenceHelper getInstance(){
 		return INSTANCE;
	}
	
	/**
	 * Recebe uma coleção de NamedQueries e devolve a NamedQuery padrão QBE
 	 * @since jCompany 3.0
	 * @param nqs Coleção de NamedQueries
	 * @return NamedQuery com sufixo padrão propQBE
	 */
	public NamedQuery getNamedQueryQBEDefault(NamedQueries nqs) throws PlcException {
		
		log.debug("############### Entered in getNamedQueryQBEDefault");
		
		for (int i = 0; i < nqs.value().length; i++) {
			NamedQuery nq =nqs.value()[i];
			if (nq.name().endsWith(PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_QBE_DEFAULT))
				return nq;
		}
		return null;
	}

	/**
	 * Recebe uma coleção de NamedQueries e devolve a NamedQuery padrão QBE
	 * @since jCompany 3.0
	 * @param nqs Coleção de NamedQueries
	 * @return NamedQuery com sufixo padrão propQBE
	 */
	public NamedQuery getNamedQuerySelDefault(NamedQueries nqs) throws PlcException {
		
		log.debug("############### Entered in getNamedQuerySelDefault");
		
		for (int i = 0; i < nqs.value().length; i++) {
			NamedQuery nq =nqs.value()[i];
			if (nq.name().endsWith(PlcConstantsCommons.ANNOTATION.SUFFIX_QUERYSEL_DEFAULT))
				return nq;
		}
		return null;
	}
	
	
	/**
	 * Recebe uma coleção de NamedQueries e devolve a NamedQuery padrão de Relatório
	 * @param nqs Coleção de NamedQueries
	 * @return NamedQuery com sufixo padrão propQBE
	 * @since jCompany 3.0
	 */
	public NamedQuery getNamedQuerySelReport(NamedQueries nqs, String reportNamedQuery) throws PlcException {
		
		log.debug("############### Entered in getNamedQuerySelReport");
		
		/*
		 * Se o parâmetro reportNamedQueryCorrente estiver null é porque é o mestre, portanto recupera com o namedQuery padão de relatório
		 */
		if (( reportNamedQuery  == null ) ||  ( reportNamedQuery.equals("")))
			reportNamedQuery = PlcConstantsCommons.ANNOTATION.SUFFIX_REPORT_DEFAULT;
		
		
		for (int i = 0; i < nqs.value().length; i++) {
			NamedQuery nq =nqs.value()[i];
			if (nq.name().indexOf(reportNamedQuery) >1)
				return nq;
		}
		return null;
	}


	/**
	 * jCompany 3.0 Pega anotaçoes que contenham ".naoDeveExistir" (TOKEN Reservado)
	 * @param clazz
	 * @return lista de annotations "NaoDeveExistir"
	 * @throws PlcException
	 */
	public List<NamedQuery> getAnnotationsQueryShouldntExist(Class clazz) throws PlcException {
		log.debug("############### Entered in getAnnotationsQueryShouldntExist");
		
		List<NamedQuery> avoidEqualsAnnotations = new ArrayList<NamedQuery>();
		
		String noPackageClassName = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
		
		NamedQueries nqs = (NamedQueries) clazz.getAnnotation(NamedQueries.class);
		
		if (nqs == null) {
			NamedQuery nq = (NamedQuery)getAnnotationsQueryShouldntExist(clazz,noPackageClassName);
			if (nq != null)
				avoidEqualsAnnotations.add(nq);
		} else {
			
			for (int i = 0; i < nqs.value().length; i++) {
				NamedQuery nq = nqs.value()[i];
				if (nq.name().indexOf(noPackageClassName+ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_AVOID_EQUALS_DEFAULT)>-1)
					avoidEqualsAnnotations.add(nq);
			}
			
		}
		
		return avoidEqualsAnnotations;
		
	}
	
	/**
	 * Pega uma anotação de query avoidEquals para uma classe.
	 * @since jCompany 3.0
	 * @param clazz Classe com anotação
	 * @param noPackageClassName Nome da classe sem pacote
	 * @return Anotação NamedQuery para a classe ou null se não encontrar
	 */
	protected NamedQuery getAnnotationsQueryShouldntExist(Class<? extends PlcBaseEntity> clazz, String noPackageClassName) {
		log.debug("############### Entered in getAnnotationsQueryShouldntExist");
		
		NamedQuery nq = (NamedQuery) clazz.getAnnotation(NamedQuery.class);
		if (nq!=null && nq.name().indexOf(noPackageClassName+ANNOTATION.QUERY_SEPARATOR+ANNOTATION.SUFFIX_AVOID_EQUALS_DEFAULT)>-1)
			return nq;
		else
			return null;
	}
	
	/**
	 * Pega uma anotação de query por nome para uma  classe
	 * @since jCompany 3.0
	 * @param clazz Classe com anotação
	 * @param apiQuerySel Nome do NamedQuery desejado
	 * @return NamedQuery para nome passado ou null se não encontrar
	 */
	public NamedQuery getNamedQueryByName(Class clazz, String apiQuerySel) throws PlcException {
		
		log.debug("############### Entered in getNamedQueryByName");
			
		String noPackageClassName = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
		
		NamedQueries nqs = (	NamedQueries) clazz.getAnnotation(NamedQueries.class);
		
		if (nqs == null) {
			NamedQuery nq = (NamedQuery) clazz.getAnnotation(NamedQuery.class);
			if (nq != null && nq.name().equals(noPackageClassName+ANNOTATION.QUERY_SEPARATOR+apiQuerySel))
				return nq;
		} else {
			
			for (int i = 0; i < nqs.value().length; i++) {
				NamedQuery nq = nqs.value()[i];
				if (nq.name().endsWith(noPackageClassName+ANNOTATION.QUERY_SEPARATOR+apiQuerySel))
					return nq;
			}
			
		}
		
		return null;
	}
	

	/**
	 * Devolve query de edição padrão, se existir, seguindo a convenção '[nome da classe sem pacote].queryEdita'
	 * @param vo Vo a ser investigado
	 * @return Query de edição ou null, se não encontrou anotação
	 */
	public String getAnnotationQueryEditionDefault(Class clazz) throws PlcException {

		NamedQuery nq = getNamedQueryByName(clazz,ANNOTATION.SUFFIX_QUERYEDITION_DEFAULT);
		
		if (nq == null)
			return null;
		else
			return nq.query();
	}
	
	/**
	 * Verifica se há anotação de manyToOne para uma propriedade 
	 * @since jCompany 3.0
	 * @param propertyType Tipo da propriedade
	 * @return true se tem anotação manyToOne e false se não tem.
	 */
	public boolean existsAnnotationManyToOne(Class propertyType) throws PlcException{
		log.debug("############### Entered in existsAnnotationManyToOne");
		if (propertyType.getAnnotation(ManyToOne.class) != null || 
			propertyType.getAnnotation(org.hibernate.mapping.ManyToOne.class) != null)
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se método tem anotação para transacao de leitura
	 * @since jCompany 3.0
	 * @return String O identificador da fábrica ou null se não tiver anotação PlcTransactionRead
	 */
	public String existsAnnotationTransactionRead(Method method) throws PlcException{
		log.debug("############### Entered in existsAnnotationTransactionRead");

		if (method.getAnnotation(PlcTransactionRead.class)==null)
			return null;
		else
			return ((PlcTransactionRead)method.getAnnotation(PlcTransactionRead.class)).value();
	}
	
	/**
	 * Verifica se método tem anotação para transacao de leitura
	 * @since jCompany 3.0
	 * @return String O identificador da fábrica ou null se não tiver anotação PlcTransactionPersist
	 */
	public String existsAnnotationTransactionPersist(Method method) throws PlcException{
		log.debug("############### Entered in existsAnnotationTransactionPersist");
		
		if (method.getAnnotation(PlcTransactionPersist.class)==null)
			return null;
		else
			return ((PlcTransactionPersist)method.getAnnotation(PlcTransactionPersist.class)).value();
	}
	
	/**
	 * Verifica se método tem anotação para não execução de transacao (descendentes de PlcBaseFacadeImpl já
	 * executam por default (com commit). A menos que esta anotação seja utilizada.
	 * @return String O identificador da fábrica ou null se não tiver anotação PlcTransactionNotApply
	 * @since jCompany 3.0
	 */
	public boolean existsAnnotationTransactionNotApply(Method method) throws PlcException{
		log.debug("############### Entered in existsAnnotationTransactionNotApply");

		return method.getAnnotation(PlcTransactionNotApply.class)!=null;
			
	}
	
	/**
	 * Devolve query de edição padrão, se existir, seguindo a convenção '[nome da classe sem pacote].queryEdita'
	 * @param vo Vo a ser investigado
	 * @return Query de edição ou null, se não encontrou anotação
	 */
	public String getAnnotationQuerySelLookup(Class clazz) throws PlcException {

		NamedQuery nq = getNamedQueryByName(clazz,ANNOTATION.SUFFIX_QUERYSELLOOKUP_DEFAULT);
		
		if (nq == null)
			return null;
		else
			return nq.query();
	}
	
	/**
	 * Devolve se a classe tem anotação de Entity do hibernate com  selectBeforeUpdate=true
	 * @param clazz Classe a ser investigada
	 * @return true se tiver
	 */
	public boolean existsAnnotationSelectBeforeUpdate(Class<? extends Object> clazz) throws PlcException {
		
		Entity ent = clazz.getAnnotation(Entity.class);
		if (ent == null)
			return false;
		else {
			return ent.selectBeforeUpdate();
		}

	}
	
	
	
}
