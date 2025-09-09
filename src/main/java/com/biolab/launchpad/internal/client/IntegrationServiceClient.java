package com.biolab.launchpad.internal.client;

import com.biolab.common.HealthCheckResponse;

public interface IntegrationServiceClient {
    HealthCheckResponse healthCheck();
    void asyncEvent(Object payload);
}
