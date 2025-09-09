package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Rep_resource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Rep_resourceService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Rep_resourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Rep_resourceMapper.rep_resourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rep_resources")
@Log4j2
public class Rep_resourceController {

    private final Rep_resourceService rep_resourceService;

    @PostMapping
    public Rep_resourceDto create(@Valid @RequestBody Rep_resourceDto rep_resourceDto) {
        Rep_resource created = rep_resourceService.create(rep_resourceMapper.toModel(rep_resourceDto));
        return rep_resourceMapper.toDto(created);
    }

    @GetMapping
    public List<Rep_resourceDto> getAll() {
        return rep_resourceMapper.toDtos(rep_resourceService.findAll());
    }

    @GetMapping("/{id}")
    public Rep_resourceDto getById(@PathVariable Integer id) {
        Optional<Rep_resourceDto> rep_resourceOptional = rep_resourceService.findById(id).map(rep_resourceMapper::toDto);
        if (rep_resourceOptional.isPresent()) {
            return rep_resourceOptional.get();
        } else {
            log.warn("Could not find rep_resource with id {}", id);
            throw new NotFoundByException("Rep_resource not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        rep_resourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Rep_resourceDto update(@Valid @RequestBody Rep_resourceDto rep_resourceDTO) {
        Rep_resource updated = rep_resourceService.update(rep_resourceMapper.toModel(rep_resourceDTO));
        return rep_resourceMapper.toDto(updated);
    }
}