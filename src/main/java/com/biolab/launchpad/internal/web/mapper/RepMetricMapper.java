package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.RepMetric;
import com.biolab.launchpad.internal.web.dto.RepMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepMetricMapper {
    RepMetricMapper repMetricMapper = Mappers.getMapper(RepMetricMapper.class);

    RepMetricDto toDto(RepMetric repMetric);
    RepMetric toModel(RepMetricDto repMetricDto);
    List<RepMetricDto> toDtos(List<RepMetric> repMetrics);
    List<RepMetric> toModels(List<RepMetricDto> repMetricDtos);
}