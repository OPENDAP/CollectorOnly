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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
    public List<LogLineDto> findLogLines(String hyraxInstanceId, String monthYear) {
    	
    	//logg.info("entering findLogLines() ... - startMonth : '"+monthYear+"'");
    	List<LogLineDto> logs = logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList());
    	
    	String endmmYYYY = determineEndMonth(monthYear);
    	int startIndex = -1;
    	int endIndex = -1;
    	int count = 0;
    	
    	//logg.info("findLogLines() | determining start and end");
    	for (LogLineDto lld : logs) {
    		String mmYYYY = convertDateToString(lld.getValues().get("localDateTime"));
    		//logg.info("findLogLines() | '"+mmYYYY+"'");
    		
    		if (mmYYYY.equals(monthYear) && startIndex == -1) {
    			//logg.info("findLogLines() | found start - "+ mmYYYY);
    			startIndex = logs.indexOf(lld);
    			count = 1;
    		}
    		else if (mmYYYY.equals(monthYear)) {
    			count++;
    		}
    		
    		if (mmYYYY.equals(endmmYYYY) && endIndex == -1) {
    			//logg.info("findLogLines() | found end - "+ mmYYYY);
    			endIndex = logs.indexOf(lld) - 1;
    			break;
    		}
    	}
    	
    	if(startIndex == -1) {
    		//logg.info("findLogLines() | didnt find start");
    		startIndex = 0;
    	}
    	
    	if(endIndex == -1) {
    		if (count > 0) {
    			//logg.info("findLogLines() | didnt find end but possitive count");
    			endIndex = startIndex + count;
    			//logg.info("findLogLines() | endindex - "+endIndex);
    		}
    		else {
    		//logg.info("findLogLines() | didnt find end");
    		endIndex = logs.size();
    		}
    	}
    	
    	//logg.info("findLogLines() | returning ...");
        return logs.subList(startIndex, endIndex);
    }//findLogLines()
    
	private String convertDateToString(String rubbish) {
		
		SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		Date date = null;
		
		try {
			date = formatter6.parse(rubbish);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();
		String readible = determineMonth(month) +" "+ year;
		
		return readible;
		
	}//convertDateToString()
	
	private String determineMonth(int num) {
		switch (num) {
		case 1:
			return "January";
		case 2:
			return "Febuary";
		case 3:
			return "March";
		case 4:
			return "April";
		case 5:
			return "May";
		case 6:
			return "June";
		case 7:
			return "July";
		case 8:
			return "August";
		case 9:
			return "September";
		case 10:
			return "October";
		case 11:
			return "November";
		case 12:
			return "December";
		default:
			return "NoMonth!!!WeAreAllGonnaDie!!!!";
		}
	}
	
	private String determineEndMonth(String startMonth) {
		//logg.info("entering determineEndMonth() ...");
		String month = startMonth.substring(0, startMonth.length() - 4).trim();
		int end;
		int year = Integer.parseInt(startMonth.substring(startMonth.length() - 4));
		
		//logg.info("determineEndMonth() | substring month - '"+month+"'");
		
		switch (month){
		case "January":
			end = 2;
			break;
		case "Febuary":
			end = 3;
			break;
		case "March":
			end = 4;
			break;
		case "April":
			end = 5;
			break;
		case "May":
			end = 6;
			break;
		case "June":
			end = 7;
			break;
		case "July":
			end = 8;
			break;
		case "August":
			end = 9;
			break;
		case "September":
			end = 10;
			break;
		case "October":
			end = 11;
			break;
		case "November":
			end = 12;
			break;
		case "December":
			end = 1;
			year++;
			break;
		default:
			end = 0;
			break;
		}
		
		//logg.info("determineEndMonth() | end switch: "+ end + " - "+ year);
		String endMonth = determineMonth(end);
		//logg.info("determineEndMonth() | endMonth: "+ endMonth + " - "+ year);
		//logg.info("determineEndMonth() | returning ...");
		return endMonth +" "+ year;
	}
    
    @Override
    public int findNumberLogLines(String hyraxInstanceId) {
    	List<LogLineDto> logs = logLineRepository.streamByHyraxInstanceId(hyraxInstanceId)
                .map(this::buildDto)
                .collect(Collectors.toList());
    	return logs.size();
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
