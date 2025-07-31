package com.bmlab.launchpad.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MetricDTO {

    private String name;
    private Integer measurementId;
    private Boolean negate;

}
