package com.biolab.launchpad.internal.web.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record PlayerMetricsDto(
        List<MetricAggregateDto> overall,
        List<AssessmentMetricsDto> assessments
) {}
