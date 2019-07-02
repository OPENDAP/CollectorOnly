package org.opendap.harvester.controller;

import javax.validation.Valid;

import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameModel;
import org.opendap.harvester.service.HyraxInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthCheckController {
    @Value("${harvester.version}")
    private String harvesterVersion;
    
    // /* 2/12/19 - SBL - little bit of testing
    @Autowired
    private HyraxInstanceService hyraxInstanceService;
    // */

    @RequestMapping(path = "/healthcheck", method = RequestMethod.GET)
    public String healthCheck(){
        return "Application is working! Version = " + harvesterVersion;
    }
    
    // not sure if this method is ever used or would have a reason to be used
    // CHECKME determine if method is needed. sbl 7.2.19
    @RequestMapping(path = "/healthcheck/server", method = RequestMethod.GET)
    public HyraxInstanceDto displayServerInfo(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel) {
    	HyraxInstance register = hyraxInstanceService.findHyraxInstanceByName(hyraxInstanceNameModel.getHyraxInstanceName());
    	return hyraxInstanceService.buildDto(register);
    }
    
}
