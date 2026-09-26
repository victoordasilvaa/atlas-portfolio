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
package org.jcompany.persistence.hibernate.service;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcStringHelper;


/**
 * Classe utilitária para lógicas de persistência para geração dinâmica de HQLs Hibernate<p>
 * @since jCompany 2.5.3
 * @version $Id: PlcQBEService.java,v 1.10 2006/07/21 16:34:13 pedro_neves Exp $
 */
public class PlcQBEService{
	
	private static Logger log = Logger.getLogger(PlcQBEService.class);

   /**
	 * Monta a cláusula where usando o mecanismo de binding
	 * @since jCompany 2.5.3
	 * @param persistenceObjectType Tipo do Value Object com packapge
	 * @param argEntity Value Object de argumentos para lógica QBE do jCompany
	 * @param argValues Coleção de Argumentos informados pelos usuários (valores)
	 * @param argTypes Coleção de Tipos Hibernate dos Argumentos informados pelos usuários.
	 * @return Retorna a cláusula where preparada para envio.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
    public StringBuffer mountWhereClause(String persistenceObjectType,
											  List argEntity,
											  List argValues,
											  List argTypes) throws PlcException {


		log.debug("############### Entered in mountWhereClause");
		
		try {

			StringBuffer whereSel = new StringBuffer();


			// Se informou argumentos de recuperação, acrescentar na cláusula where
			if (argEntity != null && argEntity.size() > 0 ) {

				log.debug("mountWhereClause: list arg size= " + argEntity.size());

				PlcArgEntity entityArg = null;

				// Informações do objeto de persistência
				Class entityClass = Class.forName(persistenceObjectType);
				BeanInfo info = Introspector.getBeanInfo(entityClass);
				PropertyDescriptor[] pd = info.getPropertyDescriptors();

				/**
				 * JCompany: Para cada argumento informado, procura o atributo de mesmo nome
				 * no objeto de persistência. Encontrando, verifica o type para que
				 * a cláusula where seja montada corretamente.
				 */
				
				for (int i = 0; i < argEntity.size(); i++ ) {
					
					entityArg = (PlcArgEntity) argEntity.get(i);
					
					if (entityArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ARGUMENT)) {
						
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
	 * Adiciona  na cláusula where argumentos para a propriedade nomeAux
	 * É recursivo nos casos onde a propriedade do arguemnto é um atributo de uma classe agregada.
	 * @since jCompany 3.0
	 * @param argVO Value Object de argumentos para lógica QBE do jCompany
	 * @param argValues Coleção de Argumentos informados pelos usuários (valores)
	 * @param argTypes Coleção de Tipos Hibernate dos Argumentos informados pelos usuários.
	 * @param rootName Nome classe agregada que contém a propriedade que vai ser adicionada na cláusula where.  
	 * @param nameAux Nome classe agregada "." a propriedade que vai ser adicionada na cláusula where.
	 * @param pd PropertyDescriptor da classe agregada "nomeRaiz"
	 * @return Retorna a cláusula where preparada para envio.
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
    public StringBuffer alterWhereClauseToProperty(StringBuffer whereSel, PropertyDescriptor[] pd ,PlcArgEntity entityArg,String rootName,
    		String nameAux, List argValues,  List argTypes )throws PlcException {
    	
    	
    	try{	
    		if (pd == null) 
    			return new StringBuffer();
    		
    		else{
    			
    			for (int k = 0; k < pd.length; k++ ){
    				
    				Class classArgumentType		= null;
    				Class[] typesParamConstructor	= new Class[1];
    				Object[] valuesParamConstrutor 	= new Object[1];
    				Object objValueArgument		= null;
    				String typeToHibernate 		= null;
    				
    				//Verificando se a propriedade corrente é a propriedade do argumento 
    				if (rootName.equals(pd[k].getName())) {
    					
    					String argumentValue = null;
    					
    					//java.lang.String or Enum
    					// Até a versao 3.2, somente utiliza enum por default como String
						//java.lang.String
    					if ((( pd[k].getPropertyType().getName().indexOf("java.lang.String") >= 0 ||
    							entityArg.getOperator().indexOf("*")>-1)) && (! PlcBaseEntity.class.isAssignableFrom(pd[k].getPropertyType())) ||
    							Enum.class.isAssignableFrom(pd[k].getPropertyType())) {
    						
    						if (!whereSel.toString().equals("")) whereSel.append(" and ");

        					//Alteração 03/10/2006 - by Rodrigo Magno
        					//Utilizar operador auxiliar orIsNul
    						// Testa inicio do orIsNull
    						if (entityArg.getOrIsNull() != null && entityArg.getOrIsNull().equals("S"))
    							whereSel.append("(");

							if ("N".equals(entityArg.getCaseSensitive())&& (entityArg.getOperator().equals("*%") || entityArg.getOperator().equals("%*%"))){
								if (!nameAux.equals(rootName) && !entityArg.getAlias().startsWith("obj")) 
									nameAux = nameAux.substring(rootName.length()+1, nameAux.length());
								whereSel.append(" upper(" + entityArg.getAlias() + "." + nameAux + ")");
							}
							else
    							whereSel.append( entityArg.getAlias() + "." + nameAux );
    						
    						if (entityArg.getOperator() != null) {
    							// Operator especificado pelo desenvolvedor
    							if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_BEGIN)) {
    								whereSel.append(" like ? ");
    								argumentValue = "%" + entityArg.getValue().toUpperCase();
    							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_TOTAL)) {
    								whereSel.append(" like ? ");
    								argumentValue = "%" + entityArg.getValue().toUpperCase() + "%";
    							} else if (entityArg.getOperator().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_LIKE_PERC_FINAL)) {
    								whereSel.append(" like ? ");
    								argumentValue = entityArg.getValue().toUpperCase() + "%";
    							} else {
    								whereSel.append(" " + entityArg.getOperator() + " ? ");
    								if ("N".equals(entityArg.getCaseSensitive())&& (entityArg.getOperator().equals("*%") || entityArg.getOperator().equals("%*%")))
    									argumentValue = entityArg.getValue().toUpperCase();
    								else
    									argumentValue = entityArg.getValue();
    							}
    						} else {
    							// Operator default
    							whereSel.append(" like ? ");
    							argumentValue = entityArg.getValue().toUpperCase() + "%";
    						}
    						
    						typeToHibernate = "STRING";
    						
    						classArgumentType		= String.class;
    						typesParamConstructor[0]	= Class.forName("java.lang.String");
    						valuesParamConstrutor[0]= argumentValue;
    						objValueArgument		= classArgumentType.getConstructor(typesParamConstructor).newInstance(valuesParamConstrutor);
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

        						whereSel.append(" " + entityArg.getAlias() + "." + nameAux);
    							
    							// Adicionando operador para o Argumento	
    							if (entityArg.getOperator() != null) 
    								whereSel.append(" " + entityArg.getOperator() + " ? "); // Operator especificado pelo desenvolvedor
    							else 
    								whereSel.append(" = ? "); // Operator default
    						}
    						
    						argumentValue = entityArg.getValue();
    						
    						log.debug("mountWhereClause: pd - type[" + k + "] = " + pd[k].getPropertyType().getName());
    						
    						//java.util.Date
    						if ( pd[k].getPropertyType().getName().indexOf("java.util.Date") >= 0 ) {
    							String valeuDate = null;
    							if (entityArg.getOperator() != null && (entityArg.getOperator().equals("<=") || entityArg.getOperator().equals("<"))) 
    								valeuDate = argumentValue.substring(0,10) + " 23:59:59";
    							else 
    								valeuDate = argumentValue + " 00:00:00";
    							
    							log.debug("mountWhereClause: valeuDate = " + valeuDate);
    							typeToHibernate 			= "DATE";
    							SimpleDateFormat formatter 	= new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss");
    							objValueArgument		 	= formatter.parse(valeuDate);
    							
    						} 
    						
    						else {
    							//BigDecimal
    							if (BigDecimal.class.isAssignableFrom(pd[k].getPropertyType())){
    								typeToHibernate 		= "BIG_DECIMAL";
    								classArgumentType 	= BigDecimal.class;
    								typesParamConstructor[0]	= Class.forName("java.lang.String");
    								argumentValue 			= argumentValue.replace(".", "").replace(",", ".");
    								valuesParamConstrutor[0]= argumentValue;
    								objValueArgument		= classArgumentType.getConstructor(typesParamConstructor).newInstance(valuesParamConstrutor);
    							}	
    							else
    								if (Long.class.isAssignableFrom(pd[k].getPropertyType())){
										typeToHibernate 		= "LONG";
										classArgumentType 	= Long.class;
										typesParamConstructor[0]	= Class.forName("java.lang.String");
										valuesParamConstrutor[0]= argumentValue;
										objValueArgument		= classArgumentType.getConstructor(typesParamConstructor).newInstance(valuesParamConstrutor);
    								}	
    								else {
    									// AGREGADO
    									if (PlcBaseEntity.class.isAssignableFrom(pd[k].getPropertyType())) {
    										
    										if (nameAux.indexOf(".")== -1 ){ 
    											/* ou seja o Argumento é uma classe agregada direta e não um campo de uma agregada,
    											 * será pelo ID, um Long
    											 Ex: candidato.areaInteresse
    											 */
    											typeToHibernate 		= "LONG";
    											classArgumentType 	= Long.class;
    											typesParamConstructor[0]	= Class.forName("java.lang.String");
    											valuesParamConstrutor[0]= argumentValue;
    											objValueArgument		= classArgumentType.getConstructor(typesParamConstructor).newInstance(valuesParamConstrutor);
    											
    										}
    										else{
    											/*
    											 * Recuperando a classe agregada para chamar o método novamente
    											 */
    											String entityAliasOriginal = entityArg.getAlias();
    											Class entityClass = Class.forName(pd[k].getPropertyType().getName());
    											BeanInfo info = Introspector.getBeanInfo(entityClass);
    											PropertyDescriptor[] pdAggregate = info.getPropertyDescriptors();
    											if (nameAux.indexOf(".") > -1)
    												entityArg.setAlias(entityArg.getAlias().concat(".").concat(rootName));
    											else
    												entityArg.setAlias(entityArg.getAlias().concat(".").concat(nameAux));
    											
    											nameAux		= nameAux.substring(nameAux.indexOf(".")+1);
    											if (nameAux.indexOf(".")> 0 )
    												rootName = nameAux.substring(0,nameAux.lastIndexOf("."));
    											else
    												rootName = nameAux;
    											
    											whereSel = alterWhereClauseToProperty(whereSel,pdAggregate,entityArg,rootName,nameAux,argValues,argTypes);
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
    						log.debug("mountWhereClause: type = " 	+ pd[k].getPropertyType().getName());
    						log.debug("mountWhereClause: nameTypo = " + typeToHibernate);
    					}
    					
    					Hibernate hibernate 	= null;
    					Class classe			= Class.forName("org.hibernate.Hibernate");
    					Field field 			= classe.getField(typeToHibernate);
    					NullableType nullableType = (NullableType) field.get(hibernate);
    					
    					// Atenção: Para verificação futura - uso do TIMESTAMP
    					if (typeToHibernate.equals("DATE"))
    						argTypes.add(Hibernate.TIMESTAMP);
    					else
    						argTypes.add(nullableType);
    					
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
	public StringBuffer mountWhereClauseQBE(String typeObjectPersistence,
											  List argEntity,
											  List objFind,
											  List typeFind) throws PlcException {

	try {

		StringBuffer whereSel = new StringBuffer();

		if (log.isDebugEnabled())
			log.debug("mountWhereClause: arg list size = " + argEntity.size());

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
						log.debug("mountWhereClause: valueDate = " + valueDate);

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
		log.debug("Where clause ="+whereSel);

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
    public StringBuffer mountOrderByClause(List<PlcArgEntity> argEntity) throws PlcException{
    	
    	log.debug("############### Entered in mountOrderByClause");

		try {

			StringBuffer orderBySel = new StringBuffer();

				if (argEntity != null && argEntity.size() > 0 ) {

					if (log.isDebugEnabled()) log.debug("mountOrderByClause:  arg list size = " + argEntity.size());

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
	public Object[] mountArgsQBE (String typeObjectPersistence, List<PlcArgEntity> argEntity)
																	throws PlcException {

		log.debug("############### Entered in montaArgsQBE");
		
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
					PlcArgEntity entityArg = (PlcArgEntity) argEntity.get(0);
					callOptimized = entityArg.getFormat() != null &&
								entityArg.getType().equals(PlcConstantsCommons.CONSULTATION.QBE.QBE_TYPE_ARGUMENT);
					log.debug("entityArg is not zero!");
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
					log.debug("Mounting type array ");

					typeFinal = new Type[typeFind.size()];

					for (int i = 0; i < typeFinal.length; i++ ) {
						typeFinal[i] = (Type) typeFind.get(i);
					}
				}

				log.debug("Finished mounting where");


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
	public Object[] mountArrayFromCollection (List<PlcArgEntity> argEntity)	throws PlcException {

		log.debug("############### Entered in mountArrayFromCollection");
		
		List lVal = new ArrayList();
		List lTipos = new ArrayList();
		
		for (Iterator iter = argEntity.iterator(); iter.hasNext();) {
			
			PlcArgEntity arg = (PlcArgEntity) iter.next();
			
			if (arg.getValue()!=null) {
				
				// TODO Fazer para todos os tipos
				lVal.add(arg.getValue());
				
				if (arg.getValue().getClass().equals(Long.class))
					lTipos.add(PlcConstantsCommons.TYPES.LONG);
				else if (arg.getValue().getClass().equals(String.class))
					lTipos.add(PlcConstantsCommons.TYPES.STRING);
				else if (arg.getValue().getClass().equals(Date.class))
					lTipos.add(PlcConstantsCommons.TYPES.DATE);
				
			}
			
		}
		String[] lTypeS = new String[lTipos.size()];
		int cont=0;
		for (Iterator iter = lTipos.iterator(); iter.hasNext();) {
			String tipo = (String) iter.next();
			lTypeS[cont] = tipo;
			cont++;
		}
		
		return new Object[]{(Object[])lVal.toArray(),lTypeS};
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
	protected String verifyName(String name) {

		log.debug("############### Entered in verifyName");

		if (name.indexOf("_") > 0) {

			PlcStringHelper plUtil = PlcStringHelper.getInstance();

			name = plUtil.changePart(name,"_",".");

			if (log.isDebugEnabled())
            	log.debug(" name with dots = " + name);

			return name;

		} else
			return name;

	}

	/**
	 * Monta cláusula FROM
	 * @since jCompany 1.5
	 * @param aliasObj String a ser utilizada como alias. Default "obj".
	 * @param typoObjectPersistencia Tipo do Value Object com package
	 *
	 * @return Retorna uma String contendo a cláusula from de um HQL.
	 *
	 * @throws PlcException Trata exceções e transforma em PlcException, para tratamento genérico e exibição para usuário.
	 */
	public StringBuffer mountSelectFrom (String aliasObj, String typoObjectPersistencia)
																	throws PlcException {

		Logger log = Logger.getLogger(this.getClass());

		StringBuffer sqlSel;

		try {

				sqlSel = new StringBuffer("from " );

				sqlSel.append(aliasObj);
				sqlSel.append(" in class ");
				sqlSel.append(typoObjectPersistencia);

				if (log.isDebugEnabled()) log.debug("sqlSel (1) = " + sqlSel.toString());

				return sqlSel;

		}catch( Exception ex ){

			log.error("Error trying to mount select , from clauses"+ex);
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
		int groupByPoint = hqlBase.indexOf("group by");
		int orderByPoint = hqlBase.indexOf("order by");

		if (wherePoint>-1)
			insertionPoint = wherePoint+6;
		if (wherePoint==-1 && groupByPoint>-1)
			insertionPoint = groupByPoint-1;
		if (wherePoint==-1 && orderByPoint>-1)
			insertionPoint = orderByPoint-1;

		if (insertionPoint==0)  // nao tem where
			return hqlBase+ " where "+whereSel.toString();
		else if (wherePoint>0) // j&#65533; tem where e nao tem    	  // orderby ent&#65533;o insere
			return hqlBase.substring(0,insertionPoint)+ " ("+whereSel.toString()+ ") and "+hqlBase.substring(insertionPoint);
		else if (wherePoint==-1 && orderByPoint==-1 && groupByPoint>-1) // 	nao tem where mas tem groupby
			return hqlBase.substring(0,insertionPoint)+ " where "+whereSel.toString()+ " "+hqlBase.substring(insertionPoint);
		else if (wherePoint==-1 && groupByPoint==-1 && orderByPoint>-1) //	nao tem where mas tem orderby
			return hqlBase.substring(0,insertionPoint)+ " where "+whereSel.toString()+ " "+hqlBase.substring(insertionPoint);
		else if (wherePoint==-1 && groupByPoint>-1 && orderByPoint>-1) // nao tem where mas tem orderby e groupby, pegar o de menor posicao
			return hqlBase.substring(0,orderByPoint>groupByPoint?(groupByPoint-1):(orderByPoint-1))+ 
			" where "+whereSel.toString()+ " "+hqlBase.substring(orderByPoint>groupByPoint?(groupByPoint-1):	(orderByPoint-1));
		else 
			throw new PlcException("jcompany.errors.mount.hql.args",new Object[]{hqlBase,whereSel.toString()});
	}

	
	public String changeOrderBy(String hql, String orderByDynamic) throws PlcException {
		log.debug("############### Entered in changeOrderBy");
		int orderByPosition = hql.toString().indexOf("order by");
		if (orderByPosition==-1) {
			return hql+" order by "+orderByDynamic;
		} else {
			return hql.substring(0,orderByPosition-1)+" order by "+orderByDynamic;
		}
	}
	
	/**
	 * jCompany 3.0 Recebe uma query com possíveis argumentos na forma ':arg' e os devolve.<p>Obs: considera um máximo de 15 argumentos.
	 * @param query query contendo argumentos. Ex: "from Class obj where obj.id=:idPai and obj.nome like ':nome%'"
	 * @return relação de nomes dos argumentos. Ex: {"idPai","nome"}
	 */
	public String[] distillArgumentsHQL(String query) throws PlcException {
		log.debug("############### Entered in distillArgumentsHQL");
	
		int argPosition = -1;
		int endArgPosition = -1;
		int count=0;
		String[] nameArgs = new String[]{null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
		
		do {
			
			argPosition = query.indexOf(":",argPosition+1);
			
			if (argPosition != -1) {
				// Separa nome arg
				endArgPosition = query.indexOf(" ",argPosition);
				if (endArgPosition == -1)
					endArgPosition = query.length();
				nameArgs[count]=query.substring(argPosition+1,endArgPosition);
				if (nameArgs[count].endsWith(")"))
					nameArgs[count]=nameArgs[count].substring(0,nameArgs[count].length()-1);
				count++;
			}
			
			
		} while (argPosition != -1);
	
		return nameArgs;
	}
	
	/**
	 * jCompany 3.0 Troca todas as ocorrencias dos argumentos no padrao ':nome' para ' is null'
	 * TODO jCompany 3.1 Revisao de parser.
	 * @param query query contendo argumentos. Ex: 'from MinhaClasse obj where obj.idPai=:id and obj.nome=:nome'
	 * @param nullArgNames Ex: {nome}
	 * @return query contendo argumentos substituidos. Exemplo: 'from MinhaClasse obj where obj.idPai=:id and obj.nome is null'
	 */
	public String changeWhereClauseToNull(String query, String[] nullArgNames) throws PlcException {
		
		log.debug("############### Entered in trocaClausulaWhereParaNulos");
	
		for (int i = 0; i < nullArgNames.length; i++) {
			String nullArgName = nullArgNames[i];
			if (nullArgName != null) {
				query = query.replaceAll(">=:"+nullArgName," is null");
				query = query.replaceAll(">= :"+nullArgName," is null");
				query = query.replaceAll("<=:"+nullArgName," is null");
				query = query.replaceAll("<= :"+nullArgName," is null");
				query = query.replaceAll("<>:"+nullArgName," is null");
				query = query.replaceAll("<> :"+nullArgName," is null");
				query = query.replaceAll("<:"+nullArgName," is null");
				query = query.replaceAll("< :"+nullArgName," is null");
				query = query.replaceAll(">:"+nullArgName," is null");
				query = query.replaceAll("> :"+nullArgName," is null");
				query = query.replaceAll("=:"+nullArgName," is null");
				query = query.replaceAll("= :"+nullArgName," is null");
			}
		}
		
		return query;

	}

	
	
	
    
	

}

