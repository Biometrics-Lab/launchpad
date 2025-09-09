package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record OrganisationDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name
) { }

