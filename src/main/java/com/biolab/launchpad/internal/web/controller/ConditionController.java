package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Condition;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.ConditionService;
import com.biolab.launchpad.internal.web.dto.ConditionDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.ConditionMapper.conditionMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conditions")
@Log4j2
public class ConditionController {

    private final ConditionService conditionService;

    @PostMapping
    public ConditionDto create(@Valid @RequestBody ConditionDto conditionDto) {
        Condition created = conditionService.create(conditionMapper.toModel(conditionDto));
        return conditionMapper.toDto(created);
    }

    @GetMapping
    public List<ConditionDto> getAll() {
        return conditionMapper.toDtos(conditionService.findAll());
    }

    @GetMapping("/{id}")
    public ConditionDto getById(@PathVariable Integer id) {
        Optional<ConditionDto> conditionOptional = conditionService.findById(id).map(conditionMapper::toDto);
        if (conditionOptional.isPresent()) {
            return conditionOptional.get();
        } else {
            log.warn("Could not find condition with id {}", id);
            throw new NotFoundByException("Condition not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        conditionService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public ConditionDto update(@Valid @RequestBody ConditionDto conditionDto) {
        Condition updated = conditionService.update(conditionMapper.toModel(conditionDto));
        return conditionMapper.toDto(updated);
    }
}
