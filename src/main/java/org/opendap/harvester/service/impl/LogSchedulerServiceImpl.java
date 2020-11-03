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
	private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	private boolean logOutput = false;
	
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
                    LogDataDto logDataDto;
                    ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
                    if (hi.getLastAccessTime() == null){
                        logDataDto = logCollectorService.collectAllLogs(hi);
                        if(logOutput) {logg.info(" /!\\ collectAllLogs called on "+hi.getName()+" : "+logDataDto.numOfLines()+" log lines collected /!\\");}
                    } else {
                        logDataDto = logCollectorService.collectLogs(hi, hi.getLastAccessTime());
                        if(logOutput) {logg.info(" /!\\ collectLogs called on "+hi.getName()+" : "+logDataDto.numOfLines()+" log lines collected /!\\");}
                    }
                    
                    hyraxInstanceService.updateLastAccessTime(hi, utc.toLocalDateTime());
                    if(logOutput) {logg.info("checkHyraxInstances() updated last access time");}
                    
                    if(logDataDto.numOfLines() != -1) {
                    	if(logOutput) {logg.info("checkHyraxInstances() num of log lines != -1");}
                    	hyraxInstanceService.updateLastSuccessPullTime(hi, utc.toLocalDateTime());
                    	if(logOutput) {logg.info("checkHyraxInstances() updated last successful pull time");}
                    	logLineService.addLogLines(hi.getId(), logDataDto.getLines());
                    	if(logOutput) {logg.info("checkHyraxInstances() log lines added");}
                    	if (hi.getAccessible() == null || !hi.getAccessible()) {
                    		hyraxInstanceService.updateAccessibleStatus(hi, true);
                    		if(logOutput) {logg.info("checkHyraxInstances() updated accessible status");}
                    		hyraxInstanceService.updateErrorCount(hi, 0, true);
                    		if(logOutput) {logg.info("checkHyraxInstances() updated error count");}
                    		//holding for later date, will be used to track error intervals. SBL 12.12.19
                    		//hyraxInstanceService.updateErrorCountList(hi);
                    		//logg.info("checkHyraxInstances() updated error count list");
                    	}
                    }
                    else {
                    	if(logOutput) {logg.info("checkHyraxInstances() error handler");}
                    	hyraxInstanceService.updateAccessibleStatus(hi, false);
                    	if(logOutput) {logg.info("checkHyraxInstances() updated accessible status");}
                    	hyraxInstanceService.updateErrorCount(hi, 1, false);
                    	if(logOutput) {logg.info("checkHyraxInstances() updated error count");}
                    }
                }); //.filter(...).forEach()
    }//checkHyraxinstances()

    private boolean isTimeToCheck(HyraxInstance hyraxInstance) {
        if (hyraxInstance.getLastAccessTime() == null) {
            return true;
        }
        return Duration.between(hyraxInstance.getLastAccessTime(), ZonedDateTime.now(ZoneId.of("UTC")))
                .getSeconds() > hyraxInstance.getPing();
    }
}
