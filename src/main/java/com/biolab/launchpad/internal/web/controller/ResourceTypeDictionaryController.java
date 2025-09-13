package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.ResourceTypeDictionaryService;
import com.biolab.launchpad.internal.web.dto.ResourceTypeDictionaryDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.ResourceTypeDictionaryMapper.resourceTypeDictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resource_type_dictionarys")
@Log4j2
public class ResourceTypeDictionaryController {

    private final ResourceTypeDictionaryService resourceTypeDictionaryService;

    @PostMapping
    public ResourceTypeDictionaryDto create(@Valid @RequestBody ResourceTypeDictionaryDto resource_typeDictionaryDto) {
        ResourceTypeDictionary created = resourceTypeDictionaryService.create(resourceTypeDictionaryMapper.toModel(resource_typeDictionaryDto));
        return resourceTypeDictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<ResourceTypeDictionaryDto> getAll() {
        return resourceTypeDictionaryMapper.toDtos(resourceTypeDictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public ResourceTypeDictionaryDto getById(@PathVariable String id) {
        Optional<ResourceTypeDictionaryDto> resource_type_dictionaryOptional = resourceTypeDictionaryService.findById(id).map(resourceTypeDictionaryMapper::toDto);
        if (resource_type_dictionaryOptional.isPresent()) {
            return resource_type_dictionaryOptional.get();
        } else {
            log.warn("Could not find resource_type_dictionary with id {}", id);
            throw new NotFoundByException("Resource_type_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        resourceTypeDictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public ResourceTypeDictionaryDto update(@Valid @RequestBody ResourceTypeDictionaryDto resource_typeDictionaryDTO) {
        ResourceTypeDictionary updated = resourceTypeDictionaryService.update(resourceTypeDictionaryMapper.toModel(resource_typeDictionaryDTO));
        return resourceTypeDictionaryMapper.toDto(updated);
    }
}