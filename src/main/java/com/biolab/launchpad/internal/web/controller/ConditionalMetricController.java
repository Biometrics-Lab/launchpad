package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.ConditionalMetricService;
import com.biolab.launchpad.internal.web.dto.ConditionalMetricDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.ConditionalMetricMapper.conditionalMetricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conditionalMetrics")
@Log4j2
public class ConditionalMetricController {

    private final ConditionalMetricService conditionalMetricService;

    @PostMapping
    public ConditionalMetricDto create(@Valid @RequestBody ConditionalMetricDto conditionalMetricDto) {
        ConditionalMetric created = conditionalMetricService.create(conditionalMetricMapper.toModel(conditionalMetricDto));
        return conditionalMetricMapper.toDto(created);
    }

    @GetMapping
    public List<ConditionalMetricDto> getAll() {
        return conditionalMetricMapper.toDtos(conditionalMetricService.findAll());
    }

    @GetMapping("/{id}")
    public ConditionalMetricDto getById(@PathVariable Integer id) {
        Optional<ConditionalMetricDto> conditionalMetricOptional = conditionalMetricService.findById(id).map(conditionalMetricMapper::toDto);
        if (conditionalMetricOptional.isPresent()) {
            return conditionalMetricOptional.get();
        } else {
            log.warn("Could not find conditionalMetric with id {}", id);
            throw new NotFoundByException("ConditionalMetric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        conditionalMetricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public ConditionalMetricDto update(@Valid @RequestBody ConditionalMetricDto conditionalMetricDto) {
        ConditionalMetric updated = conditionalMetricService.update(conditionalMetricMapper.toModel(conditionalMetricDto));
        return conditionalMetricMapper.toDto(updated);
    }
}
