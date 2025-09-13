package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.AssessmentMetricService;
import com.biolab.launchpad.internal.web.dto.AssessmentMetricDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.AssessmentMetricMapper.assessmentMetricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment_metrics")
@Log4j2
public class AssessmentMetricController {

    private final AssessmentMetricService assessmentMetricService;

    @PostMapping
    public AssessmentMetricDto create(@Valid @RequestBody AssessmentMetricDto assessment_metricDto) {
        AssessmentMetric created = assessmentMetricService.create(assessmentMetricMapper.toModel(assessment_metricDto));
        return assessmentMetricMapper.toDto(created);
    }

    @GetMapping
    public List<AssessmentMetricDto> getAll() {
        return assessmentMetricMapper.toDtos(assessmentMetricService.findAll());
    }

    @GetMapping("/{id}")
    public AssessmentMetricDto getById(@PathVariable Integer id) {
        Optional<AssessmentMetricDto> assessment_metricOptional = assessmentMetricService.findById(id).map(assessmentMetricMapper::toDto);
        if (assessment_metricOptional.isPresent()) {
            return assessment_metricOptional.get();
        } else {
            log.warn("Could not find assessment_metric with id {}", id);
            throw new NotFoundByException("Assessment_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessmentMetricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public AssessmentMetricDto update(@Valid @RequestBody AssessmentMetricDto assessment_metricDTO) {
        AssessmentMetric updated = assessmentMetricService.update(assessmentMetricMapper.toModel(assessment_metricDTO));
        return assessmentMetricMapper.toDto(updated);
    }
}

