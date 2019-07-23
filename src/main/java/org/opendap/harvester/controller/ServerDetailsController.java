package org.opendap.harvester.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.Valid;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogDataDto;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ServerDetailsController{
	//private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
	
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
	@RequestMapping(value="/server", method = RequestMethod.GET)
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
	 * @param hyraxInstanceNameModel
	 * @return
	 */
	@RequestMapping(value="/remove", method = RequestMethod.GET)
	public ModelAndView removeReporter(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
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
	

	/**
	 * 
	 */
	@RequestMapping(value="/repull", method = RequestMethod.GET)
	public ModelAndView repullReporterLogs(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
		HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
		String hyraxInstanceId = register.getId();
		ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
		
		logLineService.removeLogLines(hyraxInstanceId);
		LogDataDto logDataDto = logCollectorService.collectAllLogs(register);
		hyraxInstanceService.updateLastAccessTime(register, utc.toLocalDateTime());
        logLineService.addLogLines(hyraxInstanceId, logDataDto.getLines());
        
        return new ModelAndView("redirect:/server?hyraxInstanceName="+hyraxInstanceNameModel.getHyraxInstanceName());
	}//end repullReporterLogs()
	
}//end class ServerDetailsController