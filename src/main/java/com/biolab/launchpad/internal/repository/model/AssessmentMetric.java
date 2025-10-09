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
@Table("assessment_metric")
public class AssessmentMetric extends ID {
    @NotNull(message = "AssesmentMetric assessmentId cannot be null")
    @Column("assessment_id")
    private Integer assessmentId;
    @NotNull(message = "AssesmentMetric metricId cannot be null")
    @Column("metric_id")
    private Integer metricId;
    @NotNull(message = "AssesmentMetric sourceId cannot be null")
    @Column("source_id")
    private Integer sourceId;
    @Column("min_value")
    private Number minValue;
    @Column("max_value")
    private Number maxValue;
    @Column("avg_value")
    private Number avgValue;
    @Column("last_value")
    private Number lastValue;
}
