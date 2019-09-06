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
 * New Server Controller 
 * 		handles initial access of newServer.jsp page
 * 		as well as handling validation of variables on the page 
 * 		and execution of page methods
 * 
 * 12/19/18 - SBL - Initial creation of controller
 * 12/26/18 - SBL - addNewServer method added
 */

package org.opendap.harvester.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NewServerController {

	/**
	 * access method for the newServer.jsp page
	 * @return ModelAndView object to the New Server page
	 * 
	 */
	@RequestMapping(path = "/newServer", method = RequestMethod.GET)
	public ModelAndView pageAccess(){
		return new ModelAndView("newServer");
	}
	
	/**
	 * takes new server and reporter information and passes to 
	 * 		registration in harvester controller
	 * @param serverUrl - URL of server
	 * @param reporterUrl - URL of reporter 
	 * @param ping - 
	 * @param log - 
	 * @return - redirection to registration method
	 * 
	 */
	@RequestMapping(path="/AddNewServer", method = RequestMethod.POST)
	public ModelAndView addNewServer(@RequestParam String serverUrl, 
			@RequestParam String reporterUrl, 
			@RequestParam String ping, 
			@RequestParam String log){
		long convertedPing = Long.parseLong(ping);
		int convertedLog = Integer.parseInt(log);
		
		if (reporterUrl.isEmpty()){
			return new ModelAndView("redirect:/harvester/registration?serverUrl="+serverUrl
					+"&ping="+convertedPing
					+"&log="+convertedLog);
		}
		else{
			return new ModelAndView("redirect:/harvester/registration?serverUrl="+serverUrl
					+"&reporterUrl="+reporterUrl
					+"&ping="+convertedPing
					+"&log="+convertedLog);
		}
		
		
	}
}
