package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Rep_metricDto(
        Integer id,
        @NotNull(message = "Rep_metric rep_id cannot be null")
        Integer rep_id,
        @NotNull(message = "Rep_metric metric_id cannot be null")
        Integer metric_id,
        Number value
) { }

