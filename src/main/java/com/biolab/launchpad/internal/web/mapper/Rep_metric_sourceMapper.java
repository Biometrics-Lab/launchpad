package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Rep_metric_source;
import com.biolab.launchpad.internal.web.dto.Rep_metric_sourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Rep_metric_sourceMapper {
    Rep_metric_sourceMapper rep_metric_sourceMapper = Mappers.getMapper(Rep_metric_sourceMapper.class);

    Rep_metric_sourceDto toDto(Rep_metric_source rep_metric_source);
    Rep_metric_source toModel(Rep_metric_sourceDto rep_metric_sourceDto);
    List<Rep_metric_sourceDto> toDtos(List<Rep_metric_source> rep_metric_sources);
    List<Rep_metric_source> toModels(List<Rep_metric_sourceDto> rep_metric_sourceDtos);
}