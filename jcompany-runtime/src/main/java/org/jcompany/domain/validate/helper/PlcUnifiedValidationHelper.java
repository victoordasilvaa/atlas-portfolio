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
package org.jcompany.domain.validate.helper;

import org.apache.log4j.Logger;

/**
 * @since jCompany 3.2 Regra de validação de CPF fatorada para reutilização em validações variantes 
 * (Commons Validator, JSF) e Invariantes (Hibernate Validator)
 */
public class PlcUnifiedValidationHelper {

	protected static final Logger log = Logger.getLogger(PlcUnifiedValidationHelper.class);

 	private static PlcUnifiedValidationHelper INSTANCE = null;
 	private PlcUnifiedValidationHelper() { }
 	public static PlcUnifiedValidationHelper getInstance(){
 		if (INSTANCE==null)
 			INSTANCE = new PlcUnifiedValidationHelper();
 	   return INSTANCE;
 	}
 	
 	/**
 	 * Validação de CPF
 	 * @param cpf Número de CPF, somente com numeros, em formato String
 	 * @return true de número for válido e false se não for.
 	 */
	public boolean validateCpf(String cpf) {

		 	// Inicio
		 	int sum=0;
		 	int rest=0;
		 	int I=0;
	
		 	if (cpf == null || "".equals(cpf.trim()))
		 		return true;
	
		 	if (cpf.length() != 11)
		 		return false;
	
		 	sum = 0 ;
		 	for (I=0;I<=8;I++) {
		 		sum = sum +  (Integer.valueOf(cpf.substring(I,I+1))).intValue() * (10 - I);
		 	}
	
		 	int result = (sum - (sum%11))/11;
		 	rest = 11 - (sum - result * 11);
	
		 	if (rest == 10 || rest == 11) {
		 		rest = 0;
		 	}
	
		 	if (rest != (Integer.valueOf(cpf.substring(9,10)).intValue())) 
		 		return false;
	
		 	sum = 0;
		 	for (I=0; I<=9;I++)   {
		 		sum = sum + (Integer.valueOf(cpf.substring(I,I+1))).intValue() * (11-I);
		 	}
	
		 	result = (sum - (sum%11))/11;
		 	rest = 11 - (sum - result * 11);
	
		 	if (rest == 10 || rest ==11)  {
		 		rest = 0;
		 	}
	
		 	if (rest != (Integer.valueOf(cpf.substring(10,11))).intValue())
		 		return false;
		
		 	return true;

	}
	
	/**
 	 * Validação de CNPJ
 	 * @param cnpj Número de CNPJ, somente com numeros, em formato String
 	 * @return true de número for válido e false se não for.
 	 */
	public boolean validateCnpj(String cnpj) {


		if (cnpj == null || cnpj.equals("")){
			log.debug("########### CNPJ null or empty");
			return true;
		}

		if (cnpj.length()!=14){
			log.debug("########### CNPJ invalid (size not 14)");
			return false;
		}

		// Verifica se todos os numeros sao iguais
		int equals = 0;
		String c = cnpj.substring(0,1);
		for(int i=1; i < cnpj.length(); i++){
			if (c.equals(cnpj.substring(i, i+1))) {
				equals++;
			}
		}
		if (equals >= 12){
			log.debug("########### CNPJ invalid (characteres similar)");
			return false;
		}


		int sum = 0, result1 = 0, result2 = 0;

		int[] number = new int[14];

		for(int j=0; j < cnpj.length(); j++){
			number[j] = Integer.parseInt(cnpj.substring(j, j+1));
		}

		sum = (number[0] * 5) + (number[1] * 4) + (number[2] * 3) +
					(number[3] * 2) + (number[4] * 9) + (number[5] * 8) +
					(number[6] * 7) + (number[7] * 6) + (number[8] * 5) +
					(number[9] * 4) + (number[10] * 3) + (number[11] * 2);

		sum -= (11 * ((int)(sum / 11)));

		if (sum == 0 || sum == 1){
			result1 = 0;
		}else{
			result1 = 11 - sum;
		}

		if(result1 == number[12]){
			sum = (number[0] * 6) + (number[1] * 5) + (number[2] * 4) +
					(number[3] * 3) + (number[4] * 2) + (number[5] * 9) +
					(number[6] * 8) + (number[7] * 7) + (number[8] * 6) +
					(number[9] * 5) + (number[10] * 4) + (number[11] * 3) +
					(number[12] * 2);

			sum -= (11 * ((int)(sum/11)));

			if(sum == 0 || sum == 1){
				result2 = 0;
			}else{
				result2 = 11 - sum;
			}

			if(result2 != number[13]){
				log.debug("########### CNPJ Invalid (verifier 13 invalid)");
				return false;
			}

		}else{
			log.debug("########### CNPJ Invalid (verifier invalid)");
			return false;
		}

		log.debug("########### CNPJ OK");
		return true;

	}

}
