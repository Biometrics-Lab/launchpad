package com.biolab.launchpad.internal.service.reports;

import com.biolab.launchpad.internal.web.dto.reports.ReportDataDto;
import org.springframework.util.MultiValueMap;

public interface ReportExecutor<T extends ReportRequestDto> {

    String reportType();

    T buildRequest(MultiValueMap<String, String> params);

    ReportDataDto execute(T request, ReportConfigService.ResolvedConfig config);

    default ReportDataDto dispatch(MultiValueMap<String, String> params, ReportConfigService.ResolvedConfig config) {
        return execute(buildRequest(params), config);
    }
}
