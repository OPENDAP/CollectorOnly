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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.DownTimes;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.document.MonthTotals;
import org.opendap.harvester.entity.dto.LogDataDto;
import org.opendap.harvester.entity.dto.LogLineDto;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameHostsModel;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameModel;
import org.opendap.harvester.service.DateTimeUtilService;
import org.opendap.harvester.service.HyraxInstanceService;
import org.opendap.harvester.service.LogCollectorService;
import org.opendap.harvester.service.LogLineParsingUtilService;
import org.opendap.harvester.service.LogLineService;
import org.opendap.harvester.service.MonthTotalsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private boolean logOutput = false;
	private boolean verbose = false;
	
	@Value("${harvester.version}")
    private String harvesterVersion;
	
	@Autowired
	private HyraxInstanceService hyraxInstanceService;
	
	@Autowired
	private LogLineService logLineService;

	@Autowired
	private MonthTotalsService monthTotalsService;
	
	@Autowired
	private LogCollectorService logCollectorService;

	@Autowired
	private LogLineParsingUtilService logLineParsingUtilService;

	@Autowired
	private DateTimeUtilService dateTimeUtilService;
	
	/**
	 * 
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(path="/server", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView serverDetails(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		if(logOutput) {log.info("serverDetails() fct start ... ");}
		long starttime = System.nanoTime();
		
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		if(logOutput) {log.info("serverDetails() 	found hyraxinstance");}
		if(logOutput) {log.info("serverDetails() 	creating model and view ...");}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("serverDetails");
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// SERVER INFORMATION
		/////////////////////////////////////////
		
		if(logOutput) {log.info("//////////////////////////////////////////////////");}
		if(logOutput) {log.info("serverDetails() 		setting server info ...");}
		mav.addObject("serverUrl", register.getName());
		mav.addObject("serverVer", register.getVersionNumber());
		mav.addObject("serverRun", (register.getServerRunning() == null) ? "server status currently unknown" : register.getServerRunning());
		mav.addObject("serverLAT", (register.getServerLastAccessTime() == null) ? "server last access time unknown" : dateTimeUtilService.convertDatetoReadible(register.getServerLastAccessTime()));
		mav.addObject("serverLET", (register.getServerLastErrorTime() == null) ? "server last error time unknown" : dateTimeUtilService.convertDatetoReadible(register.getServerLastErrorTime()));
		if(logOutput) {log.info("serverDetails() 		... server info set");}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// REPORTER INFORMATION
		/////////////////////////////////////////
		if(logOutput) {log.info("//////////////////////////////////////////////////");}
		if(logOutput) {log.info("serverDetails() 		setting reporter info ...");}
		mav.addObject("serverId", register.getServerUUID());
		mav.addObject("reporterUrl", register.getReporterUrl());
		mav.addObject("reporterVer", (register.getReporterVersionNumber() == null) ? "reporter version unknown" : register.getReporterVersionNumber());
		mav.addObject("ping", logLineParsingUtilService.createInterval(register.getPing()));
		mav.addObject("log", register.getLog());
		mav.addObject("number", logLineService.findNumberLogLines(register.getId()));
		register.setReporterRunning(register.getAccessible());
		mav.addObject("reporterRun", register.getReporterRunning());
		mav.addObject("reporterRT", dateTimeUtilService.convertDatetoReadible(register.getRegistrationTime()));
		mav.addObject("reporterLAT", (register.getLastAccessTime() == null) ? "reporter last access time unknown" : dateTimeUtilService.convertDatetoReadible(register.getLastAccessTime()));
		mav.addObject("reporterLSP", (register.getLastSuccessfulPull() == null) ? "reporter last successful pull time unknown" : dateTimeUtilService.convertDatetoReadible(register.getLastSuccessfulPull()));
		mav.addObject("reporterLET", (register.getReporterLastErrorTime() == null) ? "reporter last error time unknown" : dateTimeUtilService.convertDatetoReadible(register.getReporterLastErrorTime()));
		mav.addObject("active", register.getActive());
		
		mav.addObject("name", register.getName());
		if(logOutput) {log.info("serverDetails() 		... reporter info set");}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// SERVER HISTORY
		/////////////////////////////////////////
		
		if(logOutput) {log.info("//////////////////////////////////////////////////");}
		if(logOutput) {log.info("serverDetails() 		setting server history info ...");}
		if(register.getServerDownTimes() == null) {
			register.initServerDownTime();
		}
		
		int size = (register.getServerDownTimes().size() < 5) ? register.getServerDownTimes().size() : 5;
		String[][] serverHistory = new String[size][3];
		if(logOutput) {log.info("serverDetails() 			- serverhistory[][] created");}
		
		List<DownTimes> dts = null;
		if (register.getServerDownTimes().size() < 5) {
			dts = register.getServerDownTimes();
		}
		if (register.getServerDownTimes().size() >= 5) {
			dts = register.getServerDownTimes().subList(register.getServerDownTimes().size() - 5, register.getServerDownTimes().size());
		}
		if(logOutput) {log.info("serverDetails() 			- list<DownTimes> retrieved");}
		
		int index = 0;
		for (DownTimes dt : dts) {
			if(logOutput) {log.info("serverDetails() 			- dates : "+dt.getStart()+" | "+dt.getEnd());}
			serverHistory[index][0] = (dt.getStart() == null) ? "start date not known" : dateTimeUtilService.convertDatetoReadible(dt.getStart());
			serverHistory[index][1] = (dt.getEnd() == null) ? "end date not known" : dateTimeUtilService.convertDatetoReadible(dt.getEnd());
			
			if(logOutput) {log.info("serverDetails() 			- generating interval ...");}
			String interval = "Interval Unknown";
			if ((dt.getStart() != null) && (dt.getEnd() != null)) {
				if(logOutput) {log.info("serverDetails() 				- both times present");}
				interval = dateTimeUtilService.determineInterval(dt.getStart(), dt.getEnd());
			}
			else if (dt.getStart() != null && dt.getEnd() == null && index == dts.size() - 1) {
				if(logOutput) {log.info("serverDetails() 				- start time present, end time null, last entry");}
				LocalDateTime ldt = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
				if(logOutput) {log.info("serverDetails() 			- time 1 :"+dt.getStart() + " | time 2 : "+ ldt);}
				interval = dateTimeUtilService.determineInterval(dt.getStart(), ldt);
			}
			if(logOutput) {log.info("serverDetails() 			- ... interval generated : "+ interval);}
			serverHistory[index][2] = interval;
			
			index++;
		}
		if(logOutput) {log.info("serverDetails() 		... server history info set");}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// REPORTER HISTORY
		/////////////////////////////////////////
		
		if(logOutput) {log.info("//////////////////////////////////////////////////");}
		if(logOutput) {log.info("serverDetails() 		setting reporter history info ...");}
		if(register.getReporterDownTimes() == null) {
			register.initReporterDownTime();
		}
		
		size = (register.getReporterDownTimes().size() < 5) ? register.getReporterDownTimes().size() : 5;
		String[][] reporterHistory = new String[size][3];
		
		if (register.getReporterDownTimes().size() < 5) {
			dts = register.getReporterDownTimes();
		}
		if (register.getReporterDownTimes().size() >= 5) {
			dts = register.getReporterDownTimes().subList(register.getReporterDownTimes().size() - 5, register.getReporterDownTimes().size());
		}
		
		index = 0;
		for (DownTimes dt : dts) {
			
			reporterHistory[index][0] = (dt.getStart() == null) ? "start date not known" : dateTimeUtilService.convertDatetoReadible(dt.getStart());
			reporterHistory[index][1] = (dt.getEnd() == null) ? "end date not known" : dateTimeUtilService.convertDatetoReadible(dt.getEnd());
			
			String interval = "Interval Unknown";
			if ((dt.getStart() != null) && (dt.getEnd() != null)) {
				interval = dateTimeUtilService.determineInterval(dt.getStart(), dt.getEnd());
			}
			else if (dt.getStart() != null && dt.getEnd() == null && index == dts.size() - 1) {
				LocalDateTime ldt = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
				if(logOutput) {log.info("serverDetails() 			- time 1 :"+dt.getStart() + " | time 2 : "+ ldt);}
				interval = dateTimeUtilService.determineInterval(dt.getStart(), ldt);
			}
			reporterHistory[index][2] = interval;
			
			index++;
		}
		if(logOutput) {log.info("serverDetails() 		... reporter history info set");}
		
		mav.addObject("serverHistory", serverHistory);
		mav.addObject("reporterHistory", reporterHistory);
		if(logOutput) {log.info("serverDetails() 	... model and view created");}
		
		mav.addObject("version", harvesterVersion);
		
		long endtime = System.nanoTime();
		long duration = endtime - starttime;
		String time =  logLineParsingUtilService.parseTime(duration);
		mav.addObject("time", time);
		
		if(logOutput) {log.info("serverDetails() returning <<<");}
		return mav;
	}//end serverDetails()
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(path="/months", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showMonths(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		if(logOutput) {log.info("showMonths() entered ...");}
		long starttime = System.nanoTime();
		
		HyraxInstance serverInstance = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("serverMonths");
		
		//////////////////////////////////////////////////////////////////////////
		// RETRIEVING MONTHTOTALS
		/////////////////////////////////////////////
		
		if(logOutput) {log.info("retrieving monthTotals ...");}
		List<MonthTotals> monthTotals = monthTotalsService.findAllMonthTotals(serverInstance.getId());
		
		
		if(logOutput) {log.info(" ... retrieved monthTotals");}
		
		//////////////////////////////////////////////////////////////////////////
		// BUILDING THE ITEMS FOR THE JSP PAGE
		/////////////////////////////////////////////
		
		if(logOutput) {log.info("building table items");}
		String[][] tableItems = new String[monthTotals.size()][3];
		
		int index = 0;
		for (MonthTotals m : monthTotals) {
			tableItems[index][0] = m.getMonthId(); //
			tableItems[index][1] = m.getLogCount().toString();
			tableItems[index][2] = logLineParsingUtilService.readibleSize(m.getByteCount(), true);
			index++;
		}
		
		mav.addObject("serverName", serverInstance.getName());
		mav.addObject("tableItems", tableItems);
		mav.addObject("version", harvesterVersion);
		
		long endtime = System.nanoTime();
		long duration = endtime - starttime;
		String time =  logLineParsingUtilService.parseTime(duration);
		mav.addObject("time", time);
		
		if(logOutput) {log.info("returning ...");}
		return mav;
	}// showMonths()
	
	@RequestMapping(path="/hosts", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showHosts(@Valid @ModelAttribute HyraxInstanceNameHostsModel hyraxInstanceNameHostsModel) {
		if(logOutput) {log.info("entering showHosts()");}
		long starttime = System.nanoTime();
		
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
		
		int dayCount = dateTimeUtilService.convertDateToMonthLength(list.get(0).getValues().get("localDateTime"));
		int startDay = dateTimeUtilService.determineDayOfWeek(list.get(0).getValues().get("localDateTime"));
		
		///////////////////////////////////////////////////////////////////////////
		// CALCULATE LISTS FROM LOGS
		/////////////////////////////////////////
		
		if(logOutput) {log.info("showHosts() | making the lists");}
		for (LogLineDto lld : list){
			
			///////////////////////////////////////////////////////////////////////////
			// CALCULATE HOST DATA FOR TABLE
			
			String name = lld.getValues().get("host");
			if(logOutput &&  verbose) {log.info("showHosts() : log line host : " + name);}
			if (names.contains(name)) { //if user agent has already been added
				//increase the count for the user agent
				hostCount.set(names.indexOf(name), hostCount.get(names.indexOf(name)) + 1);
				//increase the total access count 
				accessTotal += 1;
				
				//retrieve the size from the logline
				long sizeLong = logLineParsingUtilService.parseSize(lld);
				//add size to total size for user agent
				dataSize.set(names.indexOf(name), dataSize.get(names.indexOf(name)) + sizeLong);
				//add to total size for all agents
				dataTotal += sizeLong;
			}
			else { //if user agent hasn't been added to the list yet
				names.add(name); //add the user agent
				hostCount.add(1); //begin access count record for user agent
				accessTotal += 1; //add to total count for all agents
				
				long sizeLong = logLineParsingUtilService.parseSize(lld); //get data size 
				dataSize.add(sizeLong); //begin data size record for user agent
				dataTotal += sizeLong; //add to total data size for all user agents
				
			}
			
			///////////////////////////////////////////////////////////////////////////
			// CALCULATES DAY ACCESS TOTALS FOR CHART
			
			int day = dateTimeUtilService.convertDateToDayInt(lld.getValues().get("localDateTime"));
			if(dailyCount.size() < day) {
				if(dailyCount.size() == day - 1) {
					dailyCount.add(1);
					if(logOutput && verbose) {log.info("next day init : " + dailyCount.size());}
				}
				else {
					for (int x = dailyCount.size(); x < day-1; x++) {
						dailyCount.add(0);
						if(logOutput && verbose) {log.info("loop day init : " + dailyCount.size());}
					}
					dailyCount.add(1);
					if(logOutput && verbose) {log.info("end loop day init : " + dailyCount.size());}
				}
				
			} //end if
			else {
				dailyCount.set(day-1, dailyCount.get(day-1) + 1);
				if(logOutput && verbose) {log.info("day addition : " + day);}
			}
			
		} //end for loop
		
		///////////////////////////////////////////////////////////////////////////
		// COMPILE TABLE DATA
		/////////////////////////////////////////
		
		if(logOutput) {log.info("showHosts() | making the 2d matrix");}
		String[][] hostItems = new String[names.size()][4];
		
		int index = 0;
		for (String n : names) { // data in the table
			hostItems[index][0] = n;
			hostItems[index][1] = hostCount.get(index).toString();
			hostItems[index][2] = n.replace("+", "%2B");
			hostItems[index][3] = logLineParsingUtilService.readibleSize(dataSize.get(names.indexOf(n)),false);
			
			index++;
		}
		
		// static data
		String[] totalItems = new String[6];
		totalItems[0] = names.size() + " unquie user agents"; //total number of user agents
		totalItems[1] = ""+accessTotal; // total number of accesses
		totalItems[2] = logLineParsingUtilService.readibleSize(dataTotal, false); //total data downloaded
		totalItems[4] = ""+dayCount;
		
		///////////////////////////////////////////////////////////////////////////
		// COMPILE GRAPH DATA
		/////////////////////////////////////////
		
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
		
		if(logOutput) {log.info("showHosts() | returning ...");}
		mav.addObject("serverName", serverInstance.getName());
		mav.addObject("month",month);
		mav.addObject("hostItems", hostItems);
		mav.addObject("totals", totalItems);
		mav.addObject("graph1Data", graph1Data);
		
		mav.addObject("version", harvesterVersion);
		
		long endtime = System.nanoTime();
		long duration = endtime - starttime;
		String time =  logLineParsingUtilService.parseTime(duration);
		mav.addObject("time", time);
		
		return mav;
	}//showHosts()
	
	@RequestMapping(path="/hostDetails", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showHostDetails(@Valid @ModelAttribute HyraxInstanceNameHostsModel hyraxInstanceNameHostsModel) {
		if(logOutput) {log.info("\t /!\\ Enter showHostDetails() ... /!\\");}
		long starttime = System.nanoTime();
		
		HyraxInstance serverInstance = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameHostsModel.getHyraxInstanceName());
		String month = hyraxInstanceNameHostsModel.getMonth();
		String host = hyraxInstanceNameHostsModel.getHost();
		if(logOutput) {log.info("\t hostName: '"+host+"'");}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("hostDetails");
		
		List<LogLineDto> list = logLineService.findLogLines(serverInstance.getId(), month);
		List<String> resourceIds = new ArrayList<String>();
		List<String> queries = new ArrayList<String>();
		List<String> sizes = new ArrayList<String>();
		List<String> durations = new ArrayList<String>();
		
		///////////////////////////////////////////////////////////////////////////
		// CALCULATE LISTS FROM LOGS
		/////////////////////////////////////////
		
		if(logOutput) {log.info("\t showHostDetails() - entering foreach loop");}
		for (LogLineDto lld : list) {			
			if (lld.getValues().get("host").trim().equals(host)) {
				resourceIds.add(lld.getValues().get("resourceId"));
				queries.add(lld.getValues().get("query"));
				sizes.add(lld.getValues().get("size"));
				durations.add(lld.getValues().get("duration"));
			}//end if
		}//end foreach
		
		///////////////////////////////////////////////////////////////////////////
		// COMPILE DATA FOR TABLE
		/////////////////////////////////////////
		
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
		
		mav.addObject("version", harvesterVersion);
		
		long endtime = System.nanoTime();
		long duration = endtime - starttime;
		String time =  logLineParsingUtilService.parseTime(duration);
		mav.addObject("time", time);
		
		if(logOutput) {log.info("\t showHostDetails() - returning << ");}
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
		if(logOutput) {log.info("/remove.1/5) removeReporter() entry, finding instance ...");}
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		
		if(logOutput) {log.info("/remove.2/5) found instance, retrieving id ...");}
		String hyraxInstanceId = register.getId();
		
		if(logOutput) {log.info("/remove.3/5) id : "+ hyraxInstanceId +" - calling removeLogLines() ...");}
		logLineService.removeLogLines(hyraxInstanceId);
		
		if(logOutput) {log.info("/remove.4/5) log lines removed, calling removeHyraxInstance() ...");}
		hyraxInstanceService.removeHyraxInstance(hyraxInstanceId);
		
		if(logOutput) {log.info("/remove.5/5) hyrax removed, returning <<");}
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
		if(logOutput) { log.info("/remove.1/5) removeReporter() entry, finding instance ..."); }
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		if(logOutput) { log.info("/remove.2/5) found instance, retrieving id ..."); }
		String hyraxInstanceId = register.getId();
		if(logOutput) { log.info("/remove.3/5) id : "+ hyraxInstanceId +" - calling removeLogLines() ..."); }
		logLineService.removeLogLines(hyraxInstanceId);
		
		//List<LogLineDto> list = logLineService.findLogLines(hyraxInstanceNameModel.getHyraxInstanceName());
		//String name = list.get(0).getValues().get("host");
		
		if(logOutput) { log.info("/remove.4/5) log lines removed, calling removeHyraxInstance() ..."); }
		hyraxInstanceService.removeHyraxInstance(hyraxInstanceId);
		if(logOutput) { log.info("/remove.5/5) hyrax removed, returning <<"); }
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
		if(logOutput) {log.info("repullReporterLogsPOST() : entered ...");}
		
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		if(logOutput) {log.info("repullReporterLogsPOST() : instance found ...");}
		
		String hyraxInstanceId = register.getId();
		if(logOutput) {log.info("repullReporterLogsPOST() : id found ...");}
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
		if(logOutput) {log.info("repullReporterLogsPOST() : timezone found ...");}
		
		logLineService.removeLogLines(hyraxInstanceId);
		if(logOutput) {log.info("repullReporterLogsPOST() : log lines removed ...");}
		
		monthTotalsService.clearAllMonthTotals(hyraxInstanceId);
		if(logOutput) {log.info("repullReporterLogsPOST() : Month Totals reset ...");}
		
		LogDataDto logDataDto = logCollectorService.collectAllLogs(register);
		if(logOutput) {log.info("repullReporterLogsPOST() : collected new log lines ...");}
		
		hyraxInstanceService.updateLastAccessTime(register, utc.toLocalDateTime());
		if(logOutput) {log.info("repullReporterLogsPOST() : updated last access time ...");}
		
		hyraxInstanceService.updateLastSuccessPullTime(register, utc.toLocalDateTime());
		if(logOutput) {log.info("repullReporterLogsPOST() : updated last success pull time ...");}
		
        logLineService.addLogLines(hyraxInstanceId, logDataDto.getLines());
        if(logOutput) {log.info("repullReporterLogsPOST() : added new log lines ...");}
        
        if(logOutput) {log.info("repullReporterLogsPOST() : returning <<");}
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