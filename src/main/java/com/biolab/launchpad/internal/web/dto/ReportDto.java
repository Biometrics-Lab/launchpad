package com.bmlab.launchpad.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReportDto(
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        String ext_ref
) { }

