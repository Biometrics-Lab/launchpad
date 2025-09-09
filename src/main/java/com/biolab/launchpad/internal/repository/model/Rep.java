package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("rep")
public class Rep  extends ID {
    @NotNull(message = "Rep session_id cannot be null")
    private Integer session_id;
    @NotNull(message = "Rep start_time cannot be null")
    private Timestamp start_time;
}
