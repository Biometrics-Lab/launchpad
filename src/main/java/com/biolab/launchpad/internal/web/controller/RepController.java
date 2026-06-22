package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.RepResourceService;
import com.biolab.launchpad.internal.service.RepService;
import com.biolab.launchpad.internal.web.dto.RepDto;
import com.biolab.launchpad.internal.web.dto.RepResourceDto;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.RepMapper.repMapper;
import static com.biolab.launchpad.internal.web.mapper.RepResourceMapper.repResourceMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reps")
@Log4j2
public class RepController {

    private final RepService repService;
    private final RepResourceService repResourceService;

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

    @GetMapping("/{id}/resources")
    public List<RepResourceDto> getResources(@PathVariable Integer id) {
        return repResourceMapper.toDtos(repResourceService.findAllByRepId(id));
    }
}
