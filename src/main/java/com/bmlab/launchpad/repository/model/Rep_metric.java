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
@Table("Rep_metric")
public class Rep_metric {
    @Id
    private Integer id;
    @NotNull(message = "Rep_metric rep_id cannot be null")
    private Integer rep_id;
    @NotNull(message = "Rep_metric metric_id cannot be null")
    private Integer metric_id;
    private Number value;
}
