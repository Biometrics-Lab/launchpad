package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.RepMetric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.RepMetricService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.RepMetricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.RepMetricMapper.repMetricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/repMetrics")
@Log4j2
public class RepMetricController {

    private final RepMetricService repMetricService;

    @PostMapping
    public RepMetricDto create(@Valid @RequestBody RepMetricDto repMetricDto) {
        RepMetric created = repMetricService.create(repMetricMapper.toModel(repMetricDto));
        return repMetricMapper.toDto(created);
    }

    @GetMapping
    public List<RepMetricDto> getAll() {
        return repMetricMapper.toDtos(repMetricService.findAll());
    }

    @GetMapping("/{id}")
    public RepMetricDto getById(@PathVariable Integer id) {
        Optional<RepMetricDto> repMetricOptional = repMetricService.findById(id).map(repMetricMapper::toDto);
        if (repMetricOptional.isPresent()) {
            return repMetricOptional.get();
        } else {
            log.warn("Could not find repMetric with id {}", id);
            throw new NotFoundByException("RepMetric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        repMetricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public RepMetricDto update(@Valid @RequestBody RepMetricDto repMetricDto) {
        RepMetric updated = repMetricService.update(repMetricMapper.toModel(repMetricDto));
        return repMetricMapper.toDto(updated);
    }
}