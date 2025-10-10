package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
public record PlayerDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        Integer graduationYear,
        @NotNull(message = "Player teamId cannot be null")
        Integer teamId,
        LocalDate dob
) { }

