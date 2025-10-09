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
@Table("assessment_resource")
public class AssessmentResource extends ID {
    @NotNull(message = "AssessmentResource assessmentId cannot be null")
    @Column("assessment_id")
    private Integer assessmentId;
    @NotNull(message = "AssessmentResource type cannot be null")
    private String type;
    @NotNull(message = "AssessmentResource url cannot be null")
    private String url;
}
