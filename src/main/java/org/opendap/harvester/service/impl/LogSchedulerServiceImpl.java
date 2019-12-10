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

package org.opendap.harvester.service.impl;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogDataDto;
import org.opendap.harvester.service.HyraxInstanceService;
import org.opendap.harvester.service.LogCollectorService;
import org.opendap.harvester.service.LogLineService;
import org.opendap.harvester.service.LogSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Service which periodically check do we need to get new data from reporter
 */
@Service
public class LogSchedulerServiceImpl implements LogSchedulerService {
	private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
	
    @Autowired
    private HyraxInstanceService hyraxInstanceService;

    @Autowired
    private LogCollectorService logCollectorService;

    @Autowired
    private LogLineService logLineService;

    @Scheduled(fixedDelay = 1000)
    void checkHyraxInstances() {
        hyraxInstanceService.allHyraxInstances(true)
                .filter(this::isTimeToCheck)
                .forEach(hi -> {
                	try {
	                    LogDataDto logDataDto;
	                    ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
		                    if (hi.getLastAccessTime() == null){
		                        logDataDto = logCollectorService.collectAllLogs(hi);
		                    } else {
		                        logDataDto = logCollectorService.collectLogs(hi, hi.getLastAccessTime());
		                    }
	                    hyraxInstanceService.updateLastAccessTime(hi, utc.toLocalDateTime());
	                    logLineService.addLogLines(hi.getId(), logDataDto.getLines());
                    }catch (Error e) {
			    		log.error("/!\\ LogCollectorServiceImpl.java - checkHyraxInstances() : "+e.getMessage()+" /!\\");
			    	}
                });
    }

    private boolean isTimeToCheck(HyraxInstance hyraxInstance) {
        if (hyraxInstance.getLastAccessTime() == null) {
            return true;
        }
        return Duration.between(hyraxInstance.getLastAccessTime(), ZonedDateTime.now(ZoneId.of("UTC")))
                .getSeconds() > hyraxInstance.getPing();
    }
}
