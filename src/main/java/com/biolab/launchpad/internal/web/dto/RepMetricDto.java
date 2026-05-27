package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RepMetricDto(
        Integer id,
        @NotNull(message = "RepMetric repId cannot be null")
        Integer repId,
        @NotNull(message = "RepMetric conditionalMetricId cannot be null")
        Integer conditionalMetricId,
        Integer dataSourceId,
        Number value
) { }
