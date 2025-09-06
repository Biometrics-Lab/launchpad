package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("assesment_metric")
public class Assesment_metric {
    @Id
    private Integer id;
    @NotNull(message = "Assesment_metric assessment_id cannot be null")
    private Integer assessment_id;
    @NotNull(message = "Assesment_metric metric_id cannot be null")
    private Integer metric_id;
    @NotNull(message = "Assesment_metric source_id cannot be null")
    private Integer source_id;
    private Number min_value;
    private Number max_value;
    private Number avg_value;
    private Number last_value;
}
