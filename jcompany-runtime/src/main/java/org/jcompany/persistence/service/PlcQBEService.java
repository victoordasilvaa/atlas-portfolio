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
package org.jcompany.persistence.service;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.PlcConstantsCommons.ANNOTATION;
import org.jcompany.commons.annotation.PlcPrimaryKey;
import org.jcompany.commons.helper.PlcAnnotationHelper;
import org.jcompany.commons.helper.PlcStringHelper;
import org.jcompany.config.PlcConfigHelper;
import org.jcompany.config.commons.PlcConfigSuffixClass;
import org.jcompany.persistence.hibernate.helper.PlcAnnotationPersistenceHelper;


/**
 * Classe utilitária para lógicas de persistência para geração dinâmica de HQLs<p>
 * @since jCompany 3.5
 * @version $Id: PlcQBEService.java,v 1.10 2006/07/21 16:34:13 pedro_neves Exp $
 */
public abstract class PlcQBEService {

	protected static Logger log 			= Logger.getLogger(PlcQBEService.class);
	private static final DecimalFormat df 	= (DecimalFormat)DecimalFormat.getNumberInstance(new Locale("pt","BR"));
	static {df.setMinimumFractionDigits(2);}
	/**
	 * Monta a cláusula where usando o mecanismo de binding
	 * @since jCompany 2.5.3
	 * @param typeObjectPersistence Tipo do Value Object com packapge
	 * @param argEntity Value Object de argumentos para lógica QBE do jCompany
	 * @param argValues Coleção de Argumentos informados pelos usuários (valores)
	 * @param argTypes Coleção de Tipos Hibernate dos Argumentos informados pelos usuários.
	 * @return Retorna a cláusula where preparada para envio.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public StringBuffer mountWhereClause(String typeObjectPersistence, List argEntity, List argValues, List argTypes) throws PlcException {
	
	
		log.debug("############### Entered in mountWhereClause");
		
		try {
	
			StringBuffer whereSel = new StringBuffer();
	
	
			// Se informou argumentos de recuperação, acrescentar na cláusula where
			if (argEntity != null && argEntity.size() > 0 ) {
	
				log.debug("mountWhereClause: arg list size = " + argEntity.size());
	
				PlcArgEntity entityArg = null;
	
				// Informações do objeto de persistência
				Class entityClass = Class.forName(typeObjectPersistence);
				BeanInfo info = Introspector.getBeanInfo(entityClass);
				PropertyDescriptor[] pd = info.getPropertyDescriptors();
	
				PlcPrimaryKey pk = (PlcPrimaryKey) entityClass.getAnnotation(PlcPrimaryKey.class);
				List propList = new ArrayList ();
				if (pk != null){
					String[] properties = pk.properties();
					for (String prop : properties) {
						propList.add(prop);
					}
				}
				
				/**
				 * JCompany: Para cada argumento informado, procura o atributo de mesmo nome
				 * no objeto de persistência. Encontrando, verifica o type para que
				 * a cláusula where seja montada corretamente.
				 */
				
				for (int i = 0; i < argEntity.size(); i++ ) {
					
					entityArg = (PlcArgEntity) argEntity.get(i);
					
					if (entityArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ARGUMENT)) {
						
						if (propList.contains(entityArg.getName())){
							String nome = entityArg.getName();
							if (!nome.contains("idNatural_"))
								entityArg.setName("idNatural_" + nome);
						}
						
						String nameAux 	= verifyName(entityArg.getName());  // Troca "_" por "."
						String nameRoot = nameAux;
						if (nameAux.indexOf(".")> 0 )
							nameRoot = nameAux.substring(0,nameAux.indexOf("."));
						
						whereSel = alterWhereClauseToProperty(whereSel,pd,entityArg,nameRoot,nameAux,argValues,argTypes);
						
					}
				}
			}
	
	
	
			return( whereSel );
	
		}catch( Exception ex ){
			throw new PlcException("jcompany.errors.persistence.mount.where.clause",new Object[]{ex},ex,log);
		}
	
	}

	/**
	 * Troca um nome com sublinhado por pontos. Na declaração de clausulas
	 * para restrição de existência (duplicidade) e outras, no caso de propriedades
	 * de argumentos serem de classes agregadas, o usuário deve informar o separador
	 * entre propriedades como sublinhados ou invés de ponto, para não ferir a lógica
	 * automática do jCompany. Este método reconverte para a notação de ponto para envio
	 * ao SGBD.<p>
	 *  Exemplo: Declaração no struts-config.xml:
	 *
	 *      obj.idAgregado.nome=:idAgregado_nome
	 *
	 * @since jCompany 1.5
	 * @param name String a ser inspecionada, com sublinhado
	 * @return String alterada, com pontos
	 */
	public String verifyName(String name) {

		log.debug("############### Entered in verifyName");

		if (name.indexOf("_") > 0) {

			PlcStringHelper plUtil = PlcStringHelper.getInstance();;

			name = plUtil.changePart(name,"_",".");

			if (log.isDebugEnabled())
            	log.debug(" name with dots = " + name);

			return name;

		} else
			return name;

	}
	
	/**
	 * Adiciona  na cláusula where argumentos para a propriedade nomeAux
	 * É recursivo nos casos onde a propriedade do arguemnto é um atributo de uma classe agregada.
	 * @since jCompany 3.0
	 * @param argVO Value Object de argumentos para lógica QBE do jCompany
	 * @param argValues Coleção de Argumentos informados pelos usuários (valores)
	 * @param argTypes Coleção de Tipos Hibernate dos Argumentos informados pelos usuários.
	 * @param nameRoot Nome classe agregada que contém a propriedade que vai ser adicionada na cláusula where.  
	 * @param nameAux Nome classe agregada "." a propriedade que vai ser adicionada na cláusula where.
	 * @param pd PropertyDescriptor da classe agregada "nomeRaiz"
	 * @return Retorna a cláusula where preparada para envio.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public StringBuffer alterWhereClauseToProperty(StringBuffer whereSel, PropertyDescriptor[] pd, PlcArgEntity entityArg, 
				String nameRoot, String nameAux, List argValues, List argTypes) throws PlcException {
		
		try{	
			if (pd == null) 
				return new StringBuffer();
			
			else{
				
				for (int k = 0; k < pd.length; k++ ){
					
					Class classTypoArgument		= null;
					Class[] typesParamConstructor	= new Class[1];
					Object[] valuesParamConstructor 	= new Object[1];
					Object objValueArgument		= null;
					String typeToHibernate 		= null;

					//Verificando se a propriedade corrente é a propriedade do argumento 
					if (nameRoot.equals(pd[k].getName())) {
						
						String valArgument = null;
						
						//java.lang.String or Enum
						// Até a versao 3.2, somente utiliza enum por default como String
						//java.lang.String
						if ((( pd[k].getPropertyType().getName().indexOf("java.lang.String") >= 0 ||
								entityArg.getOperator().indexOf("*")>-1)) && (! PlcBaseEntity.class.isAssignableFrom(pd[k].getPropertyType()) &&
								!Enum.class.isAssignableFrom(pd[k].getPropertyType()))) {
							
							if (!whereSel.toString().equals("")) whereSel.append(" and ");
	
	    					//Alteração 03/10/2006 - by Rodrigo Magno
	    					//Utilizar operador auxiliar orIsNul
							// Testa inicio do orIsNull
							if (entityArg.getOrIsNull() != null && entityArg.getOrIsNull().equals("S"))
								whereSel.append("(");
	
							if ("N".equals(entityArg.getCaseSensitive())&& (entityArg.getOperator().equals("*%") || entityArg.getOperator().equals("%*%"))){
								if (!nameAux.equals(nameRoot) && !entityArg.getAlias().startsWith("obj"))
									nameAux = nameAux.substring(nameRoot.length()+1, nameAux.length());
								whereSel.append(" upper(" + entityArg.getAlias() + "." + nameAux + ")");
							}	
							else
								whereSel.append( entityArg.getAlias() + "." + nameAux );
							
							if (entityArg.getOperator() != null) {
								// Operator especificado pelo desenvolvedor
								if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_BEGIN)) {
									whereSel.append(" like ? ");
									valArgument = "%" + entityArg.getValue().toUpperCase();
								} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_TOTAL)) {
									whereSel.append(" like ? ");
									valArgument = "%" + entityArg.getValue().toUpperCase() + "%";
								} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_FINAL)) {
									whereSel.append(" like ? ");
									valArgument = entityArg.getValue().toUpperCase() + "%";
								} else {
									whereSel.append(" " + entityArg.getOperator() + " ? ");
									if ("N".equals(entityArg.getCaseSensitive())&& (entityArg.getOperator().equals("*%") || entityArg.getOperator().equals("%*%")))
										valArgument = entityArg.getValue().toUpperCase();
									else
										valArgument = entityArg.getValue();
								}
							} else {
								// Operator default
								whereSel.append(" like ? ");
								valArgument = entityArg.getValue().toUpperCase() + "%";
							}
							
							typeToHibernate = "STRING";
							
							classTypoArgument		= String.class;
							typesParamConstructor[0]	= Class.forName("java.lang.String");
							valuesParamConstructor[0]= valArgument;
							objValueArgument		= classTypoArgument.getConstructor(typesParamConstructor).newInstance(valuesParamConstructor);
						} 
						
						// O Argumento NÃO É do Tipo String
						else {
							
							log.debug("Not String");
							
							if ( (! PlcBaseEntity.class.isAssignableFrom(pd[k].getPropertyType())) ||(nameAux.indexOf(".")== -1 )) {
								if (!whereSel.toString().equals("")) whereSel.append(" and ");
								
		    					//Alteração 03/10/2006 - by Rodrigo Magno
		    					//Utilizar operador auxiliar orIsNul
	    						// Testa inicio do orIsNull
	    						if (entityArg.getOrIsNull() != null && entityArg.getOrIsNull().equals("S"))
	    							whereSel.append("(");
	
	    						whereSel.append(" " + entityArg.getAlias() + "." + nameAux );
								
								// Adicionando operador para o Argumento	
								if (entityArg.getOperator() != null) 
									whereSel.append(" " + entityArg.getOperator() + " ? "); // Operator especificado pelo desenvolvedor
								else 
									whereSel.append(" = ? "); // Operator default
							}
							
							valArgument = entityArg.getValue();
							
							log.debug("alterWhereClauseToProperty: pd - type[" + k + "] = " + pd[k].getPropertyType().getName());
							
							//java.util.Date
							if ( pd[k].getPropertyType().getName().indexOf("java.util.Date") >= 0 ) {
								
								String novoValorArgumento = valArgument;
								// Tratamento para quando utilizado mascara
								if (novoValorArgumento != null && novoValorArgumento.length() == 7)
									novoValorArgumento = "01/".concat(novoValorArgumento);
								else if (novoValorArgumento != null && novoValorArgumento.length() == 5)
									novoValorArgumento = "01/".concat(novoValorArgumento.substring(0,2)).concat("20").concat(novoValorArgumento.substring(3,2));
								
								String valueDate = null;
								
								if (entityArg.getOperator() != null && (entityArg.getOperator().equals("<=") || entityArg.getOperator().equals("<"))) 
									valueDate = novoValorArgumento.substring(0,10) + " 23:59:59";
								else 
									valueDate = novoValorArgumento + " 00:00:00";
								
								log.debug("alterWhereClauseToProperty: valueDate = " + valueDate);
								typeToHibernate 			= "DATE";
								SimpleDateFormat formatter 	= new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss");
								objValueArgument		 	= formatter.parse(valueDate);
								
							} 
							
							else {
								//BigDecimal
								if (BigDecimal.class.isAssignableFrom(pd[k].getPropertyType())){
									typeToHibernate 		= "BIG_DECIMAL";
									classTypoArgument 	= BigDecimal.class;
									typesParamConstructor[0]	= Class.forName("java.lang.String");
    								valArgument 			= valArgument.replace(".", "").replace(",", ".");
    								valuesParamConstructor[0]= valArgument;
									objValueArgument		= classTypoArgument.getConstructor(typesParamConstructor).newInstance(valuesParamConstructor);
									
								}	
								//Enum
								if (Enum.class.isAssignableFrom(pd[k].getPropertyType())){
									typeToHibernate 		= "CLASS";
									Class s 				=  pd[k].getPropertyType();
									objValueArgument		= Enum.valueOf(s, valArgument);
								}								
								else
									if (Integer.class.isAssignableFrom(pd[k].getPropertyType()) || Long.class.isAssignableFrom(pd[k].getPropertyType())){
										typeToHibernate 		= "LONG";
										classTypoArgument 	= Long.class;
										typesParamConstructor[0]	= Class.forName("java.lang.String");
										valuesParamConstructor[0]= valArgument;
										objValueArgument		= classTypoArgument.getConstructor(typesParamConstructor).newInstance(valuesParamConstructor);
									}
									else
										if (Boolean.class.isAssignableFrom(pd[k].getPropertyType()) || boolean.class.isAssignableFrom(pd[k].getPropertyType())){
											typeToHibernate 		= "BOOLEAN";
											classTypoArgument 	= Boolean.class;
											typesParamConstructor[0]	= Class.forName("java.lang.String");
											valuesParamConstructor[0]= valArgument;
											objValueArgument		= classTypoArgument.getConstructor(typesParamConstructor).newInstance(valuesParamConstructor);
										}								
										else {
											// AGREGADO
											if (PlcBaseEntity.class.isAssignableFrom(pd[k].getPropertyType())) {

												if (nameAux.indexOf(".")== -1 ){ 
													/* ou seja o Argumento é uma classe agregada direta e não um campo de uma agregada,
													 * será pelo ID, um Long Ex: candidato.areaInteresse
													 */
														typeToHibernate 		= "LONG";
														classTypoArgument 	= Long.class;
														typesParamConstructor[0]	= Class.forName("java.lang.String");
														valuesParamConstructor[0]= valArgument;
														objValueArgument		= classTypoArgument.getConstructor(typesParamConstructor).newInstance(valuesParamConstructor);

												}
												else{
													/*
													 * Recuperando a classe agregada para chamar o método novamente
													 */
													String entityAliasOriginal = entityArg.getAlias();
													
													Class entityClass = Class.forName(pd[k].getPropertyType().getName());
													
													PlcConfigSuffixClass suffixClass = PlcConfigHelper.getInstance().get(PlcConfigSuffixClass.class);
													String sufixo = null;
													
													if (suffixClass != null){
														sufixo = suffixClass.entitySuffix();
													} else {
														sufixo = "ENTITY";
													}
													
													PlcPrimaryKey pk = null;
													if (!pd[k].getPropertyType().getName().endsWith(sufixo)){
														try{
															Class entidadeClassPk = Class.forName(pd[k].getPropertyType().getName() + sufixo);
															pk = (PlcPrimaryKey)entidadeClassPk.getAnnotation(PlcPrimaryKey.class);
														}catch (Exception e) {
															pk = null;
														}
													} else {
														pk = (PlcPrimaryKey)entityClass.getAnnotation(PlcPrimaryKey.class);
													}
													
													BeanInfo info = Introspector.getBeanInfo(entityClass);
													PropertyDescriptor[] pdAggregate = info.getPropertyDescriptors();

													if (nameAux.indexOf(".") > -1)
														entityArg.setAlias(entityArg.getAlias().concat(".").concat(nameRoot));
													else
														entityArg.setAlias(entityArg.getAlias().concat(".").concat(nameAux));

													nameAux		= nameAux.substring(nameAux.indexOf(".")+1);
													if (nameAux.indexOf(".")> 0 )
														nameRoot = nameAux.substring(0,nameAux.lastIndexOf("."));
													else
														nameRoot = nameAux;
													
													// Para quando possui chave natural
													if (pk != null){
														nameRoot = "idNatural";
														if (nameAux != null && !nameAux.contains("idNatural"))
															nameAux = "idNatural." + nameAux;
													}
													// Quando possui chave natural, nomeRaiz = "idNatural" e nomeAux = "idNatural.propriedadeDoId"
													whereSel = alterWhereClauseToProperty(whereSel,pdAggregate,entityArg,nameRoot,nameAux,argValues,argTypes);
													entityArg.setAlias(entityAliasOriginal);
													/*
													 * Quando for uma campo da propriedade agregada não pode executar o restante do código, pois já foi executado quando este método é chamada em recursividade
													 * Ex: candidato.cidade.estado.nome, o método abaixo vai ser executado somente para o nome do candidato. 
													 */
													break;
												}
											} 
										}

							}


						}

						//Alteração 03/10/2006 - by Rodrigo Magno
						//Utilizar operador auxiliar orIsNul
						if (entityArg.getOrIsNull() != null && entityArg.getOrIsNull().equals("S")){
							whereSel.append(" or "+entityArg.getAlias()+"."+nameAux+ " is null ) ");
						}

						/*
						 * Adiciona o objeto de valor na lista de Valores de argumentos
						 */
						argValues.add(objValueArgument);
						
						if (log.isDebugEnabled()) {
							log.debug("alterWhereClauseToProperty: type = " 	+ pd[k].getPropertyType().getName());
							log.debug("alterWhereClauseToProperty: typeName = " + typeToHibernate);
						}
						
						Hibernate hibernate 	= null;
						Class classe			= Class.forName("org.hibernate.Hibernate");
						Field field 			= classe.getField(typeToHibernate);
						
						
						// Atenção: Para verificação futura - uso do TIMESTAMP
						if (typeToHibernate.equals("DATE"))
							argTypes.add(Hibernate.TIMESTAMP);
						else
							if (typeToHibernate.equals("CLASS"))
								argTypes.add(Hibernate.CLASS);
						
						else{
							NullableType nullableType = (NullableType) field.get(hibernate);
							argTypes.add(nullableType);
						}
						
	    				/*
	    				 * A Propriedade desejada já foi encontratada então não há a necessidade de percorrer tod propertyDescriptor
	    				 */
	    				break;
					}
					
	
				}
			}
			return whereSel;
			
			
		}catch( Exception ex ){
			throw new PlcException("jcompany.errors.persistence.mount.where.clause",new Object[]{ex},ex,log);
		}
	}

	/**
	 * Monta a cláusula where de forma mais simples e eficiente para lógicas
	 * de Query By Example automáticas, por receber o type de cada argumento no ENTITY de argumentos.
	 * @since jCompany 1.5
	 * @param typeObjectPersistence Tipo do Value Object com packapge
	 * @param argEntity Value Object de argumentos para lógica QBE do jCompany
	 * @param objFind Coleção de Argumentos informados pelos usuários (valores)
	 * @param typeFind Coleção de Tipos Hibernate dos Argumentos informados pelos usuários.
	 * @return Retorna a cláusula where preparada para envio.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	@SuppressWarnings("unchecked")
	public StringBuffer mountWhereClauseQBE(String typeObjectPersistence, List argEntity, List objFind, List typeFind) throws PlcException {
	
	log.debug("############### Entered in mountWhereClauseQBE");
	
	try {
	
		StringBuffer whereSel = new StringBuffer();
	
		if (log.isDebugEnabled())
			log.debug("mountWhereClauseQBE: arg list size = " + argEntity.size());
	
		PlcArgEntity entityArg = null;
	
		for (int i = 0; i < argEntity.size(); i++ ) {
	
			entityArg = (PlcArgEntity) argEntity.get(i);
	
			if (entityArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ARGUMENT)) {
	
				// Trata atributos do type String
				// Quando string, faz consulta case insensitive
				String nameAux = verifyName(entityArg.getName());
				/*String nomeRaiz = nomeAux;
				if (nomeAux.indexOf(".") > 0)
					nomeRaiz = nomeAux.substring(0,nomeAux.indexOf("."));*/
	
				String value = null;
	
				if (!whereSel.toString().equals("")) whereSel.append(" and ");
	
				// Testa inicio do orIsNull
				if (entityArg.getOrIsNull() != null && entityArg.getOrIsNull().equals("S"))
					whereSel.append("(");
	
				// Monta prefixo da cláusula Where para Strings
				if (entityArg.getFormat().toLowerCase().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_STRING)) {
	
					whereSel.append(" upper(" + entityArg.getAlias() + "." + nameAux + ")");
	
					if (entityArg.getOperator() != null) {
						// Operator especificado pelo desenvolvedor
						if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_BEGIN)) {
							whereSel.append(" like ? ");
							value = "%" + entityArg.getValue().toUpperCase();
						} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_TOTAL)) {
							whereSel.append(" like ? ");
							value = "%" + entityArg.getValue().toUpperCase() + "%";
						} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_FINAL)) {
							whereSel.append(" like ? ");
							value = entityArg.getValue().toUpperCase() + "%";
						} else {
							whereSel.append(" " + entityArg.getOperator() + " ? ");
							value = entityArg.getValue().toUpperCase();
						}
					} else {
						// Operator default
						whereSel.append(" like ? ");
						value = entityArg.getValue().toUpperCase() + "%";
					}
	
				} else {
					// Monta prefixo da cláusula where para outros
					log.debug("Not String");
	
					whereSel.append(" " + entityArg.getAlias() + "." + nameAux);
	
					if (entityArg.getOperator() != null)
						whereSel.append(" " + entityArg.getOperator() + " ? ");
					else
						whereSel.append(" = ? ");
	
					value = entityArg.getValue();
				}
	
				Object obj = null;
	
				if (entityArg.getFormat().toLowerCase().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_DATE)) {
					String valueDate = null;
					if (entityArg.getOperator() != null &&
						(entityArg.getOperator().equals("<=") ||
						 entityArg.getOperator().equals("<")))
						valueDate = value.substring(0,10) + " 23:59:59";
					else
						valueDate = value + " 00:00:00";
	
					if (log.isDebugEnabled())
						log.debug("mountWhereClauseQBE: valorData = " + valueDate);
	
					SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss");
					obj = formatter.parse(valueDate);
	
				} else if (entityArg.getFormat().toLowerCase().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_FORMAT_LONG))
					obj = new Long(value);
				else
					obj = value+"";
	
				objFind.add(obj);
	
				if (log.isDebugEnabled())
					log.debug("Testing type "+entityArg.getFormat().toLowerCase()+" for "+
						" value "+ obj);
	
				Hibernate hibernate = null;
				Class c = Class.forName("org.hibernate.Hibernate");
	
				String typeName = entityArg.getFormat().toUpperCase();
				Field field = c.getField(typeName);
				NullableType nullableType = (NullableType) field.get(hibernate);
	
				if (typeName.equals("DATE"))
					typeFind.add(Hibernate.TIMESTAMP);
				else
					typeFind.add(nullableType);
	
				if (entityArg.getOrIsNull() != null && entityArg.getOrIsNull().equals("S")){
	
					whereSel.append(" or "+entityArg.getAlias()+"."+nameAux+ " is null ) ");
				}
	
			}
		}
	
	if (log.isDebugEnabled())
		log.debug("Where clause="+whereSel);
	
	return( whereSel );
	
	} catch( Exception ex ){
		throw new PlcException("jcompany.errors.persistence.mount.where.clause",new Object[]{ex},ex,log);
	}
	
	}

	/**
	 * jCompany 1.5: Monta a cláusula order by
	 *
	 * @param argEntity Coleção de Value Objects do type PlcArgEntity, contendo critérios de ordenação
	  * interpretados da declaração do Action no arquivo struts-config.xml.
	 * @return Retorna a cláusula where preparada para envio.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public StringBuffer mountOrderByClause(List<PlcArgEntity> argEntity) throws PlcException {
		
		log.debug("############### Entered in mountOrderByClause");
	
		try {
	
			StringBuffer orderBySel = new StringBuffer();
	
				if (argEntity != null && argEntity.size() > 0 ) {
	
					if (log.isDebugEnabled()) log.debug("mountOrderByClause: arg list size = " + argEntity.size());
	
					PlcArgEntity entityArg = null;
	
					for (int i = 0; i < argEntity.size(); i++ ) {
	
						entityArg = (PlcArgEntity) argEntity.get(i);
	
						if (entityArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ORDER_BY)) {
	
							if (!orderBySel.toString().equals("")) orderBySel.append(" , ");
	
			                if (entityArg.getName().indexOf(".") == -1)
								orderBySel.append(" " + entityArg.getAlias() + "." + entityArg.getName());
							else
								orderBySel.append(" " + entityArg.getName());
						}
					}
				}
	
			return( orderBySel );
	
		}catch( Exception ex ){
			throw new PlcException("jcompany.errors.persistence.mount.orderby.clause",new Object[]{ex},ex,log);
		}
	}

	/**
	 * jCompany 3.0: Recebe um nome de objeto e vetor de argumentos e devolve array com valores de argumentos.
	 * @param typeObjectPersistence Tipo do Value Object com package
	 * @param argEntity Coleção de VOs do type PlcArgEntity, contendo informações para QBE
	 *
	 * @return Retorna um vetor de Object contendo StringBuffer-Clausula Where, ArrayList-Lista
	 * de argumentos informados, ArrayList-Lista de Tipos Hibernate.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public Object[] mountArgsQBE(String typeObjectPersistence, List<PlcArgEntity> argEntity) throws PlcException {
	
		log.debug("############### Entered in mountArgsQBE");
		
		List objFind = null;
		List typeFind = null;
		Type[] typeFinal = null;
	
		try {
	
				// Monta a cláusula where
				objFind = new ArrayList();
				typeFind = new ArrayList();
	
				StringBuffer whereSel = null;
	
				// Comunta entre lógica otimizada e original por enquanto. Após homologar
				// nova lógica retirar a primeira
				boolean callOptimized = false;
	
				if (argEntity.size()>0) {
					PlcArgEntity voArg = (PlcArgEntity) argEntity.get(0);
					callOptimized = voArg.getFormat() != null &&
								voArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ARGUMENT);
					log.debug("argEntity not equals zero!");
				} else
					callOptimized = true;
	
				if (callOptimized) {
					log.debug("Calling optimized");
					whereSel = mountWhereClauseQBE (typeObjectPersistence, argEntity,
															objFind, typeFind);
				} else {
					log.debug("Calling conventional");
					 whereSel = mountWhereClause (typeObjectPersistence, argEntity,
															objFind, typeFind);
				}
	
				if (objFind != null && objFind.size() > 0 &&
					typeFind != null && typeFind.size() > 0) {
					log.debug("Mounting types array");
	
					typeFinal = new Type[typeFind.size()];
	
					for (int i = 0; i < typeFinal.length; i++ ) {
						typeFinal[i] = (Type) typeFind.get(i);
					}
				}
	
				log.debug("Finished mount where");
	
	
				if (whereSel == null)
					whereSel = new StringBuffer("");
				else if (whereSel.toString().indexOf("INI")>-1 || whereSel.toString().indexOf("FIM")>-1 ){
					// Retira tokens de INI e FIM para o caso de datas, por exemplo
					String aux = whereSel.toString();
					aux = aux.replaceAll("INI","");
					aux = aux.replaceAll("FIM","");
					whereSel = new StringBuffer(aux);
				}
				return new Object[] {whereSel,objFind,typeFinal};
	
		}catch( Exception ex ){
			throw new PlcException("jcompany.errors.persistence.mountquery",new Object[] {ex},ex,log);
	
		}
	
	}

	/**
	 * jCompany 3.0: Recebe um nome de objeto e vetor de argumentos e devolve array com valores de argumentos.
	 * @param argEntity Coleção de VOs do type PlcArgEntity, contendo informações para QBE
	 *
	 * @return Object[] e Type[] contendo respectivamente valores e tipos dos argumentos para HQL
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public Object[] mountArrayFromCollection(List<PlcArgEntity> argEntity) throws PlcException {
	
		log.debug("############### Entered in mountArrayFromCollection");
		
		List lValues = new ArrayList();
		List lTypes = new ArrayList();
		
		for (Iterator iter = argEntity.iterator(); iter.hasNext();) {
			
			PlcArgEntity arg = (PlcArgEntity) iter.next();
			
			if (arg.getValue()!=null) {
				
				// TODO Fazer para todos os tipos
				lValues.add(arg.getValue());
				
				if (arg.getValue().getClass().equals(Long.class))
					lTypes.add(PlcConstantsCommons.TYPES.LONG);
				else if (arg.getValue().getClass().equals(String.class))
					lTypes.add(PlcConstantsCommons.TYPES.STRING);
				else if (arg.getValue().getClass().equals(Date.class))
					lTypes.add(PlcConstantsCommons.TYPES.DATE);
				
			}
			
		}
		String[] lTypeS = new String[lTypes.size()];
		int cont=0;
		for (Iterator iter = lTypes.iterator(); iter.hasNext();) {
			String type = (String) iter.next();
			lTypeS[cont] = type;
			cont++;
		}
		
		return new Object[]{(Object[])lValues.toArray(),lTypeS};
	}

	/**
	 * Monta cláusula FROM
	 * @since jCompany 1.5
	 * @param aliasObj String a ser utilizada como alias. Default "obj".
	 * @param typeObjectPersistence Tipo do Value Object com package
	 *
	 * @return Retorna uma String contendo a cláusula from de um HQL.
	 *
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public StringBuffer mountSelectFrom(String aliasObj, String typeObjectPersistence) throws PlcException {
	
		Logger log = Logger.getLogger(this.getClass());
	
		StringBuffer sqlSel;
	
		try {
	
				sqlSel = new StringBuffer("from " );
	
				sqlSel.append(aliasObj);
				sqlSel.append(" in class ");
				sqlSel.append(typeObjectPersistence);
	
				if (log.isDebugEnabled()) log.debug("sqlSel (1) = " + sqlSel.toString());
	
				return sqlSel;
	
		}catch( Exception ex ){
	
			log.error("Error mounting select , from clauses"+ex);
			throw new PlcException("jcompany.errors.persistence.mountquery",new Object[] {ex},ex,log);
	
		}
	
	}

	/**
	 * Inclui a cláusula where na select passada
	 * @since jCompany 2.5
	 * @param hqlBase HQL base
	 * @param whereSel Where a ser incluida, sem incluir o 'where' em si
	 * @return HQL com where incluida
	 */
	public String composeSelectWhere(String hqlBase, String whereSel) throws PlcException {
		
		log.debug("############### Entered in composeSelectWhere");
		int insertionPoint = 0;
		int wherePoint = hqlBase.indexOf("where");
		int orderByPoint = hqlBase.indexOf("order by");
		int groupByPoint = hqlBase.indexOf("group by");
		
		if (wherePoint>-1)
			insertionPoint = wherePoint+6;
		if (wherePoint==-1 && orderByPoint>-1)
			insertionPoint = orderByPoint-1;
		if (wherePoint==-1 && groupByPoint>-1)
			insertionPoint = groupByPoint-1;
		
		if (insertionPoint==0)  // nao tem where
			return hqlBase+ " where "+whereSel.toString();
		else if (wherePoint>0) // já tem where e nao tem orderby e nem group by então insere
			return hqlBase.substring(0,insertionPoint)+ "("+whereSel.toString()+ ") and "+hqlBase.substring(insertionPoint);
		else if (wherePoint==-1 && orderByPoint>-1) // nao tem where mas tem orderby
			return hqlBase.substring(0,insertionPoint)+ " where "+whereSel.toString()+ " "+hqlBase.substring(insertionPoint);
		else if (wherePoint==-1 && groupByPoint>-1) // nao tem where mas tem groupby
			return hqlBase.substring(0,insertionPoint)+ " where "+whereSel.toString()+ " "+hqlBase.substring(insertionPoint);
		else 
			throw new PlcException("jcompany.errors.mount.hql.args",new Object[]{hqlBase,whereSel.toString()});
	}

	public String changeOrderBy(String hql, String orderByDynamic) throws PlcException {
		log.debug("############### Entered in changeOrderBy");
		int orderPosition = hql.toString().indexOf("order by");
		if (orderPosition==-1) {
			return hql+" order by "+orderByDynamic;
		} else {
			return hql.substring(0,orderPosition-1)+" order by "+orderByDynamic;
		}
	}

	/**
	 * jCompany 3.0 Recebe uma query com possíveis argumentos na forma ':arg' e os devolve.<p>Obs: considera um máximo de 15 argumentos.
	 * @param query query contendo argumentos. Ex: "from Class obj where obj.id=:idPai and obj.nome like ':nome%'"
	 * @return relação de nomes dos argumentos. Ex: {"idPai","nome"}
	 */
	public String[] distillHQLArguments(String query) throws PlcException {
		log.debug("############### Entered in distillHQLArguments");
	
		int posArg = -1;
		int posArgEnd = -1;
		int counter=0;
		String[] argsNames = new String[]{null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
		
		do {
			
			posArg = query.indexOf(":",posArg+1);
			
			if (posArg != -1) {
				// Separa nome arg
				posArgEnd = query.indexOf(" ",posArg);
				if (posArgEnd == -1)
					posArgEnd = query.length();
				argsNames[counter]=query.substring(posArg+1,posArgEnd);
				if (argsNames[counter].endsWith(")"))
					argsNames[counter]=argsNames[counter].substring(0,argsNames[counter].length()-1);
				counter++;
			}
			
			
		} while (posArg != -1);
	
		return argsNames;
	}

	/**
	 * jCompany 3.0 Troca todas as ocorrencias dos argumentos no padrao ':nome' para ' is null'
	 * TODO jCompany 3.1 Revisao de parser.
	 * @param query query contendo argumentos. Ex: 'from MinhaClasse obj where obj.idPai=:id and obj.nome=:nome'
	 * @param nullArgsNames Ex: {nome}
	 * @return query contendo argumentos substituidos. Exemplo: 'from MinhaClasse obj where obj.idPai=:id and obj.nome is null'
	 */
	public String changeWhereClauseToNulls(String query, String[] nullArgsNames) throws PlcException {
		
		log.debug("############### Entered in changeWhereClauseToNulls");
	
		for (int i = 0; i < nullArgsNames.length; i++) {
			String nullArgsName = nullArgsNames[i];
			if (nullArgsName != null) {
				query = query.replaceAll(">=:"+nullArgsName," is null");
				query = query.replaceAll(">= :"+nullArgsName," is null");
				query = query.replaceAll("<=:"+nullArgsName," is null");
				query = query.replaceAll("<= :"+nullArgsName," is null");
				query = query.replaceAll("<>:"+nullArgsName," is null");
				query = query.replaceAll("<> :"+nullArgsName," is null");
				query = query.replaceAll("<:"+nullArgsName," is null");
				query = query.replaceAll("< :"+nullArgsName," is null");
				query = query.replaceAll(">:"+nullArgsName," is null");
				query = query.replaceAll("> :"+nullArgsName," is null");
				query = query.replaceAll("=:"+nullArgsName," is null");
				query = query.replaceAll("= :"+nullArgsName," is null");
			}
		}
		
		return query;
	
	}

	/**
	 * @since jCompany 5.0
	 * 
	 * Recebe um HQL e inclui ou extende a cláusula where dinamicamente, em função dos argumentos recebidos
	 * @param hqlBase HQL a ser modificado
	 * @param classe Classe principal 
	 * @param argsQBE Lista com VOs PlcArgEntity que contém argumentos informados na camada Visão
	 */
	public String hqlArgsQBE( String hqlBase, Class classe,List<PlcArgEntity> argsQBE,List argValues, List argTypes) throws PlcException {
			
		try {
			
			if (argsQBE == null || argsQBE.size()==0)
				return hqlBase;
			
			StringBuffer whereSel = mountWhereClause(classe.getName(),argsQBE,argValues,argTypes);
		
			if ( (whereSel != null) && (!whereSel.toString().equals("")))
				return composeSelectWhere(hqlBase,whereSel.toString());
			else
				return hqlBase;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"hqlArgsQBE", e }, e, log);
		}
		
	}
	
	/**	
	 * Devolve a queryCount se esta existir na Anotação.
	 *  
	 * @param classe de endidade para procurar a named query
	 * 
	 * @return a query ulitizada para o select count
	 * @throws PlcException
	 */
	public String getQueryCount(Class clazz) throws PlcException {
		
		NamedQuery nq = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz,"queryCount");
		if(nq!=null)
			return nq.query();
		return null;
	}
	
	/**
	 * @since jCompany 5.0
	 * 
	 * Devolve query padrão, preferencialmente de anotações. Pode ser sobreposto nos descendentes
	 * para montagem específica de HQLs
	 * @param clazz Classe principal
	 * @return Annotation contendo query padrão para QBE ou um select "from [Classe] obj"
	 */
	public String getQuerySelDefault(Class clazz, PlcBaseContextVO context) throws PlcException {
			
		String query = null;
		String complementoExclusaoLogica = "";
		String complementoExclusaoLogicaSemWhere = "";
		if (context.getExcludeModeAux().equals("L")) {
			complementoExclusaoLogica = " where sitHistoricoPlc='A' ";
			complementoExclusaoLogicaSemWhere = " and sitHistoricoPlc='A' ";
		}
		
		if (context != null) {
			
			// lógicas de manutenção tabulares ou crud-tabulares usam queryMan se existir.
			if (context.getApiQuerySel() != null)
				query = context.getApiQuerySel();
			else if (PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TABULAR.equals(context.getLogic()) ||
					PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_CRUD_TABULAR.equals(context.getLogic()))
				query = "queryMan";
			else if (PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_TREEVIEW.equals(context.getLogic()))
				query = "queryTreeView";
		
		}
		
		NamedQuery nq = null;
		
		if (query != null) {
			
			nq = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz,query);
			
			// Se nao há NamedQuery especifica para treeview, procura a de seleção padrão como alternativa
			if (nq == null && query.equals("queryTreeView"))
				nq = PlcAnnotationPersistenceHelper.getInstance().getNamedQueryByName(clazz,"querySel");
			
			if (nq == null) {
			
				if (query.equals("queryMan") || query.equals("querySel") || query.equals("queryTreeView")) {
					String nameClassWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
					return "from "+nameClassWithoutPackage+" obj" + complementoExclusaoLogica;
				} else					
				  throw new PlcException("jcompany.namedquery.not.found",new Object[]{clazz.getName(),
						clazz.getName().substring(clazz.getName().lastIndexOf(".")+1)+"."+query});
			}
		} else {
			
			String apiQuerySel = context.getApiQuerySel();
			
			if (StringUtils.isEmpty(apiQuerySel))
				apiQuerySel = ANNOTATION.SUFFIX_QUERYSEL_DEFAULT;
			
			Annotation a = PlcAnnotationHelper.getInstance().getAnnotationQueryQbeOrSelDefault(clazz, apiQuerySel);
			   	
	    	if (a != null && NamedQueries.class.isAssignableFrom(a.getClass())){
				/* jCompany 3.0 
				 * Se for lógica de Relatório tem que buscar a anotação querySelRel "NamedQuery"
				 * @autor - Pedro Henrique - 22/03/2006
				 */
	    		if (context != null && context.getLogic() != null &&
	    				context.getLogic().startsWith(PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_REPORT))
		    		a = PlcAnnotationPersistenceHelper.getInstance().getNamedQuerySelReport((NamedQueries)a,context.getReportCurrentNamedQuery());
				else
		    		a = PlcAnnotationPersistenceHelper.getInstance().getNamedQuerySelDefault((NamedQueries)a);

	    	}
	
			if (a != null) {
				nq = (NamedQuery)a;
			} else if (context != null && PlcConstantsCommons.DEFAULT_LOGIC.DP.PATTERN_USER_PREF.equals(context.getLogic())){
				String nameClassWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
				return "from " + nameClassWithoutPackage + " obj where obj." + context.getArgPreference() + " = ? " + complementoExclusaoLogicaSemWhere; 
			}
			else {
				String nameClassWithoutPackage = clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
				return "from "+nameClassWithoutPackage+" obj" + complementoExclusaoLogica;
			}
		}
		
		return nq.query();		
	}
	
	/**
     * @since jCompany 5.0
     * 
     * Troca o orderBy definido na query padrão "querySel" anotada pelo informado pelo usuário
     * @param orderByDynamic cláusula OQL contendo o "order by"
     * @throws PlcException
     */
	public String hqlOrderByDynamic( String hql,
			String orderByDynamic,Class clazz) throws PlcException {
	
		if (orderByDynamic != null && !orderByDynamic.equals("")) {

			return changeOrderBy(hql,orderByDynamic);
			
		} else
			return hql;
		
	}
	
	/** 
	 * Verifica existência de filtros do jCompany no padrão da Hibernate 3.x. 
	 * @since jCompany 5.0
	 * @param clazz Classe corrente
	 */
	public boolean existDefaultFilters(Class clazz) throws PlcException {
	
		try {
			Annotation[] annotations = clazz.getDeclaredAnnotations();
			for (int i = 0; i < annotations.length; i++) {
				Annotation annotation = annotations[i];
				// Somente anotações de filtros da hibernate
				// Neste caso tem somente uma anotação
				if (Filter.class.isAssignableFrom(annotation.getClass()) ||
					Filters.class.isAssignableFrom(annotation.getClass()))
					return true;
			}
			
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"existDefaultFilters", e }, e, log);
		}
		return false;
	}
	
	/**
	 * 
	 * jCompany. Acrescenta a where condition com restrições específicas do usuario,
	 * montadas em AppPerfilUsuarioBO. Acha um ponto adequado para inserção e concatena.
	 * A cláusula, registrada em AppPerfilUsuarioBO, deve conter os parênteses necessários
	 * para que não haja problemas na lógica "booleana" da where.
	 *
	 * @param hqlOriginal HQL a ser alterado
	 * @param filter Cláusula registrada por AppPerfilUsuarioBO
	 * @return Retorna o HQL modificado, com o filtro.
	 *
	 * @throws PlcException Trata exceção geral e converte para PlcException
	 * @deprecated Utilizar aplicaFiltro, no padrão da 3.0, após converter filtros para Annotations.
	 */
	public StringBuffer applyVerticalSecurity(String hqlOriginal,String filter) throws PlcException {

		String hqlSecurity = hqlOriginal;

		try {

			if (filter != null && !filter.equals("")) {

				if (log.isDebugEnabled()) log.debug("hqlOriginal="+hqlOriginal);

				int iWhere = hqlOriginal.indexOf("where");

				if (iWhere == -1) {

					int iOrder = hqlOriginal.indexOf("order by");
					int iGroup = hqlOriginal.indexOf("group by");

					if (iOrder == -1 && iGroup == -1)
						hqlSecurity = hqlOriginal+" where "+filter;
					else {
						int iNext = 0;
						if (iOrder == -1) {
							iNext = iGroup ;
						} else if (iGroup == -1) {
							iNext = iOrder;
						} else if (iGroup < iOrder) {
							iNext = iGroup;
						} else iNext = iOrder;

						hqlSecurity = hqlOriginal.substring(0,iNext-1)+
								" where " + filter + " " +
							hqlOriginal.substring(iNext,hqlOriginal.length());

					}

				} else {

					hqlSecurity = hqlOriginal.substring(0,iWhere+5)+
								" " + filter + " and " +hqlOriginal.substring(iWhere+6,hqlOriginal.length());

				}

				if (log.isDebugEnabled()) log.debug("hql with vertical filter="+hqlSecurity);

			}

			return new StringBuffer(hqlSecurity);
	
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"applyVerticalSecurity", e }, e, log);
		}
	}
	
	/**
	 * Monta a parte FROM ... WHERE ..., considerando a Classe do objeto id
	 * para lidar com chaves naturais
	 * 
	 * @since jCompany 5.0
	 * 
	 * @param query sem a parte FROM ... WHERE ...
	 * @param clazz da entidade
	 * @param id identificador da instância
	 * 
	 * @throws PlcException
	 */
	public String mountFromWhere(String query, Class clazz, Object id) throws PlcException {
		
		if (query == null && ( id instanceof Long || id instanceof String) ) 
			query = "from "+clazz.getName()+" obj where obj.id=?";
		else if (query == null && id instanceof PlcBaseEntity) 
			query = "from "+clazz.getName()+" obj where obj.idNatural=?";
		else if (query == null)
			throw new PlcException("jcompany.errors.invalid.identifier", new Object[] {id});
		
		return query;
	}

}
