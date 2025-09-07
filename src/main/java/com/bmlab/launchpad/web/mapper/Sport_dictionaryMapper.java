package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Sport_dictionary;
import com.bmlab.launchpad.web.dto.Sport_dictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Sport_dictionaryMapper {
    Sport_dictionaryMapper sport_dictionaryMapper = Mappers.getMapper(Sport_dictionaryMapper.class);

    Sport_dictionaryDto toDto(Sport_dictionary sport_dictionary);
    Sport_dictionary toModel(Sport_dictionaryDto sport_dictionaryDto);
    List<Sport_dictionaryDto> toDtos(List<Sport_dictionary> sport_dictionarys);
    List<Sport_dictionary> toModels(List<Sport_dictionaryDto> sport_dictionaryDtos);
}