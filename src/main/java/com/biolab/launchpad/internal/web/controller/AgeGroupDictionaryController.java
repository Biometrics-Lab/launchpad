package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.AgeGroupDictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.AgeGroupDictionaryService;
import com.biolab.launchpad.internal.web.dto.AgeGroupDictionaryDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.AgeGroupDictionaryMapper.ageGroupDictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/age_group_dictionarys")
@Log4j2
public class AgeGroupDictionaryController {

    private final AgeGroupDictionaryService ageGroupDictionaryService;

    @PostMapping
    public AgeGroupDictionaryDto create(@Valid @RequestBody AgeGroupDictionaryDto age_groupDictionaryDto) {

        AgeGroupDictionary created = ageGroupDictionaryService.create(ageGroupDictionaryMapper.toModel(age_groupDictionaryDto));
        return ageGroupDictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<AgeGroupDictionaryDto> getAll() {
        return ageGroupDictionaryMapper.toDtos(ageGroupDictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public AgeGroupDictionaryDto getById(@PathVariable String id) {
        Optional<AgeGroupDictionaryDto> age_group_dictionaryOptional = ageGroupDictionaryService.findById(id).map(ageGroupDictionaryMapper::toDto);
        if (age_group_dictionaryOptional.isPresent()) {
            return age_group_dictionaryOptional.get();
        } else {
            log.warn("Could not find age_group_dictionary with id {}", id);
            throw new NotFoundByException("Age_group_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        ageGroupDictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public AgeGroupDictionaryDto update(@Valid @RequestBody AgeGroupDictionaryDto age_groupDictionaryDTO) {
        AgeGroupDictionary updated = ageGroupDictionaryService.update(ageGroupDictionaryMapper.toModel(age_groupDictionaryDTO));
        return ageGroupDictionaryMapper.toDto(updated);
    }
}

