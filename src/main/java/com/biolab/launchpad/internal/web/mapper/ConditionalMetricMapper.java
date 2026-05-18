package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
import com.biolab.launchpad.internal.web.dto.ConditionalMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConditionalMetricMapper {
    ConditionalMetricMapper conditionalMetricMapper = Mappers.getMapper(ConditionalMetricMapper.class);

    ConditionalMetricDto toDto(ConditionalMetric conditionalMetric);
    ConditionalMetric toModel(ConditionalMetricDto conditionalMetricDto);
    List<ConditionalMetricDto> toDtos(List<ConditionalMetric> conditionalMetrics);
    List<ConditionalMetric> toModels(List<ConditionalMetricDto> conditionalMetricDtos);
}
