package com.biolab.launchpad.internal.web.dto.reports;

import lombok.Builder;

@Builder
public record ReportCellDto(Integer columnId, Double min, Double max, Double avg, Integer count) {}
