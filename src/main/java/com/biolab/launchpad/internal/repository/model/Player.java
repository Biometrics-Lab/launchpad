package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@Data
@SuperBuilder
@NoArgsConstructor
@Table("player")
public class Player extends IDName {
    @Column("graduation_year")
    private Integer graduationYear;
    @NotNull(message = "Player teamId cannot be null")
    @Column("team_id")
    private Integer teamId;
    private LocalDate dob;
}
