package com.biolab.launchpad.internal.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReportSaveDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotBlank(message = "ReportType cannot be blank")
        String reportType,
        @NotNull(message = "Config cannot be null")
        JsonNode config
) {}
