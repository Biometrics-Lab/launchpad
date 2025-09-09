package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.Team;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.TeamService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.TeamDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.TeamMapper.teamMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@Log4j2
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public TeamDto create(@Valid @RequestBody TeamDto teamDto) {
        Team created = teamService.create(teamMapper.toModel(teamDto));
        return teamMapper.toDto(created);
    }

    @GetMapping
    public List<TeamDto> getAll() {
        return teamMapper.toDtos(teamService.findAll());
    }

    @GetMapping("/{id}")
    public TeamDto getById(@PathVariable Integer id) {
        Optional<TeamDto> teamOptional = teamService.findById(id).map(teamMapper::toDto);
        if (teamOptional.isPresent()) {
            return teamOptional.get();
        } else {
            log.warn("Could not find team with id {}", id);
            throw new NotFoundByException("Team not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        teamService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public TeamDto update(@Valid @RequestBody TeamDto teamDTO) {
        Team updated = teamService.update(teamMapper.toModel(teamDTO));
        return teamMapper.toDto(updated);
    }
}