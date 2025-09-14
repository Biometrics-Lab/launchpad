package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.UserPlayer;
import com.biolab.launchpad.internal.web.dto.UserPlayerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPlayerMapper {
    UserPlayerMapper userPlayerMapper = Mappers.getMapper(UserPlayerMapper.class);

    UserPlayerDto toDto(UserPlayer user_player);
    UserPlayer toModel(UserPlayerDto user_playerDto);
    List<UserPlayerDto> toDtos(List<UserPlayer> user_players);
    List<UserPlayer> toModels(List<UserPlayerDto> user_playerDtos);
}