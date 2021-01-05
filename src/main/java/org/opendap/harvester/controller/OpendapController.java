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

/**
 * Opendap Home Controller
 * 		Home page of Opendap Controller 
 * 		handles displaying list of hyrax instances and 
 * 		redirecting to '/logLines', '/newServer', '/Statistic' pages 
 * 
 * 11/15/18 - SBL - Initial creation of controller
 * 12/19/18 - SBL - Modified 'opendap' method
 */

package org.opendap.harvester.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.service.DateTimeUtilService;
import org.opendap.harvester.service.HyraxInstanceService;
import org.opendap.harvester.service.LogLineParsingUtilService;
import org.opendap.harvester.service.LogLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OpendapController{
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
	private LogLineParsingUtilService logLineParsingUtilService;
	
	@Autowired
	private DateTimeUtilService dateTimeUtilService;
	
	/**
	 * access method for the opendap.jsp page
	 * @return ModelAndView object to the Opendap home page
	 * 
	 */
	@RequestMapping(value="/opendap", method = RequestMethod.GET)
	public ModelAndView opendap() {
		if(logOutput) { log.info("/opendap | >>> function start"); }
		long starttime = System.nanoTime();
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("opendap");
		
		String str = "OPENDAP Collector Home";
		mav.addObject("message", str);
		
		List<HyraxInstanceDto> list = hyraxInstanceService.allHyraxInstances(false)
				.map(hyraxInstanceService::buildDto)
				.collect(Collectors.toList());
		if(logOutput) { log.info("/opendap | list of hyraxInstanceDto retrieved"); }
				
		String[][] nameList = new String[list.size()][6];
		int index = 0;
		for(HyraxInstanceDto hid : list){
			if(logOutput && verbose) { log.info("/opendap | populating namelist["+index+"][~]"); }
			
			nameList[index][0] = hid.getName(); // Profile Instance
			if(logOutput && verbose) { log.info("/opendap | 	- name : "+ hid.getName()); }
			
			nameList[index][1] = hid.getActive().toString(); // Profile Active
			if(logOutput && verbose) { log.info("/opendap | 	- active : " + hid.getActive()); }

			nameList[index][2] = (hid.getLastAccessTime() == null) ? 
					"Last Access Time Unknown" : dateTimeUtilService.convertDatetoReadible(hid.getLastAccessTime()); // Profile Last Access Time
			if(logOutput && verbose) { log.info("/opendap | 	- LAT : "+ nameList[index][2]); }
			
			nameList[index][3] = hid.getServerRunning().toString(); // Server Running
			if(logOutput && verbose) { log.info("/opendap | 	- Server Running : "+ hid.getServerRunning()); }
			
			nameList[index][4] = hid.getReporterRunning().toString(); // Reporter Running
			if(logOutput && verbose) { log.info("/opendap | 	- Reporter Running : "+ hid.getReporterRunning()); }

			nameList[index][5] = ""+logLineService.findNumberLogLinesByHyraxName(hid.getName()); // Reporter Logs Pulled
			if(logOutput && verbose) { log.info("/opendap | 	- Reporter Logs Pulled : "+ nameList[index][5]); }
			
			index++;
		}//end for loop
		if(logOutput) { log.info("/opendap | namelist[][] built"); }
		
		mav.addObject("items", nameList);
		mav.addObject("version", harvesterVersion);
		
		long endtime = System.nanoTime();
		long duration = endtime - starttime;
		String time =  logLineParsingUtilService.parseTime(duration);
		
		mav.addObject("time", time);
		
		if(logOutput) { log.info("/opendap | returning <<<"); }
		return mav;
	}//end opendap()
	
	/**
	 * handles the 'New Server' button click and redirects to new server page
	 * @return ModelAndView object of New Server page
	 * 
	 */
	@RequestMapping(value="/NewServerRedirect", method=RequestMethod.POST)
	public ModelAndView newServerRedirect(){
		return new ModelAndView("redirect:/newServer");
	}//end newServerRedirect()
	
}