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

import org.opendap.harvester.dao.HyraxInstanceRepository;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.dto.LogDataDto;
import org.opendap.harvester.service.LogCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Component which will be associated with each active HyraxInstance.
 * It will create cron behaviour and will call LogCollector for getting logs.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LogScheduler {
	//private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
    @Autowired
    private HyraxInstanceRepository hyraxInstanceRepository;

    @Autowired
    private LogCollectorService logCollectorService;

    private HyraxInstance hyraxInstance;
    private ScheduledExecutorService scheduler;

    public void setUp(String hyraxInstanceId){
        if (hyraxInstance != null){
            throw new IllegalStateException("Instance is already constructed");
        }
        hyraxInstance = hyraxInstanceRepository.findByIdAndActiveTrue(hyraxInstanceId);
        scheduler = Executors.newScheduledThreadPool(1);
        Long ping = hyraxInstance.getPing();
        scheduler.scheduleAtFixedRate(getTask(), ping, ping, TimeUnit.SECONDS);
    }

    private Runnable getTask() {
        return () -> {
            System.out.println("--------------");
            System.out.println(Instant.now().toString());
            //logg.info("Getting Logs from " + hyraxInstance.getName());
            System.out.println("Getting Logs from " + hyraxInstance.getName());
            LogDataDto logDataDto = logCollectorService.collectAllLogs(hyraxInstance);
            int size = logDataDto.getLines().size();
            System.out.println("Received: " + size + " lines");
            System.out.println("--------------");
        };
    }
}
