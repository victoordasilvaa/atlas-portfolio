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
package org.jcompany.persistence.hibernate;

import org.apache.log4j.Logger;
import org.jcompany.commons.annotation.PlcFactory;


/**
 * jCompany 3.0. Exemplo de gerenciador de fábricas específico, registrando uma segunda fábrica diferente
 * de 'default', que usa 'hibernate.cfg.xml'
 * @since jCompany 3.0
 * @version $Id: PlcHibernateManager.java,v 1.4 2006/06/21 19:12:20 bruno_grossi Exp $
 */
@PlcFactory(nome="default",autoDetectDialect=true)
public class PlcHibernateManager extends PlcBaseHibernateManager {
	  
	protected static Logger log = Logger.getLogger(PlcHibernateManager.class);  

}

