package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.DataSourceTypeDictionary;
import com.biolab.launchpad.internal.web.dto.DataSourceTypeDictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataSourceTypeDictionaryMapper {
    DataSourceTypeDictionaryMapper dataSourceTypeDictionaryMapper = Mappers.getMapper(DataSourceTypeDictionaryMapper.class);

    DataSourceTypeDictionaryDto toDto(DataSourceTypeDictionary data_sourceTypeDictionary);
    DataSourceTypeDictionary toModel(DataSourceTypeDictionaryDto data_sourceTypeDictionaryDto);
    List<DataSourceTypeDictionaryDto> toDtos(List<DataSourceTypeDictionary> data_sourceTypeDictionaries);
    List<DataSourceTypeDictionary> toModels(List<DataSourceTypeDictionaryDto> data_sourceTypeDictionaryDtos);
}