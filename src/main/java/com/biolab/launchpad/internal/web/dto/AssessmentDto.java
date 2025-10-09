package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.relational.core.mapping.Column;

@Builder
public record AssessmentDto (
        Integer id,
        @NotNull(message = "Assessment playerId cannot be null")
        Integer playerId,
        @NotNull(message = "Assessment sport cannot be null")
        String sport,
        @NotNull(message = "Assessment templateId cannot be null")
        Integer templateId
){}
