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
@Table("rep_resource")
public class RepResource extends ID {
    @NotNull(message = "RepResource repId cannot be null")
    @Column("rep_id")
    private Integer repId;
    @NotNull(message = "RepResource type cannot be null")
    private String type;
    @NotNull(message = "RepResource url cannot be null")
    private String url;
    @NotNull(message = "RepResource uuid cannot be null")
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    @Column("external_url")
    private String externalUrl;
    @NotNull(message = "RepResource urlStatus cannot be null")
    @Column("url_status")
    @Builder.Default
    private UrlStatus urlStatus = UrlStatus.PENDING;
}
