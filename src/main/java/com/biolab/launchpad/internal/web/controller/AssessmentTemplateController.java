package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.AssessmentTemplate;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.AssessmentTemplateService;
import com.biolab.launchpad.internal.web.dto.AssessmentTemplateDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.AssessmentTemplateMapper.assessmentTemplateMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment_templates")
@Log4j2
public class AssessmentTemplateController {

    private final AssessmentTemplateService assessmentTemplateService;

    @PostMapping
    public AssessmentTemplateDto create(@Valid @RequestBody AssessmentTemplateDto assessment_templateDto) {
        AssessmentTemplate created = assessmentTemplateService.create(assessmentTemplateMapper.toModel(assessment_templateDto));
        return assessmentTemplateMapper.toDto(created);
    }

    @GetMapping
    public List<AssessmentTemplateDto> getAll() {
        return assessmentTemplateMapper.toDtos(assessmentTemplateService.findAll());
    }

    @GetMapping("/{id}")
    public AssessmentTemplateDto getById(@PathVariable Integer id) {
        Optional<AssessmentTemplateDto> assessment_templateOptional = assessmentTemplateService.findById(id).map(assessmentTemplateMapper::toDto);
        if (assessment_templateOptional.isPresent()) {
            return assessment_templateOptional.get();
        } else {
            log.warn("Could not find assessment_template with id {}", id);
            throw new NotFoundByException("Assessment_template not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessmentTemplateService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public AssessmentTemplateDto update(@Valid @RequestBody AssessmentTemplateDto assessment_templateDTO) {
        AssessmentTemplate updated = assessmentTemplateService.update(assessmentTemplateMapper.toModel(assessment_templateDTO));
        return assessmentTemplateMapper.toDto(updated);
    }
}

