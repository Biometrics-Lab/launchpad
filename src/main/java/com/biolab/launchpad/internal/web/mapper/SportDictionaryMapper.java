package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.SportDictionary;
import com.biolab.launchpad.internal.web.dto.SportDictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SportDictionaryMapper {
    SportDictionaryMapper sportDictionaryMapper = Mappers.getMapper(SportDictionaryMapper.class);

    SportDictionaryDto toDto(SportDictionary sport_dictionary);
    SportDictionary toModel(SportDictionaryDto sport_dictionaryDto);
    List<SportDictionaryDto> toDtos(List<SportDictionary> sport_dictionaries);
    List<SportDictionary> toModels(List<SportDictionaryDto> sport_dictionaryDtos);
}