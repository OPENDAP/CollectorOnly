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
	// 5/2/19 - SBL - initial code
	// 5/8/19 - SBL - changed function to only query relevant log lines for efficiency 
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
