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
@Table("rep_resource")
public class RepResource extends ID {
    @NotNull(message = "RepResource repId cannot be null")
    @Column("rep_id")
    private Integer repId;
    @NotNull(message = "RepResource type cannot be null")
    private String type;
    @NotNull(message = "RepResource url cannot be null")
    private String url;
}
