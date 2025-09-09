package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Rep;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.RepService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.RepDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.RepMapper.repMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reps")
@Log4j2
public class RepController {

    private final RepService repService;

    @PostMapping
    public RepDto create(@Valid @RequestBody RepDto repDto) {
        Rep created = repService.create(repMapper.toModel(repDto));
        return repMapper.toDto(created);
    }

    @GetMapping
    public List<RepDto> getAll() {
        return repMapper.toDtos(repService.findAll());
    }

    @GetMapping("/{id}")
    public RepDto getById(@PathVariable Integer id) {
        Optional<RepDto> repOptional = repService.findById(id).map(repMapper::toDto);
        if (repOptional.isPresent()) {
            return repOptional.get();
        } else {
            log.warn("Could not find rep with id {}", id);
            throw new NotFoundByException("Rep not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        repService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public RepDto update(@Valid @RequestBody RepDto repDTO) {
        Rep updated = repService.update(repMapper.toModel(repDTO));
        return repMapper.toDto(updated);
    }
}