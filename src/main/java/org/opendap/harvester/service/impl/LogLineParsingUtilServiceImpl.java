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


import org.opendap.harvester.entity.document.LogLine;
import org.opendap.harvester.entity.dto.LogLineDto;
import org.opendap.harvester.service.LogLineParsingUtilService;
import org.springframework.stereotype.Service;

@Service
public class LogLineParsingUtilServiceImpl implements LogLineParsingUtilService {

	@Override
	public Long parseSize(LogLineDto lld) {
		String sizeString = lld.getValues().get("size");
		//log.info("SET - before parse: "+sizeString);
		long sizeInt;
		
		if(sizeString.equals("")) {
			sizeInt = 0;
		}
		else if (sizeString.contains("bytes")) {
			sizeInt = Integer.parseInt(sizeString.substring(0, sizeString.length() - 5).trim());
		}
		else {
			sizeInt = Integer.parseInt(sizeString.trim());
		}
		return sizeInt;
	}//end parseSize(LogLineDto lld)
	
	@Override
	public Long parseSize(LogLine ll) {
		String sizeString = ll.getValues().get("size");
		//log.info("SET - before parse: "+sizeString);
		long sizeInt;
		
		if(sizeString.equals("")) {
			sizeInt = 0;
		}
		else if (sizeString.contains("bytes")) {
			sizeInt = Integer.parseInt(sizeString.substring(0, sizeString.length() - 5).trim());
		}
		else {
			sizeInt = Integer.parseInt(sizeString.trim());
		}
		return sizeInt;
	}//end parseSize(LogLine ll)

	@Override
	public String readibleSize(long size, boolean expanded) {
		long kb = size / 1024;
		long mb = kb / 1024;
		long gb = mb / 1024;
		
		if(size < 1024) {
			return size + " bytes";
		}
		else if (kb < 1024) {
			
			return kb + ((expanded) ? " kilobytes" : " KBs");
		}
		else if (mb < 1024) {
			
			return mb + ((expanded) ? " megabytes" : " MBs");
		}
		else {
			return gb + ((expanded) ? " gigabyte" : " GBs");
		}
	} // end readibleSize(long size, boolean expanded)
	
	@Override
	public String createInterval(long ping) {
		String s = "";
		
		long mins, hours, days;
		mins = ping/60;
		hours = mins/60;
		days = hours/24;
		
		if(days <= 0) {
			
			if (hours <= 0) {
				s = ping +" - "+ mins +"m";
			}// *m
			else if ((mins % 60) == 0) {
				s = ping +" - "+ hours +"h";
			}// *h
			else {
				mins = mins % 60;
				s = ping +" - "+ hours +"h "+ mins +"m";
			}// *h *m
		}
		else {
			mins = mins % 60;
			hours = hours % 24;
			if ((hours == 0)&&(mins == 0)) {
				s = ping +" - "+ days +"d";
			}// *d
			else if(hours == 0) {
				s = ping +" - "+ days +"d "+ mins +"m";
			}// *d *m
			else if(mins == 0) {
				s = ping +" - "+ days +"d "+ hours +"h";
			}// *d *h
			else {
				s = ping +" - "+ days +"d "+ hours +"h "+ mins +"m";
			}// *d *h *m
		}
		
		return s;
	}//end createInterval(long ping)

	public String parseTime(long duration) {
		String time;
		
		if(duration > 1000000000) {
			time = (duration / 1000000000) + " seconds";
		}
		else if (duration > 1000000) {
			time = (duration / 1000000) + " milliseconds";
		}
		else {
			time = duration + " nanoseconds";
		}
		return time;
	} // parseTime(long duration)
	
}