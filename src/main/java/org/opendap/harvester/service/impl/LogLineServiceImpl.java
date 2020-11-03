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
import org.opendap.harvester.dao.MonthTotalsRepository;
import org.opendap.harvester.entity.document.HyraxInstance;
import org.opendap.harvester.entity.document.LogLine;
import org.opendap.harvester.entity.document.MonthTotals;
import org.opendap.harvester.entity.dto.LogLineDto;
import org.opendap.harvester.service.DateTimeUtilService;
import org.opendap.harvester.service.LogLineParsingUtilService;
import org.opendap.harvester.service.LogLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogLineServiceImpl implements LogLineService {
	private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	private boolean logOutput = true;
	
    @Autowired
    private HyraxInstanceRepository hyraxInstanceRepository;

    @Autowired
    private LogLineRepository logLineRepository;
    
    @Autowired
    private MonthTotalsRepository monthTotalsRepository;
    
    @Autowired 
    private LogLineParsingUtilService logLineParsingUtilService;

    @Autowired
    private DateTimeUtilService dateTimeUtilService;

    @Override
    public void addLogLines(String hyraxInstanceId, List<LogLineDto> logLineDtoList) {
    	if (logOutput) {logg.info("\naddLogLines() : Entering fct ...");}
        HyraxInstance hyraxInstance = hyraxInstanceRepository.findByIdAndActiveTrue(hyraxInstanceId);
        if (logOutput) {logg.info("addLogLines() : hyraxInstance "+ hyraxInstance.getName());}
        
		////////////////////////////////////////////////////////////////////////////
		// BUILDING LOG LINES
		///////////////////////////////////////
        
        if (logOutput) {logg.info("addLogLines() : building log lines ...");}
        if (hyraxInstance != null) {
            List<LogLine> logLines = logLineDtoList.stream()
                    .map(dto -> LogLine.builder()
                            .hyraxInstanceId(hyraxInstanceId)
                            .monthId(dateTimeUtilService.convertDateToString(dto.getValues().get("localDateTime")))
                            .values(dto.getValues())
                            .build())
                    .collect(Collectors.toList());
            
            ////////////////////////////////////////////////////////////////////////////
            // SETUP FOR MONTHLY TOTALS
            ///////////////////////////////////////
            
            if (logOutput) {logg.info("addLogLines() : ... log lines built");}
            //setup for calculating monthTotals 
            MonthTotals monthTotal = null;
            String curMonth = null;
            
			////////////////////////////////////////////////////////////////////////////
			// COMPILING MONTH TOTALS
			///////////////////////////////////////
            
            if (logOutput) {logg.info("addLogLines() : " + logLines.size() + " logs - calculating monthly totals ...");}
            for(LogLine ll : logLines) {
            	// for-each logLine we have received
            	// if (logOutput) {logg.info("addLogLines() : loop " + logLines.indexOf(ll));}
            	if (curMonth == null) {
            		if (logOutput) {logg.info("addLogLines() : curMonth == null, first loop");}
            		//for the first logLine we process, init the whole process
	            	if(monthTotalsRepository.existsMonthTotalByHyraxInstanceIdAndMonthId(ll.getHyraxInstanceId(), ll.getMonthId())) {
	            		if (logOutput) {logg.info("addLogLines() : monthTotal exists, retrieving ...");}
	            		//check if MonthTtotal exists, if so retrieve
	            		monthTotal = monthTotalsRepository.findByHyraxInstanceIdAndMonthId(ll.getHyraxInstanceId(), ll.getMonthId());
	            		// set curMonth
	            		curMonth = ll.getMonthId();
	            		if (logOutput) {logg.info("addLogLines() : ... retrieved - month : " + monthTotal.getMonthId());}
	            	} 
	            	else {
	            		if (logOutput) {logg.info("addLogLines() : monthTotal does not exists ...");}
	            		// MonthTotal doesn't exist, generate a new MonthTotal
	            		monthTotal = MonthTotals.builder()
	            				.hyraxInstanceId(ll.getHyraxInstanceId())
	            				.monthId(ll.getMonthId())
	            				.logCount((long) 0)
	            				.byteCount((long) 0)
	            				.build();
	            		//set curMonth
	            		curMonth = ll.getMonthId();
	            		if (logOutput) {logg.info("addLogLines() : ... generated - month : "+monthTotal.getMonthId());}	            		
	            	} 
            	} //end if
            	else if (!ll.getMonthId().equals(curMonth)) {
            		if (logOutput) {logg.info("addLogLines() : logline.Month != curmonth, curmonth : '" + curMonth + "' - log month : '" + ll.getMonthId() +"'");}
            		//if in the same month
	            	if(monthTotalsRepository.existsMonthTotalByHyraxInstanceIdAndMonthId(ll.getHyraxInstanceId(), ll.getMonthId())) {
	            		if (logOutput) {logg.info("addLogLines() : monthTotal exists, retrieving ...");}
	            		//check if MonthTtotal exists, if so retrieve
	            		monthTotal = monthTotalsRepository.findByHyraxInstanceIdAndMonthId(ll.getHyraxInstanceId(), ll.getMonthId());
	            		// set curMonth
	            		curMonth = ll.getMonthId();
	            		if (logOutput) {logg.info("addLogLines() : ... retrieved - month : " + monthTotal.getMonthId());}
	            	} 
	            	else {
	            		if (logOutput) {logg.info("addLogLines() : monthTotal does not exists ...");}
	            		// MonthTotal doesn't exist
	            		if (monthTotal != null) {
	            			if (logOutput) {logg.info("addLogLines() : saving pervious month - old month : "+monthTotal.getMonthId());}
	            			//checking if a monthTotal already init-ed, if so save it before generating new one
	            			monthTotalsRepository.save(monthTotal);
	            		}
	            		// generate a new MonthTotal
	            		monthTotal = MonthTotals.builder()
	            				.hyraxInstanceId(ll.getHyraxInstanceId())
	            				.monthId(ll.getMonthId())
	            				.logCount((long) 0)
	            				.byteCount((long) 0)
	            				.build();
	            		// set curMonth
	            		curMonth = ll.getMonthId();
	            		if (logOutput) {logg.info("addLogLines() : ... generated - new month : "+monthTotal.getMonthId());}	            		
	            	} 
            	} //end else if
            	
            	// increment the log count for the month
            	monthTotal.incrementLogCount();
            	// add the log line bytes to the total bytes for the month
            	monthTotal.addToByteCount(logLineParsingUtilService.parseSize(ll));
            	
            } //end foreach loop
            if (logOutput) {logg.info("addLogLines() : ... monthly totals calculated");}
            
			////////////////////////////////////////////////////////////////////////////
			// SAVING LOG LINES AND MONTH TOTALS
			///////////////////////////////////////
            
            if (logOutput) {logg.info("addLogLines() : saving log lines and monthly totals ...");}
            // save the monthTotal and the logLines
            
            if (monthTotal != null) {monthTotalsRepository.save(monthTotal);}
            logLineRepository.save(logLines);
            
            if (logOutput) {logg.info("addLogLines() : ... log lines and monthly totals saved");}
            
            if (logOutput) {logg.info("addLogLines() : returning << ");}
        } //end if 
    } //end addLogLines

    @Override
    public List<LogLineDto> findLogLines(String hyraxInstanceId) {
    	List<LogLineDto> logs =logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList());
    	return logs;
    }
    
    @Override
    public List<LogLine> findLogLinesVer2(String hyraxInstanceId) { // <-- temporary ... maybe, SBL 8/10/20
    	List<LogLine> logs = logLineRepository.findByHyraxInstanceId(hyraxInstanceId);
                //.collect(Collectors.toList());
    	return logs;
    }
    
    public void saveLogLine(LogLine logline) { // <-- temporary ... maybe, SBL 8/10/20
    	logLineRepository.save(logline);
    }
    
    @Override
    public List<LogLineDto> findLogLines(String hyraxInstanceId, int count) {
    	List<LogLineDto> logs = logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList());
    	
    	int index = logs.size() - count;
    	if( index < 0) {
    		index = 0;
    	}
    	
        return logs.subList(index, logs.size());
    }
    
    @Override
    public List<LogLineDto> findLogLines(String hyraxInstanceId, int start, int end) {
    	List<LogLineDto> logs = logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList()).subList(start, end);
                
        return logs;
    }
    
    @Override
    public List<LogLineDto> findLogLines(String hyraxInstanceId, String monthYear) {
    	// TODO change over to using hyraxInstanceId and monthID
    	//logg.info("entering findLogLines() ... - startMonth : '"+monthYear+"'");
    	List<LogLineDto> logs = logLineRepository.streamByHyraxInstanceIdAndMonthId(hyraxInstanceId, monthYear) 
                .map(this::buildDto)
                .collect(Collectors.toList());
    	
        return logs;
    }//findLogLines()
	
    @Override
    public long findNumberLogLines(String hyraxInstanceId) {
    	return logLineRepository.countByHyraxInstanceId(hyraxInstanceId);
    	/*
    	List<LogLineDto> logs = logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList());
    	return logs.size();
    	*/
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
