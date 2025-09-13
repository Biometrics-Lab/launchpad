package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.ModelMetric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.ModelMetricService;
import com.biolab.launchpad.internal.web.dto.ModelMetricDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.ModelMetricMapper.modelMetricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/model_metrics")
@Log4j2
public class ModelMetricController {

    private final ModelMetricService modelMetricService;

    @PostMapping
    public ModelMetricDto create(@Valid @RequestBody ModelMetricDto model_metricDto) {
        ModelMetric created = modelMetricService.create(modelMetricMapper.toModel(model_metricDto));
        return modelMetricMapper.toDto(created);
    }

    @GetMapping
    public List<ModelMetricDto> getAll() {
        return modelMetricMapper.toDtos(modelMetricService.findAll());
    }

    @GetMapping("/{id}")
    public ModelMetricDto getById(@PathVariable Integer id) {
        Optional<ModelMetricDto> model_metricOptional = modelMetricService.findById(id).map(modelMetricMapper::toDto);
        if (model_metricOptional.isPresent()) {
            return model_metricOptional.get();
        } else {
            log.warn("Could not find model_metric with id {}", id);
            throw new NotFoundByException("Model_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        modelMetricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public ModelMetricDto update(@Valid @RequestBody ModelMetricDto model_metricDTO) {
        ModelMetric updated = modelMetricService.update(modelMetricMapper.toModel(model_metricDTO));
        return modelMetricMapper.toDto(updated);
    }
}