package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Rep;
import com.bmlab.launchpad.web.dto.RepDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepMapper {
    RepMapper repMapper = Mappers.getMapper(RepMapper.class);

    RepDto toDto(Rep rep);
    Rep toModel(RepDto repDto);
    List<RepDto> toDtos(List<Rep> reps);
    List<Rep> toModels(List<RepDto> repDtos);
}