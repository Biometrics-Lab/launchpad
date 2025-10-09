package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("assessment")
public class Assessment  extends ID {
    @NotNull(message = "Assessment player_id cannot be null")
    @Column("player_id")
    private Integer playerId;
    @NotNull(message = "Assessment sport cannot be null")
    private String sport;
    @NotNull(message = "Assessment template_id cannot be null")
    @Column("template_id")
    private Integer templateId;
}
