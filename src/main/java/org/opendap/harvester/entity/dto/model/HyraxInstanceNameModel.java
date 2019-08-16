package org.opendap.harvester.entity.dto.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class HyraxInstanceNameModel {
    @Getter @Setter
    @URL
    @NotBlank
    private String hyraxInstanceName;
    @Getter @Setter
    private boolean bonus = false;
}
