package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.SessionMetric;
import com.biolab.launchpad.internal.web.dto.SessionMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionMetricMapper {
    SessionMetricMapper sessionMetricMapper = Mappers.getMapper(SessionMetricMapper.class);

    SessionMetricDto toDto(SessionMetric session_metric);
    SessionMetric toModel(SessionMetricDto session_metricDto);
    List<SessionMetricDto> toDtos(List<SessionMetric> session_metrics);
    List<SessionMetric> toModels(List<SessionMetricDto> session_metricDtos);
}