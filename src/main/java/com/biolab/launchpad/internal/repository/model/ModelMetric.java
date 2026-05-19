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
@Table("model_metric")
public class ModelMetric extends ID {
    @NotNull(message = "ModelMetric modelId cannot be null")
    @Column("model_id")
    private Integer modelId;
    @NotNull(message = "ModelMetric conditionalMetricId cannot be null")
    @Column("conditional_metric_id")
    private Integer conditionalMetricId;
    private Number value;

}
