package org.opendap.harvester.entity.document;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class DownTimes {
	@Getter @Setter
	LocalDateTime start;
	@Getter @Setter
	LocalDateTime end;
	
	public DownTimes() {
		start = null;
		end = null;
	}
}
