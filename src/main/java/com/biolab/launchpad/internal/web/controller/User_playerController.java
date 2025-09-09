package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.User_player;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.User_playerService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.User_playerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.User_playerMapper.user_playerMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user_players")
@Log4j2
public class User_playerController {

    private final User_playerService user_playerService;

    @PostMapping
    public User_playerDto create(@Valid @RequestBody User_playerDto user_playerDto) {
        User_player created = user_playerService.create(user_playerMapper.toModel(user_playerDto));
        return user_playerMapper.toDto(created);
    }

    @GetMapping
    public List<User_playerDto> getAll() {
        return user_playerMapper.toDtos(user_playerService.findAll());
    }

    @GetMapping("/{id}")
    public User_playerDto getById(@PathVariable Integer id) {
        Optional<User_playerDto> user_playerOptional = user_playerService.findById(id).map(user_playerMapper::toDto);
        if (user_playerOptional.isPresent()) {
            return user_playerOptional.get();
        } else {
            log.warn("Could not find user_player with id {}", id);
            throw new NotFoundByException("User_player not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        user_playerService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public User_playerDto update(@Valid @RequestBody User_playerDto user_playerDTO) {
        User_player updated = user_playerService.update(user_playerMapper.toModel(user_playerDTO));
        return user_playerMapper.toDto(updated);
    }
}