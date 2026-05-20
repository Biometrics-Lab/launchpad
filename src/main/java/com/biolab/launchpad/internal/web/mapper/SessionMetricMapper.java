package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.SessionMetric;
import com.biolab.launchpad.internal.web.dto.SessionMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionMetricMapper {
    SessionMetricMapper sessionMetricMapper = Mappers.getMapper(SessionMetricMapper.class);

    SessionMetricDto toDto(SessionMetric sessionMetric);
    SessionMetric toModel(SessionMetricDto sessionMetricDto);
    List<SessionMetricDto> toDtos(List<SessionMetric> sessionMetrics);
    List<SessionMetric> toModels(List<SessionMetricDto> sessionMetricDtos);
}