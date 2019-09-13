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

import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogDataDto;
import org.opendap.harvester.service.LogCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public LogDataDto collectLogs(HyraxInstance hyraxInstance, LocalDateTime since) {
        try {
            return restTemplate.getForObject(
                    new URI(hyraxInstance.getReporterUrl() + "/log?since=" + since),
                    LogDataDto.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
