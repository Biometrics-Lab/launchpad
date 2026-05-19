package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Integration;
import com.biolab.launchpad.internal.web.dto.IntegrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IntegrationMapper {
    IntegrationMapper integrationMapper = Mappers.getMapper(IntegrationMapper.class);

    IntegrationDto toDto(Integration integration);
    Integration toModel(IntegrationDto integrationDto);
    List<IntegrationDto> toDtos(List<Integration> integrations);
    List<Integration> toModels(List<IntegrationDto> integrationDtos);
}
