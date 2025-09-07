package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Age_group_dictionary;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.Age_group_dictionaryService;
import com.bmlab.launchpad.web.dto.Age_group_dictionaryDto;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.Age_group_dictionaryMapper.age_group_dictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/age_group_dictionarys")
@Log4j2
public class Age_group_dictionaryController {

    private final Age_group_dictionaryService age_group_dictionaryService;

    @PostMapping
    public Age_group_dictionaryDto create(@Valid @RequestBody Age_group_dictionaryDto age_group_dictionaryDto) {
        Age_group_dictionary created = age_group_dictionaryService.create(age_group_dictionaryMapper.toModel(age_group_dictionaryDto));
        return age_group_dictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<Age_group_dictionaryDto> getAll() {
        return age_group_dictionaryMapper.toDtos(age_group_dictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public Age_group_dictionaryDto getById(@PathVariable String id) {
        Optional<Age_group_dictionaryDto> age_group_dictionaryOptional = age_group_dictionaryService.findById(id).map(age_group_dictionaryMapper::toDto);
        if (age_group_dictionaryOptional.isPresent()) {
            return age_group_dictionaryOptional.get();
        } else {
            log.warn("Could not find age_group_dictionary with id {}", id);
            throw new NotFoundByException("Age_group_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        age_group_dictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Age_group_dictionaryDto update(@Valid @RequestBody Age_group_dictionaryDto age_group_dictionaryDTO) {
        Age_group_dictionary updated = age_group_dictionaryService.update(age_group_dictionaryMapper.toModel(age_group_dictionaryDTO));
        return age_group_dictionaryMapper.toDto(updated);
    }
}

