package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("user_player")
public class UserPlayer extends ID {
    @NotNull(message = "UserPlayer userId cannot be null")
    @Column("user_id")
    private Integer userId;
    @NotNull(message = "UserPlayer playerId cannot be null")
    @Column("player_id")
    private Integer playerId;
}
