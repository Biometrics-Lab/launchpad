package com.biolab.integration.internal.bat;

import com.biolab.common.SessionMetricConfig;

import java.util.List;

public interface BadSensorService {
    void startSession(Integer sessionId, List<SessionMetricConfig> metrics);
    void stopSession(Integer sessionId);
}
