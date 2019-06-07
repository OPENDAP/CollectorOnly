package org.opendap.harvester.controller;

import javax.validation.Valid;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameModel;
import org.opendap.harvester.service.HyraxInstanceService;
import org.opendap.harvester.service.LogLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ServerDetailsController{
	//private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
	
	@Autowired
	private HyraxInstanceService hyraxInstanceService;
	
	@Autowired
	private LogLineService logLineService;
	
	/**
	 * 
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(value="/server", method = RequestMethod.GET)
	public ModelAndView serverDetails(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("serverDetails");
		
		long ping = register.getPing();
		long mins = 0, hours = 0, days = 0;
		mins = ping/60;
		hours = mins/60;
		days = hours/24;
		String s;
		if(days <= 0 && hours <= 0) {s = ping +" - "+ mins +"m";}
		else if(days <= 0) {s = ping +" - "+ hours +"h "+ mins +"m";}
		//else if(hours <= 0) {s = ping +" - "+ mins +"m";}
		else {s = ping +" - "+ days +"d "+ hours +"h "+ mins+"m";}
		
		mav.addObject("serverId", register.getServerUUID());
		mav.addObject("serverUrl", register.getName());
		mav.addObject("reporterUrl", register.getReporterUrl());
		mav.addObject("ping", s);
		mav.addObject("log", register.getLog());
		mav.addObject("version", register.getVersionNumber());
		mav.addObject("registrationTime", register.getRegistrationTime());
		mav.addObject("lastAccessTime", register.getLastAccessTime());
		mav.addObject("active", register.getActive());
		mav.addObject("name", register.getName());
		
		return mav;
	}
	
	/**
	 * 
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(value="/remove", method = RequestMethod.GET)
	public ModelAndView removeReporter(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
	// 5/2/19 - SBL - initial code
		//log.info("/remove.1/5) removeReporter() entry, finding instance ...");
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		//log.info("/remove.2/5) found instance, retrieving id ...");
		String hyraxInstanceId = register.getId();
		//log.info("/remove.3/5) id : "+ hyraxInstanceId +" - calling removeLogLines() ...");
		logLineService.removeLogLines(hyraxInstanceId);
		//log.info("/remove.4/5) log lines removed, calling removeHyraxInstance() ...");
		hyraxInstanceService.removeHyraxInstance(hyraxInstanceId);
		//log.info("/remove.5/5) hyrax removed, returning <<");
		return new ModelAndView("redirect:/opendap");
	}//end removeReporter() 
}//end class ServerDetailsController