package com.biolab.common;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ResourceCreatedEvent(
        UUID uuid,
        Integer contextId,
        ResourceType resourceType,
        String internalUrl,
        String externalUrl,
        UrlType urlType,
        UrlStatus urlStatus,
        String targetModule,
        String contentType
) {}
