package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.DataSource;
import com.biolab.launchpad.internal.web.dto.DataSourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataSourceMapper {
    DataSourceMapper dataSourceMapper = Mappers.getMapper(DataSourceMapper.class);

    DataSourceDto toDto(DataSource data_source);
    DataSource toModel(DataSourceDto data_sourceDto);
    List<DataSourceDto> toDtos(List<DataSource> data_sources);
    List<DataSource> toModels(List<DataSourceDto> data_sourceDtos);
}