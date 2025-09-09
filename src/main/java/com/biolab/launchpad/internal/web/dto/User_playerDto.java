package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record User_playerDto(
        Integer id,
        @NotNull(message = "User_player user_id cannot be null")
        Integer user_id,
        @NotNull(message = "User_player player_id cannot be null")
        Integer player_id
){}
