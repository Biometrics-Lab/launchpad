package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Session_metric;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Session_metricService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Session_metricDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Session_metricMapper.session_metricMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/session_metrics")
@Log4j2
public class Session_metricController {

    private final Session_metricService session_metricService;

    @PostMapping
    public Session_metricDto create(@Valid @RequestBody Session_metricDto session_metricDto) {
        Session_metric created = session_metricService.create(session_metricMapper.toModel(session_metricDto));
        return session_metricMapper.toDto(created);
    }

    @GetMapping
    public List<Session_metricDto> getAll() {
        return session_metricMapper.toDtos(session_metricService.findAll());
    }

    @GetMapping("/{id}")
    public Session_metricDto getById(@PathVariable Integer id) {
        Optional<Session_metricDto> session_metricOptional = session_metricService.findById(id).map(session_metricMapper::toDto);
        if (session_metricOptional.isPresent()) {
            return session_metricOptional.get();
        } else {
            log.warn("Could not find session_metric with id {}", id);
            throw new NotFoundByException("Session_metric not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        session_metricService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Session_metricDto update(@Valid @RequestBody Session_metricDto session_metricDTO) {
        Session_metric updated = session_metricService.update(session_metricMapper.toModel(session_metricDTO));
        return session_metricMapper.toDto(updated);
    }
}