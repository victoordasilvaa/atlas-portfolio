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
package org.jcompany.model;

import org.apache.log4j.Logger;

/**
 * jCompany 3. Classe Ancestral para Application Services.<P>
 * Segundo 'Core J2EE Design Patterns': Application Services encapsulam os Business Objects,
 * implementando regras de negócios que realizam um determinado 'serviço'. Para isso podem ser chamados
 * por outros ASs ou Façades, e por sua vez chamam BOs, ASs ou DAOs, estes últimos em casos mais simples de
 * recuperação de dados que dispensem regras especiais de negócio.
 * @since jCompany 3.0
 * @version $Id: PlcBaseAS.java,v 1.4 2006/06/21 19:12:20 bruno_grossi Exp $
 */
public class PlcBaseAS extends PlcBaseBC {

    protected Logger log = Logger.getLogger(PlcBaseAS.class);

}

