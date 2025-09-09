package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Session1;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Session1Service;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Session1Dto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Session1Mapper.SESSION_1_MAPPER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
@Log4j2
public class Session1Controller {

    private final Session1Service session1Service;

    @PostMapping
    public Session1Dto create(@Valid @RequestBody Session1Dto session1Dto) {
        Session1 created = session1Service.create(SESSION_1_MAPPER.toModel(session1Dto));
        return SESSION_1_MAPPER.toDto(created);
    }

    @GetMapping
    public List<Session1Dto> getAll() {
        return SESSION_1_MAPPER.toDtos(session1Service.findAll());
    }

    @GetMapping("/{id}")
    public Session1Dto getById(@PathVariable Integer id) {
        Optional<Session1Dto> sessionOptional = session1Service.findById(id).map(SESSION_1_MAPPER::toDto);
        if (sessionOptional.isPresent()) {
            return sessionOptional.get();
        } else {
            log.warn("Could not find session with id {}", id);
            throw new NotFoundByException("Session not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        session1Service.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Session1Dto update(@Valid @RequestBody Session1Dto session1DTO) {
        Session1 updated = session1Service.update(SESSION_1_MAPPER.toModel(session1DTO));
        return SESSION_1_MAPPER.toDto(updated);
    }
}