package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RepMetricSourceDto(
        Integer id,
        @NotNull(message = "RepMetricSource repMetricId cannot be null")
        Integer repMetricId,
        @NotNull(message = "RepMetricSource dataSourceId cannot be null")
        Integer dataSourceId,
        String description
) { }

