package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.web.dto.RepDto;
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