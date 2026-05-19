package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ModelMetricDto(
        Integer id,
        @NotNull(message = "ModelMetric modelId cannot be null")
        Integer modelId,
        @NotNull(message = "ModelMetric conditionalMetricId cannot be null")
        Integer conditionalMetricId,
        Number value
){}
