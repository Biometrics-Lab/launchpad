package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Organisation;
import com.biolab.launchpad.internal.web.dto.OrganisationDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganisationMapper {
    OrganisationMapper organisationMapper = Mappers.getMapper(OrganisationMapper.class);

    OrganisationDto toDto(Organisation organisation);
    Organisation toModel(OrganisationDto organisationDto);
    List<OrganisationDto> toDtos(List<Organisation> organisations);
    List<Organisation> toModels(List<OrganisationDto> organisationDtos);
}