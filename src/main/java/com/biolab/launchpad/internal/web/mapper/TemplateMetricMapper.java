package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.TemplateMetric;
import com.biolab.launchpad.internal.web.dto.TemplateMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMetricMapper {
    TemplateMetricMapper templateMetricMapper = Mappers.getMapper(TemplateMetricMapper.class);

    TemplateMetricDto toDto(TemplateMetric template_metric);
    TemplateMetric toModel(TemplateMetricDto template_metricDto);
    List<TemplateMetricDto> toDtos(List<TemplateMetric> template_metrics);
    List<TemplateMetric> toModels(List<TemplateMetricDto> template_metricDtos);
}