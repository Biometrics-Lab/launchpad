package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Player;
import com.biolab.launchpad.internal.web.dto.PlayerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerMapper playerMapper = Mappers.getMapper(PlayerMapper.class);

    PlayerDto toDto(Player player);
    Player toModel(PlayerDto playerDto);
    List<PlayerDto> toDtos(List<Player> players);
    List<Player> toModels(List<PlayerDto> playerDtos);
}