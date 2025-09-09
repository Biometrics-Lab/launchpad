package com.bmlab.launchpad.web.controller;

import com.bmlab.launchpad.repository.model.User_role_dictionary;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.service.User_role_dictionaryService;
import com.bmlab.launchpad.web.dto.ResponseCode;
import com.bmlab.launchpad.web.dto.ResponseDto;
import com.bmlab.launchpad.web.dto.User_role_dictionaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.bmlab.launchpad.web.mapper.User_role_dictionaryMapper.user_role_dictionaryMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user_role_dictionarys")
@Log4j2
public class User_role_dictionaryController {

    private final User_role_dictionaryService user_role_dictionaryService;

    @PostMapping
    public User_role_dictionaryDto create(@Valid @RequestBody User_role_dictionaryDto user_role_dictionaryDto) {
        User_role_dictionary created = user_role_dictionaryService.create(user_role_dictionaryMapper.toModel(user_role_dictionaryDto));
        return user_role_dictionaryMapper.toDto(created);
    }

    @GetMapping
    public List<User_role_dictionaryDto> getAll() {
        return user_role_dictionaryMapper.toDtos(user_role_dictionaryService.findAll());
    }

    @GetMapping("/{id}")
    public User_role_dictionaryDto getById(@PathVariable String id) {
        Optional<User_role_dictionaryDto> user_role_dictionaryOptional = user_role_dictionaryService.findById(id).map(user_role_dictionaryMapper::toDto);
        if (user_role_dictionaryOptional.isPresent()) {
            return user_role_dictionaryOptional.get();
        } else {
            log.warn("Could not find user_role_dictionary with id {}", id);
            throw new NotFoundByException("User_role_dictionary not found by id: %d", id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable String id) {
        user_role_dictionaryService.deleteById(id);
        return ResponseCode.OK.getResponseDto();
    }

    @PutMapping
    public User_role_dictionaryDto update(@Valid @RequestBody User_role_dictionaryDto user_role_dictionaryDTO) {
        User_role_dictionary updated = user_role_dictionaryService.update(user_role_dictionaryMapper.toModel(user_role_dictionaryDTO));
        return user_role_dictionaryMapper.toDto(updated);
    }
}