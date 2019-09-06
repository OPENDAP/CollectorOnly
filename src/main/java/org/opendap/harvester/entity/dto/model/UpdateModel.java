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

/**
 * Model class for updating Hyrax Instances
 */
package org.opendap.harvester.entity.dto.model;

import java.util.UUID;
import org.hibernate.validator.constraints.URL;
import lombok.Getter;
import lombok.Setter;

public class UpdateModel {
	@Getter @Setter
	@URL
	private String serverUrl;
    @Getter @Setter
    @URL
    private String reporterUrl;
    @Getter @Setter
    private Long ping;
    @Getter @Setter
    private int log;
    @Getter @Setter
    private UUID serverUUID;
}
