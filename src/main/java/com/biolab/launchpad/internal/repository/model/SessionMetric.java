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
@Table("session_metric")
public class SessionMetric extends ID {
    @NotNull(message = "SessionMetric sessionId cannot be null")
    @Column("session_id")
    private Integer sessionId;
    @NotNull(message = "SessionMetric conditionalMetricId cannot be null")
    @Column("conditional_metric_id")
    private Integer conditionalMetricId;
    @Column("min_value")
    private Number minValue;
    @Column("max_value")
    private Number maxValue;
    @Column("avg_value")
    private Number avgValue;
}
