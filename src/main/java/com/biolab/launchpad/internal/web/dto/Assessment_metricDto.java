package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Assessment_metricDto (
        Integer id,
        @NotNull(message = "Assesment_metric assessment_id cannot be null")
        Integer assessment_id,
        @NotNull(message = "Assesment_metric metric_id cannot be null")
        Integer metric_id,
        @NotNull(message = "Assesment_metric source_id cannot be null")
        Integer source_id,
        Number min_value,
        Number max_value,
        Number avg_value,
        Number last_value
){}
