package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Assessment_template;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.Assessment_templateService;
import com.bmlab.launchpad.web.dto.Assessment_templateDto;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.Assessment_templateMapper.assessment_templateMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment_templates")
@Log4j2
public class Assessment_templateController {

    private final Assessment_templateService assessment_templateService;

    @PostMapping
    public Assessment_templateDto create(@Valid @RequestBody Assessment_templateDto assessment_templateDto) {
        Assessment_template created = assessment_templateService.create(assessment_templateMapper.toModel(assessment_templateDto));
        return assessment_templateMapper.toDto(created);
    }

    @GetMapping
    public List<Assessment_templateDto> getAll() {
        return assessment_templateMapper.toDtos(assessment_templateService.findAll());
    }

    @GetMapping("/{id}")
    public Assessment_templateDto getById(@PathVariable Integer id) {
        Optional<Assessment_templateDto> assessment_templateOptional = assessment_templateService.findById(id).map(assessment_templateMapper::toDto);
        if (assessment_templateOptional.isPresent()) {
            return assessment_templateOptional.get();
        } else {
            log.warn("Could not find assessment_template with id {}", id);
            throw new NotFoundByException("Assessment_template not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessment_templateService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Assessment_templateDto update(@Valid @RequestBody Assessment_templateDto assessment_templateDTO) {
        Assessment_template updated = assessment_templateService.update(assessment_templateMapper.toModel(assessment_templateDTO));
        return assessment_templateMapper.toDto(updated);
    }
}

