package org.opendap.harvester.service;

import org.opendap.harvester.entity.document.LogLine;
import org.opendap.harvester.entity.dto.LogLineDto;

import java.util.List;

public interface LogLineService {
    void addLogLines(String hyraxInstanceId, List<LogLineDto> logLineDtoList);
    List<LogLineDto> findLogLines(String hyraxInstanceId);
    List<LogLineDto> findLogLines(String hyraxInstanceId, int count);
    String findLogLinesAsString(String hyraxInstanceId);
    LogLineDto buildDto(LogLine logLine);
    void removeLogLines(String hyraxInstanceId);
}
