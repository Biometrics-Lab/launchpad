package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Sport_dictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.Sport_dictionaryService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.Sport_dictionaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.Sport_dictionaryMapper.sport_dictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sport_dictionarys")
@Log4j2
public class Sport_dictionaryController {

    private final Sport_dictionaryService sport_dictionaryService;

    @PostMapping
    public Sport_dictionaryDto create(@Valid @RequestBody Sport_dictionaryDto sport_dictionaryDto) {
        Sport_dictionary created = sport_dictionaryService.create(sport_dictionaryMapper.toModel(sport_dictionaryDto));
        return sport_dictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<Sport_dictionaryDto> getAll() {
        return sport_dictionaryMapper.toDtos(sport_dictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public Sport_dictionaryDto getById(@PathVariable String id) {
        Optional<Sport_dictionaryDto> sport_dictionaryOptional = sport_dictionaryService.findById(id).map(sport_dictionaryMapper::toDto);
        if (sport_dictionaryOptional.isPresent()) {
            return sport_dictionaryOptional.get();
        } else {
            log.warn("Could not find sport_dictionary with id {}", id);
            throw new NotFoundByException("Sport_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        sport_dictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Sport_dictionaryDto update(@Valid @RequestBody Sport_dictionaryDto sport_dictionaryDTO) {
        Sport_dictionary updated = sport_dictionaryService.update(sport_dictionaryMapper.toModel(sport_dictionaryDTO));
        return sport_dictionaryMapper.toDto(updated);
    }
}