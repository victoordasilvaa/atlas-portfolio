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
package org.jcompany.commons.facade;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;

import org.jcompany.commons.IPlcFileEntity;
import org.jcompany.commons.PlcArgEntity;
import org.jcompany.commons.PlcFileEntity;
import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.PlcBaseUserProfileEntity;
import org.jcompany.commons.PlcBaseEntity;
import org.jcompany.commons.PlcException;


@Remote
public interface IPlcFacadeRemote extends IPlcFacade {
	
}

