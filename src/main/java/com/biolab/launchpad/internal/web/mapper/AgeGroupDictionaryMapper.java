package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.AgeGroupDictionary;
import com.biolab.launchpad.internal.web.dto.AgeGroupDictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AgeGroupDictionaryMapper {
    AgeGroupDictionaryMapper ageGroupDictionaryMapper = Mappers.getMapper(AgeGroupDictionaryMapper.class);

    AgeGroupDictionaryDto toDto(AgeGroupDictionary age_groupDictionary);
    AgeGroupDictionary toModel(AgeGroupDictionaryDto age_groupDictionaryDto);
    List<AgeGroupDictionaryDto> toDtos(List<AgeGroupDictionary> age_groupDictionaries);
    List<AgeGroupDictionary> toModels(List<AgeGroupDictionaryDto> age_groupDictionaryDtos);
}
