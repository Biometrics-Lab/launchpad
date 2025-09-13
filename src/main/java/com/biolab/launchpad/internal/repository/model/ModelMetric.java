package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("model_metric")
public class ModelMetric extends ID {
    @NotNull(message = "Model_metric model_id cannot be null")
    private Integer model_id;
    @NotNull(message = "Model_metric metric_id cannot be null")
    private Integer metric_id;
    private Number value;

}
