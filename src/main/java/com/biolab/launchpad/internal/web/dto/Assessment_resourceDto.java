package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Assessment_resourceDto (
        Integer id,
        @NotNull(message = "Assessment_resource assessment_id cannot be null")
        Integer assessment_id,
        @NotNull(message = "Assessment_resource type cannot be null")
        String type,
        @NotNull(message = "Assessment_resource url cannot be null")
        String url
){}
