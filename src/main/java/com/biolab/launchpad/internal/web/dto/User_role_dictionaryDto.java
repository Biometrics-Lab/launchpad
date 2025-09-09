package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record User_role_dictionaryDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        String description
){}
