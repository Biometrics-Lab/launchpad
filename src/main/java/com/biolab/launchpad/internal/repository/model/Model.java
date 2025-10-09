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
@Table("model")
public class Model  extends ID {
    @NotNull(message = "Model sport cannot be null")
    private String sport;
    @NotNull(message = "Model ageGroup cannot be null")
    @Column("age_group")
    private String ageGroup;
    private String description;

}
