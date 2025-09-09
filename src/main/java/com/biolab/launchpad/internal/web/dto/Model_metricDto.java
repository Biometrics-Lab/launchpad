package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Model_metricDto (
        Integer id,
        @NotNull(message = "Model_metric model_id cannot be null")
        Integer model_id,
        @NotNull(message = "Model_metric metric_id cannot be null")
        Integer metric_id,
        Number value
){}
