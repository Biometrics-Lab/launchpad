package com.biolab.launchpad.internal.client;

import com.biolab.common.AsyncRequest;
import com.biolab.common.Dictionary;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import com.biolab.integration.IntegrationServiceGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationServiceClientModulith implements IntegrationServiceClient {
    private final ApplicationEventPublisher eventPublisher;
    private final IntegrationServiceGateway integrationServiceGateway;

    @Override
    public HealthCheckResponse healthCheck() {
        HealthCheckRequest request = HealthCheckRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .build();
        return integrationServiceGateway.healthCheck(request);
    }

    @Override
    public void asyncEvent(Object payload) {
        AsyncRequest asyncRequest = AsyncRequest.builder()
                .consumerServiceName(Dictionary.Service.integration)
                .requestId(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        eventPublisher.publishEvent(asyncRequest);
    }
}
