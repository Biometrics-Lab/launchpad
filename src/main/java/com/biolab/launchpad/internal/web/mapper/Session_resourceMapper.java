package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Session_resource;
import com.biolab.launchpad.internal.web.dto.Session_resourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Session_resourceMapper {
    Session_resourceMapper session_resourceMapper = Mappers.getMapper(Session_resourceMapper.class);

    Session_resourceDto toDto(Session_resource session_resource);
    Session_resource toModel(Session_resourceDto session_resourceDto);
    List<Session_resourceDto> toDtos(List<Session_resource> session_resources);
    List<Session_resource> toModels(List<Session_resourceDto> session_resourceDtos);
}