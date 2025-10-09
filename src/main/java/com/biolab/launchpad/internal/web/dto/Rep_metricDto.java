package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Rep_metricDto(
        Integer id,
        @NotNull(message = "RepMetric repId cannot be null")
        Integer repId,
        @NotNull(message = "RepMetric metricId cannot be null")
        Integer metricId,
        Number value
) { }

