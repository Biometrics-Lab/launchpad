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
@Table("data_source")
public class DataSource extends ID {
    @NotNull(message = "DataSource integrationId cannot be null")
    @Column("integration_id")
    private Integer integrationId;
    @NotNull(message = "DataSource metricId cannot be null")
    @Column("metric_id")
    private Integer metricId;
    private String name;
    @NotNull(message = "DataSource type cannot be null")
    private String type;
    private String content;
}
