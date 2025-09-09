package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.User_player;
import com.biolab.launchpad.internal.web.dto.User_playerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface User_playerMapper {
    User_playerMapper user_playerMapper = Mappers.getMapper(User_playerMapper.class);

    User_playerDto toDto(User_player user_player);
    User_player toModel(User_playerDto user_playerDto);
    List<User_playerDto> toDtos(List<User_player> user_players);
    List<User_player> toModels(List<User_playerDto> user_playerDtos);
}