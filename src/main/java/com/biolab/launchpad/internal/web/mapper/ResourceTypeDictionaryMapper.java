package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
import com.biolab.launchpad.internal.web.dto.ResourceTypeDictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceTypeDictionaryMapper {
    ResourceTypeDictionaryMapper resourceTypeDictionaryMapper = Mappers.getMapper(ResourceTypeDictionaryMapper.class);

    ResourceTypeDictionaryDto toDto(ResourceTypeDictionary resource_typeDictionary);
    ResourceTypeDictionary toModel(ResourceTypeDictionaryDto resource_typeDictionaryDto);
    List<ResourceTypeDictionaryDto> toDtos(List<ResourceTypeDictionary> resource_typeDictionaries);
    List<ResourceTypeDictionary> toModels(List<ResourceTypeDictionaryDto> resource_typeDictionaryDtos);
}