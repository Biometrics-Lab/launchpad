package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Metric;
import com.biolab.launchpad.internal.web.dto.MetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricMapper {
    MetricMapper metricMapper = Mappers.getMapper(MetricMapper.class);

    MetricDto toDto(Metric metric);
    Metric toModel(MetricDto metricDto);
    List<MetricDto> toDtos(List<Metric> metrics);
    List<Metric> toModels(List<MetricDto> metricDtos);
}