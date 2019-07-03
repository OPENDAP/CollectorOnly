/**
 * Index Controller 
 * 		To be used to redirect users from partial urls 
 * 			to the collector home page
 * 
 * 12/19/18 - SBL - Initial creation of controller
 */

package org.opendap.harvester.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
	
	/**
	 * method to redirect user to default page
	 * @return ModelAndView object redirecting to opendap.jsp page
	 * 
	 */
	@RequestMapping("/")
	public ModelAndView index(){
		return new ModelAndView("redirect:/opendap");
	}
	
}
