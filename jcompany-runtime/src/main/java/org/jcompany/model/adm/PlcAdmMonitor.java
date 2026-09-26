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
package org.jcompany.model.adm;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcException;
import org.jcompany.model.PlcBaseManager;
import org.jcompany.persistence.PlcBaseDAO;


/**
 *  Classe de monitoria de ULT
 * @since jCompany 3.0
 * @version $Id: PlcAdmMonitor.java,v 1.3 2006/08/09 13:33:44 joaopaulo_santos Exp $
*/
public class PlcAdmMonitor extends PlcBaseManager {

	PlcBaseDAO baseDAO;
	
	/**
	 * jCompany 3.0 Construtor com Injecao de Dependencia
	 */
	public PlcAdmMonitor(PlcBaseDAO baseDAO){
		this.baseDAO = baseDAO;
	}
	
	/**
	 * jCompanyAdm. Recupera o valor de uma coluna para uma classe, baseado no Id passado
	 * @return String contendo o nome
	 */
	public String retrieve(org.hibernate.classic.Session sess, String className, Long id) throws PlcException {

		if (log.isDebugEnabled())
			log.debug("######## Entered to retrieve from class :"+className+" for column name "+
				":"+id);

		try {

			List l = (List) sess.createQuery("select obj.nome from obj in class "+
					className+" where obj.id="+id).list();

			return (String) l.get(0);

		} catch (Exception e) {
			throw new PlcException("jcompanyadm.erroe.retrieve.value",new Object[]{className, id, e},e,log);
		}

	}

	/**
	 * jCompanyAdm. Recupera o valor de uma coluna para uma classe, baseado no Id passado
	 */
	public void alterWithException(String className, Long id,String flush) throws PlcException {

		if (log.isDebugEnabled())
			log.debug("######## Entered to update class:"+className+" for column name "+
				":"+id);

		try {

		//	PlcBaseDAO  baseDAO = getDAOPadrao();
			PlcBaseEntity entity = (PlcBaseEntity)
					baseDAO.retrieve(Class.forName(className),id);


			PropertyUtils.setProperty(entity,"name",PropertyUtils.getProperty(entity,"name")+"#1#");

//			Envia o update
			baseDAO.insert(entity);
			if (flush.equals("S"))
				baseDAO.sendCacheCommands(entity.getClass());

			PropertyUtils.setProperty(entity,"name",PropertyUtils.getProperty(entity,"name")+"#2#");

//			Envia o update
			baseDAO.insert(entity);
			if (flush.equals("S"))
				baseDAO.sendCacheCommands(entity.getClass());

			// Provoca erro - o rollback do jCompany deve retornar ao valor original se o driver JDBC está em
			// autocommit = false;
			//int a = 22 / 0;

		} catch (Exception e) {
			throw new PlcException("jcompanyadm.error.retrieve.value",new Object[]{className,id,e},e,log);
		}

	}

}

