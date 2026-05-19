package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.web.dto.Session1Dto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Session1Mapper {
    Session1Mapper session1Mapper = Mappers.getMapper(Session1Mapper.class);

    Session1Dto toDto(Session session);
    Session toModel(Session1Dto session1Dto);
    List<Session1Dto> toDtos(List<Session> sessions);
    List<Session> toModels(List<Session1Dto> session1Dtos);
}