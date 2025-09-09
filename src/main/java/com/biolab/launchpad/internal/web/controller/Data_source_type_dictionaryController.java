package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Data_source_type_dictionary;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.Data_source_type_dictionaryService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.Data_source_type_dictionaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.Data_source_type_dictionaryMapper.data_source_type_dictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data_source_type_dictionarys")
@Log4j2
public class Data_source_type_dictionaryController {

    private final Data_source_type_dictionaryService data_source_type_dictionaryService;

    @PostMapping
    public Data_source_type_dictionaryDto create(@Valid @RequestBody Data_source_type_dictionaryDto data_source_type_dictionaryDto) {
        Data_source_type_dictionary created = data_source_type_dictionaryService.create(data_source_type_dictionaryMapper.toModel(data_source_type_dictionaryDto));
        return data_source_type_dictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<Data_source_type_dictionaryDto> getAll() {
        return data_source_type_dictionaryMapper.toDtos(data_source_type_dictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public Data_source_type_dictionaryDto getById(@PathVariable String id) {
        Optional<Data_source_type_dictionaryDto> data_source_type_dictionaryOptional = data_source_type_dictionaryService.findById(id).map(data_source_type_dictionaryMapper::toDto);
        if (data_source_type_dictionaryOptional.isPresent()) {
            return data_source_type_dictionaryOptional.get();
        } else {
            log.warn("Could not find data_source_type_dictionary with id {}", id);
            throw new NotFoundByException("Data_source_type_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        data_source_type_dictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Data_source_type_dictionaryDto update(@Valid @RequestBody Data_source_type_dictionaryDto data_source_type_dictionaryDTO) {
        Data_source_type_dictionary updated = data_source_type_dictionaryService.update(data_source_type_dictionaryMapper.toModel(data_source_type_dictionaryDTO));
        return data_source_type_dictionaryMapper.toDto(updated);
    }
}

