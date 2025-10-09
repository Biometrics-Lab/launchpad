package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TemplateMetricDto(
        Integer id,
        @NotNull(message = "TemplateMetric templateId cannot be null")
        Integer templateId,
        @NotNull(message = "TemplateMetric metricId cannot be null")
        Integer metricId,
        @NotNull(message = "TemplateMetric sourceId cannot be null")
        Integer sourceId
) { }

