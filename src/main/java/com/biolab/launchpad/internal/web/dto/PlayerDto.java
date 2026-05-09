package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PlayerDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Graduation year cannot be null")
        Integer graduationYear,
        Integer teamId,
        LocalDate dob
) { }

