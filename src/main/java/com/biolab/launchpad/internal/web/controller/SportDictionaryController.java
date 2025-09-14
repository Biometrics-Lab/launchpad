package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.SportDictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.SportDictionaryService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.SportDictionaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.SportDictionaryMapper.sportDictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sport_dictionarys")
@Log4j2
public class SportDictionaryController {

    private final SportDictionaryService sportDictionaryService;

    @PostMapping
    public SportDictionaryDto create(@Valid @RequestBody SportDictionaryDto sport_dictionaryDto) {
        SportDictionary created = sportDictionaryService.create(sportDictionaryMapper.toModel(sport_dictionaryDto));
        return sportDictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<SportDictionaryDto> getAll() {
        return sportDictionaryMapper.toDtos(sportDictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public SportDictionaryDto getById(@PathVariable String id) {
        Optional<SportDictionaryDto> sportDictionaryOptional = sportDictionaryService.findById(id).map(sportDictionaryMapper::toDto);
        if (sportDictionaryOptional.isPresent()) {
            return sportDictionaryOptional.get();
        } else {
            log.warn("Could not find sport_dictionary with id {}", id);
            throw new NotFoundByException("Sport_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        sportDictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public SportDictionaryDto update(@Valid @RequestBody SportDictionaryDto sport_dictionaryDTO) {
        SportDictionary updated = sportDictionaryService.update(sportDictionaryMapper.toModel(sport_dictionaryDTO));
        return sportDictionaryMapper.toDto(updated);
    }
}