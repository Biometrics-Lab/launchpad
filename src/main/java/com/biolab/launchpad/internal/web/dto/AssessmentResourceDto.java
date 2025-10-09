package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssessmentResourceDto(
        Integer id,
        @NotNull(message = "AssessmentResource assessment_id cannot be null")
        Integer assessmentId,
        @NotNull(message = "AssessmentResource type cannot be null")
        String type,
        @NotNull(message = "AssessmentResource url cannot be null")
        String url
){}
