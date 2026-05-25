package com.biolab.launchpad.internal.web.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AssessmentMetricsDto(
        Integer assessmentId,
        String sport,
        List<MetricAggregateDto> metrics
) {}
