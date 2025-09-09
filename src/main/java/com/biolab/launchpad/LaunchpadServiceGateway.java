package com.biolab.launchpad;

import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import com.biolab.integration.IntegrationServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class LaunchpadServiceGateway {
    private final ApplicationEventPublisher events;
    private final IntegrationServiceGateway integrationServiceGateway;

    public HealthCheckResponse healthCheck(HealthCheckRequest request) {
        log.info("{} - health check - OK: {}", this.getClass().getSimpleName(), request);
        return HealthCheckResponse.builder()
                .requestId(request.requestId())
                .status("OK")
                .build();
    }
}
