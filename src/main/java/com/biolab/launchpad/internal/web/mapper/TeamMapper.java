package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Team;
import com.biolab.launchpad.internal.web.dto.TeamDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    TeamDto toDto(Team team);
    Team toModel(TeamDto teamDto);
    List<TeamDto> toDtos(List<Team> teams);
    List<Team> toModels(List<TeamDto> teamDtos);
}