package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MeasurementDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name
) { }

