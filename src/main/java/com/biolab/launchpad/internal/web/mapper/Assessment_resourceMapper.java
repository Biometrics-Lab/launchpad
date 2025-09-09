package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Assessment_resource;
import com.biolab.launchpad.internal.web.dto.Assessment_resourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Assessment_resourceMapper {
    Assessment_resourceMapper assessment_resourceMapper = Mappers.getMapper(Assessment_resourceMapper.class);

    Assessment_resourceDto toDto(Assessment_resource assessment_resource);
    Assessment_resource toModel(Assessment_resourceDto assessment_resourceDto);
    List<Assessment_resourceDto> toDtos(List<Assessment_resource> assessment_resources);
    List<Assessment_resource> toModels(List<Assessment_resourceDto> assessment_resourceDtos);
}