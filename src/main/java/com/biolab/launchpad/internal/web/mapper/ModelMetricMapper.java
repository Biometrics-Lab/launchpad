package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.ModelMetric;
import com.biolab.launchpad.internal.web.dto.ModelMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMetricMapper {
    ModelMetricMapper modelMetricMapper = Mappers.getMapper(ModelMetricMapper.class);

    ModelMetricDto toDto(ModelMetric model_metric);
    ModelMetric toModel(ModelMetricDto model_metricDto);
    List<ModelMetricDto> toDtos(List<ModelMetric> model_metrics);
    List<ModelMetric> toModels(List<ModelMetricDto> model_metricDtos);
}