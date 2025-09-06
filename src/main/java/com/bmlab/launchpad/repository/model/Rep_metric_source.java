package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("rep_metric_source")
public class Rep_metric_source  extends ID {
    @NotNull(message = "Rep_metric_source rep_metric_id cannot be null")
    private Integer rep_metric_id;
    @NotNull(message = "Rep_metric_source data_source_id cannot be null")
    private Integer data_source_id;
    private String description;
}
