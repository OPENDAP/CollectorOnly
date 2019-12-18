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
	 */
	@RequestMapping(value="/opendap", method = RequestMethod.GET)
	public ModelAndView opendap() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("opendap");
		
		String str = "OPENDAP Collector Home";
		mav.addObject("message", str);
		
		List<HyraxInstanceDto> list = hyraxInstanceService.allHyraxInstances(false)
				.map(hyraxInstanceService::buildDto)
				.collect(Collectors.toList());
				
		String[][] nameList = new String[list.size()][4];
		int index = 0;
		for(HyraxInstanceDto hid : list){
			nameList[index][0] = hid.getName();
			nameList[index][1] = hid.getAccessible().toString();
			//nameList[index][1] = "placeholder"; max = (a > b) ? a : b;
			nameList[index][2] = (hid.getErrorCount() != null) ? hid.getErrorCount().toString() : "0";
			//nameList[index][2] = "placeholder";
			nameList[index][3] = hid.getActive().toString();
			index++;
		}//end for loop
		
		mav.addObject("items", nameList);
		
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