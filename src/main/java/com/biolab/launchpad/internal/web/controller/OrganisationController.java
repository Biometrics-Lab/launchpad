package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Organisation;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.OrganisationService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.OrganisationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.OrganisationMapper.organisationMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisations")
@Log4j2
public class OrganisationController {

    private final OrganisationService organisationService;

    @PostMapping
    public OrganisationDto create(@Valid @RequestBody OrganisationDto organisationDto) {
        Organisation created = organisationService.create(organisationMapper.toModel(organisationDto));
        return organisationMapper.toDto(created);
        //return new OrganisationDto(2, "bb");
    }

    @GetMapping
    public List<OrganisationDto> getAll() {
        return organisationMapper.toDtos(organisationService.findAll());
    }

    @GetMapping("/{id}")
    public OrganisationDto getById(@PathVariable Integer id) {
        Optional<OrganisationDto> organisationOptional = organisationService.findById(id).map(organisationMapper::toDto);
        if (organisationOptional.isPresent()) {
            return organisationOptional.get();
        } else {
            log.warn("Could not find organisation with id {}", id);
            throw new NotFoundByException("Organisation not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        organisationService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public OrganisationDto update(@Valid @RequestBody OrganisationDto organisationDTO) {
        Organisation updated = organisationService.update(organisationMapper.toModel(organisationDTO));
        return organisationMapper.toDto(updated);
    }
}