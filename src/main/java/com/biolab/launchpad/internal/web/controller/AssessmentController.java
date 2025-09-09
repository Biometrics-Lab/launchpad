package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Assessment;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.AssessmentService;
import com.bmlab.launchpad.web.dto.AssessmentDto;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.AssessmentMapper.assessmentMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessments")
@Log4j2
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    public AssessmentDto create(@Valid @RequestBody AssessmentDto assessmentDto) {
        Assessment created = assessmentService.create(assessmentMapper.toModel(assessmentDto));
        return assessmentMapper.toDto(created);
    }

    @GetMapping
    public List<AssessmentDto> getAll() {
        return assessmentMapper.toDtos(assessmentService.findAll());
    }

    @GetMapping("/{id}")
    public AssessmentDto getById(@PathVariable Integer id) {
        Optional<AssessmentDto> assessmentOptional = assessmentService.findById(id).map(assessmentMapper::toDto);
        if (assessmentOptional.isPresent()) {
            return assessmentOptional.get();
        } else {
            log.warn("Could not find assessment with id {}", id);
            throw new NotFoundByException("Assessment not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        assessmentService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public AssessmentDto update(@Valid @RequestBody AssessmentDto assessmentDTO) {
        Assessment updated = assessmentService.update(assessmentMapper.toModel(assessmentDTO));
        return assessmentMapper.toDto(updated);
    }
}

