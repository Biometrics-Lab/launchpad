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
@Table("assessment_template")
public class AssessmentTemplate extends IDName {
    @NotNull(message = "AssessmentTemplate sport cannot be null")
    private String sport;
    private String description;
    @Column("condition_id")
    private Integer conditionId;
    @Column("allow_external_urls")
    private boolean allowExternalUrls;
}
