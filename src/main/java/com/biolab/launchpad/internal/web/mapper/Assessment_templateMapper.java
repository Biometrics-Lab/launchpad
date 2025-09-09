package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Assessment_template;
import com.biolab.launchpad.internal.web.dto.Assessment_templateDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Assessment_templateMapper {
    Assessment_templateMapper assessment_templateMapper = Mappers.getMapper(Assessment_templateMapper.class);

    Assessment_templateDto toDto(Assessment_template assessment_template);
    Assessment_template toModel(Assessment_templateDto assessment_templateDto);
    List<Assessment_templateDto> toDtos(List<Assessment_template> assessment_templates);
    List<Assessment_template> toModels(List<Assessment_templateDto> assessment_templateDtos);
}