package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.RepMetricSource;
import com.biolab.launchpad.internal.web.dto.RepMetricSourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepMetricSourceMapper {
    RepMetricSourceMapper repMetricSourceMapper = Mappers.getMapper(RepMetricSourceMapper.class);

    RepMetricSourceDto toDto(RepMetricSource rep_metricSource);
    RepMetricSource toModel(RepMetricSourceDto rep_metricSourceDto);
    List<RepMetricSourceDto> toDtos(List<RepMetricSource> rep_metricSources);
    List<RepMetricSource> toModels(List<RepMetricSourceDto> rep_metricSourceDtos);
}