package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Rep_resource;
import com.bmlab.launchpad.web.dto.Rep_resourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Rep_resourceMapper {
    Rep_resourceMapper rep_resourceMapper = Mappers.getMapper(Rep_resourceMapper.class);

    Rep_resourceDto toDto(Rep_resource rep_resource);
    Rep_resource toModel(Rep_resourceDto rep_resourceDto);
    List<Rep_resourceDto> toDtos(List<Rep_resource> rep_resources);
    List<Rep_resource> toModels(List<Rep_resourceDto> rep_resourceDtos);
}