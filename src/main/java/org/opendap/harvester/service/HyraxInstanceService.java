package org.opendap.harvester.service;

import org.opendap.harvester.entity.dto.HyraxInstanceDto;
import org.opendap.harvester.entity.document.HyraxInstance;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface HyraxInstanceService {
    HyraxInstance register(String serverUrl, String reporterUrl, Long ping, int log) throws Exception;
    Stream<HyraxInstance> allHyraxInstances();
    Stream<HyraxInstance> allHyraxInstances(boolean onlyActive);
    HyraxInstanceDto buildDto(HyraxInstance hyraxInstance);
    void updateLastAccessTime(HyraxInstance hi, LocalDateTime localDateTime);
    HyraxInstance findHyraxInstanceByName(String hyraxInstanceName);
    public HyraxInstance updatePing(String serverUrl, long ping);
    //TODO implement 'public HyraxInstance updateHyraxInstance(UpdateModel updateModel)'. sbl 7.2.19 ;
    public void removeHyraxInstance(String hyraxInstanceId); 
}
