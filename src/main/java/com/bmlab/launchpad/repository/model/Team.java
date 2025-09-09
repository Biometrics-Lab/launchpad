package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@Data
@SuperBuilder
@NoArgsConstructor
@Table("team")
public class Team extends IDName {
    @NotNull(message = "Team organisation_id cannot be null")
    private Integer organisation_id;
    @NotNull(message = "Team sport cannot be null")
    private String sport;
    private String description;
}
