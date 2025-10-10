package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.SessionResource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.SessionResourceService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.SessionResourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.SessionResourceMapper.sessionResourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessionResources")
@Log4j2
public class SessionResourceController {

    private final SessionResourceService sessionResourceService;

    @PostMapping
    public SessionResourceDto create(@Valid @RequestBody SessionResourceDto session_resourceDto) {
        SessionResource created = sessionResourceService.create(sessionResourceMapper.toModel(session_resourceDto));
        return sessionResourceMapper.toDto(created);
    }

    @GetMapping
    public List<SessionResourceDto> getAll() {
        return sessionResourceMapper.toDtos(sessionResourceService.findAll());
    }

    @GetMapping("/{id}")
    public SessionResourceDto getById(@PathVariable Integer id) {
        Optional<SessionResourceDto> session_resourceOptional = sessionResourceService.findById(id).map(sessionResourceMapper::toDto);
        if (session_resourceOptional.isPresent()) {
            return session_resourceOptional.get();
        } else {
            log.warn("Could not find sessionResource with id {}", id);
            throw new NotFoundByException("SessionResource not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        sessionResourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public SessionResourceDto update(@Valid @RequestBody SessionResourceDto session_resourceDTO) {
        SessionResource updated = sessionResourceService.update(sessionResourceMapper.toModel(session_resourceDTO));
        return sessionResourceMapper.toDto(updated);
    }
}