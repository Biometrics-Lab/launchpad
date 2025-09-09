package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Model_metric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Model_metricService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Model_metricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Model_metricMapper.model_metricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/model_metrics")
@Log4j2
public class Model_metricController {

    private final Model_metricService model_metricService;

    @PostMapping
    public Model_metricDto create(@Valid @RequestBody Model_metricDto model_metricDto) {
        Model_metric created = model_metricService.create(model_metricMapper.toModel(model_metricDto));
        return model_metricMapper.toDto(created);
    }

    @GetMapping
    public List<Model_metricDto> getAll() {
        return model_metricMapper.toDtos(model_metricService.findAll());
    }

    @GetMapping("/{id}")
    public Model_metricDto getById(@PathVariable Integer id) {
        Optional<Model_metricDto> model_metricOptional = model_metricService.findById(id).map(model_metricMapper::toDto);
        if (model_metricOptional.isPresent()) {
            return model_metricOptional.get();
        } else {
            log.warn("Could not find model_metric with id {}", id);
            throw new NotFoundByException("Model_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        model_metricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Model_metricDto update(@Valid @RequestBody Model_metricDto model_metricDTO) {
        Model_metric updated = model_metricService.update(model_metricMapper.toModel(model_metricDTO));
        return model_metricMapper.toDto(updated);
    }
}