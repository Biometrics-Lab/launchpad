package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record RepDto(
        Integer id,
        @NotNull(message = "Rep session1_id cannot be null")
        Integer session1_id,
        @NotNull(message = "Rep start_time cannot be null")
        Timestamp start_time
) { }

