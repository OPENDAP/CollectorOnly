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
