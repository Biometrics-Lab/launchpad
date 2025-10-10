package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.AssessmentResource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.AssessmentResourceService;
import com.biolab.launchpad.internal.web.dto.AssessmentResourceDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.AssessmentResourceMapper.assessmentResourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessmentResources")
@Log4j2
public class AssessmentResourceController {

    private final AssessmentResourceService assessmentResourceService;

    @PostMapping
    public AssessmentResourceDto create(@Valid @RequestBody AssessmentResourceDto assessment_resourceDto) {
        AssessmentResource created = assessmentResourceService.create(assessmentResourceMapper.toModel(assessment_resourceDto));
        return assessmentResourceMapper.toDto(created);
    }

    @GetMapping
    public List<AssessmentResourceDto> getAll() {
        return assessmentResourceMapper.toDtos(assessmentResourceService.findAll());
    }

    @GetMapping("/{id}")
    public AssessmentResourceDto getById(@PathVariable Integer id) {
        Optional<AssessmentResourceDto> assessment_resourceOptional = assessmentResourceService.findById(id).map(assessmentResourceMapper::toDto);
        if (assessment_resourceOptional.isPresent()) {
            return assessment_resourceOptional.get();
        } else {
            log.warn("Could not find assessmentResource with id {}", id);
            throw new NotFoundByException("AssessmentResource not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessmentResourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public AssessmentResourceDto update(@Valid @RequestBody AssessmentResourceDto assessment_resourceDTO) {
        AssessmentResource updated = assessmentResourceService.update(assessmentResourceMapper.toModel(assessment_resourceDTO));
        return assessmentResourceMapper.toDto(updated);
    }
}

