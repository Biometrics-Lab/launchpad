package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Metric;
import com.bmlab.launchpad.web.dto.MetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricMapper {
    MetricMapper metricMapper = Mappers.getMapper(MetricMapper.class);

    MetricDto toDto(Metric metricDto);
    Metric toModel(MetricDto metricDto);
    List<MetricDto> toDtos(List<Metric> metrics);
    List<Metric> toModels(List<MetricDto> metricDtos);
}
