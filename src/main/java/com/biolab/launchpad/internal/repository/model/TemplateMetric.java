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
@Table("template_metric")
public class TemplateMetric extends ID {
    @NotNull(message = "TemplateMetric template_id cannot be null")
    @Column("template_id")
    private Integer templateId;
    @NotNull(message = "TemplateMetric metric_id cannot be null")
    @Column("metric_id")
    private Integer metricId;
    @NotNull(message = "TemplateMetric source_id cannot be null")
    @Column("source_id")
    private Integer sourceId;
}
