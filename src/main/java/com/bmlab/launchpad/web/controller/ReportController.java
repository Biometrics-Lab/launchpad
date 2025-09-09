package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Report;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.ReportService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.ReportDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.ReportMapper.reportMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Log4j2
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ReportDto create(@Valid @RequestBody ReportDto reportDto) {
        Report created = reportService.create(reportMapper.toModel(reportDto));
        return reportMapper.toDto(created);
    }

    @GetMapping
    public List<ReportDto> getAll() {
        return reportMapper.toDtos(reportService.findAll());
    }

    @GetMapping("/{id}")
    public ReportDto getById(@PathVariable Integer id) {
        Optional<ReportDto> reportOptional = reportService.findById(id).map(reportMapper::toDto);
        if (reportOptional.isPresent()) {
            return reportOptional.get();
        } else {
            log.warn("Could not find report with id {}", id);
            throw new NotFoundByException("Report not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        reportService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public ReportDto update(@Valid @RequestBody ReportDto reportDTO) {
        Report updated = reportService.update(reportMapper.toModel(reportDTO));
        return reportMapper.toDto(updated);
    }
}