package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ModelDto (
        Integer id,
        @NotNull(message = "Model sport cannot be null")
        String sport,
        @NotNull(message = "Model ageGroup cannot be null")
        String ageGroup,
        String description
){}
