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
@Table("rep_resource")
public class Rep_resource {
    @Id
    private Integer id;
    @NotNull(message = "Rep_resource rep_id cannot be null")
    private Integer rep_id;
    @NotNull(message = "Rep_resource type cannot be null")
    private String type;
    @NotNull(message = "Rep_resource url cannot be null")
    private String url;
}
