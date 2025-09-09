package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Resource_type_dictionary;
import com.biolab.launchpad.internal.web.dto.Resource_type_dictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Resource_type_dictionaryMapper {
    Resource_type_dictionaryMapper resource_type_dictionaryMapper = Mappers.getMapper(Resource_type_dictionaryMapper.class);

    Resource_type_dictionaryDto toDto(Resource_type_dictionary resource_type_dictionary);
    Resource_type_dictionary toModel(Resource_type_dictionaryDto resource_type_dictionaryDto);
    List<Resource_type_dictionaryDto> toDtos(List<Resource_type_dictionary> resource_type_dictionarys);
    List<Resource_type_dictionary> toModels(List<Resource_type_dictionaryDto> resource_type_dictionaryDtos);
}