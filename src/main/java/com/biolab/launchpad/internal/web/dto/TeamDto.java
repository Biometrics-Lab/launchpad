package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TeamDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Team organisationId cannot be null")
        Integer organisationId,
        @NotNull(message = "Team sport cannot be null")
        String sport,
        String description
) { }

