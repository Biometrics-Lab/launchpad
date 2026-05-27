package com.biolab.common;

public record SessionMetricConfig(
        Integer conditionalMetricId,
        DataSourceConfig dataSource
) {}
