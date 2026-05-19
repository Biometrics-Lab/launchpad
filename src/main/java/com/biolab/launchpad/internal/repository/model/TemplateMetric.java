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
    @NotNull(message = "TemplateMetric templateId cannot be null")
    @Column("template_id")
    private Integer templateId;
    @NotNull(message = "TemplateMetric conditionalMetricId cannot be null")
    @Column("conditional_metric_id")
    private Integer conditionalMetricId;
    @Column("data_source_id")
    private Integer dataSourceId;
    private String description;
}
