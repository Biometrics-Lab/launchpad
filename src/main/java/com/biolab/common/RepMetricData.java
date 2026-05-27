package com.biolab.common;

public record RepMetricData(
        Integer conditionalMetricId,
        Integer dataSourceId,
        Number value
) {}
