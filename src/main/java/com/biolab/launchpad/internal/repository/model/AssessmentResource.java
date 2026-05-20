package com.biolab.launchpad.internal.repository.model;

import com.biolab.common.UrlStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

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
    @NotNull(message = "AssessmentResource uuid cannot be null")
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    @Column("external_url")
    private String externalUrl;
    @NotNull(message = "AssessmentResource urlStatus cannot be null")
    @Column("url_status")
    @Builder.Default
    private UrlStatus urlStatus = UrlStatus.PENDING;
}
