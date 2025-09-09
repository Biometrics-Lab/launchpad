package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Rep_metric;
import com.bmlab.launchpad.web.dto.Rep_metricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Rep_metricMapper {
    Rep_metricMapper rep_metricMapper = Mappers.getMapper(Rep_metricMapper.class);

    Rep_metricDto toDto(Rep_metric rep_metric);
    Rep_metric toModel(Rep_metricDto rep_metricDto);
    List<Rep_metricDto> toDtos(List<Rep_metric> rep_metrics);
    List<Rep_metric> toModels(List<Rep_metricDto> rep_metricDtos);
}