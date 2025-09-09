package com.biolab.integration.internal.client;

import com.biolab.common.AsyncRequest;
import com.biolab.common.Dictionary;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import com.biolab.hardware.HardwareServiceGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HardwareServiceClientModulith implements HardwareServiceClient {
    private final ApplicationEventPublisher eventPublisher;
    private final HardwareServiceGateway hardwareServiceGateway;

    @Override
    public HealthCheckResponse healthCheck() {
        HealthCheckRequest request = HealthCheckRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .build();
        return hardwareServiceGateway.healthCheck(request);
    }

    @Override
    public void asyncEvent(Object payload) {
        AsyncRequest asyncRequest = AsyncRequest.builder()
                .consumerServiceName(Dictionary.Service.hardware)
                .requestId(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        eventPublisher.publishEvent(asyncRequest);
    }
}
