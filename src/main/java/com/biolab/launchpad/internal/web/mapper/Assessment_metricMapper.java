package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Assessment_metric;
import com.biolab.launchpad.internal.web.dto.Assessment_metricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Assessment_metricMapper {
    Assessment_metricMapper assessment_metricMapper = Mappers.getMapper(Assessment_metricMapper.class);

    Assessment_metricDto toDto(Assessment_metric assessment_metric);
    Assessment_metric toModel(Assessment_metricDto assessment_metricDto);
    List<Assessment_metricDto> toDtos(List<Assessment_metric> assessment_metrics);
    List<Assessment_metricDto> toModels(List<Assessment_metricDto> assessment_metricDtos);
}