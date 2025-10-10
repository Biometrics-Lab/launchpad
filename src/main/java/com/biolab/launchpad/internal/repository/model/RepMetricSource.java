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
@Table("rep_metric_source")
public class RepMetricSource extends ID {
    @NotNull(message = "RepMetricSource repMetricId cannot be null")
    @Column("rep_metric_id")
    private Integer repMetricId;
    @NotNull(message = "RepMetricSource dataSourceId cannot be null")
    @Column("data_source_id")
    private Integer dataSourceId;
    private String description;
}
