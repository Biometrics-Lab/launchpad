package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Rep_metric_source;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Rep_metric_sourceService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Rep_metric_sourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Rep_metric_sourceMapper.rep_metric_sourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rep_metric_sources")
@Log4j2
public class Rep_metric_sourceController {

    private final Rep_metric_sourceService rep_metric_sourceService;

    @PostMapping
    public Rep_metric_sourceDto create(@Valid @RequestBody Rep_metric_sourceDto rep_metric_sourceDto) {
        Rep_metric_source created = rep_metric_sourceService.create(rep_metric_sourceMapper.toModel(rep_metric_sourceDto));
        return rep_metric_sourceMapper.toDto(created);
    }

    @GetMapping
    public List<Rep_metric_sourceDto> getAll() {
        return rep_metric_sourceMapper.toDtos(rep_metric_sourceService.findAll());
    }

    @GetMapping("/{id}")
    public Rep_metric_sourceDto getById(@PathVariable Integer id) {
        Optional<Rep_metric_sourceDto> rep_metric_sourceOptional = rep_metric_sourceService.findById(id).map(rep_metric_sourceMapper::toDto);
        if (rep_metric_sourceOptional.isPresent()) {
            return rep_metric_sourceOptional.get();
        } else {
            log.warn("Could not find rep_metric_source with id {}", id);
            throw new NotFoundByException("Rep_metric_source not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        rep_metric_sourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Rep_metric_sourceDto update(@Valid @RequestBody Rep_metric_sourceDto rep_metric_sourceDTO) {
        Rep_metric_source updated = rep_metric_sourceService.update(rep_metric_sourceMapper.toModel(rep_metric_sourceDTO));
        return rep_metric_sourceMapper.toDto(updated);
    }
}