package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.UserRoleDictionary;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.service.UserRoleDictionaryService;
import com.biolab.launchpad.internal.web.dto.ResponseCode;
import com.biolab.launchpad.internal.web.dto.ResponseDto;
import com.biolab.launchpad.internal.web.dto.UserRoleDictionaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.UserRoleDictionaryMapper.user_role_dictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user_role_dictionarys")
@Log4j2
public class UserRoleDictionaryController {

    private final UserRoleDictionaryService userRoleDictionaryService;

    @PostMapping
    public UserRoleDictionaryDto create(@Valid @RequestBody UserRoleDictionaryDto user_roleDictionaryDto) {
        UserRoleDictionary created = userRoleDictionaryService.create(user_role_dictionaryMapper.toModel(user_roleDictionaryDto));
        return user_role_dictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<UserRoleDictionaryDto> getAll() {
        return user_role_dictionaryMapper.toDtos(userRoleDictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public UserRoleDictionaryDto getById(@PathVariable String id) {
        Optional<UserRoleDictionaryDto> userRoleDictionaryOptional = userRoleDictionaryService.findById(id).map(user_role_dictionaryMapper::toDto);
        if (userRoleDictionaryOptional.isPresent()) {
            return userRoleDictionaryOptional.get();
        } else {
            log.warn("Could not find user_role_dictionary with id {}", id);
            throw new NotFoundByException("User_role_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        userRoleDictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public UserRoleDictionaryDto update(@Valid @RequestBody UserRoleDictionaryDto user_roleDictionaryDTO) {
        UserRoleDictionary updated = userRoleDictionaryService.update(user_role_dictionaryMapper.toModel(user_roleDictionaryDTO));
        return user_role_dictionaryMapper.toDto(updated);
    }
}