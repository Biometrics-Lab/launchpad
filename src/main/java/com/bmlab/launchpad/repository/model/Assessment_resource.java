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
@Table("assessment_resource")
public class Assessment_resource {
    @Id
    private Integer id;
    @NotNull(message = "Assessment_resource assessment_id cannot be null")
    private Integer assessment_id;
    @NotNull(message = "Assessment_resource type cannot be null")
    private String type;
    @NotNull(message = "Assessment_resource url cannot be null")
    private String url;
}
