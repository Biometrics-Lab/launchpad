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
@Table("rep_metric")
public class RepMetric extends ID {
    @NotNull(message = "RepMetric repId cannot be null")
    @Column("rep_id")
    private Integer repId;
    @NotNull(message = "RepMetric conditionalMetricId cannot be null")
    @Column("conditional_metric_id")
    private Integer conditionalMetricId;
    @Column("data_source_id")
    private Integer dataSourceId;
    private Number value;
}
