package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RepResourceDto(
        Integer id,
        @NotNull(message = "Rep_resource rep_id cannot be null")
        Integer rep_id,
        @NotNull(message = "Rep_resource type cannot be null")
        String type,
        @NotNull(message = "Rep_resource url cannot be null")
        String url
) { }

