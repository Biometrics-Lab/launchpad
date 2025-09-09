package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Session_metricDto(
        Integer id,
        @NotNull(message = "session_metric session_id cannot be null")
        Integer session_id,
        @NotNull(message = "session_metric metric_id cannot be null")
        Integer metric_id,
        Number min_value,
        Number max_value,
        Number avg_value
) { }

