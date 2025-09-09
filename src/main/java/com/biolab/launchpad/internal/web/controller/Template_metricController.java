package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Template_metric;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.Template_metricService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.Template_metricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.Template_metricMapper.template_metricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/template_metrics")
@Log4j2
public class Template_metricController {

    private final Template_metricService template_metricService;

    @PostMapping
    public Template_metricDto create(@Valid @RequestBody Template_metricDto template_metricDto) {
        Template_metric created = template_metricService.create(template_metricMapper.toModel(template_metricDto));
        return template_metricMapper.toDto(created);
    }

    @GetMapping
    public List<Template_metricDto> getAll() {
        return template_metricMapper.toDtos(template_metricService.findAll());
    }

    @GetMapping("/{id}")
    public Template_metricDto getById(@PathVariable Integer id) {
        Optional<Template_metricDto> template_metricOptional = template_metricService.findById(id).map(template_metricMapper::toDto);
        if (template_metricOptional.isPresent()) {
            return template_metricOptional.get();
        } else {
            log.warn("Could not find template_metric with id {}", id);
            throw new NotFoundByException("Template_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        template_metricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Template_metricDto update(@Valid @RequestBody Template_metricDto template_metricDTO) {
        Template_metric updated = template_metricService.update(template_metricMapper.toModel(template_metricDTO));
        return template_metricMapper.toDto(updated);
    }
}