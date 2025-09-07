package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.User;
import com.bmlab.launchpad.web.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);
    User toModel(UserDto userDto);
    List<UserDto> toDtos(List<User> users);
    List<User> toModels(List<UserDto> userDtos);
}