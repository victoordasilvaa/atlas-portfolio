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

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * jCompany 3.0 Classe bridge que encasula Log4j para aprimoramento de 
 * lógicas de logging e testes de unidade.
 */
public class PlcLogger extends Logger {
	
	private static final boolean LOGGER_DEBUG = true;
	
	private static final String FQCN = PlcLogger.class.getName() + ".";
	
    private static final PlcLoggerFactory factory = new PlcLoggerFactory();
    
    private Logger root;
    
    protected PlcLogger(String name) {
    	super(name);
    	this.root = null;
    }
    
	protected PlcLogger(Logger root) {
		this(root.getName());
		this.root = root;
		
		setLevel(root.getEffectiveLevel());
		setAdditivity(root.getAdditivity());
		setResourceBundle(root.getResourceBundle());
		this.repository = root.getLoggerRepository();
		//this.repository = root.repository;
		for (Enumeration allAppenders = root.getAllAppenders(); allAppenders.hasMoreElements(); ) {
			this.addAppender((Appender)allAppenders.nextElement());
		}
	}

    
    public static PlcLogger getLogger(String name) {
    	return (PlcLogger)getInstance(name);
    }
    
    public static PlcLogger getLogger(Class clazz) {
    	return (PlcLogger)getInstance(clazz.getName());
    }
    
    public static Category getInstance(String name) {
      Logger logger = LogManager.getLogger(name, factory);
      if (!(logger instanceof PlcLogger)) {
    	  logger = new PlcLogger(logger);
      }
      return logger; 
    }
    
//    public static Logger getRootLogger() {
//    	return (Logger)LogManager.getRootLogger();
//    }

	@Override
	public void trace(Object message, Throwable t) {
		
		super.log(FQCN, Level.TRACE, message, t);
	}

	@Override
	public void trace(Object message) {
		
		super.log(FQCN, Level.TRACE, message, null);
	}

	@Override
	public void debug(Object message, Throwable t) {
		
		super.log(FQCN, Level.DEBUG, message, t);
	}

	@Override
	public void debug(Object message) {
		
		super.log(FQCN, Level.DEBUG, message, null);
	}

	@Override
	public void error(Object message, Throwable t) {
		
		super.log(FQCN, Level.ERROR, message, t);
	}

	@Override
	public void error(Object message) {
	
		super.log(FQCN, Level.ERROR, message, null);
	}

	@Override
	public void fatal(Object message, Throwable t) {
	
		super.log(FQCN, Level.FATAL, message, t);
	}

	@Override
	public void fatal(Object message) {
		
		super.log(FQCN, Level.FATAL, message, null);
	}

	@Override
	public void info(Object message, Throwable t) {
		
		super.log(FQCN, Level.INFO, message, t);
	}

	@Override
	public void info(Object message) {
		
		super.log(FQCN, Level.INFO, message, null);
	}

	@Override
	public void warn(Object message, Throwable t) {
		
		super.log(FQCN, Level.WARN, message, t);
	}

	@Override
	public void warn(Object message) {
	
		super.log(FQCN, Level.WARN, message, null);
	}

	@Override
	public boolean isTraceEnabled() {
		
		return super.isTraceEnabled();
	}

	@Override
	public Level getEffectiveLevel() {
		
		return super.getEffectiveLevel();
	}

	@Override
	public boolean isDebugEnabled() {
		
		return super.isDebugEnabled();
	}

	@Override
	public boolean isEnabledFor(Priority level) {
		
		return super.isEnabledFor(level);
	}

	@Override
	public void setLevel(Level level) {
		
		super.setLevel(level);
	}

}
