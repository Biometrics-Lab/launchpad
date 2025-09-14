package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.AssessmentTemplate;
import com.biolab.launchpad.internal.web.dto.AssessmentTemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssessmentTemplateMapper {
    AssessmentTemplateMapper assessmentTemplateMapper = Mappers.getMapper(AssessmentTemplateMapper.class);

    AssessmentTemplateDto toDto(AssessmentTemplate assessment_template);
    AssessmentTemplate toModel(AssessmentTemplateDto assessment_templateDto);
    List<AssessmentTemplateDto> toDtos(List<AssessmentTemplate> assessment_templates);
    List<AssessmentTemplate> toModels(List<AssessmentTemplateDto> assessment_templateDtos);
}