package com.biolab.launchpad.internal.service.reports;

import com.biolab.launchpad.internal.web.dto.reports.ReportDataDto;

public interface ReportExecutor {
    String reportType();
    ReportDataDto execute(Integer entityId, ReportConfigService.ResolvedConfig config);
}
