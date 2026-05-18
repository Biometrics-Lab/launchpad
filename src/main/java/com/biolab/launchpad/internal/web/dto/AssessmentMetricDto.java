package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssessmentMetricDto(
        Integer id,
        @NotNull(message = "AssesmentMetric assessmentId cannot be null")
        Integer assessmentId,
        @NotNull(message = "AssesmentMetric conditionalMetricId cannot be null")
        Integer conditionalMetricId,
        @NotNull(message = "AssesmentMetric sourceId cannot be null")
        Integer sourceId,
        Number minValue,
        Number maxValue,
        Number avgValue,
        Number lastValue,
        String description
){}
