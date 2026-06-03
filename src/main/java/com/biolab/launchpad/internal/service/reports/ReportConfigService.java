package com.biolab.launchpad.internal.service.reports;

import com.biolab.launchpad.internal.repository.reports.ReportPresetRegistry;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.ReportService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportConfigService {

    private final ReportPresetRegistry presetRegistry;
    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    public record ResolvedConfig(String name, String reportType, JsonNode config, boolean preset) {}

    public ResolvedConfig resolve(String configName, String reportType) {
        return presetRegistry.findByName(configName)
            .map(p -> new ResolvedConfig(p.name(), p.reportType(), p.config(), true))
            .orElseGet(() -> reportService.findByName(configName)
                .map(r -> {
                    try {
                        JsonNode cfg = r.getConfig() != null
                            ? objectMapper.readTree(r.getConfig())
                            : objectMapper.createObjectNode();
                        return new ResolvedConfig(r.getName(), r.getReportType(), cfg, false);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid config JSON for: " + configName, e);
                    }
                })
                .orElseThrow(() -> new NotFoundByException("Report config not found by name: %s", configName)));
    }

    public ResolvedConfig resolveDefault(String reportType) {
        return presetRegistry.findDefault(reportType)
            .map(p -> new ResolvedConfig(p.name(), p.reportType(), p.config(), true))
            .orElseThrow(() -> new NotFoundByException("Default report config not found for type: %s", reportType));
    }
}
