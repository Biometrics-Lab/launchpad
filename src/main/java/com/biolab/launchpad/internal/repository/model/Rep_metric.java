package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("Rep_metric")
public class Rep_metric  extends ID {
    @NotNull(message = "Rep_metric rep_id cannot be null")
    private Integer rep_id;
    @NotNull(message = "Rep_metric metric_id cannot be null")
    private Integer metric_id;
    private Number value;
}
