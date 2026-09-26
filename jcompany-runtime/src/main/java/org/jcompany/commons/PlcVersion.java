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

import org.apache.log4j.Logger;

/**
 * Classe que mantém a versão atual do jCompany
 * @since jCompany 3.1.1
 */
public class PlcVersion {

	protected static final Logger log = Logger.getLogger(PlcVersion.class);
	  
	public static String VERSION ;

	static {
		String jarName = PlcVersion.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		VERSION = jarName.substring(jarName.indexOf("jcompany_commons-")+16, jarName.indexOf(".jar"));
		log.info( "===================================================================" );
		log.info( "=========== jCompany Community " + VERSION + " ===============" );
		log.info( "===================================================================" );
	}

	public static void touch() {
	}
}
