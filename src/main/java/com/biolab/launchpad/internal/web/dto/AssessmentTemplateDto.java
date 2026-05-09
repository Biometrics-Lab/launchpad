package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssessmentTemplateDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "AssessmentTemplate sport cannot be null")
        String sport,
        String description
){}
