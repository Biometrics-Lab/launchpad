package com.biolab.launchpad.internal.web.dto;

import java.sql.Timestamp;
import java.util.List;

public record RepBroadcastDto(
        Integer id,
        Integer sessionId,
        Integer repNumber,
        Timestamp startTime,
        List<RepMetricBroadcastData> metrics,
        List<RepResourceBroadcastData> resources
) {}
