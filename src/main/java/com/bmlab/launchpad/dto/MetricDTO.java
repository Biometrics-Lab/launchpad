package com.bmlab.launchpad.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricDTO {

    private String name;
    private Integer measurementId;
    private Boolean negate;

}
