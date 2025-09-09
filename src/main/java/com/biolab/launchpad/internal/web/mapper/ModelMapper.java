package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Model;
import com.biolab.launchpad.internal.web.dto.ModelDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {
    ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);

    ModelDto toDto(Model model);
    Model toModel(ModelDto modelDto);
    List<ModelDto> toDtos(List<Model> models);
    List<Model> toModels(List<ModelDto> modelDtos);
}