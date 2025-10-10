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
@Table("team")
public class Team extends IDName {
    @NotNull(message = "Team organisationId cannot be null")
    @Column("organisation_id")
    private Integer organisationId;
    @NotNull(message = "Team sport cannot be null")
    private String sport;
    private String description;
}
