package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.web.dto.RepResourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepResourceMapper {
    RepResourceMapper repResourceMapper = Mappers.getMapper(RepResourceMapper.class);

    RepResourceDto toDto(RepResource rep_resource);
    RepResource toModel(RepResourceDto rep_resourceDto);
    List<RepResourceDto> toDtos(List<RepResource> rep_resources);
    List<RepResource> toModels(List<RepResourceDto> rep_resourceDtos);
}