package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.UserRoleDictionary;
import com.biolab.launchpad.internal.web.dto.UserRoleDictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRoleDictionaryMapper {
    UserRoleDictionaryMapper user_role_dictionaryMapper = Mappers.getMapper(UserRoleDictionaryMapper.class);

    UserRoleDictionaryDto toDto(UserRoleDictionary user_role_dictionary);
    UserRoleDictionary toModel(UserRoleDictionaryDto user_roleDictionaryDto);
    List<UserRoleDictionaryDto> toDtos(List<UserRoleDictionary> user_role_dictionarys);
    List<UserRoleDictionary> toModels(List<UserRoleDictionaryDto> user_roleDictionaryDtos);
}