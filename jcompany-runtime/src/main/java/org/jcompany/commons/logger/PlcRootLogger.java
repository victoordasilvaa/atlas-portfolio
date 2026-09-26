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
package org.jcompany.commons.logger;

import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.RootLogger;

public class PlcRootLogger extends PlcLogger {
	
	public PlcRootLogger(Level level) {
		super("root");
		setLevel(level);
	}


	public PlcRootLogger(RootLogger root) {
		super(root);
		setLevel(root.getChainedLevel());
	}
	
	public final Level getChainedLevel() {
		return level;
	}
	
	@Override
	public final void setLevel(Level level) {
		if (level == null) {
			LogLog.error(
					"You have tried to set a null level to root.", new Throwable());
		} else {
			this.level = level;
		}
	}
	
}
