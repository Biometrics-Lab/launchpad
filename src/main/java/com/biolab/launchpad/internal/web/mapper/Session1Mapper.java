package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Session1;
import com.biolab.launchpad.internal.web.dto.Session1Dto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Session1Mapper {
    Session1Mapper SESSION_1_MAPPER = Mappers.getMapper(Session1Mapper.class);

    Session1Dto toDto(Session1 session1);
    Session1 toModel(Session1Dto session1Dto);
    List<Session1Dto> toDtos(List<Session1> session1s);
    List<Session1> toModels(List<Session1Dto> session1Dtos);
}