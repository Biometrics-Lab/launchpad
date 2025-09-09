package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Resource_type_dictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Resource_type_dictionaryService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Resource_type_dictionaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Resource_type_dictionaryMapper.resource_type_dictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resource_type_dictionarys")
@Log4j2
public class Resource_type_dictionaryController {

    private final Resource_type_dictionaryService resource_type_dictionaryService;

    @PostMapping
    public Resource_type_dictionaryDto create(@Valid @RequestBody Resource_type_dictionaryDto resource_type_dictionaryDto) {
        Resource_type_dictionary created = resource_type_dictionaryService.create(resource_type_dictionaryMapper.toModel(resource_type_dictionaryDto));
        return resource_type_dictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<Resource_type_dictionaryDto> getAll() {
        return resource_type_dictionaryMapper.toDtos(resource_type_dictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public Resource_type_dictionaryDto getById(@PathVariable String id) {
        Optional<Resource_type_dictionaryDto> resource_type_dictionaryOptional = resource_type_dictionaryService.findById(id).map(resource_type_dictionaryMapper::toDto);
        if (resource_type_dictionaryOptional.isPresent()) {
            return resource_type_dictionaryOptional.get();
        } else {
            log.warn("Could not find resource_type_dictionary with id {}", id);
            throw new NotFoundByException("Resource_type_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        resource_type_dictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Resource_type_dictionaryDto update(@Valid @RequestBody Resource_type_dictionaryDto resource_type_dictionaryDTO) {
        Resource_type_dictionary updated = resource_type_dictionaryService.update(resource_type_dictionaryMapper.toModel(resource_type_dictionaryDTO));
        return resource_type_dictionaryMapper.toDto(updated);
    }
}