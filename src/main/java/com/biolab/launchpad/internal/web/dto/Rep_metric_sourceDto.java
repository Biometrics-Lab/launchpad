package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record Rep_metric_sourceDto(
        Integer id,
        @NotNull(message = "Rep_metric_source rep_metric_id cannot be null")
        Integer rep_metric_id,
        @NotNull(message = "Rep_metric_source data_source_id cannot be null")
        Integer data_source_id,
        String description
) { }

