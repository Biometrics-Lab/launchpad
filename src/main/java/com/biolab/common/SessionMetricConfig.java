package com.biolab.common;

public record SessionMetricConfig(
        Integer conditionalMetricId,
        Integer dataSourceId,
        String dataSourceContent
) {}
