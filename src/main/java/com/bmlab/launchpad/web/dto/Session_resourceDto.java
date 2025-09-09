package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Session_resourceDto(
        Integer id,
        @NotNull(message = "Session_resource session_id cannot be null")
        Integer session_id,
        @NotNull(message = "Session_resource type cannot be null")
        String type,
        @NotNull(message = "Session_resource url cannot be null")
        String url
) { }

