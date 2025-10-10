package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record RepDto(
        Integer id,
        @NotNull(message = "Rep session1Id cannot be null")
        Integer session1Id,
        @NotNull(message = "Rep startTime cannot be null")
        Timestamp startTime
) { }

