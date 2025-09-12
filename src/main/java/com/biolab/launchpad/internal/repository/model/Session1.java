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
@Table("session1")
public class Session1 extends ID {
    @NotNull(message = "Session assessment_id cannot be null")
    private Integer assessment_id;
    @NotNull(message = "Session start_time cannot be null")
    private Timestamp start_time;
}
