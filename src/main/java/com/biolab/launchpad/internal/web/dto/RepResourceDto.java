package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RepResourceDto(
        Integer id,
        @NotNull(message = "RepResource repId cannot be null")
        Integer repId,
        @NotNull(message = "RepResource type cannot be null")
        String type,
        @NotNull(message = "RepResource url cannot be null")
        String url
) { }

