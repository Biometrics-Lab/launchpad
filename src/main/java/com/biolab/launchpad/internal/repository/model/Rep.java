package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("rep")
public class Rep  extends ID {
    @NotNull(message = "Rep session1Id cannot be null")
    @Column("session1_id")
    private Integer session1Id;
    @NotNull(message = "Rep startTime cannot be null")
    @Column("start_time")
    private Timestamp startTime;
}
