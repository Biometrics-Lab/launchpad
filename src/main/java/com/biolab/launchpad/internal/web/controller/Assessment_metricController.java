package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Assessment_metric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Assessment_metricService;
import com.biolab.launchpad.internal.web.dto.Assessment_metricDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Assessment_metricMapper.assessment_metricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment_metrics")
@Log4j2
public class Assessment_metricController {

    private final Assessment_metricService assessment_metricService;

    @PostMapping
    public Assessment_metricDto create(@Valid @RequestBody Assessment_metricDto assessment_metricDto) {
        Assessment_metric created = assessment_metricService.create(assessment_metricMapper.toModel(assessment_metricDto));
        return assessment_metricMapper.toDto(created);
    }

    @GetMapping
    public List<Assessment_metricDto> getAll() {
        return assessment_metricMapper.toDtos(assessment_metricService.findAll());
    }

    @GetMapping("/{id}")
    public Assessment_metricDto getById(@PathVariable Integer id) {
        Optional<Assessment_metricDto> assessment_metricOptional = assessment_metricService.findById(id).map(assessment_metricMapper::toDto);
        if (assessment_metricOptional.isPresent()) {
            return assessment_metricOptional.get();
        } else {
            log.warn("Could not find assessment_metric with id {}", id);
            throw new NotFoundByException("Assessment_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessment_metricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Assessment_metricDto update(@Valid @RequestBody Assessment_metricDto assessment_metricDTO) {
        Assessment_metric updated = assessment_metricService.update(assessment_metricMapper.toModel(assessment_metricDTO));
        return assessment_metricMapper.toDto(updated);
    }
}

