package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
public record PlayerDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        Integer graduation_year,
        @NotNull(message = "Player team_id cannot be null")
        Integer team_id,
        Data dob
) { }

