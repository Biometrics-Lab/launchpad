package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record Session1Dto(
        Integer id,
        @NotNull(message = "Session assessment_id cannot be null")
        Integer assessment_id,
        @NotNull(message = "Session start_time cannot be null")
        Timestamp start_time
) { }

