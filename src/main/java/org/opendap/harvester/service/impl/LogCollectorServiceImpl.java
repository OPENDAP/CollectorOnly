package org.opendap.harvester.service.impl;

import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogDataDto;
import org.opendap.harvester.service.LogCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

/**
 * Log collector
 * Service which will get data from reporter
 */

@Service
public class LogCollectorServiceImpl implements LogCollectorService {
	private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);
	
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public LogDataDto collectLogs(HyraxInstance hyraxInstance, LocalDateTime since) {
        try {
            return restTemplate.getForObject(
                    new URI(hyraxInstance.getReporterUrl() + "/log?since=" + since),
                    LogDataDto.class);
        }catch (URISyntaxException e) {
        	log.error("/!\\ LogCollectorServiceImpl.java - collectLogs() : "+e.getMessage()+" /!\\");
            //e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    @Override
    public LogDataDto collectAllLogs(HyraxInstance hyraxInstance) {
        try {
            return restTemplate.getForObject(
                    new URI(hyraxInstance.getReporterUrl() + "/log"),
                    LogDataDto.class);
        } catch (URISyntaxException e) {
        	log.error("/!\\ LogCollectorServiceImpl.java - collectAllLogs() : "+e.getMessage()+" /!\\");
            //e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
