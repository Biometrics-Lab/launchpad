package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record Session1Dto(
        Integer id,
        @NotNull(message = "Session assessmentId cannot be null")
        Integer assessmentId,
        @NotNull(message = "Session startTime cannot be null")
        Timestamp startTime
) { }

