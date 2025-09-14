package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.DataSource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.DataSourceService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.DataSourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.DataSourceMapper.dataSourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data_sources")
@Log4j2
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @PostMapping
    public DataSourceDto create(@Valid @RequestBody DataSourceDto data_sourceDto) {
        DataSource created = dataSourceService.create(dataSourceMapper.toModel(data_sourceDto));
        return dataSourceMapper.toDto(created);
    }

    @GetMapping
    public List<DataSourceDto> getAll() {
        return dataSourceMapper.toDtos(dataSourceService.findAll());
    }

    @GetMapping("/{id}")
    public DataSourceDto getById(@PathVariable Integer id) {
        Optional<DataSourceDto> data_sourceOptional = dataSourceService.findById(id).map(dataSourceMapper::toDto);
        if (data_sourceOptional.isPresent()) {
            return data_sourceOptional.get();
        } else {
            log.warn("Could not find data_source with id {}", id);
            throw new NotFoundByException("Data_source not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        dataSourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public DataSourceDto update(@Valid @RequestBody DataSourceDto data_sourceDTO) {
        DataSource updated = dataSourceService.update(dataSourceMapper.toModel(data_sourceDTO));
        return dataSourceMapper.toDto(updated);
    }
}

