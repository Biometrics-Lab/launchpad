package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Session_metric;
import com.bmlab.launchpad.web.dto.Session_metricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Session_metricMapper {
    Session_metricMapper session_metricMapper = Mappers.getMapper(Session_metricMapper.class);

    Session_metricDto toDto(Session_metric session_metric);
    Session_metric toModel(Session_metricDto session_metricDto);
    List<Session_metricDto> toDtos(List<Session_metric> session_metrics);
    List<Session_metric> toModels(List<Session_metricDto> session_metricDtos);
}