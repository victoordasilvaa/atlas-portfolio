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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jcompany.commons.PlcException;


/**
 * jCompany 2.5.3. Singleton. Classe utilitária para datas
 */
public class PlcFileHelper  {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -3975123986085226038L;
	private static PlcFileHelper INSTANCE = new PlcFileHelper();
    private PlcFileHelper() { }
    public static PlcFileHelper getInstance(){
       return INSTANCE;
    }
   	
	protected static final Logger log = Logger.getLogger(PlcFileHelper.class);
	
	
	/**
	 * @since jCompany 3.0
	 * Verifica se existe um diretório. Se não existir, cria o diretório
	 * @param dir Diretório completo a ser verificado
	 */
	public void existDirectory(String dir) {

		log.info("###Verify directory for indexing");												
				
		existDirectory(dir,true);						
		
	}
	
	/**
	 * @since jCompany 3.0
	 * Verifica se existe um diretório. 
	 * @param dir Diretório a ser verificado
	 * @param cria Se deve criar caso não exista, ou somente retornar false.
	 * @return true ou false, conforme exista ou não. Se for criado, retorna true.
	 */
	public boolean existDirectory(String dir, boolean cria) {
		
		log.debug("###### Entered in existDirectory");
		
		File filesystem;
            
		if (dir != null) {
        
			filesystem = new File(dir);
			
			if(!filesystem.exists()) {
				
				if(log.isDebugEnabled()) 
						log.debug("Directory not found."+dir);

				if (cria) {
					filesystem.mkdir();	
					return true;
				} else
					return false;
			
			} else
				return true;
		} else
			return false;
	}
	

	/**
	 * @since jCompany 3.0
	 * Abre o arquivo texto e devolve o conteúdo como String.
	 * @param file - Nome do arquivo texto, incluindo o path.
	 * @return String contendo o conteúdo do arquivo.
	 */
	public String loadTextFile(String file)
		throws PlcException
	{
		StringBuffer html = new StringBuffer();
		
		try
		{
			if (file != null)
			{
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr); 
					
				String lineRead = br.readLine();
				while (lineRead != null)
				{
					html.append(lineRead);
					lineRead = br.readLine();
				}
				
				br.close();
				br = null;
				fr = null;
			}
		}
		catch (Exception e)	{
			throw new PlcException("jcompany.errors.load.file",new Object[]{file,e},e,log);
		}
		
		return html.toString();
	}
	
	/**
	 * @since jCompany 3.0
	 * Lê um arquivo texto e retorna o conteúdo deste
	 * @param fileName nomeArquivo nome do Arquivo
	 * @return Vector conteúdo do arquivo
	 */
	public Vector readTextFile (String fileName) throws PlcException {	
		Vector fileContent = new Vector();
		try 
		{
			File name = new File(fileName);		
					
			BufferedReader input = new BufferedReader(new FileReader(name));		
			String text;		
			while((text = input.readLine()) != null) {
				fileContent.addElement(text);		
			} 
		}catch(Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {"readTextFile", e }, e,log);
		}
		
		return fileContent;
	}	 

}