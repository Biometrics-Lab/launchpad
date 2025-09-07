package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "User role cannot be null")
        Integer role
){}
