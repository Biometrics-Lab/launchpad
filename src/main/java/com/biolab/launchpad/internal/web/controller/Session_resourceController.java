package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Session_resource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Session_resourceService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Session_resourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Session_resourceMapper.session_resourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/session_resources")
@Log4j2
public class Session_resourceController {

    private final Session_resourceService session_resourceService;

    @PostMapping
    public Session_resourceDto create(@Valid @RequestBody Session_resourceDto session_resourceDto) {
        Session_resource created = session_resourceService.create(session_resourceMapper.toModel(session_resourceDto));
        return session_resourceMapper.toDto(created);
    }

    @GetMapping
    public List<Session_resourceDto> getAll() {
        return session_resourceMapper.toDtos(session_resourceService.findAll());
    }

    @GetMapping("/{id}")
    public Session_resourceDto getById(@PathVariable Integer id) {
        Optional<Session_resourceDto> session_resourceOptional = session_resourceService.findById(id).map(session_resourceMapper::toDto);
        if (session_resourceOptional.isPresent()) {
            return session_resourceOptional.get();
        } else {
            log.warn("Could not find session_resource with id {}", id);
            throw new NotFoundByException("Session_resource not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        session_resourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Session_resourceDto update(@Valid @RequestBody Session_resourceDto session_resourceDTO) {
        Session_resource updated = session_resourceService.update(session_resourceMapper.toModel(session_resourceDTO));
        return session_resourceMapper.toDto(updated);
    }
}