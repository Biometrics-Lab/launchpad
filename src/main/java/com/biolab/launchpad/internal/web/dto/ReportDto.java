package com.biolab.launchpad.internal.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReportDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        String reportType,
        JsonNode config,
        boolean preset
) {}
