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
@Table("session_resource")
public class Session_resource {
    @Id
    private Integer id;
    @NotNull(message = "Session_resource session_id cannot be null")
    private Integer session_id;
    @NotNull(message = "Session_resource type cannot be null")
    private String type;
    @NotNull(message = "Session_resource url cannot be null")
    private String url;
}
