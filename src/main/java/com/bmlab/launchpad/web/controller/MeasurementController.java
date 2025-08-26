package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Measurement;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.MeasurementService;
import com.bmlab.launchpad.web.dto.MeasurementDto;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.MeasurementMapper.measurementMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/measurements")
@Log4j2
public class MeasurementController {

    private final MeasurementService measurementService;

    @PostMapping
    public MeasurementDto create(@Valid @RequestBody MeasurementDto measurementDto) {
        Measurement created = measurementService.create(measurementMapper.toModel(measurementDto));
        return measurementMapper.toDto(created);
    }

    @GetMapping
    public List<MeasurementDto> getAll() {
        return measurementMapper.toDtos(measurementService.findAll());
    }

    @GetMapping("/{id}")
    public MeasurementDto getById(@PathVariable Integer id) {
        Optional<MeasurementDto> measurementOptional = measurementService.findById(id).map(measurementMapper::toDto);
        if (measurementOptional.isPresent()) {
            return measurementOptional.get();
        } else {
            log.warn("Could not find measurement with id {}", id);
            throw new NotFoundByException("Measurement not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        measurementService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public MeasurementDto update(@Valid @RequestBody MeasurementDto measurementDTO) {
        Measurement updated = measurementService.update(measurementMapper.toModel(measurementDTO));
        return measurementMapper.toDto(updated);
    }
}

