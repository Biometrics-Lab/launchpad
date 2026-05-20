package com.biolab.launchpad.internal.web.dto;

import com.biolab.common.UrlStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SessionResourceDto(
        Integer id,
        @NotNull(message = "SessionResource sessionId cannot be null")
        Integer session1Id,
        @NotNull(message = "SessionResource type cannot be null")
        String type,
        @NotNull(message = "SessionResource url cannot be null")
        String url,
        String externalUrl,
        UrlStatus urlStatus
) { }

