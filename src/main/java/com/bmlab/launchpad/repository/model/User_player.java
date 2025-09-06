package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("user_player")
public class User_player {
    @Id
    private Integer id;
    @NotNull(message = "User_player user_id cannot be null")
    private Integer user_id;
    @NotNull(message = "User_player player_id cannot be null")
    private Integer player_id;
}
