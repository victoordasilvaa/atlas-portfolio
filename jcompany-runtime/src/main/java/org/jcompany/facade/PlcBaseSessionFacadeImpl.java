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
package org.jcompany.facade;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcConstantsCommons;
import org.jcompany.commons.PlcException;
import org.jcompany.commons.helper.PlcEntityHelper;
import org.jcompany.model.IPlcManager;
import org.jcompany.model.PlcBaseAS;
import org.jcompany.model.PlcModelLocator;
import org.jcompany.model.annotation.PlcTransactionNotApply;
import org.jcompany.persistence.IPlcDAO;
import org.jcompany.persistence.PlcContextManager;
import org.jcompany.persistence.PlcPersistenceLocator;


/**
 * Façade base utilizando DP "Abstract Factory". Para novas implementações de
 * camada de persistência ou uso de objetos remotos, deve-se reimplementar esta classe
 * @since jCompany 3.0
 * @version $Id: PlcBaseSessionFacadeImpl.java,v 1.8 2006/08/17 17:04:03 alvim Exp $
 */
public abstract class PlcBaseSessionFacadeImpl  implements Serializable{

	protected static final Logger logModel = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_MODEL);
	protected static final Logger logAdvertencia = Logger.getLogger(PlcConstantsCommons.LOGGERs.JCOMPANY_WARNING_DEVELOPMENT);
 	protected static Logger log = Logger.getLogger(PlcBaseSessionFacadeImpl.class);
    
    /**
     * Pega a referencia DAO do cache do Service Locator através de referencia direta ou indiretamente
     * através do nome do ENTITY, utilizando IoC padrão do jCompany (padrão de pacotes)
     * @return DAO
     */
    @PlcTransactionNotApply
    protected IPlcDAO getDAO(Class classeDAOouVO) throws PlcException {
    	
        return (IPlcDAO)PlcPersistenceLocator.getInstance().get(classeDAOouVO);
    }
    
    /**
     * Pega a referencia AS do cache do Service Locator
     */
    @PlcTransactionNotApply
    public PlcBaseAS getAS(Class classeAS) throws PlcException {
    	   return (PlcBaseAS) PlcModelLocator.getInstance().getAS(classeAS);
    }
    
    /**
     * Pega a referencia BO do cache do Service Locator através de referencia direta ou indiretamente
     * através do nome do ENTITY, utilizando IoC padrão do jCompany (padrão de pacotes)
     * @return DAO
     */
    @PlcTransactionNotApply
    public IPlcManager getManager(Class classeBOouVO) throws PlcException {
    	   return (IPlcManager)PlcModelLocator.getInstance().get(classeBOouVO);
    } 
    
    /**
     * jCompany 3.0 DP Composite. Devolve o POJO contendo informações de contexto do cliente
     */
    @PlcTransactionNotApply
    protected PlcBaseContextVO getContext() {
        return PlcContextManager.getContextEntity();
    }

    /**
 	 * jCompany. Verifica a operação de persistência a ser realizada para um
 	 * item de uma coleção de Value Objects, comparando-o com a situação anterior, a
 	 * saber<p>:
 	 *
 	 * Se indExcPlc for "S", exclui<br>
 	 * Se item existia anteriormente e foi alterado, altera<br>
 	 * Se item não existir anteriormente, inclui<p>
 	 *
 	 * Importante: Esta averiguação manual é necessário porque a Hibernate na versão
 	 * 2.0 beta3 não comportava bem com persistência de coleções "top-level". Em breve
 	 * poderá se tornar desnecessárias, a partir do momento em que a framework de
 	 * persitência interpretar essas lógicas automaticamente.
 	 *
 	 * @param listAnt Coleção de Value Objects anterior, para comparação.
 	 * @return Object[] Retorna uma String e o ENTITY anterior em caso de alteração,
 	 *      sendo a String a operação escolhida: ""-ignora, "E"-exclui, "A"-altera,
 	 *      "I"-inclui
 	 */
    @PlcTransactionNotApply
 	protected Object[] verifyOperation(Object voAtual,List listAnt) throws PlcException {

 		log.debug("########## Entrou para verificar operacao");

 		Object[] ret = null;
 		PlcEntityHelper voHelper = PlcEntityHelper.getInstance();

 		// Tabulares devem ter chaves naturais na mesma classe para usarem as lógicas
 		// genéricas do jCompany (não podem usar classes externas)
        ret = voHelper.verifyOperationById(voAtual);

 		String oper = (String) ret[0];
 		Object voAnt = ret[1];

 		if (log.isDebugEnabled()) {
 			log.debug("operacao="+oper);
 			if (voAnt != null) log.debug(" anterior="+voAnt);
 		}

 		return new Object[] {oper,voAnt};

 	}
 
	/**
	 * Método para execução através de Service Locator para classes de AS, BO ou DAO.
	 * Deve fazer gerencia da finalização da transação através com encerramentoGravacaoOk no final 
	 * (tipicamente, gerando um commit na persistencia)
	 * @param classeAsBoDao Classe para disparo de serviço, com instancia reutilizada (service locator)
	 * @param metodo Método a ser chamado via reflexão
	 * @param params Arranjo de parâmetros do negócio, na ordem em que aparecem no método
	 * @return Object Qualquer objeto (List, String[], Long, etc.)
	 * @throws PlcException Exceções devem ser transformadas em PlcException no padrão jCompany
	 */
	protected abstract Object executeOperation(PlcBaseContextVO context,Class classeAsBoDao, String metodo, Object[] params) throws PlcException;


	/**
	 * Método para execução através de Service Locator para classes de AS, BO ou DAO.
	 * Deve fazer gerencia da finalização da transação através com encerramentoLeituraOk no final 
	 * (tipicamente, gerando um rollback na persistencia)
	 * @param classeAsBoDao Classe para disparo de serviço, com instancia reutilizada (service locator)
	 * @param metodo Método a ser chamado via reflexão
	 * @param params Arranjo de parâmetros do negócio, na ordem em que aparecem no método
	 * @return Object Qualquer objeto (List, String[], Long, etc.)
	 * @throws PlcException Exceções devem ser transformadas em PlcException no padrão jCompany
	 */
	@Deprecated
	protected abstract Object executeReading(PlcBaseContextVO context,Class classeAsBoDao, String metodo, Object[] params) throws PlcException;
	

}
