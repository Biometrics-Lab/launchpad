package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Integration;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.IntegrationService;
import com.biolab.launchpad.internal.web.dto.IntegrationDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.IntegrationMapper.integrationMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/integrations")
@Log4j2
public class IntegrationController {

    private final IntegrationService integrationService;

    @PostMapping
    public IntegrationDto create(@Valid @RequestBody IntegrationDto integrationDto) {
        Integration created = integrationService.create(integrationMapper.toModel(integrationDto));
        return integrationMapper.toDto(created);
    }

    @GetMapping
    public List<IntegrationDto> getAll() {
        return integrationMapper.toDtos(integrationService.findAll());
    }

    @GetMapping("/{id}")
    public IntegrationDto getById(@PathVariable Integer id) {
        Optional<IntegrationDto> integrationOptional = integrationService.findById(id).map(integrationMapper::toDto);
        if (integrationOptional.isPresent()) {
            return integrationOptional.get();
        } else {
            log.warn("Could not find integration with id {}", id);
            throw new NotFoundByException("Integration not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        integrationService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public IntegrationDto update(@Valid @RequestBody IntegrationDto integrationDto) {
        Integration updated = integrationService.update(integrationMapper.toModel(integrationDto));
        return integrationMapper.toDto(updated);
    }
}
