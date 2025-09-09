package com.biolab.integration.internal.client;

import com.biolab.common.AsyncRequest;
import com.biolab.common.HealthCheckResponse;

public interface HardwareServiceClient {
    HealthCheckResponse healthCheck();
    void asyncEvent(Object payload);
}
