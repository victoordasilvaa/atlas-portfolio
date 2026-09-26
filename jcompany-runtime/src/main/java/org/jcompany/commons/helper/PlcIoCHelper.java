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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * jCompany 3.0. Singleton. Classe utilitária para auxilio em lógicas de Inversão de Controle/Injeção de Dependência
 */
public class PlcIoCHelper {

	private static PlcIoCHelper INSTANCE = new PlcIoCHelper();
    private PlcIoCHelper() {   }
    /**
     * @since jCompany 3.0
     */
    public static PlcIoCHelper getInstance() {  return INSTANCE;  }
    
    protected static final Logger log = Logger.getLogger(PlcIoCHelper.class);

    /**
     * jCompany 3.0 Recupera o objeto que implementa uma interface utilizando uma classe de Factory Singleton (que possua getInstance()) e 
     * que retorne serviços na forma de interfaces.
     */
	public Object getImplementationByFactory(Class interfaceClass, Class factoryClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class[] c = null;
		// Existe, entao captura servico
		Method m = factoryClass.getMethod("getInstance",c);
		
		Object[] b = null;
		// Aqui tem uma instancia da Fábrica 
		Object obj = m.invoke(null,b);
		
		String creationMethodName=interfaceClass.getName().substring(interfaceClass.getName().lastIndexOf(".")+1);
		
		// Se nome da interface inicia com 'I' trocar por 'create', já que o padrão é createMeuServico para IMeuServico
		if (creationMethodName.startsWith("I"))
			creationMethodName = "create" + creationMethodName.substring(1);
		else
			creationMethodName = "create" + creationMethodName;
		
		
		Method creationMethod = obj.getClass().getMethod(creationMethodName,c);

		// Obtém uma interface com implementação concreta cedida pelo Factory.
		return creationMethod.invoke(obj,b);
	}
	


}