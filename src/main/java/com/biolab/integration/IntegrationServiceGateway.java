package com.biolab.integration;

import com.biolab.common.AsyncRequest;
import com.biolab.common.Dictionary;
import com.biolab.common.ResourceContentType;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import com.biolab.common.ResourceCreatedEvent;
import com.biolab.common.UrlType;
import com.biolab.integration.internal.service.ResourceStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class IntegrationServiceGateway {

    private final ResourceStorageService resourceStorageService;
    private final ApplicationEventPublisher eventPublisher;

    public HealthCheckResponse healthCheck(HealthCheckRequest request) {
        log.info("{} - health check - OK: {}", this.getClass().getSimpleName(), request);
        return HealthCheckResponse.builder()
                .requestId(request.requestId())
                .status("OK")
                .build();
    }

    @ApplicationModuleListener
    public void asyncEvent(AsyncRequest request) {
        log.info("{} - async event received - OK: {}", this.getClass().getSimpleName(), request);
    }

    @ApplicationModuleListener(condition = "'" + Dictionary.Service.integration + "'.equals(#event.targetModule)")
    public void onResourceCreated(ResourceCreatedEvent event) {
        log.info("Integration received ResourceCreatedEvent uuid={} type={}", event.uuid(), event.urlType());

        if (event.urlType() == UrlType.EXTERNAL) {
            resourceStorageService.upload(event.externalUrl(), event.uuid());
        }

        eventPublisher.publishEvent(ResourceCreatedEvent.builder()
                .uuid(event.uuid())
                .contextId(event.contextId())
                .resourceType(event.resourceType())
                .internalUrl(event.internalUrl())
                .externalUrl(event.externalUrl())
                .urlType(event.urlType())
                .urlStatus(event.urlStatus())
                .targetModule(Dictionary.Service.launchpad)
                .contentType(ResourceContentType.VIDEO.getValue())
                .build());
    }
}
