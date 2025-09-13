package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.RepMetric;
import com.biolab.launchpad.internal.web.dto.Rep_metricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepMetricMapper {
    RepMetricMapper repMetricMapper = Mappers.getMapper(RepMetricMapper.class);

    Rep_metricDto toDto(RepMetric rep_metric);
    RepMetric toModel(Rep_metricDto rep_metricDto);
    List<Rep_metricDto> toDtos(List<RepMetric> rep_metrics);
    List<RepMetric> toModels(List<Rep_metricDto> rep_metricDtos);
}