package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.TemplateMetric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.TemplateMetricService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.TemplateMetricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.TemplateMetricMapper.templateMetricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templateMetrics")
@Log4j2
public class TemplateMetricController {

    private final TemplateMetricService templateMetricService;

    @PostMapping
    public TemplateMetricDto create(@Valid @RequestBody TemplateMetricDto template_metricDto) {
        TemplateMetric created = templateMetricService.create(templateMetricMapper.toModel(template_metricDto));
        return templateMetricMapper.toDto(created);
    }

    @GetMapping
    public List<TemplateMetricDto> getAll() {
        return templateMetricMapper.toDtos(templateMetricService.findAll());
    }

    @GetMapping("/{id}")
    public TemplateMetricDto getById(@PathVariable Integer id) {
        Optional<TemplateMetricDto> template_metricOptional = templateMetricService.findById(id).map(templateMetricMapper::toDto);
        if (template_metricOptional.isPresent()) {
            return template_metricOptional.get();
        } else {
            log.warn("Could not find templateMetric with id {}", id);
            throw new NotFoundByException("TemplateMetric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        templateMetricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public TemplateMetricDto update(@Valid @RequestBody TemplateMetricDto template_metricDTO) {
        TemplateMetric updated = templateMetricService.update(templateMetricMapper.toModel(template_metricDTO));
        return templateMetricMapper.toDto(updated);
    }
}