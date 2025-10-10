package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserPlayerDto(
        Integer id,
        @NotNull(message = "UserPlayer userId cannot be null")
        Integer userId,
        @NotNull(message = "UserPlayer playerId cannot be null")
        Integer playerId
){}
