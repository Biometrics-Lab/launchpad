package com.biolab.common;

import java.sql.Timestamp;
import java.util.List;

public record RepDataReceivedEvent(
        Integer sessionId,
        int repNumber,
        Timestamp startTime,
        List<RepMetricData> metrics,
        List<RepResourceData> resources
) {}
