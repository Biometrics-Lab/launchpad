package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Age_group_dictionary;
import com.bmlab.launchpad.web.dto.Age_group_dictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Age_group_dictionaryMapper {
    Age_group_dictionaryMapper age_group_dictionaryMapper = Mappers.getMapper(Age_group_dictionaryMapper.class);

    Age_group_dictionaryDto toDto(Age_group_dictionary age_group_dictionary);
    Age_group_dictionary toModel(Age_group_dictionaryDto age_group_dictionaryDto);
    List<Age_group_dictionaryDto> toDtos(List<Age_group_dictionary> age_group_dictionarys);
    List<Age_group_dictionary> toModels(List<Age_group_dictionaryDto> age_group_dictionaryDtos);
}
