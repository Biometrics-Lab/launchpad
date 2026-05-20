package com.biolab.integration.internal.service;

import com.biolab.common.Dictionary;
import com.biolab.common.ResourceStatusUpdatedEvent;
import com.biolab.common.UrlStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResourceStatusPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void publish(UUID uuid, UrlStatus status, String internalUrl) {
        eventPublisher.publishEvent(ResourceStatusUpdatedEvent.builder()
                .uuid(uuid)
                .urlStatus(status)
                .internalUrl(internalUrl)
                .targetModule(Dictionary.Service.launchpad)
                .build());
    }
}
