package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Assessment_resource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Assessment_resourceService;
import com.biolab.launchpad.internal.web.dto.Assessment_resourceDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Assessment_resourceMapper.assessment_resourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment_resources")
@Log4j2
public class Assessment_resourceController {

    private final Assessment_resourceService assessment_resourceService;

    @PostMapping
    public Assessment_resourceDto create(@Valid @RequestBody Assessment_resourceDto assessment_resourceDto) {
        Assessment_resource created = assessment_resourceService.create(assessment_resourceMapper.toModel(assessment_resourceDto));
        return assessment_resourceMapper.toDto(created);
    }

    @GetMapping
    public List<Assessment_resourceDto> getAll() {
        return assessment_resourceMapper.toDtos(assessment_resourceService.findAll());
    }

    @GetMapping("/{id}")
    public Assessment_resourceDto getById(@PathVariable Integer id) {
        Optional<Assessment_resourceDto> assessment_resourceOptional = assessment_resourceService.findById(id).map(assessment_resourceMapper::toDto);
        if (assessment_resourceOptional.isPresent()) {
            return assessment_resourceOptional.get();
        } else {
            log.warn("Could not find assessment_resource with id {}", id);
            throw new NotFoundByException("Assessment_resource not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessment_resourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Assessment_resourceDto update(@Valid @RequestBody Assessment_resourceDto assessment_resourceDTO) {
        Assessment_resource updated = assessment_resourceService.update(assessment_resourceMapper.toModel(assessment_resourceDTO));
        return assessment_resourceMapper.toDto(updated);
    }
}

