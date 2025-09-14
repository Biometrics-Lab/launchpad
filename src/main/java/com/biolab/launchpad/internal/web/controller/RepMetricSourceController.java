package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.RepMetricSource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.RepMetricSourceService;
import com.biolab.launchpad.internal.web.dto.RepMetricSourceDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.RepMetricSourceMapper.repMetricSourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rep_metric_sources")
@Log4j2
public class RepMetricSourceController {

    private final RepMetricSourceService repMetricSourceService;

    @PostMapping
    public RepMetricSourceDto create(@Valid @RequestBody RepMetricSourceDto rep_metricSourceDto) {
        RepMetricSource created = repMetricSourceService.create(repMetricSourceMapper.toModel(rep_metricSourceDto));
        return repMetricSourceMapper.toDto(created);
    }

    @GetMapping
    public List<RepMetricSourceDto> getAll() {
        return repMetricSourceMapper.toDtos(repMetricSourceService.findAll());
    }

    @GetMapping("/{id}")
    public RepMetricSourceDto getById(@PathVariable Integer id) {
        Optional<RepMetricSourceDto> rep_metric_sourceOptional = repMetricSourceService.findById(id).map(repMetricSourceMapper::toDto);
        if (rep_metric_sourceOptional.isPresent()) {
            return rep_metric_sourceOptional.get();
        } else {
            log.warn("Could not find rep_metric_source with id {}", id);
            throw new NotFoundByException("Rep_metric_source not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        repMetricSourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public RepMetricSourceDto update(@Valid @RequestBody RepMetricSourceDto rep_metricSourceDTO) {
        RepMetricSource updated = repMetricSourceService.update(repMetricSourceMapper.toModel(rep_metricSourceDTO));
        return repMetricSourceMapper.toDto(updated);
    }
}