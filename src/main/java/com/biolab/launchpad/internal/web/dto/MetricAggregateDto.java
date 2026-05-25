package com.biolab.launchpad.internal.web.dto;

import lombok.Builder;

@Builder
public record MetricAggregateDto(
        Integer conditionalMetricId,
        String name,
        Boolean negated,
        Double minValue,
        Double maxValue,
        Double avgValue
) {}
