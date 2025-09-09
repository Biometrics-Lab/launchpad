package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Model_metric;
import com.bmlab.launchpad.web.dto.Model_metricDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Model_metricMapper {
    Model_metricMapper model_metricMapper = Mappers.getMapper(Model_metricMapper.class);

    Model_metricDto toDto(Model_metric model_metric);
    Model_metric toModel(Model_metricDto model_metricDto);
    List<Model_metricDto> toDtos(List<Model_metric> model_metrics);
    List<Model_metric> toModels(List<Model_metricDto> model_metricDtos);
}