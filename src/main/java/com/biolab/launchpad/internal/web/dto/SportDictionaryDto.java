package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SportDictionaryDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        String description
){}
