package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Rep_metric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Rep_metricService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Rep_metricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Rep_metricMapper.rep_metricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rep_metrics")
@Log4j2
public class Rep_metricController {

    private final Rep_metricService rep_metricService;

    @PostMapping
    public Rep_metricDto create(@Valid @RequestBody Rep_metricDto rep_metricDto) {
        Rep_metric created = rep_metricService.create(rep_metricMapper.toModel(rep_metricDto));
        return rep_metricMapper.toDto(created);
    }

    @GetMapping
    public List<Rep_metricDto> getAll() {
        return rep_metricMapper.toDtos(rep_metricService.findAll());
    }

    @GetMapping("/{id}")
    public Rep_metricDto getById(@PathVariable Integer id) {
        Optional<Rep_metricDto> rep_metricOptional = rep_metricService.findById(id).map(rep_metricMapper::toDto);
        if (rep_metricOptional.isPresent()) {
            return rep_metricOptional.get();
        } else {
            log.warn("Could not find rep_metric with id {}", id);
            throw new NotFoundByException("Rep_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        rep_metricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Rep_metricDto update(@Valid @RequestBody Rep_metricDto rep_metricDTO) {
        Rep_metric updated = rep_metricService.update(rep_metricMapper.toModel(rep_metricDTO));
        return rep_metricMapper.toDto(updated);
    }
}