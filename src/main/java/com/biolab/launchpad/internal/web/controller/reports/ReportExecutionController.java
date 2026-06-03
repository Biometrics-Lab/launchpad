package com.biolab.launchpad.internal.web.controller.reports;

import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.reports.ReportConfigService;
import com.biolab.launchpad.internal.service.reports.ReportExecutor;
import com.biolab.launchpad.internal.web.dto.reports.ReportDataDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
@Log4j2
public class ReportExecutionController {

    private final ReportConfigService configService;
    private final Map<String, ReportExecutor<?>> executorsByType;

    public ReportExecutionController(List<ReportExecutor<?>> executors, ReportConfigService configService) {
        this.configService = configService;
        this.executorsByType = executors.stream()
            .collect(Collectors.toMap(ReportExecutor::reportType, e -> e));
    }

    @GetMapping("/{reportType}")
    public ReportDataDto execute(
            @PathVariable String reportType,
            @RequestParam MultiValueMap<String, String> params,
            @RequestParam(required = false) String configName) {

        ReportExecutor<?> executor = executorsByType.get(reportType);
        if (executor == null) {
            throw new NotFoundByException("Report type not found: %s", reportType);
        }

        ReportConfigService.ResolvedConfig config = configName != null
            ? configService.resolve(configName, reportType)
            : configService.resolveDefault(reportType);

        return executor.dispatch(params, config);
    }
}
