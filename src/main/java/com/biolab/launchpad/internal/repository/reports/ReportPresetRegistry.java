package com.biolab.launchpad.internal.repository.reports;

import com.biolab.common.ReportType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class ReportPresetRegistry {

    private final ObjectMapper objectMapper;
    private final Map<String, ReportPreset> presetsByName = new LinkedHashMap<>();

    public record ReportPreset(String name, String reportType, boolean isDefault, JsonNode config) {}

    @PostConstruct
    public void load() {
        presetsByName.clear();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath:reports/*.json");
            for (Resource resource : resources) {
                JsonNode root = objectMapper.readTree(resource.getInputStream());
                String name = root.path("name").asText();
                String reportTypeStr = root.path("reportType").asText();
                boolean validType = Arrays.stream(ReportType.values())
                    .anyMatch(t -> t.getValue().equals(reportTypeStr));
                if (!validType) {
                    throw new IllegalStateException(
                        "Unknown reportType '" + reportTypeStr + "' in preset file: " + resource.getFilename());
                }
                boolean isDefault = root.path("isDefault").asBoolean(false);
                presetsByName.put(name, new ReportPreset(name, reportTypeStr, isDefault, root));
            }
            log.info("Loaded {} report presets", presetsByName.size());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load report presets", e);
        }
    }

    public Optional<ReportPreset> findByName(String name) {
        return Optional.ofNullable(presetsByName.get(name));
    }

    public Optional<ReportPreset> findDefault(String reportType) {
        return presetsByName.values().stream()
            .filter(p -> p.reportType().equals(reportType) && p.isDefault())
            .findFirst();
    }

    public List<ReportPreset> findAll(String reportType) {
        if (reportType == null) {
            return List.copyOf(presetsByName.values());
        }
        return presetsByName.values().stream()
            .filter(p -> p.reportType().equals(reportType))
            .toList();
    }

    public boolean exists(String name) {
        return presetsByName.containsKey(name);
    }
}
