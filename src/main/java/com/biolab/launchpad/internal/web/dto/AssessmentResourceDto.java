package com.biolab.launchpad.internal.web.dto;

import com.biolab.common.UrlStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssessmentResourceDto(
        Integer id,
        @NotNull(message = "AssessmentResource assessmentId cannot be null")
        Integer assessmentId,
        @NotNull(message = "AssessmentResource type cannot be null")
        String type,
        @NotNull(message = "AssessmentResource url cannot be null")
        String url,
        String externalUrl,
        UrlStatus urlStatus
){}
