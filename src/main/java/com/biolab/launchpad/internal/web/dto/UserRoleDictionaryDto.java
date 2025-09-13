package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRoleDictionaryDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        String description
){}
