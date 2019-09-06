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
import org.opendap.harvester.dao.HyraxInstanceRepository;
import org.opendap.harvester.dao.LogLineRepository;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.document.LogLine;
import org.opendap.harvester.entity.dto.LogLineDto;
import org.opendap.harvester.service.LogLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogLineServiceImpl implements LogLineService {
	//private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	
    @Autowired
    private HyraxInstanceRepository hyraxInstanceRepository;

    @Autowired
    private LogLineRepository logLineRepository;

    @Override
    public void addLogLines(String hyraxInstanceId, List<LogLineDto> logLineDtoList) {
        HyraxInstance hyraxInstance = hyraxInstanceRepository.findByIdAndActiveTrue(hyraxInstanceId);
        if (hyraxInstance != null) {
            List<LogLine> logLines = logLineDtoList.stream()
                    .map(dto -> LogLine.builder()
                            .hyraxInstanceId(hyraxInstanceId)
                            .values(dto.getValues())
                            .build())
                    .collect(Collectors.toList());
            logLineRepository.save(logLines);
        }
    }

    @Override
    public List<LogLineDto> findLogLines(String hyraxInstanceId) {
        return logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList());
    }

    @Override
    public String findLogLinesAsString(String hyraxInstanceId) {
        return logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .map(LogLineDto::toString)
                .collect(Collectors.joining("\r\n"));
    }

    @Override
    public LogLineDto buildDto(LogLine logLine) {
        return LogLineDto.builder()
                .values(logLine.getValues())
                .build();
    }

	@Override
	public void removeLogLines(String hyraxInstanceId) {
		//logg.info("removeLL.1/3) removeLogLines() entry, finding log lines ...");
		List<LogLine> logLines = logLineRepository.streamByHyraxInstanceId(hyraxInstanceId).collect(Collectors.toList());
		//logg.info("removeLL.2/3) log lines found, entering forloop ...");
		//int x = 0; // <-- used during debugging
		for(LogLine line : logLines) {
			//logg.info("removeLL.2."+x+".1) testing : "+line.getId());
			//logg.info("removeLL.2."+x+".2) hyrax : "+line.getHyraxInstanceId() +" =?= "+ hyraxInstanceId);
			//logg.info("removeLL.2."+x+".3) removing");
			logLineRepository.delete(line.getId());
			//x++;
		}//end for
		//logg.info("removeLL.3/3) lines deleted, returning <<");
	}
}
