package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("session_resource")
public class SessionResource extends ID {
    @NotNull(message = "Session_resource session_id cannot be null")
    private Integer session1_id;
    @NotNull(message = "Session_resource type cannot be null")
    private String type;
    @NotNull(message = "Session_resource url cannot be null")
    private String url;
}
