package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.User;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.UserService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.UserMapper.userMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Log4j2
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User created = userService.create(userMapper.toModel(userDto));
        return userMapper.toDto(created);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userMapper.toDtos(userService.findAll());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Integer id) {
        Optional<UserDto> userOptional = userService.findById(id).map(userMapper::toDto);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            log.warn("Could not find user with id {}", id);
            throw new NotFoundByException("User not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Integer id) {
        userService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto userDTO) {
        User updated = userService.update(userMapper.toModel(userDTO));
        return userMapper.toDto(updated);
    }
}