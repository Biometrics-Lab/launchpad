package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.DataSourceTypeDictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.DataSourceTypeDictionaryService;
import com.biolab.launchpad.internal.web.dto.DataSourceTypeDictionaryDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.DataSourceTypeDictionaryMapper.dataSourceTypeDictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data_source_type_dictionarys")
@Log4j2
public class DataSourceTypeDictionaryController {

    private final DataSourceTypeDictionaryService dataSourceTypeDictionaryService;

    @PostMapping
    public DataSourceTypeDictionaryDto create(@Valid @RequestBody DataSourceTypeDictionaryDto data_sourceTypeDictionaryDto) {
        DataSourceTypeDictionary created = dataSourceTypeDictionaryService.create(dataSourceTypeDictionaryMapper.toModel(data_sourceTypeDictionaryDto));
        return dataSourceTypeDictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<DataSourceTypeDictionaryDto> getAll() {
        return dataSourceTypeDictionaryMapper.toDtos(dataSourceTypeDictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public DataSourceTypeDictionaryDto getById(@PathVariable String id) {
        Optional<DataSourceTypeDictionaryDto> data_source_type_dictionaryOptional = dataSourceTypeDictionaryService.findById(id).map(dataSourceTypeDictionaryMapper::toDto);
        if (data_source_type_dictionaryOptional.isPresent()) {
            return data_source_type_dictionaryOptional.get();
        } else {
            log.warn("Could not find data_source_type_dictionary with id {}", id);
            throw new NotFoundByException("Data_source_type_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        dataSourceTypeDictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public DataSourceTypeDictionaryDto update(@Valid @RequestBody DataSourceTypeDictionaryDto data_sourceTypeDictionaryDTO) {
        DataSourceTypeDictionary updated = dataSourceTypeDictionaryService.update(dataSourceTypeDictionaryMapper.toModel(data_sourceTypeDictionaryDTO));
        return dataSourceTypeDictionaryMapper.toDto(updated);
    }
}

