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
@Table("conditional_metric")
public class ConditionalMetric extends IDName {
    @NotNull(message = "ConditionalMetric conditionId cannot be null")
    @Column("condition_id")
    private Integer conditionId;
    @NotNull(message = "ConditionalMetric metricId cannot be null")
    @Column("metric_id")
    private Integer metricId;
}
