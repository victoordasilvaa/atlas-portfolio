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
 /** ****************************** META-DADOS COMMONS DO JCOMPANY ****************************
  ********************** Defaults de Valores de Declaração Global ****************************
  ************** Deve ser empacotado em todas as camadas - WAR e JARs EJBs, quando remotos ***
  *******************************************************************************************/

@PlcConfigEnterprise(name = "Powerlogic SA", domain = "www.powerlogic.com.br", initials = "PLC", 
			logotype = "/plc/midia/marca_empresa.gif", address = "Rua Paraíba, 330 / 19o andar. CEP:30113-140 - Belo Horizonte/MG",
			callcenterEmail = "suporte@powerlogic.com.br",callcenterTelephone = "55 31 3224-6469")
@PlcConfigEncryption
@PlcConfigJSecurity
@PlcConfigAlert
@PlcConfigPackage
@PlcConfigAudit
@PlcConfigSuffixClass
@PlcConfigModelTechnology
@PlcConfigServices({})
			
package org.jcompany.config.commons;

import org.jcompany.config.commons.PlcConfigAlert;
import org.jcompany.config.commons.PlcConfigAudit;
import org.jcompany.config.commons.PlcConfigEncryption;
import org.jcompany.config.commons.PlcConfigEnterprise;
import org.jcompany.config.commons.PlcConfigJSecurity;
import org.jcompany.config.commons.PlcConfigModelTechnology;
import org.jcompany.config.commons.PlcConfigPackage;
import org.jcompany.config.commons.PlcConfigServices;
import org.jcompany.config.commons.PlcConfigSuffixClass;

