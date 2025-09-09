package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("template_metric")
public class Template_metric  extends ID {
    @NotNull(message = "Template_metric template_id cannot be null")
    private Integer template_id;
    @NotNull(message = "Template_metric metric_id cannot be null")
    private Integer metric_id;
    @NotNull(message = "Template_metric source_id cannot be null")
    private Integer source_id;
}
