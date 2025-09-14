package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.SessionResource;
import com.biolab.launchpad.internal.web.dto.SessionResourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionResourceMapper {
    SessionResourceMapper sessionResourceMapper = Mappers.getMapper(SessionResourceMapper.class);

    SessionResourceDto toDto(SessionResource session_resource);
    SessionResource toModel(SessionResourceDto session_resourceDto);
    List<SessionResourceDto> toDtos(List<SessionResource> session_resources);
    List<SessionResource> toModels(List<SessionResourceDto> session_resourceDtos);
}