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
@Table("session_resource")
public class SessionResource extends ID {
    @NotNull(message = "SessionResource sessionId cannot be null")
    @Column("session_id")
    private Integer session1Id;
    @NotNull(message = "SessionResource type cannot be null")
    private String type;
    @NotNull(message = "SessionResource url cannot be null")
    private String url;
}
