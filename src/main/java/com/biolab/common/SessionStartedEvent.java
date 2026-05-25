package com.biolab.common;

import java.util.List;

public record SessionStartedEvent(
        Integer sessionId,
        List<SessionMetricConfig> metrics
) {}
