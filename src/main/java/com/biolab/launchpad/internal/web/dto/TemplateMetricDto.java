package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TemplateMetricDto(
        Integer id,
        @NotNull(message = "Template_metric template_id cannot be null")
        Integer template_id,
        @NotNull(message = "Template_metric metric_id cannot be null")
        Integer metric_id,
        @NotNull(message = "Template_metric source_id cannot be null")
        Integer source_id
) { }

