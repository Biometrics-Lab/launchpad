package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DataSourceDto(
        Integer id,
        @NotNull(message = "DataSource integrationId cannot be null")
        Integer integrationId,
        @NotNull(message = "DataSource metricId cannot be null")
        Integer metricId,
        @NotNull(message = "DataSource type cannot be null")
        String type,
        String content
) {}
