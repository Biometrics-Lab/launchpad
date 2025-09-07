package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Data_source;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.Data_sourceService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.Data_sourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.Data_sourceMapper.data_sourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data_sources")
@Log4j2
public class Data_sourceController {

    private final Data_sourceService data_sourceService;

    @PostMapping
    public Data_sourceDto create(@Valid @RequestBody Data_sourceDto data_sourceDto) {
        Data_source created = data_sourceService.create(data_sourceMapper.toModel(data_sourceDto));
        return data_sourceMapper.toDto(created);
    }

    @GetMapping
    public List<Data_sourceDto> getAll() {
        return data_sourceMapper.toDtos(data_sourceService.findAll());
    }

    @GetMapping("/{id}")
    public Data_sourceDto getById(@PathVariable Integer id) {
        Optional<Data_sourceDto> data_sourceOptional = data_sourceService.findById(id).map(data_sourceMapper::toDto);
        if (data_sourceOptional.isPresent()) {
            return data_sourceOptional.get();
        } else {
            log.warn("Could not find data_source with id {}", id);
            throw new NotFoundByException("Data_source not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        data_sourceService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public Data_sourceDto update(@Valid @RequestBody Data_sourceDto data_sourceDTO) {
        Data_source updated = data_sourceService.update(data_sourceMapper.toModel(data_sourceDTO));
        return data_sourceMapper.toDto(updated);
    }
}

