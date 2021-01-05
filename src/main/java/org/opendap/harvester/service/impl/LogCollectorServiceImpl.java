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
import org.opendap.harvester.service.LogCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

/**
 * Log collector
 * Service which will get data from reporter
 */

@Service
public class LogCollectorServiceImpl implements LogCollectorService {
	private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	private boolean logOutput = false;
	
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public LogDataDto collectLogs(HyraxInstance hyraxInstance, LocalDateTime since) {
        try {
        	if(logOutput) {logg.info("collectLogs 1/3) retrieving log lines from "+hyraxInstance.getName()+"\tReporter URL : "+hyraxInstance.getReporterUrl());}
        	LogDataDto logDataDto = restTemplate.getForObject(
                    new URI(hyraxInstance.getReporterUrl() + "/log?since=" + since),
                    LogDataDto.class);
        	if(logOutput) {logg.info("collectLogs 2/3) retrieved log lines");}
            /*
             	return restTemplate.getForObject(
                    new URI(hyraxInstance.getReporterUrl() + "/log?since=" + since),
                    LogDataDto.class);
            */
        	if(logOutput) {logg.info("collectLogs 3/3) returning ...");}
        	return logDataDto;
        } catch (HttpClientErrorException e){
        	//TODO fix collectLogs so that if reporter is offline program will report issue and continue.
        	if(logOutput) {logg.info("collectLogs 2e/3) /!\\ HttpClientErrorException : "+e.getMessage()+" /!\\");}
        	LogDataDto logDataDto = new LogDataDto();
        	if(logOutput) {logg.info("collectLogs 3e/3) returning");}
        	return logDataDto;
        } catch (URISyntaxException e) {
        	if(logOutput) {logg.info("/!\\ URISyntaxxception - collectLogs() : "+e.getMessage()+" /!\\");}
            e.printStackTrace();
            throw new IllegalStateException();
        } 
    }

    @Override
    public LogDataDto collectAllLogs(HyraxInstance hyraxInstance) {
        try {//TODO add catch for HttpClientErrorexception
            return restTemplate.getForObject(
                    new URI(hyraxInstance.getReporterUrl() + "/log"),
                    LogDataDto.class);
        } catch (URISyntaxException e) {
        	if(logOutput) {logg.info("/!\\ Error - collectAllLogs() : "+e.getMessage()+" /!\\");}
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
