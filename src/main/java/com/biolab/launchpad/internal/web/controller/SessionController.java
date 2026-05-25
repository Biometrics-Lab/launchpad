package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.SessionService;
import com.biolab.launchpad.internal.service.SessionWorkflowService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.SessionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.SessionMapper.sessionMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
@Log4j2
public class SessionController {

    private final SessionService sessionService;
    private final SessionWorkflowService sessionWorkflowService;

    @PostMapping
    public SessionDto create(@Valid @RequestBody SessionDto sessionDto) {
        Session created = sessionService.create(sessionMapper.toModel(sessionDto));
        return sessionMapper.toDto(created);
    }

    @GetMapping
    public List<SessionDto> getAll() {
        return sessionMapper.toDtos(sessionService.findAll());
    }

    @GetMapping("/{id}")
    public SessionDto getById(@PathVariable Integer id) {
        Optional<SessionDto> sessionOptional = sessionService.findById(id).map(sessionMapper::toDto);
        if (sessionOptional.isPresent()) {
            return sessionOptional.get();
        } else {
            log.warn("Could not find session with id {}", id);
            throw new NotFoundByException("Session not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        sessionService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public SessionDto update(@Valid @RequestBody SessionDto sessionDTO) {
        Session updated = sessionService.update(sessionMapper.toModel(sessionDTO));
        return sessionMapper.toDto(updated);
    }

    @PostMapping("/{id}/start")
    public SessionDto start(@PathVariable Integer id) {
        return sessionWorkflowService.startSession(id);
    }

    @PostMapping("/{id}/stop")
    public SessionDto stop(@PathVariable Integer id) {
        return sessionWorkflowService.stopSession(id);
    }
}
