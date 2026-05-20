package com.biolab.common;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ResourceStatusUpdatedEvent(
        UUID uuid,
        UrlStatus urlStatus,
        String internalUrl,
        String targetModule
) {}
