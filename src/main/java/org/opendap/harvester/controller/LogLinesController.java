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

package org.opendap.harvester.controller;

import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogLineDto;
import org.opendap.harvester.entity.dto.model.HyraxInstanceNameModel;
import org.opendap.harvester.entity.dto.model.RegisterModel;
import org.opendap.harvester.service.HyraxInstanceService;
import org.opendap.harvester.service.LogLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("harvester/logLines")
public class LogLinesController {
    @Autowired
    private LogLineService logLineService;

    @Autowired
    private HyraxInstanceService hyraxInstanceService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseBody
    public List<LogLineDto> findAllLogLines(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel){
        HyraxInstance hyraxInstance = hyraxInstanceService.findHyraxInstanceByName(
                hyraxInstanceNameModel.getHyraxInstanceName());
        if (hyraxInstance == null){
            return Collections.emptyList();
        }
        
        if (hyraxInstanceNameModel.isBonus()) {
        	return logLineService.findLogLines(hyraxInstance.getId());
        }
        else {
        	return logLineService.findLogLines(hyraxInstance.getId(), 500);
        }
        
    }

    @RequestMapping(path = "/string", method = RequestMethod.GET)
    @ResponseBody
    public String findAllLogLinesAsString(@Valid @ModelAttribute HyraxInstanceNameModel hyraxInstanceNameModel){
        HyraxInstance hyraxInstance = hyraxInstanceService.findHyraxInstanceByName(
                hyraxInstanceNameModel.getHyraxInstanceName());
        if (hyraxInstance == null){
            return "";
        }
        return logLineService.findLogLinesAsString(hyraxInstance.getId());
    }

}
