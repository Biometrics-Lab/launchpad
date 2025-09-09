package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.User_role_dictionary;
import com.biolab.launchpad.internal.web.dto.User_role_dictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface User_role_dictionaryMapper {
    User_role_dictionaryMapper user_role_dictionaryMapper = Mappers.getMapper(User_role_dictionaryMapper.class);

    User_role_dictionaryDto toDto(User_role_dictionary user_role_dictionary);
    User_role_dictionary toModel(User_role_dictionaryDto user_role_dictionaryDto);
    List<User_role_dictionaryDto> toDtos(List<User_role_dictionary> user_role_dictionarys);
    List<User_role_dictionary> toModels(List<User_role_dictionaryDto> user_role_dictionaryDtos);
}