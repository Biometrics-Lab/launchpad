package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Model;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.ModelService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.ModelDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.ModelMapper.modelMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/models")
@Log4j2
public class ModelController {

    private final ModelService modelService;

    @PostMapping
    public ModelDto create(@Valid @RequestBody ModelDto modelDto) {
        Model created = modelService.create(modelMapper.toModel(modelDto));
        return modelMapper.toDto(created);
    }

    @GetMapping
    public List<ModelDto> getAll() {
        return modelMapper.toDtos(modelService.findAll());
    }

    @GetMapping("/{id}")
    public ModelDto getById(@PathVariable Integer id) {
        Optional<ModelDto> modelOptional = modelService.findById(id).map(modelMapper::toDto);
        if (modelOptional.isPresent()) {
            return modelOptional.get();
        } else {
            log.warn("Could not find model with id {}", id);
            throw new NotFoundByException("Model not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        modelService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public ModelDto update(@Valid @RequestBody ModelDto modelDTO) {
        Model updated = modelService.update(modelMapper.toModel(modelDTO));
        return modelMapper.toDto(updated);
    }
}

