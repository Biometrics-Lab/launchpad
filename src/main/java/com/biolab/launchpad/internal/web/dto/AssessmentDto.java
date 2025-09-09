package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssessmentDto (
        Integer id,
        @NotNull(message = "Assessment player_id cannot be null")
        Integer player_id,
        @NotNull(message = "Assessment sport cannot be null")
        String sport,
        @NotNull(message = "Assessment template_id cannot be null")
        Integer template_id
){}
