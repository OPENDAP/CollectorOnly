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
	private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	private boolean logOutput = false;
	
    @Autowired
    private HyraxInstanceRepository hyraxInstanceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public HyraxInstance register(String serverUrl, String reporterUrl, Long ping, int log) throws Exception {
    	//logg.info("register.1/7) register checkpoint, checking domain and version ...");
        String hyraxVersion = checkDomainNameAndGetVersion(serverUrl);
        
        //logg.info("register.2/7) good domain, hyrax version : "+hyraxVersion);
        if (isEmpty(hyraxVersion)){
        	//logg.info("register.2e) bad domain or hyrax version");
            throw new IllegalStateException("Bad version, or can not get version of hyrax instance");
        }
        
        //logg.info("register.3/7) checking reporter - /!\\ DISABLED /!\\"); //
        //disabled due to method causing race condition. sbl 7.2.19
        //checkReporter(reporterUrl);
        
        //logg.info("register.4/7) reporter passed, saving server - /!\\ DISABLED /!\\");
        //removed due to method incorrectly handling reporter servers updates. sbl 7.2.19
        /* 
        // 5/13/19 - SBL - removed redundant code
        hyraxInstanceRepository.streamByName(serverUrl)
                .filter(HyraxInstance::getActive)
                .forEach(a -> {
                    a.setActive(false);
                    hyraxInstanceRepository.save(a);
                });
        */
        
        //logg.info("register.5/7) server saved, retrieving default ping - /!\\ DISABLED /!\\");
        //disabled due to method causing race condition. sbl 7.2.19
        //Long reporterDefaultPing = getReporterDefaultPing(reporterUrl);
        Long reporterDefaultPing = ping;
        
        UUID serverId = UUID.randomUUID();

        //logg.info("register.6/7) default ping retrieved, building hyrax instance ...");
        HyraxInstance hyraxInstance = HyraxInstance.builder()
                .name(serverUrl)
                .reporterUrl(reporterUrl)
                .log(log)
                .ping(Math.min(ping == null ? Long.MAX_VALUE : ping, reporterDefaultPing))
                .versionNumber(hyraxVersion)
                .serverVersionNumber(hyraxVersion)
                .reporterVersionNumber("*.*.*")
                .registrationTime(LocalDateTime.now())
                .active(true)//                
                .serverUUID(serverId)
                .build();
        //logg.info("register.6.1/7) UUID : "+serverId); // <---
        //logg.info("register.7/7) hyrax instance built, returning <<");
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
    	HyraxInstance hyraxInstance = findHyraxInstanceByName(serverUrl);
    	if (hyraxInstance.getPing() != ping) {
    		hyraxInstance.setPing(ping);
    		return hyraxInstanceRepository.save(hyraxInstance);
    	}
    	else {
    		return hyraxInstance;
    	 }
    }// end updatePing()
    
    @Override
    public void updateActiveStatus(HyraxInstance hi, boolean active) {
    	//HyraxInstance hyraxInstance = hyraxInstanceRepository.findByIdAndActiveTrue(hi.getId());
    	HyraxInstance hyraxInstance = hyraxInstanceRepository.findById(hi.getId());
    	hyraxInstance.setActive(active);
    	hyraxInstanceRepository.save(hyraxInstance);
    }//updateActiveStatus()
    
    @Override
    public void updateAccessibleStatus(HyraxInstance hi, boolean accessible) {
    	HyraxInstance hyraxInstance = hyraxInstanceRepository.findById(hi.getId());
    	hyraxInstance.setAccessible(accessible);
    	hyraxInstanceRepository.save(hyraxInstance);
    }//updateAccessibleStatus()
    
    /**
     * 
     * @param updateModel
     * @return
     */ // TODO implement update method. sbl 7.2.19
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
    
    public String getHyraxVersion(String serverUrl) {
    	if(logOutput) { logg.info("getHyraxVersion) start, calling checkDomainNameAndGetVersion() ...");}
    	String hyraxVer = "error";
    	try {
			hyraxVer = checkDomainNameAndGetVersion(serverUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(logOutput) { logg.info("getHyraxVersion) ... called checkDomainNameAndGetVersion()");}
    	if(logOutput) { logg.info("getHyraxVersion) hyraxVersion = '"+hyraxVer+"'");}
    	
        if (isEmpty(hyraxVer)){
        	hyraxVer = "error";
        }
    	
    	if(logOutput) { logg.info("getHyraxVersion) returning <<<");}
    	return hyraxVer;
    }
    
    public String getReporterVersion(String reporterUrl){
    	if(logOutput) { logg.info("getReporterVersion) start, calling checkreporter() ...");}
    	String reporterVer = "error";
    	try {
			reporterVer = checkReporter(reporterUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(logOutput) { logg.info("getReporterVersion) ... called checkreporter(), version = '"+reporterVer+"'");}
    	
        if (isEmpty(reporterVer)){
        	reporterVer = "error";
        }
        
    	if(logOutput) { logg.info("getReporterVersion) returning <<<");}
    	return reporterVer;
    }

    private String checkReporter(String server) throws Exception {
    	if(logOutput) { logg.info("checkReporter) checkReporter() entry, calling reporter ...");}
    	
    	//////////////////////////////////////////////////////////////////////
    	// CALL THE REPORTER
        ResponseEntity<String> entity = restTemplate.getForEntity(new URI(server + "/healthcheck"), String.class);
        if(logOutput) { logg.info("checkReporter) reporter returned : "+entity.getStatusCode());}
        if (!entity.getStatusCode().is2xxSuccessful()){
        	if(logOutput) { logg.info("checkReporter.e) failure"); }
            throw new IllegalStateException("Can not find reporter on this Hyrax Instance");
        }
        
    	//////////////////////////////////////////////////////////////////////
    	// PARSE THE VERSION
        String version = entity.getBody();
        version = version.substring(18);
        
        if(logOutput) { logg.info("checkReporter) returning <<"); }
        return version;
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
                .registrationTime(hyraxInstance.getRegistrationTime())
                .lastAccessTime(hyraxInstance.getLastAccessTime())
                .serverUUID(hyraxInstance.getServerUUID())
                .active(hyraxInstance.getActive())
                .accessible(hyraxInstance.getAccessible())
                .lastSuccessfulPull(hyraxInstance.getLastSuccessfulPull())
                .reporterRunning(hyraxInstance.getReporterRunning())
                .serverRunning(hyraxInstance.getServerRunning())
                .build();
    }

    @Override
    public void updateLastAccessTime(HyraxInstance hi, LocalDateTime localDateTime) {
        //HyraxInstance hyraxInstance = hyraxInstanceRepository.findByIdAndActiveTrue(hi.getId());
    	HyraxInstance hyraxInstance = hyraxInstanceRepository.findById(hi.getId());
        hyraxInstance.setLastAccessTime(localDateTime);
        hyraxInstanceRepository.save(hyraxInstance);
    }
    
    public void updateLastSuccessPullTime(HyraxInstance hi, LocalDateTime localDateTime) {
    	HyraxInstance hyraxInstance = hyraxInstanceRepository.findById(hi.getId());
        hyraxInstance.setLastSuccessfulPull(localDateTime);
        hyraxInstanceRepository.save(hyraxInstance);
    }

    @Override
    public HyraxInstance findHyraxInstanceByName(String hyraxInstanceName) {
        //return hyraxInstanceRepository.findByNameAndActiveTrue(hyraxInstanceName);
        return hyraxInstanceRepository.findByName(hyraxInstanceName);
    }
    
    public void saveHyraxInstance(HyraxInstance hi) {
    	hyraxInstanceRepository.save(hi);
    }
    
    @Override
	public void removeHyraxInstance(String hyraxInstanceId) {
    	//logg.info("removeHI.1/2) removeHyraxInstance() entry, calling delete() ...");
    	//logg.info("removeHI.1.1) id : "+ hyraxInstanceId);
		hyraxInstanceRepository.delete(hyraxInstanceId);
		//logg.info("removeHI.2/2) instance deleted, returning <<");
	}//end removeHyraxInstance()


    
}//end class HyraxInstanceServiceImpl
