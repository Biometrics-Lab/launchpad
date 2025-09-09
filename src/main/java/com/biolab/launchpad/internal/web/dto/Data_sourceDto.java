package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Data_sourceDto (
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Data_source type cannot be null")
        String type,
        String description
){}
