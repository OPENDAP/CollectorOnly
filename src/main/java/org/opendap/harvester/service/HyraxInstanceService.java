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

package org.opendap.harvester.service;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.HyraxInstanceDto;

public interface HyraxInstanceService {
    HyraxInstance register(String serverUrl, String reporterUrl, Long ping, int log) throws Exception;
    Stream<HyraxInstance> allHyraxInstances();
    Stream<HyraxInstance> allHyraxInstances(boolean onlyActive);
    HyraxInstanceDto buildDto(HyraxInstance hyraxInstance);
    
    void updateLastAccessTime(HyraxInstance hi, LocalDateTime localDateTime);
    void updateLastSuccessPullTime(HyraxInstance hi, LocalDateTime localDateTime);
    void updateActiveStatus(HyraxInstance hi, boolean active);
    void updateAccessibleStatus(HyraxInstance hi, boolean accessible);
    
    String getHyraxVersion(String serverUrl);
    String getReporterVersion(String reporterUrl);
    
    HyraxInstance findHyraxInstanceByName(String hyraxInstanceName);
    public HyraxInstance updatePing(String serverUrl, long ping);
    //TODO implement 'public HyraxInstance updateHyraxInstance(UpdateModel updateModel)'. sbl 7.2.19 ;
    public void saveHyraxInstance(HyraxInstance hi);
    public void removeHyraxInstance(String hyraxInstanceId);
}
