package com.biolab.launchpad.internal.web.dto.reports;

import lombok.Builder;

import java.util.List;

@Builder
public record ReportDataDto(List<ReportColumnDto> columns, List<ReportRowDto> rows) {}
