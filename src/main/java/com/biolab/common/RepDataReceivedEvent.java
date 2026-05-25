package com.biolab.common;

import java.sql.Timestamp;
import java.util.List;

public record RepDataReceivedEvent(
        Integer sessionId,
        Timestamp startTime,
        List<RepMetricData> metrics,
        List<RepResourceData> resources
) {}
