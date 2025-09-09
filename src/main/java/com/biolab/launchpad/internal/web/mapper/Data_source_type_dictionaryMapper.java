package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Data_source_type_dictionary;
import com.bmlab.launchpad.web.dto.Data_source_type_dictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Data_source_type_dictionaryMapper {
    Data_source_type_dictionaryMapper data_source_type_dictionaryMapper = Mappers.getMapper(Data_source_type_dictionaryMapper.class);

    Data_source_type_dictionaryDto toDto(Data_source_type_dictionary data_source_type_dictionary);
    Data_source_type_dictionary toModel(Data_source_type_dictionaryDto data_source_type_dictionaryDto);
    List<Data_source_type_dictionaryDto> toDtos(List<Data_source_type_dictionary> data_source_type_dictionarys);
    List<Data_source_type_dictionary> toModels(List<Data_source_type_dictionaryDto> data_source_type_dictionaryDtos);
}