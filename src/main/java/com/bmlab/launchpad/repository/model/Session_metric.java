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
@Table("session_metric")
public class Session_metric {
    @Id
    private Integer id;
    @NotNull(message = "session_metric session_id cannot be null")
    private Integer session_id;
    @NotNull(message = "session_metric metric_id cannot be null")
    private Integer metric_id;
    private Number min_value;
    private Number max_value;
    private Number avg_value;
}
