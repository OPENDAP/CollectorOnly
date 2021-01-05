/**
 Copyright (c) 2019 OPeNDAP, Inc.
 Please read the full copyright statement in the file LICENSE.

 Authors: 
	James Gallagher	 <jgallagher@opendap.org>
    Samuel Lloyd	 <slloyd@opendap.org>

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 You can contact OPeNDAP, Inc. at PO Box 112, Saunderstown, RI. 02874-0112.
*/

package org.opendap.harvester.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.service.DateTimeUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DateTimeUtilServiceImpl implements DateTimeUtilService {
	private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
	private boolean logOutput = false;
	private boolean verbose = false;
	
	private SimpleDateFormat formatter6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");

	/**
	 * 
	 */
	@Override
	public int convertDateToMonthLength(String rubbish) {
		
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			e.printStackTrace();
		}		

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.lengthOfMonth();
		
		return month;
	} //convertDateToMonthlength()

	/**
	 * 
	 */
	@Override
	public String convertDateToString(String rubbish) {
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			e.printStackTrace();
		}		

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();
		String readible = determineMonth(month) +" "+ year;
		
		return readible;
	} //convertDatetoString()

	/**
	 * 
	 */
	@Override
	public int convertDateToDayInt(String rubbish) {
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int dayNumber = localDate.getDayOfMonth();
		
		return dayNumber;
	} //convertDatetoDayInt()

	/**
	 * 
	 */
	@Override
	public int determineDayOfWeek(String rubbish) {
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		DayOfWeek dayNumber = localDate.getDayOfWeek();
		
		return dayNumber.getValue();
	} // determineDayOfWeek()

	/**
	 * 
	 */
	@Override
	public String determineMonth(int num) {
			switch (num) {
				case 1:
					return "January";
				case 2:
					return "Febuary";
				case 3:
					return "March";
				case 4:
					return "April";
				case 5:
					return "May";
				case 6:
					return "June";
				case 7:
					return "July";
				case 8:
					return "August";
				case 9:
					return "September";
				case 10:
					return "October";
				case 11:
					return "November";
				case 12:
					return "December";
				default:
					return "NoMonth!!!WeAreAllGonnaDie!!!!";
			}
	}// end determineMonth()
	
	public String determineEndMonth(String startMonth) {
		
		String month = startMonth.substring(0, startMonth.length() - 4).trim();
		int end;
		int year = Integer.parseInt(startMonth.substring(startMonth.length() - 4));
		
		switch (month){
		case "January":
			end = 2;
			break;
		case "Febuary":
			end = 3;
			break;
		case "March":
			end = 4;
			break;
		case "April":
			end = 5;
			break;
		case "May":
			end = 6;
			break;
		case "June":
			end = 7;
			break;
		case "July":
			end = 8;
			break;
		case "August":
			end = 9;
			break;
		case "September":
			end = 10;
			break;
		case "October":
			end = 11;
			break;
		case "November":
			end = 12;
			break;
		case "December":
			end = 1;
			year++;
			break;
		default:
			end = 0;
			break;
		}
		
		String endMonth = determineMonth(end);

		return endMonth +" "+ year;
	}
	
	public String convertDatetoReadible(LocalDateTime localDateTime) {
		if(logOutput) {log.info("convertDatetoReadible() fct start ... ");}
		
		String month = determineMonth(localDateTime.getMonthValue());
		if(logOutput && verbose) {log.info("convertDatetoReadible() 	- month ");}
		int day = localDateTime.getDayOfMonth();
		if(logOutput && verbose) {log.info("convertDatetoReadible() 	- day ");}
		int year = localDateTime.getYear();
		if(logOutput && verbose) {log.info("convertDatetoReadible() 	- year ");}
		int hour = localDateTime.getHour();
		if(logOutput && verbose) {log.info("convertDatetoReadible() 	- hour ");}
		int mins = localDateTime.getMinute();
		if(logOutput && verbose) {log.info("convertDatetoReadible() 	- minute ");}
		
		String readibleTime;
		if (mins < 10) {
			readibleTime = month +" "+ day +" "+ year + " @ "+ hour +":0"+ mins;
		}
		else {
			readibleTime = month +" "+ day +" "+ year + " @ "+ hour +":"+ mins;
		}
		if(logOutput) {log.info("convertDatetoReadible() readible time : "+readibleTime);}
		
		if(logOutput) {log.info("convertDatetoReadible() returning <<<");}
		return readibleTime;
	}
	
	public String determineInterval(LocalDateTime ldt1, LocalDateTime ldt2) {
		if(logOutput) {log.info("determineInterval() fct start ... ");}
		if(logOutput) {log.info("determineInterval() time 1 : "+ldt1.toString()+" | time 2 : "+ldt2.toString());}
		Duration duration = Duration.between(ldt1, ldt2);
		long secs = duration.getSeconds();
		if(logOutput && verbose) {log.info("determineInterval() seconds : "+ secs);}
		String readibleInterval = "";
		
		long mins, hours, days;
		mins = secs/60;
		if(logOutput && verbose) {log.info("determineInterval() minutes : "+ mins);}
		hours = mins/60;
		if(logOutput && verbose) {log.info("determineInterval() hours : "+ hours);}
		days = hours/24;
		if(logOutput && verbose) {log.info("determineInterval() days : "+ days);}
		
		if(days <= 0) {
			if (hours <= 0) {
				readibleInterval = mins +" minute(s)";
			}// *m
			else if ((mins % 60) == 0) {
				readibleInterval = hours +" hour(s)";
			}// *h
			else {
				mins = mins % 60;
				readibleInterval = hours +" hour(s) "+ mins +" minute(s)";
			}// *h *m
		}
		else {
			mins = mins % 60;
			hours = hours % 24;
			if ((hours == 0)&&(mins == 0)) {
				readibleInterval = days +" day(s)";
			}// *d
			else if(hours == 0) {
				readibleInterval = days +" day(s) "+ mins +" minute(s)";
			}// *d *m
			else if(mins == 0) {
				readibleInterval = days +" day(s) "+ hours +" hour(s)";
			}// *d *h
			else {
				readibleInterval = days +" day(s) "+ hours +" hour(s) "+ mins +" minute(s)";
			}// *d *h *m
		}
		if(logOutput) {log.info("determineInterval() readible interval : "+ readibleInterval);}

		if(logOutput) {log.info("determineInterval() returning <<<");}
		return readibleInterval;
	}
	
}