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
    	//System.out.println("\n1)\tGet Method :"); // <---
    	log.info("/reg.1) Get Method :"); // <---
    	HyraxInstance register;
    	if (hyraxInstanceService.findHyraxInstanceByName(registerModel.getServerUrl())!= null) {
    		//System.out.println("2)\t\tHyrax found => update called:"); // <---
    		log.info("/reg.2) Hyrax found => update called:"); // <---
    		register = hyraxInstanceService.updatePing(registerModel.getServerUrl(), registerModel.getPing(), hyraxInstanceService);
    		//System.out.println("3)\t\tHyrax server updated:"); // <---
    		log.info("/reg.3) Hyrax server updated:"); // <---
    		return hyraxInstanceService.buildDto(register);
    	}
    	else {
    	// Calling service method and returning result
    		//System.out.println("2)\t\tHyrax not found => add server called:"); // <---
    		log.info("/reg.2) Hyrax not found => add server called:"); // <---
	        register = hyraxInstanceService.register(
	                registerModel.getServerUrl(),
	                StringUtils.isEmpty(registerModel.getReporterUrl()) ?
	                        registerModel.getServerUrl() : registerModel.getReporterUrl(),
	                registerModel.getPing(),
	                registerModel.getLog());
	        //System.out.println("3)\t\tHyrax server added:"); // <---
	        log.info("/reg.3) Hyrax server added:"); // <---
	        return hyraxInstanceService.buildDto(register);
    	}
    }//end register GET
    
    /**
     * registerPost method ...
     * @param registerModel
     * @return
     * @throws Exception
     */
    // 2/7/19 - SBL - initial code /*
    //@RequestMapping(path = "/registration", method = RequestMethod.POST)
    //public String registerPost(@Valid @ModelAttribute RegisterModel registerModel) throws Exception {
    	/* TODO fix POST method
    	 * algorithm - used to save reporter for the first time
    	 * create new register and uuid for register 
    	 * add to mongo
    	 * returns uuid to caller in response
    	 */
    	//System.out.println("\n\tPost Method\n\t"+registerModel.toString()+"\n");
    	//return registerModel.toString();
    	/*
    	HyraxInstance register;
    	String id = registerModel.getServerUrl(); //Change me!!
    	if (hyraxInstanceService.findHyraxInstanceByName(registerModel.getServerUrl())!= null) {
    		register = hyraxInstanceService.updatePing(registerModel.getServerUrl(), registerModel.getPing(), hyraxInstanceService);
    		return id;
    	}
    	else {
    	// Calling service method and returning result
	        register = hyraxInstanceService.register(
	                registerModel.getServerUrl(),
	                StringUtils.isEmpty(registerModel.getReporterUrl()) ?
	                        registerModel.getServerUrl() : registerModel.getReporterUrl(),
	                registerModel.getPing(),
	                registerModel.getLog());
	        id = UUID.randomUUID().toString();
	        return id;
    	}
    	*/
    //}//end registerPost() */
    
    /**
     * registerPut method ...
     */
    // 4/11/19 - SBL - initial code 
    /*
    public Boolean registerPut() {
    	//TODO code up PUT method
    	return false;
    } //end registerPut() 
    */ 
    
    /**
     * 
     * @param onlyActive
     * @return
     * @throws Exception
     */
    // 4/11/19 - SBL - comment
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
