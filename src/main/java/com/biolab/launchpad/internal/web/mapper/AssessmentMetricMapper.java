package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import com.biolab.launchpad.internal.web.dto.AssessmentMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssessmentMetricMapper {
    AssessmentMetricMapper assessmentMetricMapper = Mappers.getMapper(AssessmentMetricMapper.class);

    AssessmentMetricDto toDto(AssessmentMetric assessment_metric);
    AssessmentMetric toModel(AssessmentMetricDto assessment_metricDto);
    List<AssessmentMetricDto> toDtos(List<AssessmentMetric> assessment_metrics);
    List<AssessmentMetricDto> toModels(List<AssessmentMetricDto> assessment_metricDtos);
}