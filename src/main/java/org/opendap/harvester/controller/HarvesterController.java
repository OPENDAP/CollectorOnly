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
    // 1/22/19 - SBL - modified method to include if/else statement and 'updatePing' call
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
    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    @ResponseBody
    public UUID registerPost(@Valid @ModelAttribute RegisterModel registerModel) throws Exception {
    // 2/7/19 - SBL - initial code 
    // 5/13/19 - SBL - modified for POST functionality
    // 5/31/19 - SBL - halted development for foreseeable future
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
    
    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    @ResponseBody
    public void registerPost() throws Exception {
    	log.info("/regPOST.1/2) registration entry");
    	log.info("/regPOST.2/2) returing <<");
    }
    
    /**
     * registerPut method ...
     */ // 
    /*
    @RequestMapping(path = "/registration", method = RequestMethod.PUT)
    @ResponseBody
    public HyraxInstanceDto registerPut(@Valid @ModelAttribute UpdateModel updateModel) throws Exception {
    // 4/11/19 - SBL - initial code
    // 5/20/19 - SBL - modified for PUT functionality
    // 5/31/19 - SBL - halted development for foreseeable future    
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
    // 4/11/19 - SBL - comment
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
