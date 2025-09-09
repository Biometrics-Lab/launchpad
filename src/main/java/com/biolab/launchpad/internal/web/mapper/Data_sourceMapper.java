package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Data_source;
import com.bmlab.launchpad.web.dto.Data_sourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Data_sourceMapper {
    Data_sourceMapper data_sourceMapper = Mappers.getMapper(Data_sourceMapper.class);

    Data_sourceDto toDto(Data_source data_source);
    Data_source toModel(Data_sourceDto data_sourceDto);
    List<Data_sourceDto> toDtos(List<Data_source> data_sources);
    List<Data_source> toModels(List<Data_sourceDto> data_sourceDtos);
}