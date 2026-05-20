package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.web.dto.SessionDto;
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
