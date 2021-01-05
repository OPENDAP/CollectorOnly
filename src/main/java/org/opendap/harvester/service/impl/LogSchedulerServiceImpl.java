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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Service which periodically check do we need to get new data from reporter
 */
@Service
public class LogSchedulerServiceImpl implements LogSchedulerService {
	private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	private boolean logOutput = false;
	private boolean verbose = false;
	
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
                    LocalDateTime ldt = utc.toLocalDateTime();
                    
					/////////////////////////////////////////////////////////////////////////////////
					// TEST REPORTER
					///////////////////////////////////////////////////////////
                    if(logOutput) {logg.info("///////////////////////////////////////////////////");}
                    if(logOutput) {logg.info("checkHyraxInstances() checking status of "+hi.getReporterUrl()+" ...");}
                    String reporterVer = hyraxInstanceService.getReporterVersion(hi.getReporterUrl());
                    if(logOutput) {logg.info("checkHyraxInstances() ... done | version : '"+reporterVer+"'");}
                    
                    if (reporterVer.equalsIgnoreCase("error")) { // if error returned
                    	if(logOutput) {logg.info("checkHyraxInstances() reporter down ");}
                    	if (hi.getReporterRunning() == true) { //if first error signal
                    		if(logOutput) {logg.info("checkHyraxInstances() reporter offline : setting reporter status 'offline' and recording start time of outage");}
                    		hi.setReporterRunning(false);
                    		if(logOutput && verbose) {logg.info("checkHyraxInstances() not running ");}
	                    	hi.setAccessible(false);
	                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() not accessible ");}
	                    	hi.addStartToReporterDownTime(ldt);
	                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() start time recorded ");}
                    	}
                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() logging times");}
                    	hi.setLastAccessTime(ldt);
                    	hi.setReporterLastErrorTime(ldt);
                    }
                    else { // if version returned
                    	if(logOutput) {logg.info("checkHyraxInstances() reporter up ");}
                    	if (hi.getReporterRunning() == null ) {
                    		if(logOutput) {logg.info("checkHyraxInstances() reporter running init ");}
                    		hi.setReporterRunning(true);
                    	}
                    	if (hi.getReporterRunning() == false) { //if recovering from error
                    		if(logOutput) {logg.info("checkHyraxInstances() reporter online : setting reporter status 'online' and recording end time of outage ");}
                    		hi.setReporterRunning(true);
                    		if(logOutput && verbose) {logg.info("checkHyraxInstances() running ");}
                    		hi.setAccessible(true);
                    		if(logOutput && verbose) {logg.info("checkHyraxInstances() accessible ");}
                    		hi.addEndToReporterDownTime(ldt);
                    		if(logOutput && verbose) {logg.info("checkHyraxInstances() end time ");}
                    	}
                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() saving version ");}
                    	hi.setReporterVersionNumber(reporterVer);
                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() logging time ");}
                    	hi.setLastAccessTime(ldt);
                    }
                    
					/////////////////////////////////////////////////////////////////////////////////
					// TEST SERVER
					///////////////////////////////////////////////////////////
                    
                    if(logOutput) {logg.info("///////////////////////////////////////////////////");}
                    if(logOutput) {logg.info("checkHyraxInstances() checking status of "+hi.getName()+" ...");}
                    String serverVer = hyraxInstanceService.getHyraxVersion(hi.getName());
                    if(logOutput) {logg.info("checkHyraxInstances() ... done | version : '"+serverVer+"'");}
                    
                    if (serverVer.equalsIgnoreCase("error")) { // if error returned
                    	if(logOutput) {logg.info("checkHyraxInstances() server down ");}
                    	if (hi.getServerRunning() == true) { // if first error signal
                    		if(logOutput) {logg.info("checkHyraxInstances() server offline : setting server status 'offline' and recording start time of outage");}
                    		hi.setServerRunning(false);
                    		hi.addStartToServerDownTime(ldt);
                    	}
                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() logging times ");}
                    	hi.setServerLastAccessTime(ldt);
                    	hi.setServerLastErrorTime(ldt);
                    }
                    else { //if version returned
                    	if(logOutput) {logg.info("checkHyraxInstances() server up ");}
                    	if (hi.getServerRunning() == null) {
                    		if(logOutput) {logg.info("checkHyraxInstances() server running init ");}
                    		hi.setServerRunning(true);
                    	}
                    	if (hi.getServerRunning() == false) { // if recovering from error
                    		if(logOutput) {logg.info("checkHyraxInstances() server online : setting reporter status 'online' and recording end time of outage");}
                    		hi.setServerRunning(true);
                    		hi.addEndToServerDownTime(ldt);
                    	}
                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() saving version");}
                    	hi.setServerVersionNumber(serverVer);
                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() logging time ");}
                    	hi.setServerLastAccessTime(ldt);
                    }
                    
                    /////////////////////////////////////////////////////////////////////////////////
                    // RETRIEVE LOG LINES
                    ///////////////////////////////////////////////////////////
                    if(logOutput) {logg.info("///////////////////////////////////////////////////");}
                    if(hi.getReporterRunning()) {
	                    if (hi.getLastAccessTime() == null){
	                        logDataDto = logCollectorService.collectAllLogs(hi);
	                        if(logOutput) {logg.info(" /!\\ collectAllLogs called on "+hi.getName()+" : "+logDataDto.numOfLines()+" log lines collected /!\\");}
	                    } else {
	                        logDataDto = logCollectorService.collectLogs(hi, hi.getLastAccessTime());
	                        if(logOutput) {logg.info(" /!\\ collectLogs called on "+hi.getName()+" : "+logDataDto.numOfLines()+" log lines collected /!\\");}
	                    }
                    
                    
						/////////////////////////////////////////////////////////////////////////////////
						// SAVE LOG LINES
						///////////////////////////////////////////////////////////
	                    
	                    if(logOutput) {logg.info("/////////////////////////");}
	                    if(logDataDto.numOfLines() != -1) {
	                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() num of log lines != -1");}
	                    	hi.setLastSuccessfulPull(ldt);
	                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() updated last successful pull time");}
	                    	logLineService.addLogLines(hi.getId(), logDataDto.getLines());
	                    	if(logOutput && verbose) {logg.info("checkHyraxInstances() log lines added");}
	                    }
	                    else {
	                    	if(logOutput) {logg.info("checkHyraxInstances() error handler");}
	                    }
                    }
                    
                    if(logOutput) {logg.info("checkHyraxInstances() returning <<<");}
                    hyraxInstanceService.saveHyraxInstance(hi);
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
