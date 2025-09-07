package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Template_metric;
import com.bmlab.launchpad.web.dto.Template_metricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Template_metricMapper {
    Template_metricMapper template_metricMapper = Mappers.getMapper(Template_metricMapper.class);

    Template_metricDto toDto(Template_metric template_metric);
    Template_metric toModel(Template_metricDto template_metricDto);
    List<Template_metricDto> toDtos(List<Template_metric> template_metrics);
    List<Template_metric> toModels(List<Template_metricDto> template_metricDtos);
}