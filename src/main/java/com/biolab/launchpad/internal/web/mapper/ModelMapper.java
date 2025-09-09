package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Model;
import com.bmlab.launchpad.web.dto.ModelDto;
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