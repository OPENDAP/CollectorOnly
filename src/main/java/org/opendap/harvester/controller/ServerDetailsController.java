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

package org.opendap.harvester.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogDataDto;
import org.opendap.harvester.entity.dto.LogLineDto;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameHostsModel;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameModel;
import org.opendap.harvester.service.HyraxInstanceService;
import org.opendap.harvester.service.LogCollectorService;
import org.opendap.harvester.service.LogLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
//@RequestMapping("/server")
public class ServerDetailsController{
	private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
	
	@Autowired
	private HyraxInstanceService hyraxInstanceService;
	
	@Autowired
	private LogLineService logLineService;
	
	@Autowired
	private LogCollectorService logCollectorService;
	
	/**
	 * 
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(path="/server", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView serverDetails(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("serverDetails");
		
		mav.addObject("serverId", register.getServerUUID());
		mav.addObject("serverUrl", register.getName());
		mav.addObject("reporterUrl", register.getReporterUrl());
		mav.addObject("ping", createInterval(register.getPing()));
		mav.addObject("log", register.getLog());
		mav.addObject("version", register.getVersionNumber());
		mav.addObject("registrationTime", register.getRegistrationTime());
		mav.addObject("lastAccessTime", register.getLastAccessTime());
		mav.addObject("active", register.getActive());
		mav.addObject("name", register.getName());
		mav.addObject("number", logLineService.findNumberLogLines(register.getId()));
		mav.addObject("isRunning", register.getAccessible());
		mav.addObject("lastSuccessfulPull", register.getLastSuccessfulPull());
		mav.addObject("errorCount", register.getErrorCount());
		//mav.addObject("items", register.getPreviousErrorCount().toArray());
		
		return mav;
	}//end serverDetails()
	
	private String createInterval(long ping) {
		String s = "";
		
		long mins, hours, days;
		mins = ping/60;
		hours = mins/60;
		days = hours/24;
		
		if(days <= 0) {
			
			if (hours <= 0) {
				s = ping +" - "+ mins +"m";
			}// *m
			else if ((mins % 60) == 0) {
				s = ping +" - "+ hours +"h";
			}// *h
			else {
				mins = mins % 60;
				s = ping +" - "+ hours +"h "+ mins +"m";
			}// *h *m
		}
		else {
			mins = mins % 60;
			hours = hours % 24;
			if ((hours == 0)&&(mins == 0)) {
				s = ping +" - "+ days +"d";
			}// *d
			else if(hours == 0) {
				s = ping +" - "+ days +"d "+ mins +"m";
			}// *d *m
			else if(mins == 0) {
				s = ping +" - "+ days +"d "+ hours +"h";
			}// *d *h
			else {
				s = ping +" - "+ days +"d "+ hours +"h "+ mins +"m";
			}// *d *h *m
		}
		
		return s;
	}//end createInterval()
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/months", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showMonths(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		//log.info("showMonths() entered ...");
		HyraxInstance serverInstance = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("serverMonths");
		
		//////////////////////////////////////////////////////////////////////////
		// COMBING DATA FOR ANALYSIS
		/////////////////////////////////////////////
		
		List<LogLineDto> list = logLineService.findLogLines((serverInstance.getId()));
		List<String> months = new ArrayList<String>();
		List<Integer> numHosts = new ArrayList<Integer>();
		List<Long> dataSize = new ArrayList<Long>();
		int index = 0;
		
		long starttime = System.nanoTime();
		for (LogLineDto lld : list){
			//log.info(lld.toString());
			String mmYYYY = convertDateToString(lld.getValues().get("localDateTime"));
			//log.info(mmYYYY);
			if (!months.contains(mmYYYY)){
				months.add(mmYYYY);
				index = months.indexOf(mmYYYY);
				numHosts.add(1);
				
				long sizeInt = parseSize(lld);

				dataSize.add(sizeInt);
			}
			else {
				numHosts.set(index, numHosts.get(index) + 1);

				long sizeInt = parseSize(lld);

				dataSize.set(index, dataSize.get(index) + sizeInt);
			}
		}
		long endtime = System.nanoTime();
		
		//////////////////////////////////////////////////////////////////////////
		// BUILDING THE ITEMS FOR THE JSP PAGE
		/////////////////////////////////////////////
		
		//log.info("building table items");
		String[][] tableItems = new String[months.size()][3];
		
		index = 0;
		for (String m : months) {
			tableItems[index][0] = m;
			tableItems[index][1] = numHosts.get(index).toString();
			tableItems[index][2] = readibleSize(dataSize.get(index), true);
			index++;
		}
		
		long duration = endtime - starttime;
		String time =  parseTime(duration);
		
		mav.addObject("serverName", serverInstance.getName());
		mav.addObject("tableItems", tableItems);
		mav.addObject("time", time);
		
		//log.info("returning ...");
		return mav;
	}// showMonths()
	
	private String parseTime(long duration) {
		String time;
		
		if(duration > 1000000000) {
			time = (duration / 1000000000) + " seconds";
		}
		else if (duration > 1000000) {
			time = (duration / 1000000) + " milliseconds";
		}
		else {
			time = duration + " nanoseconds";
		}
		return time;
	}
	
	private String readibleSize(long size, boolean expanded) {
		long kb = size / 1024;
		long mb = kb / 1024;
		long gb = mb / 1024;
		
		if(size < 1024) {
			return size + " bytes";
		}
		else if (kb < 1024) {
			
			return kb + ((expanded) ? " kilobytes" : " KBs");
		}
		else if (mb < 1024) {
			
			return mb + ((expanded) ? " megabytes" : " MBs");
		}
		else {
			return gb + ((expanded) ? " gigabyte" : " GBs");
		}
	}
	
	private Long parseSize(LogLineDto lld) {
		String sizeString = lld.getValues().get("size");
		//log.info("SET - before parse: "+sizeString);
		long sizeInt;
		
		if(sizeString.equals("")) {
			sizeInt = 0;
		}
		else if (sizeString.contains("bytes")) {
			sizeInt = Integer.parseInt(sizeString.substring(0, sizeString.length() - 5).trim());
		}
		else {
			sizeInt = Integer.parseInt(sizeString.trim());
		}
		return sizeInt;
	}
	
	private String convertDateToString(String rubbish) {
		
		SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();
		String readible = determineMonth(month) +" "+ year;
		
		return readible;
		
	}//convertDateToString()
	
	private int convertDateToMonthLength(String rubbish) {
		
		SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.lengthOfMonth();
		
		return month;
		
	}//convertDateToString()
	
	private int convertDateToDayInt(String rubbish) {
		
		SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int dayNumber = localDate.getDayOfMonth();
		
		return dayNumber;
		
	}//convertDateToString()
	
	private String determineMonth(int num) {
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
	
	private int determineDayofWeek(String rubbish) {
		SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		DayOfWeek dayNumber = localDate.getDayOfWeek();
		
		return dayNumber.getValue();
	}
	
	@RequestMapping(path="/hosts", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showHosts(@Valid @ModelAttribute HyraxInstanceNameHostsModel hyraxInstanceNameHostsModel) {
		
		//log.info("entering showHosts()");
		HyraxInstance serverInstance = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameHostsModel.getHyraxInstanceName());
		String month = hyraxInstanceNameHostsModel.getMonth();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("serverHosts");
		
		List<LogLineDto> list = logLineService.findLogLines(serverInstance.getId(), month);
		List<String> names = new ArrayList<String>();
		List<Integer> hostCount = new ArrayList<Integer>();
		List<Long> dataSize = new ArrayList<Long>();
		List<Integer> dailyCount = new ArrayList<Integer>();
		Long dataTotal = (long) 0;
		int accessTotal = 0;
		
		int dayCount = convertDateToMonthLength(list.get(0).getValues().get("localDateTime"));
		int startDay = determineDayofWeek(list.get(0).getValues().get("localDateTime"));
		
		//log.info("showHosts() | making the lists");
		for (LogLineDto lld : list){
			String name = lld.getValues().get("host");
			if (names.contains(name)) { //if user agent has already been added
				//increase the count for the user agent
				hostCount.set(names.indexOf(name), hostCount.get(names.indexOf(name)) + 1);
				//increase the total access count 
				accessTotal += 1;
				
				//retrieve the size from the logline
				long sizeLong = parseSize(lld);
				//add size to total size for user agent
				dataSize.set(names.indexOf(name), dataSize.get(names.indexOf(name)) + sizeLong);
				//add to total size for all agents
				dataTotal += sizeLong;
			}
			else { //if user agent hasn't been added to the list yet
				names.add(name); //add the user agent
				hostCount.add(1); //begin access count record for user agent
				accessTotal += 1; //add to total count for all agents
				
				long sizeLong = parseSize(lld); //get data size 
				dataSize.add(sizeLong); //begin data size record for user agent
				dataTotal += sizeLong; //add to total data size for all user agents
				
			}
			
			int day = convertDateToDayInt(lld.getValues().get("localDateTime"));
			if(dailyCount.size() < day) {
				if(dailyCount.size() == day - 1) {
					dailyCount.add(1);
					//log.info("next day init : " + dailyCount.size());
				}
				else {
					for (int x = dailyCount.size(); x < day-1; x++) {
						dailyCount.add(0);
						//log.info("loop day init : " + dailyCount.size());
					}
					dailyCount.add(1);
					//log.info("end loop day init : " + dailyCount.size());
				}
				
			}
			else {
				dailyCount.set(day-1, dailyCount.get(day-1) + 1);
				//log.info("day addition : " + day);
			}
			
			
		}
		
		//log.info("showHosts() | making the 2d matrix");
		String[][] hostItems = new String[names.size()][4];
		
		int index = 0;
		for (String n : names) { // data in the table
			hostItems[index][0] = n;
			hostItems[index][1] = hostCount.get(index).toString();
			hostItems[index][2] = n.replace("+", "%2B");
			hostItems[index][3] = readibleSize(dataSize.get(names.indexOf(n)),false);
			
			index++;
		}
		
		// static data
		String[] totalItems = new String[6];
		totalItems[0] = names.size() + " unquie user agents"; //total number of user agents
		totalItems[1] = ""+accessTotal; // total number of accesses
		totalItems[2] = readibleSize(dataTotal, false); //total data downloaded
		totalItems[4] = ""+dayCount;
		
		int max = 0; //used to hold the max value of the daily count
		
		//convert the integer list to an string array 
		index = 0;
		String[] graph1Data = new String[dailyCount.size()];
		for(int i : dailyCount) {
			//int a = dailyCount.get(index);
			graph1Data[index] = i + "";
			if (i > max) {max = i;}
			index++;
		}
		
		// find an appropriate max value for the graph
		if (max < 100) {
			int mult = max / 10;
			max = 10 * (mult + 1);
		}
		else if (max < 500) {
			int mult = max / 50;
			max = 50 * (mult + 1);
		}
		else if (max < 10000) {
			int mult = max / 500;
			max = 500 * (mult + 1);
		}
		else {
			int mult = max / 1000;
			max = 1000 * (mult + 1);
		}
		
		//ready to pass to the .jsp page
		totalItems[3] = max + "";
		totalItems[5] = startDay + "";
		
		//log.info("showHosts() | returning ...");
		mav.addObject("serverName", serverInstance.getName());
		mav.addObject("month",month);
		mav.addObject("hostItems", hostItems);
		mav.addObject("totals", totalItems);
		mav.addObject("graph1Data", graph1Data);
		
		
		return mav;
	}//showHosts()
	
	@RequestMapping(path="/hostDetails", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showHostDetails(@Valid @ModelAttribute HyraxInstanceNameHostsModel hyraxInstanceNameHostsModel) {
		
		//log.info("\t /!\\ Enter showHostDetails() ... /!\\");
		HyraxInstance serverInstance = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameHostsModel.getHyraxInstanceName());
		String month = hyraxInstanceNameHostsModel.getMonth();
		String host = hyraxInstanceNameHostsModel.getHost();
		//log.info("\t hostName: '"+host+"'");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("hostDetails");
		
		List<LogLineDto> list = logLineService.findLogLines(serverInstance.getId(), month);
		List<String> resourceIds = new ArrayList<String>();
		List<String> queries = new ArrayList<String>();
		List<String> sizes = new ArrayList<String>();
		List<String> durations = new ArrayList<String>();
		
		//log.info("\t showHostDetails() - entering foreach loop");
		for (LogLineDto lld : list) {			
			if (lld.getValues().get("host").trim().equals(host)) {
				resourceIds.add(lld.getValues().get("resourceId"));
				queries.add(lld.getValues().get("query"));
				sizes.add(lld.getValues().get("size"));
				durations.add(lld.getValues().get("duration"));
			}//end if
		}//end foreach
		
		String[][] tableItems = new String[sizes.size()][4];
		int index = 0;
		
		for(String s : resourceIds) {
			tableItems[index][0] = s;
			tableItems[index][1] = queries.get(index);
			tableItems[index][2] = sizes.get(index);
			tableItems[index][3] = durations.get(index);
			index++;
		}//end foreach loop
		
		mav.addObject("serverName", serverInstance.getName());
		mav.addObject("month",month);
		mav.addObject("hostname", host);
		mav.addObject("tableItems", tableItems);
		
		return mav;
	}
	
	/**
	 * 
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(path="/remove", method = RequestMethod.GET)
	@ResponseBody
 	public ModelAndView removeReporter(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		//log.info("/remove.1/5) removeReporter() entry, finding instance ...");
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		//log.info("/remove.2/5) found instance, retrieving id ...");
		String hyraxInstanceId = register.getId();
		//log.info("/remove.3/5) id : "+ hyraxInstanceId +" - calling removeLogLines() ...");
		logLineService.removeLogLines(hyraxInstanceId);
		
		//List<LogLineDto> list = logLineService.findLogLines(hyraxInstanceNameModel.getHyraxInstanceName());
		//String name = list.get(0).getValues().get("host");
		
		
		//log.info("/remove.4/5) log lines removed, calling removeHyraxInstance() ...");
		hyraxInstanceService.removeHyraxInstance(hyraxInstanceId);
		//log.info("/remove.5/5) hyrax removed, returning <<");
		return new ModelAndView("redirect:/opendap");
	}//end removeReporter()
	
	/**
	 * 
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(path="/remove", method = RequestMethod.POST)
	@ResponseBody
 	public String removeReporterPOST(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		//log.info("/remove.1/5) removeReporter() entry, finding instance ...");
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		//log.info("/remove.2/5) found instance, retrieving id ...");
		String hyraxInstanceId = register.getId();
		//log.info("/remove.3/5) id : "+ hyraxInstanceId +" - calling removeLogLines() ...");
		logLineService.removeLogLines(hyraxInstanceId);
		
		//List<LogLineDto> list = logLineService.findLogLines(hyraxInstanceNameModel.getHyraxInstanceName());
		//String name = list.get(0).getValues().get("host");
		
		
		//log.info("/remove.4/5) log lines removed, calling removeHyraxInstance() ...");
		hyraxInstanceService.removeHyraxInstance(hyraxInstanceId);
		//log.info("/remove.5/5) hyrax removed, returning <<");
		return "Type : OK, Status : 200";
	}//end removeReporter()

	/**
	 * 
	 */
	@RequestMapping(path="/repull", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView repullReporterLogs(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		String hyraxInstanceId = register.getId();
		ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
		
		logLineService.removeLogLines(hyraxInstanceId);
		LogDataDto logDataDto = logCollectorService.collectAllLogs(register);
		hyraxInstanceService.updateLastAccessTime(register, utc.toLocalDateTime());
		hyraxInstanceService.updateLastSuccessPullTime(register, utc.toLocalDateTime());
        logLineService.addLogLines(hyraxInstanceId, logDataDto.getLines());
        
        return new ModelAndView("redirect:/server?hyraxInstanceName="+hyraxInstanceNameModel.getHyraxInstanceName());
	}//end repullReporterLogs()
	
	/**
	 * 
	 */
	@RequestMapping(path="/repull", method = RequestMethod.POST)
	@ResponseBody
	public String repullReporterLogsPOST(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		//log.info("repullReporterLogsPOST() entered ...");
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		//log.info("repullReporterLogsPOST() instance found ...");
		String hyraxInstanceId = register.getId();
		//log.info("repullReporterLogsPOST() id found ...");
		ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
		//log.info("repullReporterLogsPOST() timezone found ...");
		
		logLineService.removeLogLines(hyraxInstanceId);
		//log.info("repullReporterLogsPOST() log lines removed ...");
		LogDataDto logDataDto = logCollectorService.collectAllLogs(register);
		//log.info("repullReporterLogsPOST() collected new log lines ...");
		hyraxInstanceService.updateLastAccessTime(register, utc.toLocalDateTime());
		//log.info("repullReporterLogsPOST() updated last access time ...");
		hyraxInstanceService.updateLastSuccessPullTime(register, utc.toLocalDateTime());
		//log.info("repullReporterLogsPOST() updated last success pull time ...");
        logLineService.addLogLines(hyraxInstanceId, logDataDto.getLines());
        //log.info("repullReporterLogsPOST() added new log lines ...");
        
        //log.info("repullReporterLogsPOST() returning <<");
        return "Type : OK, Status : 200";
	}//end repullReporterLogs()
	
	@RequestMapping(path="/toggleActive", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toggleReporterActive(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		
		hyraxInstanceService.updateActiveStatus(register, !register.getActive());
		
		return new ModelAndView("redirect:/server?hyraxInstanceName="+hyraxInstanceNameModel.getHyraxInstanceName());
	}
	
}//end class ServerDetailsController