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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.jcompany.commons.PlcException;


/**
 * @since jCompany 2.5.3
 * Singleton. Classe utilitária para datas
 */
public class PlcDateHelper  {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -1977320696527365824L;
	private static PlcDateHelper INSTANCE = new PlcDateHelper();
    private PlcDateHelper() { }
    public static PlcDateHelper getInstance(){
       return INSTANCE;
    }
   	
	protected static final Logger log = Logger.getLogger(PlcDateHelper.class);
	
	/**
	 * @since jCompany 1.5.3
	 * Método que exibe uma data utilizando .toLocaleString(), somente se ela não for nula.
	 * @param dataToShow
	 * @return 	Se data for nula exibe "", senão exibe .toLocaleString();
	 */
	public static String showWithHoursNull(Date dataToShow) {
		if (dataToShow == null)
			return "";
		else
			return dataToShow.toLocaleString();
	}
	
	/**
	 * @since jCompany 1.5.3
	 * Método que exibe uma data utilizando .toLocaleString().substring(0,10), 
	 * somente se ela não for nula.
	 * @param dataToShow
	 * @return 	Se data for nula exibe "", senão exibe somente parte data (sem hora);
	 */
	public static String showWithoutHoursNull(Date dataToShow) {
		if (dataToShow == null)
			return "";
		else
			return dataToShow.toLocaleString().substring(0,10);
	}


	/**
	 * @since jCompany 3.0
	 * Método que deve ser chamado no "setUp" dos teste JUnit para garantir o
	 * funcionamento do Log4j.
	 * TODO Ver se existe uma classe "Mock" para o Log4j, para evitar este método
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

	/**
	 * @since jCompany 3.0
	 * Devolve o número de dias entre duas datas. Nota exemplo: Se hoje é 1/12/2004 e amanhã é 2/12/2004, devolve 0 (Zero),
	 * pois não há nenhum dia entre as duas datas!
	 */
	public float daysBetweenDates(Date initialDate, Date finalDate) {

		log.debug("###################### Entered in daysBetweenDates");

		//TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
		//Locale lc = new Locale("br", "BRA");
		//Calendar cal = Calendar.getInstance(tz, lc);

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		log.debug("initialDate:" + formatter.format(initialDate));
		log.debug("finalDate:" + formatter.format(finalDate));

		float dif =
			(finalDate.getTime() - initialDate.getTime())
				/ (24 * 60 * 60 * 1000);

		if (log.isDebugEnabled())
			log.debug("Diff between dates = " + dif);

		return dif;
	}
	
	/**
	 * @since jCompany 5.0 Devolve o número de anos entre duas datas. 
	 * Pode ser utilizado para calcular idade, por exemplo
	 * Nota exemplo: Se dataInicial é 1/12/2000 e a final é 2/12/2004, devolve 4 (quatro)
	 */
	public long yearsBetweenDates(Date initialDate, Date finalDate) {

		 long dt = (finalDate.getTime() - initialDate.getTime()) + 3600000;

		if (log.isDebugEnabled())
			log.debug("Diff between dates = " + dt);

         Date finalDateAux = new Date(dt);

         return finalDateAux.getYear()-70;

	}
	

	/**
	 * @since jCompany 3.0
		* Devolve o número de segundos entre duas data-horas.
		* 
		* @param initialDate
		* @param finalDate
		* @return dif
		*/
	public float secondsBetweenDates(Date initialDate, Date finalDate) {

		log.debug(
			"###################### Entered in secondsBetweenDates");

		try {
			//TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
			//Locale lc = new Locale("br", "BRA");
			//Calendar cal = Calendar.getInstance(tz, lc);
			
			if(log.isDebugEnabled()) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			    log.debug("initialDate:" + formatter.format(initialDate));
			    log.debug("finalDate:" + formatter.format(finalDate));
			}

			float dif = (finalDate.getTime() - initialDate.getTime()) / 1000;

			if (log.isDebugEnabled())
				log.debug("Diff between dates = " + dif);

			return dif;

		} catch (Exception e) {
			Logger log = Logger.getLogger(this.getClass());
			log.error(e.toString(), e);
			return 0;
		}
	}

	/**
	 * @since jCompany 3.0
		* Devolve próximo início de mês.
		* @param month no formato String com zeros. Ex: "01", "11", "12"
		* @param year no formato String com quatro dígitos. Ex: "2004", "2005"
		* @return Retorna um Date com parte hora 00:00:00 e primeiro dia do mês seguinte
		*/
	public Date nextBeginningOfMonth(String month, String year) throws Exception {

		log.debug("###################### Entered in nextBeginningOfMonth");

		//TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
		//Locale lc = new Locale("br", "BRA");
		//Calendar cal = Calendar.getInstance(tz, lc);

		SimpleDateFormat formatter =
			new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// Verifica próximo ano
		int nextYear = new Integer(year).intValue();
		int nextMonth = new Integer(month).intValue();
		if (month.equals("12")) {
			nextYear++;
			nextMonth = 1;
		} else
			nextMonth++;

		if (nextMonth <= 9)
			month = nextMonth + "";
		else
			month = "0" + nextMonth;

		Date nextBeginningOfMonth =
			formatter.parse("01/" + month + "/" + nextYear + " 00:00:00");

		if (log.isDebugEnabled())
			log.debug("finalDate:" + nextBeginningOfMonth.toLocaleString());

		return nextBeginningOfMonth;

	}

	/**
	 * @since jCompany 3.0
	* Devolve início de mês.
	* @param month no formato String com zeros. Ex: "01", "11", "12"
	* @param year no formato String com quatro dígitos. Ex: "2004", "2005"
	* @return Retorna um Date com parte hora 00:00:00 e primeiro dia do mês
	*/
	public Date beginningOfMonth(String month, String year) throws Exception {

		log.debug("###################### Entered in beginningOfMonth");

		//TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
		//Locale lc = new Locale("br", "BRA");
		//Calendar cal = Calendar.getInstance(tz, lc);

		SimpleDateFormat formatter =
			new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		Date beginningOfMonth = formatter.parse("01/" + month + "/" + year + " 00:00:00");

		if (log.isDebugEnabled())
			log.debug("DataFinal:" + beginningOfMonth.toLocaleString());

		return beginningOfMonth;

	}

	/**
	 * @since jCompany 3.0
	 * Retorna se a segunda data é maior que a primeira 
	 * @param initialDate Data inicial
	 * @param finalDate Data Final
	 * @return True se a segunda data for maior, incluindo precisão de milionésimos
	 * de segundo.
	 */
	public boolean finalDateGreaterThanInitial(Date initialDate, Date finalDate) {
		if (daysBetweenDates(initialDate, finalDate) > 0)
			return true;
		else
			return false;
	}

	/**
	 * @since jCompany 3.0
		* Retorna se a segunda data é maior ou igual à primeira 
		* @param initialDate Data inicial
		* @param finalDate Data Final
		* @return True se a segunda data for maior, incluindo precisão de milionésimos
		* de segundo.
		*/
	public boolean finalDateGreatThanEqualInitial(Date initialDate, Date finalDate) {
		if (daysBetweenDates(initialDate, finalDate) >= 0)
			return true;
		else
			return false;
	}

	/**
	 * @since jCompany 3.0
	 * Retorna se a segunda data é maior que a primeira 
	 * @param initialDate Data inicial
	 * @param finalDate Data Final
	 * @return True se a segunda data for maior, incluindo precisão de milionésimos
	 * de segundo.
	 */
	public boolean finalDateGreaterThanInitialInSeconds(Date initialDate, Date finalDate) {
		if (secondsBetweenDates(initialDate, finalDate) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * @since jCompany 3.0
	 * Retorna se a segunda data é maior ou igual à primeira 
	 * @param initialDate Data inicial
	 * @param finalDate Data Final
	 * @return True se a segunda data for maior, incluindo precisão de milionésimos
	 * de segundo.
	 */
	public boolean finalDateGreatThanEqualInitialInSeconds(
		Date initialDate,
		Date finalDate) {
		if (secondsBetweenDates(initialDate, finalDate) >= 0)
			return true;
		else
			return false;
	}

	/**
	 * @since jCompany 3.0
	 * Soma ou subtrar um determinado número de dias de uma data inicial, devolvendo a
	 * data resultante
	 * @param initialDate Data inicial
	 * @param numberDays Número de dias, positivo ou negativo, para conta.
	 * @return Data resultante
	 */
	public Date dateNumberDays(Date initialDate, int numberDays) {

		if (log.isDebugEnabled())
			log.debug("###################### Entered in dateNumberDays "+initialDate.toLocaleString()+" and"+
				numberDays);

		//TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
		//Locale lc = new Locale("br", "BRA");
		//Calendar cal = Calendar.getInstance(tz, lc);

		//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		if (log.isDebugEnabled())
			log.debug("initialDate:" + initialDate.toLocaleString()+" initialDate.time="+initialDate.getTime());
		
		long aAdicionar = ((24 * 60 * 60 * 1000) * (long)numberDays);
		
		long tempoFinal =	initialDate.getTime() + aAdicionar;
		
		if (log.isDebugEnabled())
			log.debug("initialDate = "+initialDate.getTime()+" adding "+aAdicionar+
				" resulting = "+tempoFinal);

		Date dataFinal = new Date(tempoFinal);

		if (log.isDebugEnabled())
			log.debug("Date = " + dataFinal.toLocaleString());

		return dataFinal;

	}

	/**
	 * @since jCompany 3.0
	 * Devolve uma data que representa o primeiro dia do mês e outra que representa o último.
	 * @param date Data corrente
	 * @return Object[] contendo dois objetos java.util.Date com dataInicial e dataFinal nas posições 0 e 1
	 */
	public Date endOfCurrentMonth(Date date) {

		Calendar c = new GregorianCalendar();
		c.setTime(date);
		
		TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
		Locale lc = new Locale("br", "BRA");
		Calendar cal = Calendar.getInstance(tz, lc);
		cal.set(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

		int dayFinal = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar dateFinal = new GregorianCalendar();
		dateFinal.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), dayFinal, 23, 59, 59);
		if (log.isDebugEnabled())
			log.debug(
				"endOfCurrentMonth="
					+ c.get(Calendar.MONTH)
					+ " is ="
					+ dateFinal.getTime());

		return dateFinal.getTime();
	}

	/**
	 * @since jCompany 3.0
	 * Recebe uma Data e devolve o nome do mês, conforme o locale.
	 * @param date
	 * @return Nome do Mês
	 */
	public String monthName(Date date) {

		SimpleDateFormat f = new SimpleDateFormat("MMMMMMMMMMMMMMM");
		return f.format(date);

	}

	/**
	 * @since jCompany 3.0
	  * Retorna a data do último dia do mês de um determinado ano e mês dados, 
	  * com a hora zerada.
	  *  
	  * @param year Ano
	  * @param month Mês (1 a 12)
	  * @return Dia (1 a 31)
	 */
	public static Date dateOfLastDayOfMonth(int year, int month) {

		return dateOfLastDayOfMonth(year, month, 0, 0, 0);

	}

	/**
	 * @since jCompany 3.0
	 * Retorna a data do último dia do mês de um determinado ano e mês dados	
	 * Este método cria também uma data com parâmetros de hora fornecidos. 
	 * 
	 * @param year Ano	
	 * @param month Mês (1 a 12)	
	 * @param hour Hora (0 a 23)	
	 * @param minute Minuto (0 a 59	
	 * @param second Segundo (0 a 59)
	 * @return Date contendo a data do último dia do mês de um determinado ano e mês dados
	*/
	public static Date dateOfLastDayOfMonth(
		int year,
		int month,
		int hour,
		int minute,
		int second) {

		GregorianCalendar cal = new GregorianCalendar();

		cal.set(year, month - 1, 1, 0, 0, 0);

		int day = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		cal.set(year, month, day, hour, minute, second);

		return cal.getTime();

	}

	/**
	 * @since jCompany 3.0
	 * Retorna a data com os parâmetros fornecidos, com a hora zerada.
	 * 
	 * @param year Ano
 	 * @param month Mês (1 a 12)
	 * @param day (1 a 31)
	 * @return Date com a hora zerada
	*/
	public static Date newData(int year, int month, int day) {

		return newData(year, month, day, 0, 0, 0);

	}

	/**
	 * @since jCompany 3.0
	 * Retorna a datahora com os parâmetros fornecidos.
	 * 
	 * @param year Ano 
	 * @param month Mês (1 a 12)
	 * @param day Dia (1 a 31)
	 * @param hour Hora (0 a 23) 
	 * @param minute Minuto (0 a 59)
	 * @param second Segundo (0 a 59)
	 * @return Date uma nova data com os parâmetros fornecidos
	 */
	public static Date newData(
		int year,
		int month,
		int day,
		int hour,
		int minute,
		int second) {

		GregorianCalendar cal = new GregorianCalendar();

		cal.set(year, month - 1, day, hour, minute, second);

		return cal.getTime();

	}
	
	/**
	 * @since jCompany 3.0
	 * @param initialDate Data inicio inclusive
	 * @param finalDate Data fim inclusive
	 * @param hhBusinessByDay HH uteis por dia
	 * @param holidays Mapa de feriados no intervalo passado,
	 * contendo a data como chave e o String I-Integral ou P-Parcial como valor
	 * @param saturdaySundayOption  Opção para considerar ou não fins de semana como descanso. Na atual versão,
	 * somente SABADO_E_DOMINGO" está funcionando
	 * @return float
	 */
	public float hhRestBetweenDates(Date initialDate, Date finalDate, long hhBusinessByDay,
			Map holidays, 			String saturdaySundayOption) {
	
		log.debug("######## Entered in hhRestBetweenDates");

		//TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
		//Locale lc = new Locale("br", "BRA");
		//Calendar cal = Calendar.getInstance(tz, lc);
	
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		if (log.isDebugEnabled()) {
			log.debug("initialDate:" + formatter.format(initialDate));
			log.debug("finalDate:" + formatter.format(finalDate));
		}
		
		float hhWeekendRest = 0;
	
		// Pega sabados e domingos entre as duas datas e coloca em um
		long totWeekend = retrieveWeekendsBetweenDates(initialDate,finalDate,saturdaySundayOption);
		
		hhWeekendRest = totWeekend * hhBusinessByDay;
		
		float hhHolidaysRest = 0;
		
		// Pega feriados entre as duas datas, e que não caiam em sábado ou domingo
		if (holidays != null) {
						
			Iterator i = holidays.keySet().iterator();
			while (i.hasNext()) {
				
				Date holidayDate = (Date) i.next();
				String type = (String) holidays.get(holidayDate);
				if ((finalDateGreatThanEqualInitial(initialDate,holidayDate) &&
						finalDateGreatThanEqualInitial(holidayDate,finalDate)) &&
						!isWeekend(holidayDate,saturdaySundayOption)) {
					if (type.equals("I"))
						hhHolidaysRest = hhHolidaysRest + hhBusinessByDay;
					else
						hhHolidaysRest = hhHolidaysRest + (hhBusinessByDay / 2);
				}

			}
		}
		
		return hhWeekendRest + hhHolidaysRest;
	}
	
	/**
	 * @since jCompany 3.0
	 * Recupera número de dias de fim de semana entre duas datas, utilizando o seguinte algoritimo:
	 * -Verifica se a datainicial começa no fim de semana. Se começar, contabiliza os dias e ajusta para o próximo dia útil. (proxima segunda)
	 * -Verifica se a dataFinal encerra no fim de semana. se encerrar, contabiliza os dias e ajusta para o dia útil anterior (última sexta)
	 * -Feitos os ajustes, pode considerar fins de semana como divisões por 7
	 * @param initialDate
	 * @param finalDate
	 * @param saturdaySundayOption Somente "SABADO_E_DOMINGO" deve ser passado nesta versão
	 * @return long numero de dias de fim de semana 
	 */
	public long retrieveWeekendsBetweenDates(Date initialDate, Date finalDate, String saturdaySundayOption) {
	    return weekendDaysBetweenDates(initialDate,finalDate);
	}

   /**
    * Calcula o número de dias de final de semana que existe entre duas datas.
    * <br>
    * <br>
    * 
    */
   private long weekendDaysBetweenDates(Date initialDate, Date finalDate) {
       log.debug("######## Entered in weekendDaysBetweenDates");

       Calendar initialCalendar = Calendar.getInstance();
       Calendar finalCalendar = Calendar.getInstance();
       
       initialDate = new Date(initialDate.getYear(), initialDate.getMonth(), initialDate.getDate());
       finalDate = new Date(finalDate.getYear(), finalDate.getMonth(), finalDate.getDate());
       
       initialCalendar.setTime(initialDate);
       finalCalendar.setTime(finalDate);
       
       log.debug(" Initial Date: " + initialCalendar.get(Calendar.DAY_OF_MONTH)+
    		   "/"+initialCalendar.get(Calendar.MONTH)+"/"+initialCalendar.get(Calendar.YEAR)+
    		   " and Final Date: " + finalCalendar.get(Calendar.DAY_OF_MONTH)+
    		   "/"+finalCalendar.get(Calendar.MONTH)+"/"+finalCalendar.get(Calendar.YEAR));
       
       int round = 0;

       // se a data inicio for no primeiro dia da semana (Domingo), adiciona
       // mais dois dias no arredondamento pois sera somado no final.
       if (initialCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
           round += 2;
       else
           // se a data inicio não for no primeiro dia da semana (sábado),
           // adiciona mais um dia no arredondamento.
           round += 1;
       
       //se a data fim for no ultimo dia da semana (sábado), adiciona mais
       // dois dias no arredondamento.
       if (finalCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
           round += 2;
       else
           // se a data fim não for no ultimo dia da semana (sábado), adiciona
           // mais um dia  no arredondamento pois sera somado no final.
           round += 1;
       
       int initialDateRound = Calendar.SATURDAY - initialCalendar.get(Calendar.DAY_OF_WEEK) ;
       int finalDateRound = finalCalendar.get(Calendar.DAY_OF_WEEK);
       
       // Adiciona os dias da semana que rtestam para passar para proxima semana
       // retirando assim os dias que não estão completos. 
       //Proporcionando uma divisão exata por 7 
       initialCalendar.add(Calendar.DAY_OF_WEEK, initialDateRound + 1 );
       finalCalendar.add(Calendar.DAY_OF_WEEK, - finalDateRound);
       
       //calcula aquantidade de dias entre o periodo divide por 7 e multiplica por 2
       int dif = (int) (((finalCalendar.getTime().getTime() - initialCalendar.getTime().getTime()) / (24 * 60 * 60 * 1000)) + 1) / 7 * 2;

       dif += round;

       log.debug("Total days in weekend: " + dif);       

       log.debug("######## Finishing weekendDaysBetweenDates");        
       return dif;
   }

	
	/**
	 * @since jCompany 3.0
	 * Retorna true se uma data for fim de semana, conforme criterio passado
	 * @param holidayDate Dia a verificar
	 * @param saturdaySundayOption "SOMENTE_SABADO", "SOMENTE_DOMINGO" ou "SABADO_E_DOMINGO" (default)
	 * @return boolean true se data for fim de semana e false caso contrario.
	 */
	public boolean isWeekend(Date holidayDate,String saturdaySundayOption) {
		
		log.debug("######## Entered in isWeekend");

		int diaSemana = holidayDate.getDay();
		
		if ((saturdaySundayOption.equalsIgnoreCase("SABADO_E_DOMINGO") && (diaSemana == 0 || diaSemana == 6)) ||
			(saturdaySundayOption.equalsIgnoreCase("SABADO") && (diaSemana == 6)) ||
			(saturdaySundayOption.equalsIgnoreCase("DOMINGO") && (diaSemana == 0)))
			return true;
		else
			return false;
	}
	
	/**
	 * Calcula no. de horas úteis em um período, descontando-se feriados e fim-de-semana
	 * @param initialDate data de Início
	 * @param finalDate data de Fim
	 * @param holidays Mapa de Feriados
	 * @param hhBusinessByDay HH úteis por dia
	 * @param saturdaySundayOption fixo nesta varsão "SABADO_E_DOMIGO".
	 * @return numero de horas uteis no período
	 */
	public float calculateHHBusinessBetweenDates(Date initialDate, Date finalDate, Map holidays,
			long hhBusinessByDay,String saturdaySundayOption) throws PlcException {

		try {
			
			if (log.isDebugEnabled())
				log.debug("######## Entered in calculateHHBusinessBetweenDates to initialDate ="+initialDate.toLocaleString()+" and "+
						" finalDate "+finalDate.toLocaleString());
	
	  		float hhRest = hhRestBetweenDates(initialDate,finalDate,hhBusinessByDay,holidays,"SABADO_E_DOMINGO");
	  		
	  		float businessDay = daysBetweenDates(initialDate,finalDate) + 1;
	
	  		float hhBusiness = (businessDay * hhBusinessByDay) - hhRest;
	  		
	  		// Não pode retornar negativo. Neste caso evita
			return hhBusiness;
		
		} catch (Exception e) {
			throw new PlcException("jcompany.error.generic", new Object[] {
					"calculateHHBusinessBetweenDates", e }, e,log);
		}
		
  		
	}
	/**
	 * @since jCompany 3.0
	 * Converte data utilizando formato informado
	 * @param date Data em String
	 * @param format Format no padrão java (ex: dd/MM/yyyy HH:mm)
	 * @return java.util.Date a partir do String ou null se entrada for nula. Dispara exceção 
	 */
	public Date convertDateToString(String date, String format) throws PlcException {
		
		log.debug("######## Entered in convertDateToString");
		
		if (date == null || date.trim().equals(""))
			return null;
		
		SimpleDateFormat sd = new SimpleDateFormat();

		try {
			
			sd.applyPattern(format);
			Date d = sd.parse(date);
			return d;
			
		} catch (Exception e) {
			throw new PlcException("jcompany.date.invalid", new Object[] { date,format });
		}
		
	}

	/**
	 * @since jCompany 3.0
	 * Recupera lista de datas atraves de uma data inicial e fianal
	 * @param initialDate Date
	 * @param finalDate Date
	 * @return Lista contendo objetos Date.
	 */
	public List listOfDates(Date initialDate, Date finalDate){

		if(initialDate == null || finalDate == null)
			return new ArrayList();

		List listReturn = new ArrayList();
		Map list = new HashMap();
		PlcDateHelper plcDateUtil = PlcDateHelper.getInstance();

		Float days = new Float(plcDateUtil.daysBetweenDates(initialDate, finalDate));

		Calendar cal = Calendar.getInstance();

		cal.setTime(initialDate);
		list.put(cal.getTime(), cal.getTime());
		for (int i = 1; days.intValue() > i - 1; i++){
			cal.add(Calendar.DAY_OF_YEAR, 1);
			list.put(cal.getTime(), cal.getTime());
		}
		listReturn = new ArrayList(list.values());
		Collections.sort(listReturn);
		return listReturn;
	}
	
	public Date convertStringToDate(String date)throws PlcException{
		DateFormat dataFormat = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pt" ,"BR"));
		try{
			return dataFormat.parse(date);
		} catch (Exception e) {
			throw new PlcException("jcompany.date.invalid", new Object[] { date});
		}
	}
	
	/**
	 * 
	 * @param date
	 * @param mask
	 * @return
	 * @throws PlcException
	 */
	public Date convertStringToDateWirhMask(String date, String mask)throws PlcException{
		SimpleDateFormat mascaraSDF = new SimpleDateFormat(mask);
		try{
			return mascaraSDF.parse(date);
		} catch (Exception e) {
			throw new PlcException("jcompany.date.invalid", new Object[] { date + " " + mask});
		}
	}


}