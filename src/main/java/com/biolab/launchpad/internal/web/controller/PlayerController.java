package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.Player;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.PlayerService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.PlayerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.PlayerMapper.playerMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/players")
@Log4j2
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public PlayerDto create(@Valid @RequestBody PlayerDto playerDto) {
        Player created = playerService.create(playerMapper.toModel(playerDto));
        return playerMapper.toDto(created);
    }

    @GetMapping
    public List<PlayerDto> getAll() {
        return playerMapper.toDtos(playerService.findAll());
    }

    @GetMapping("/{id}")
    public PlayerDto getById(@PathVariable Integer id) {
        Optional<PlayerDto> playerOptional = playerService.findById(id).map(playerMapper::toDto);
        if (playerOptional.isPresent()) {
            return playerOptional.get();
        } else {
            log.warn("Could not find player with id {}", id);
            throw new NotFoundByException("Player not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        playerService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public PlayerDto update(@Valid @RequestBody PlayerDto playerDTO) {
        Player updated = playerService.update(playerMapper.toModel(playerDTO));
        return playerMapper.toDto(updated);
    }
}