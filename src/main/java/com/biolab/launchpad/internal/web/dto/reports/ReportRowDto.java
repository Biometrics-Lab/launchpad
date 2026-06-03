package com.biolab.launchpad.internal.web.dto.reports;

import lombok.Builder;

import java.util.List;

@Builder
public record ReportRowDto(
        Integer conditionalMetricId,
        String name,
        Boolean negated,
        List<ReportCellDto> cells
) {}
