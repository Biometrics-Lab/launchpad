package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ConditionalMetricDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "ConditionalMetric conditionId cannot be null")
        Integer conditionId,
        @NotNull(message = "ConditionalMetric metricId cannot be null")
        Integer metricId
) {}
