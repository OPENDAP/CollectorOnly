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

import org.opendap.harvester.dao.MonthTotalsRepository;
import org.opendap.harvester.entity.document.MonthTotals;
import org.opendap.harvester.service.MonthTotalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service implementation. All business logic should be here.
 * Call to db are initiating from this place via Repositories
 */
@Service
public class MonthTotalsServiceImpl implements MonthTotalsService {
	//private static final Logger logg = LoggerFactory.getLogger(HarvesterApplication.class);
	
    @Autowired
    private MonthTotalsRepository monthTotalsRepository;

	@Override
	public void addMonthTotal(String hyraxInstanceId, String mmYYYY, int logs, int bytes) {
		MonthTotals monthTotal = MonthTotals.builder()
				.hyraxInstanceId(hyraxInstanceId)
				.monthId(mmYYYY)
				.logCount((long) logs)
				.byteCount((long) bytes)
				.build();
		monthTotalsRepository.save(monthTotal);
	} //end addMonthTotal()

	@Override
	public List<MonthTotals> findAllMonthTotals(String hyraxInstanceId) {
		List<MonthTotals> monthTotals = monthTotalsRepository.findByHyraxInstanceId(hyraxInstanceId);
		return monthTotals;
	} //end findAllMonthTotals

	@Override
	public MonthTotals findMonthTotal(String hyraxInstanceId, String monthYear) {
		MonthTotals monthTotal = monthTotalsRepository.findByHyraxInstanceIdAndMonthId(hyraxInstanceId, monthYear);
		return monthTotal;
	} //end findMonthTotal

	@Override
	public void saveMonthTotals(MonthTotals monthTotal) {
		monthTotalsRepository.save(monthTotal);
	} //end saveMonthTotals

	@Override
	public void clearMonthTotals(String hyraxInstanceId, String mmYYYY) {
		MonthTotals monthTotal = monthTotalsRepository.findByHyraxInstanceIdAndMonthId(hyraxInstanceId, mmYYYY);
		monthTotal.reset();
		monthTotalsRepository.save(monthTotal);
		
	} //end clearMonthTotals

	@Override
	public void clearAllMonthTotals(String hyraxInstanceId) {
		List<MonthTotals> monthTotals = monthTotalsRepository.findByHyraxInstanceId(hyraxInstanceId);
		for(MonthTotals mt : monthTotals) {
			mt.reset();
			monthTotalsRepository.save(mt);
		} //end foreach
	} //end clearAllMonthTotals


    
}//end class MonthTotalsServiceImpl
