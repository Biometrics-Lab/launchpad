package com.biolab.hardware;

import com.biolab.common.AsyncRequest;
import com.biolab.common.Dictionary;
import com.biolab.common.ResourceCreatedEvent;
import com.biolab.common.ResourceType;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import com.biolab.common.UrlStatus;
import com.biolab.common.UrlType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class HardwareServiceGateway {

    private final ApplicationEventPublisher eventPublisher;

    public HealthCheckResponse healthCheck(HealthCheckRequest request) {
        log.info("{} - health check - OK: {}", this.getClass().getSimpleName(), request);
        return HealthCheckResponse.builder()
                .requestId(request.requestId())
                .status("OK")
                .build();
    }

    @Transactional
    public void publishResourceCreated(UUID uuid, Integer contextId, ResourceType resourceType, String internalUrl) {
        log.info("Hardware publishing ResourceCreatedEvent uuid={} type={}", uuid, resourceType);
        eventPublisher.publishEvent(ResourceCreatedEvent.builder()
                .uuid(uuid)
                .contextId(contextId)
                .resourceType(resourceType)
                .internalUrl(internalUrl)
                .externalUrl(null)
                .urlType(UrlType.INTERNAL)
                .urlStatus(UrlStatus.READY)
                .targetModule(Dictionary.Service.integration)
                .build());
    }

    @ApplicationModuleListener(condition = "'hardware-service'.equals(#event.consumerServiceName)")
    public void asyncEvent(AsyncRequest event) {
        log.info("{} - async event received - OK: {}", this.getClass().getSimpleName(), event);
    }
}
