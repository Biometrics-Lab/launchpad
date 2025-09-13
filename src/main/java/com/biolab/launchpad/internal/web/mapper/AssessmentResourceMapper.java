package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.AssessmentResource;
import com.biolab.launchpad.internal.web.dto.AssessmentResourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssessmentResourceMapper {
    AssessmentResourceMapper assessmentResourceMapper = Mappers.getMapper(AssessmentResourceMapper.class);

    AssessmentResourceDto toDto(AssessmentResource assessment_resource);
    AssessmentResource toModel(AssessmentResourceDto assessment_resourceDto);
    List<AssessmentResourceDto> toDtos(List<AssessmentResource> assessment_resources);
    List<AssessmentResource> toModels(List<AssessmentResourceDto> assessment_resourceDtos);
}