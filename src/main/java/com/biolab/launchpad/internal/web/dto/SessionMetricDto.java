package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SessionMetricDto(
        Integer id,
        @NotNull(message = "SessionMetric sessionId cannot be null")
        Integer session1Id,
        @NotNull(message = "SessionMetric conditionalMetricId cannot be null")
        Integer conditionalMetricId,
        Number minValue,
        Number maxValue,
        Number avgValue
) { }

