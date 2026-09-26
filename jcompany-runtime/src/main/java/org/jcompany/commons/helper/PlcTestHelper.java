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
package org.jcompany.commons.helper;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * jCompany 2.5.3. Singleton. Classe utilitária para testes de unidade
 */
public class PlcTestHelper {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 866410867065528987L;
	private static PlcTestHelper INSTANCE = new PlcTestHelper();
    private PlcTestHelper() { }
    public static PlcTestHelper getInstance(){
       return INSTANCE;
    }
   	
	protected static final Logger log = Logger.getLogger(PlcTestHelper.class);
		
	/**
	 * @since jCompany 3.0
	 * Método que deve ser chamado no "setUp" dos teste JUnit para garantir o
	 * funcionamento do Log4j.
	 */
	public void setAppender() {
			ConsoleAppender ap = new ConsoleAppender();
			ap.setName("JUnit");

			try {
				FileWriter os = new FileWriter("junit.log");
				ap.setWriter(os);
				SimpleLayout l = new SimpleLayout();
				ap.setLayout(l);
				log.addAppender(ap);
				log.setLevel(Level.DEBUG);
			} catch (IOException e) {
				e.printStackTrace();
			}
	
	}

}
