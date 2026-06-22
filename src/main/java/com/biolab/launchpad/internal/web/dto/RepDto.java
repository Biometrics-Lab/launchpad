package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record RepDto(
        Integer id,
        @NotNull(message = "Rep sessionId cannot be null")
        Integer sessionId,
        @NotNull(message = "Rep startTime cannot be null")
        Timestamp startTime,
        Integer repNumber
) { }

