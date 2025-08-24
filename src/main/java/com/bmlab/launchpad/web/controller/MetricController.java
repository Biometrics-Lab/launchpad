package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Metric;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.MetricService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.MetricDto;
import com.bmlab.launchpad.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.MetricMapper.metricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics")
@Log4j2
public class MetricController {

    private final MetricService metricService;

    @PostMapping
    public MetricDto create(@Valid @RequestBody MetricDto metricDto) {
        Metric created = metricService.create(metricMapper.toModel(metricDto));
        return metricMapper.toDto(created);
    }

    @GetMapping
    public List<MetricDto> getAll() {
        return metricMapper.toDtos(metricService.findAll());
    }

    @GetMapping("/{id}")
    public MetricDto getById(@PathVariable Integer id) {
        Optional<MetricDto> metricOptional = metricService.findById(id).map(metricMapper::toDto);
        if (metricOptional.isPresent()) {
            return metricOptional.get();
        } else {
            log.warn("Could not find metric with id {}", id);
            throw new NotFoundByException("Metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        metricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public MetricDto update(@Valid @RequestBody MetricDto metricDTO) {
        Metric updated = metricService.update(metricMapper.toModel(metricDTO));
        return metricMapper.toDto(updated);
    }
}

