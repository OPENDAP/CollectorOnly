package org.opendap.harvester.service.impl;

import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.HarvesterApplication;
import org.opendap.harvester.dao.HyraxInstanceRepository;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.service.HyraxInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.*;

/**
 * Service implementation. All business logic should be here.
 * Call to db are initiating from this place via Repositories
 */
@Service
public class HyraxInstanceServiceImpl implements HyraxInstanceService {
	//private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
    @Autowired
    private HyraxInstanceRepository hyraxInstanceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public HyraxInstance register(String serverUrl, String reporterUrl, Long ping, int log) throws Exception {
    	//logg.info("register.1/7) register checkpoint, checking domain and version ..."); // <---
        String hyraxVersion = checkDomainNameAndGetVersion(serverUrl);
        
        //logg.info("register.2/7) good domain, hyrax version : "+hyraxVersion); // <---
        if (isEmpty(hyraxVersion)){
        	//logg.info("register.2e) bad domain or hyrax version"); // <---
            throw new IllegalStateException("Bad version, or can not get version of hyrax instance");
        }
        
        //logg.info("register.3/7) checking reporter - /!\\ DISABLED /!\\"); // <---
        //checkReporter(reporterUrl);
        
        //logg.info("register.4/7) reporter passed, saving server - /!\\ DISABLED /!\\"); // <---
        /* 
        // 5/13/19 - SBL - removed redundant code
        hyraxInstanceRepository.streamByName(serverUrl)
                .filter(HyraxInstance::getActive)
                .forEach(a -> {
                    a.setActive(false);
                    hyraxInstanceRepository.save(a);
                });
        */
        
        //logg.info("register.5/7) server saved, retrieving default ping - /!\\ DISABLED /!\\"); // <---
        //Long reporterDefaultPing = getReporterDefaultPing(reporterUrl);
        Long reporterDefaultPing = ping;
        
        //UUID serverId = UUID.randomUUID(); // << << uncomment me later << << 

        //logg.info("register.6/7) default ping retrieved, building hyrax instance ..."); // <---
        HyraxInstance hyraxInstance = HyraxInstance.builder()
                .name(serverUrl)
                .reporterUrl(reporterUrl)
                .log(log)
                .ping(Math.min(ping == null ? Long.MAX_VALUE : ping, reporterDefaultPing))
                .versionNumber(hyraxVersion)
                .registrationTime(LocalDateTime.now())
                .active(true)//                .serverUUID(serverId) // << << uncomment me later << <<
                .build();
        //logg.info("register.7/7) hyrax instance built, returning <<"); // <---
        return hyraxInstanceRepository.save(hyraxInstance);
    }

    @Override
    public Stream<HyraxInstance> allHyraxInstances() {
        return allHyraxInstances(false);
    }

    @Override
    public Stream<HyraxInstance> allHyraxInstances(boolean onlyActive) {
        return onlyActive ? hyraxInstanceRepository.streamByActiveTrue() :
                hyraxInstanceRepository.findAll().stream();
    }
    
    /**
     * SBL - gets hyraxInstance from DB and checks if ping needs to be updated
     * if no update needed, just returns current hyraxInstance
     * @return 
     */
    @Override
    public HyraxInstance updatePing(String serverUrl, long ping) {
    // 1/22/18 - SBL - initial code
    	HyraxInstance hyraxInstance = findHyraxInstanceByName(serverUrl);
    	if (hyraxInstance.getPing() != ping) {
    		hyraxInstance.setPing(ping);
    		return hyraxInstanceRepository.save(hyraxInstance);
    	}
    	else {
    		return hyraxInstance;
    	 }
    }// end updatePing()
    
    /**
     * 
     * @param updateModel
     * @return
     */ // 
    /*
    @Override
    public HyraxInstance updateHyraxInstance(UpdateModel updateModel){
    // 5/20/19 - SBL - initial code
    
    	HyraxInstance hyraxInstance = findHyraxInstanceByName(updateModel.getServerUrl());
    	
    	if(hyraxInstance.getPing() != updateModel.getPing()){
    		hyraxInstance.setPing(updateModel.getPing());
    	}// end if
    	
    	if(hyraxInstance.getLog() != updateModel.getLog()){
    		hyraxInstance.setLog(updateModel.getLog());
    	}// end if 
    	
    	return hyraxInstanceRepository.save(hyraxInstance);
    	
    }// end updateHyraxInstance() //     
    */

    private void checkReporter(String server) throws Exception {
    	//logg.info("checkR.1) checkReporter() entry, calling reporter ..."); // <---
        ResponseEntity<String> entity = restTemplate.getForEntity(new URI(server + "/healthcheck"), String.class);
        //logg.info("checkR.2) reporter returned : "+entity.getStatusCode()); // <---
        if (!entity.getStatusCode().is2xxSuccessful()){
        	//logg.info("checkR.2e) failure"); // <---
            throw new IllegalStateException("Can not find reporter on this Hyrax Instance");
        }
        //logg.info("checkR.3) returning <<"); 
    }

    private Long getReporterDefaultPing(String server) throws Exception {
        ResponseEntity<Long> entity = restTemplate.getForEntity(new URI(server + "/defaultPing"), Long.class);
        return entity.getBody();
    }

    private String checkDomainNameAndGetVersion(String server) throws Exception {
        String xmlString = restTemplate.getForObject(new URI(server + "/version"), String.class);
        XPath xPath =  XPathFactory.newInstance().newXPath();
        return xPath.compile("/HyraxCombinedVersion/Hyrax/@version")
                .evaluate(loadXMLFromString(xmlString));
    }

    private Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    @Override
    public HyraxInstanceDto buildDto(HyraxInstance hyraxInstance) {
        return HyraxInstanceDto.builder()
                .name(hyraxInstance.getName())
                .reporterUrl(hyraxInstance.getReporterUrl())
                .ping(hyraxInstance.getPing())
                .log(hyraxInstance.getLog())
                .versionNumber(hyraxInstance.getVersionNumber())
                .registrationTime(String.valueOf(hyraxInstance.getRegistrationTime()))
                .lastAccessTime(String.valueOf(hyraxInstance.getLastAccessTime()))
                .active(hyraxInstance.getActive())
                .build();
    }

    @Override
    public void updateLastAccessTime(HyraxInstance hi, LocalDateTime localDateTime) {
        HyraxInstance hyraxInstance = hyraxInstanceRepository.findByIdAndActiveTrue(hi.getId());
        hyraxInstance.setLastAccessTime(localDateTime);
        hyraxInstanceRepository.save(hyraxInstance);
    }

    @Override
    public HyraxInstance findHyraxInstanceByName(String hyraxInstanceName) {
        return hyraxInstanceRepository.findByNameAndActiveTrue(hyraxInstanceName);
    }

    @Override
	public void removeHyraxInstance(String hyraxInstanceId) {
    // 5/2/19 - SBL - initial code
    	//logg.info("removeHI.1/2) removeHyraxInstance() entry, calling delete() ...");
    	//logg.info("removeHI.1.1) id : "+ hyraxInstanceId);
		hyraxInstanceRepository.delete(hyraxInstanceId);
		//logg.info("removeHI.2/2) instance deleted, returning <<");
	}//end removeHyraxInstance()
    
}//end class HyraxInstanceServiceImpl
