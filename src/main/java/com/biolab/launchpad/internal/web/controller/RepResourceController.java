package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.RepResourceService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.RepResourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.RepResourceMapper.repResourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/repResources")
@Log4j2
public class RepResourceController {

    private final RepResourceService repResourceService;

    @PostMapping
    public RepResourceDto create(@Valid @RequestBody RepResourceDto rep_resourceDto) {
        RepResource created = repResourceService.create(repResourceMapper.toModel(rep_resourceDto));
        return repResourceMapper.toDto(created);
    }

    @GetMapping
    public List<RepResourceDto> getAll() {
        return repResourceMapper.toDtos(repResourceService.findAll());
    }

    @GetMapping("/{id}")
    public RepResourceDto getById(@PathVariable Integer id) {
        Optional<RepResourceDto> rep_resourceOptional = repResourceService.findById(id).map(repResourceMapper::toDto);
        if (rep_resourceOptional.isPresent()) {
            return rep_resourceOptional.get();
        } else {
            log.warn("Could not find repResource with id {}", id);
            throw new NotFoundByException("RepResource not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        repResourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public RepResourceDto update(@Valid @RequestBody RepResourceDto rep_resourceDTO) {
        RepResource updated = repResourceService.update(repResourceMapper.toModel(rep_resourceDTO));
        return repResourceMapper.toDto(updated);
    }
}