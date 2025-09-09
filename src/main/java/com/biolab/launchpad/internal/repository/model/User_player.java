package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("user_player")
public class User_player  extends ID {
    @NotNull(message = "User_player user_id cannot be null")
    private Integer user_id;
    @NotNull(message = "User_player player_id cannot be null")
    private Integer player_id;
}
