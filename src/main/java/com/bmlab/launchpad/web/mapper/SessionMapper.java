package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Session;
import com.bmlab.launchpad.web.dto.SessionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

    SessionDto toDto(Session session);
    Session toModel(SessionDto sessionDto);
    List<SessionDto> toDtos(List<Session> sessions);
    List<Session> toModels(List<SessionDto> sessionDtos);
}