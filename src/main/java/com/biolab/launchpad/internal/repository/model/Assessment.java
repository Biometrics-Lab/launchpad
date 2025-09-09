package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("assessment")
public class Assessment  extends ID {
    @NotNull(message = "Assessment player_id cannot be null")
    private Integer player_id;
    @NotNull(message = "Assessment sport cannot be null")
    private String sport;
    @NotNull(message = "Assessment template_id cannot be null")
    private Integer template_id;
}
