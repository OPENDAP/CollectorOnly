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
  * Entry point for REST call. All controllers receives REST request outside and reroute
 * them to internal application services. After that it returns results.
 */
package org.opendap.harvester.controller;

import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.model.RegisterModel;
import org.opendap.harvester.service.HyraxInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This annotations tell us about what type of Spring bean it is.
 * It is Controller. It means that it can receives REST requests.
 */
@Controller
@RequestMapping("/harvester")
public class HarvesterController {
	private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
    
	/**
     * Autowired automatically inject some of the HyraxInstanceRegisterService implementations to this
     * class field. It will happen on class instantiating stage.
     * After that it can be used in this class like service endpoint.
     */
    @Autowired
    private HyraxInstanceService hyraxInstanceService;

    /**
     * Called when /harvester/registration request come. Automatically setting up request attributes to special object.
     * SBL - checks if reporter is already in DB, if so passes to 'updatePing()' to check if ping value in DB needs to be updated
     * @param registerModel
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/registration", method = RequestMethod.GET)
    @ResponseBody
    public HyraxInstanceDto registerGet(@Valid @ModelAttribute RegisterModel registerModel) throws Exception {
    	//log.info("/reg.1/3) registerGet() method entry ..."); // <---
    	HyraxInstance register;
    	if (hyraxInstanceService.findHyraxInstanceByName(registerModel.getServerUrl())!= null) {
    		//log.info("/reg.2/3) Hyrax found => update called ..."); // <---
    		register = hyraxInstanceService.updatePing(registerModel.getServerUrl(), registerModel.getPing());
    		//log.info("/reg.3/3) Hyrax server updated, returning << :"); // <---
    		return hyraxInstanceService.buildDto(register);
    	}
    	else {
    	// Calling service method and returning result
    		//log.info("/reg.2/3) Hyrax not found => add server called ..."); // <---
	        register = hyraxInstanceService.register(
	                registerModel.getServerUrl(),
	                StringUtils.isEmpty(registerModel.getReporterUrl()) ?
	                        registerModel.getServerUrl() : registerModel.getReporterUrl(),
	                registerModel.getPing(),
	                registerModel.getLog());
	        //log.info("/reg.3/3) Hyrax server added, returning <<"); // <---
	        return hyraxInstanceService.buildDto(register);
    	}
    }//end register GET
    
    /**
     * 
     * @param registerModel
     * @return
     * @throws Exception
     */ //
    /*
     * TODO implement POST method for registration. sbl 7.2.19
     * 
    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    @ResponseBody
    public UUID registerPost(@Valid @ModelAttribute RegisterModel registerModel) throws Exception {
       	log.info("/regPOST.1/2) /registration entry, calling register() ...");
    	HyraxInstance register = hyraxInstanceService.register(
	                registerModel.getServerUrl(),
	                StringUtils.isEmpty(registerModel.getReporterUrl()) ?
	                        registerModel.getServerUrl() : registerModel.getReporterUrl(),
	                registerModel.getPing(),
	                registerModel.getLog());
    	log.info("/regPOST.2/2) instance registered, returning <<");
    	return register.getServerUUID();//.toString();
    }//end registerPost()
    */
    
    /*
     * Test method for checking if POST call works 
     * to be deleted after POST method is fully implemented
     * 
    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    @ResponseBody
    public void registerPost() throws Exception {
    	log.info("/regPOST.1/2) registration entry");
    	log.info("/regPOST.2/2) returing <<");
    }
    */
    
    /**
     * registerPut method ...
     */ // 
    /*
     * TODO implement PUT method for registration. sbl 7.2.19
     * 
    @RequestMapping(path = "/registration", method = RequestMethod.PUT)
    @ResponseBody
    public HyraxInstanceDto registerPut(@Valid @ModelAttribute UpdateModel updateModel) throws Exception {   
    	HyraxInstance updated = hyraxInstanceService.updateInstance(updateModel);
    	return updated;
    } //end registerPut() 
    */ 
    
    /**
     * 
     * @param onlyActive
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/allHyraxInstances", method = RequestMethod.GET)
    @ResponseBody
    public List<HyraxInstanceDto> allHyraxInstances(
            @RequestParam(defaultValue = "true") Boolean onlyActive)
            throws Exception {
        return hyraxInstanceService.allHyraxInstances(onlyActive)
                .map(hyraxInstanceService::buildDto)
                .collect(Collectors.toList());
    }
//
//    @ExceptionHandler
//    private void exception(){
//
//    }

}
