package com.biolab.common;

public record DataSourceConfig(
        Integer id,
        String name,
        String type,
        String content
) {}
