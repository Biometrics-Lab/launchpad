package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ModelDto (
        Integer id,
        @NotNull(message = "Model sport cannot be null")
        String sport,
        @NotNull(message = "Model age_group cannot be null")
        String age_group,
        String description
){}
