package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Assessment;
import com.bmlab.launchpad.web.dto.AssessmentDto;
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