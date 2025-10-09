package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.UserPlayer;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.UserPlayerService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.UserPlayerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.UserPlayerMapper.userPlayerMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user_players")
@Log4j2
public class UserPlayerController {

    private final UserPlayerService userPlayerService;

    @PostMapping
    public UserPlayerDto create(@Valid @RequestBody UserPlayerDto user_playerDto) {
        UserPlayer created = userPlayerService.create(userPlayerMapper.toModel(user_playerDto));
        return userPlayerMapper.toDto(created);
    }

    @GetMapping
    public List<UserPlayerDto> getAll() {
        return userPlayerMapper.toDtos(userPlayerService.findAll());
    }

    @GetMapping("/{id}")
    public UserPlayerDto getById(@PathVariable Integer id) {
        Optional<UserPlayerDto> user_playerOptional = userPlayerService.findById(id).map(userPlayerMapper::toDto);
        if (user_playerOptional.isPresent()) {
            return user_playerOptional.get();
        } else {
            log.warn("Could not find userPlayer with id {}", id);
            throw new NotFoundByException("UserPlayer not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        userPlayerService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public UserPlayerDto update(@Valid @RequestBody UserPlayerDto user_playerDTO) {
        UserPlayer updated = userPlayerService.update(userPlayerMapper.toModel(user_playerDTO));
        return userPlayerMapper.toDto(updated);
    }
}