/**
  * Entry point for REST call. All controllers receives REST request outside and reroute
 * them to internal application services. After that it returns results.
 */
package org.opendap.harvester.controller;

import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.model.RegisterModel;
import org.opendap.harvester.service.HyraxInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This annotations tell us about what type of Spring bean it is.
 * It is Controller. It means that it can receives REST requests.
 */
@Controller
@RequestMapping("/harvester")
public class HarvesterController {
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
     * 
     * 1/22/19 - SBL - modified method to include if/else statement and 'updatePing' call
     */
    @RequestMapping(path = "/registration", method = RequestMethod.GET)
    @ResponseBody
    public HyraxInstanceDto registerGet(@Valid @ModelAttribute RegisterModel registerModel) throws Exception {
    	//System.out.println("\n\tGet Method :");
    	HyraxInstance register;
    	if (hyraxInstanceService.findHyraxInstanceByName(registerModel.getServerUrl())!= null) {
    		//System.out.println("\t\tHyrax found => update called:");
    		register = hyraxInstanceService.updatePing(registerModel.getServerUrl(), registerModel.getPing(), hyraxInstanceService);
    		return hyraxInstanceService.buildDto(register);
    	}
    	else {
    	// Calling service method and returning result
    		//System.out.println("\t\tHyrax not found => add server called:");
	        register = hyraxInstanceService.register(
	                registerModel.getServerUrl(),
	                StringUtils.isEmpty(registerModel.getReporterUrl()) ?
	                        registerModel.getServerUrl() : registerModel.getReporterUrl(),
	                registerModel.getPing(),
	                registerModel.getLog());
	        return hyraxInstanceService.buildDto(register);
    	}
    }//end register GET
    
    
    /**
     * 
     * @param registerModel
     * @return
     * @throws Exception
     * 
     * 2/7/19 - SBL - initial code
     *//*
    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public boolean registerPost(@Valid @ModelAttribute RegisterModel registerModel) throws Exception {
    	System.out.println("\n\tPost Method\n\t"+registerModel.toString()+"\n");
    	HyraxInstance register;
    	if (hyraxInstanceService.findHyraxInstanceByName(registerModel.getServerUrl())!= null) {
    		register = hyraxInstanceService.updatePing(registerModel.getServerUrl(), registerModel.getPing(), hyraxInstanceService);
    		return true;
    	}
    	else {
    	// Calling service method and returning result
	        register = hyraxInstanceService.register(
	                registerModel.getServerUrl(),
	                StringUtils.isEmpty(registerModel.getReporterUrl()) ?
	                        registerModel.getServerUrl() : registerModel.getReporterUrl(),
	                registerModel.getPing(),
	                registerModel.getLog());
	        return false;
    	}
    }//end register POST */

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
