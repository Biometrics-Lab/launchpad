package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Assessment;
import com.biolab.launchpad.internal.web.dto.AssessmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {
    AssessmentMapper assessmentMapper = Mappers.getMapper(AssessmentMapper.class);

    AssessmentDto toDto(Assessment assessment);
    Assessment toModel(AssessmentDto assessmentDto);
    List<AssessmentDto> toDtos(List<Assessment> assessments);
    List<Assessment> toModels(List<AssessmentDto> assessmentDtos);
}