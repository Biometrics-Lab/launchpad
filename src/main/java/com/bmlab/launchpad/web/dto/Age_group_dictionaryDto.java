package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record Age_group_dictionaryDto (
        @NotBlank(message = "Name cannot be blank")
        String name,
        String description
){}
