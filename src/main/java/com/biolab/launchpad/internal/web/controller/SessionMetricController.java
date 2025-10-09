package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.SessionMetric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.SessionMetricService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.SessionMetricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.SessionMetricMapper.sessionMetricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessionMetrics")
@Log4j2
public class SessionMetricController {

    private final SessionMetricService sessionMetricService;

    @PostMapping
    public SessionMetricDto create(@Valid @RequestBody SessionMetricDto session_metricDto) {
        SessionMetric created = sessionMetricService.create(sessionMetricMapper.toModel(session_metricDto));
        return sessionMetricMapper.toDto(created);
    }

    @GetMapping
    public List<SessionMetricDto> getAll() {
        return sessionMetricMapper.toDtos(sessionMetricService.findAll());
    }

    @GetMapping("/{id}")
    public SessionMetricDto getById(@PathVariable Integer id) {
        Optional<SessionMetricDto> session_metricOptional = sessionMetricService.findById(id).map(sessionMetricMapper::toDto);
        if (session_metricOptional.isPresent()) {
            return session_metricOptional.get();
        } else {
            log.warn("Could not find sessionMetric with id {}", id);
            throw new NotFoundByException("SessionMetric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        sessionMetricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public SessionMetricDto update(@Valid @RequestBody SessionMetricDto session_metricDTO) {
        SessionMetric updated = sessionMetricService.update(sessionMetricMapper.toModel(session_metricDTO));
        return sessionMetricMapper.toDto(updated);
    }
}