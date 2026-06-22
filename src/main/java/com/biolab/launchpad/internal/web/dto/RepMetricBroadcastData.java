package com.biolab.launchpad.internal.web.dto;

public record RepMetricBroadcastData(
        Integer conditionalMetricId,
        String name,
        Number value
) {}
