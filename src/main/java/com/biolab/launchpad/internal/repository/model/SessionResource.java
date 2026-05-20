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
@Table("session_resource")
public class SessionResource extends ID {
    @NotNull(message = "SessionResource sessionId cannot be null")
    @Column("session_id")
    private Integer session1Id;
    @NotNull(message = "SessionResource type cannot be null")
    private String type;
    @NotNull(message = "SessionResource url cannot be null")
    private String url;
    @NotNull(message = "SessionResource uuid cannot be null")
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    @Column("external_url")
    private String externalUrl;
    @NotNull(message = "SessionResource urlStatus cannot be null")
    @Column("url_status")
    @Builder.Default
    private UrlStatus urlStatus = UrlStatus.PENDING;
}
