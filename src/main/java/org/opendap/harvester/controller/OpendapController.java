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

import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.service.HyraxInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OpendapController{
	
	@Autowired
	private HyraxInstanceService hyraxInstanceService;
	
	/**
	 * access method for the opendap.jsp page
	 * @return ModelAndView object to the Opendap home page
	 * 
	 * 11/15/18 - SBL - Initial code
	 * 12/19/18 - SBL - Modified method to retrieve list of all hyrax instances,
	 * 		take information and filters hyrax instances names out,
	 * 		then passes names as an array to ModelAndView object to 
	 * 			display as list on page
	 */
	@RequestMapping(value="/opendap", method = RequestMethod.GET)
	public ModelAndView opendap() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("opendap");
		
		String str = "OPENDAP Collector Home";
		mav.addObject("message", str);
		
		
		List<HyraxInstanceDto> list = hyraxInstanceService.allHyraxInstances(true)
				.map(hyraxInstanceService::buildDto)
				.collect(Collectors.toList());
				
		String[] nameList = new String[list.size()];
		int index = 0;
		for(HyraxInstanceDto hid : list){
			nameList[index] = hid.getName();
			index++;
		}
		
		//String[] nameList = new String[] {"siteOne","siteTwo","siteThree","siteFour","siteFive"};
		
		mav.addObject("items", nameList);
		
		return mav;
	}
	
	/**
	 * handles the 'New Server' button click and redirects to new server page
	 * @return ModelAndView object of New Server page
	 * 
	 * 12/19/18 - SBL - Initial Code
	 */
	@RequestMapping(value="/NewServerRedirect", method=RequestMethod.POST)
	public ModelAndView newServerRedirect(){
		return new ModelAndView("redirect:/newServer");
	}
	
}