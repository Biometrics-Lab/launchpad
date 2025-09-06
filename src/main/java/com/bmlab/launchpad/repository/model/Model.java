package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("model")
public class Model  extends ID {
    @NotNull(message = "Model sport cannot be null")
    private String sport;
    @NotNull(message = "Model age_group cannot be null")
    private String age_group;
    private String description;

}
