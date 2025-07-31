package com.bmlab.launchpad.dto;

import lombok.Data;

@Data
public class MetricDTO {

    private String name;
    private Integer measurementId;
    private Boolean negate;

}
