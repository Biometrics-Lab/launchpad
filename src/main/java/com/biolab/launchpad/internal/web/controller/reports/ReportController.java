package com.biolab.launchpad.internal.web.controller.reports;

import com.biolab.launchpad.internal.repository.model.Report;
import com.biolab.launchpad.internal.repository.reports.ReportPresetRegistry;
import com.biolab.launchpad.internal.security.exceptions.ConflictException;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.ReportService;
import com.biolab.launchpad.internal.web.dto.ReportDto;
import com.biolab.launchpad.internal.web.dto.ReportSaveDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/report-configs")
@RequiredArgsConstructor
@Log4j2
public class ReportController {

    private final ReportService reportService;
    private final ReportPresetRegistry presetRegistry;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<ReportDto> getAll(@RequestParam(required = false) String reportType) {
        List<ReportDto> result = new ArrayList<>();
        for (ReportPresetRegistry.ReportPreset p : presetRegistry.findAll(reportType)) {
            result.add(new ReportDto(null, p.name(), p.reportType(), p.config(), true));
        }
        List<Report> dbRecords = reportType != null
            ? reportService.findAllByReportType(reportType)
            : reportService.findAll();
        for (Report r : dbRecords) {
            result.add(toDto(r));
        }
        return result;
    }

    @GetMapping("/{name}")
    public ReportDto getByName(@PathVariable String name) {
        return presetRegistry.findByName(name)
            .map(p -> new ReportDto(null, p.name(), p.reportType(), p.config(), true))
            .orElseGet(() -> reportService.findByName(name)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundByException("Report not found by name: %s", name)));
    }

    @PostMapping
    public ReportDto create(@Valid @RequestBody ReportSaveDto dto) {
        if (presetRegistry.exists(dto.name())) {
            throw new ConflictException("Report name '" + dto.name() + "' is reserved by a preset");
        }
        if (reportService.existsByName(dto.name())) {
            throw new ConflictException("Report with name '" + dto.name() + "' already exists");
        }
        Report report = Report.builder()
            .name(dto.name())
            .reportType(dto.reportType())
            .config(dto.config().toString())
            .build();
        Report saved = reportService.create(report);
        return new ReportDto(saved.getId(), saved.getName(), saved.getReportType(), dto.config(), false);
    }

    @PutMapping("/{name}")
    public ReportDto update(@PathVariable String name, @Valid @RequestBody ReportSaveDto dto) {
        if (presetRegistry.exists(name)) {
            throw new ConflictException("Cannot update preset '" + name + "'");
        }
        Report existing = reportService.findByName(name)
            .orElseThrow(() -> new NotFoundByException("Report not found by name: %s", name));
        existing.setName(dto.name());
        existing.setReportType(dto.reportType());
        existing.setConfig(dto.config().toString());
        Report saved = reportService.update(existing);
        return new ReportDto(saved.getId(), saved.getName(), saved.getReportType(), dto.config(), false);
    }

    @DeleteMapping("/{name}")
    public ResponseDto delete(@PathVariable String name) {
        if (presetRegistry.exists(name)) {
            throw new ConflictException("Cannot delete preset '" + name + "'");
        }
        Report existing = reportService.findByName(name)
            .orElseThrow(() -> new NotFoundByException("Report not found by name: %s", name));
        reportService.deleteById(existing.getId());
        return ResponseCode.OK.getResponseDto();
    }

    private ReportDto toDto(Report r) {
        JsonNode config = null;
        if (r.getConfig() != null) {
            try {
                config = objectMapper.readTree(r.getConfig());
            } catch (Exception e) {
                log.warn("Failed to parse config JSON for report: {}", r.getName());
            }
        }
        return new ReportDto(r.getId(), r.getName(), r.getReportType(), config, false);
    }
}
