package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@Data
@SuperBuilder
@NoArgsConstructor
@Table("player")
public class Player extends IDName {
    private Integer graduation_year;
    @NotNull(message = "Player team_id cannot be null")
    private Integer team_id;
    private Data dob;
}
