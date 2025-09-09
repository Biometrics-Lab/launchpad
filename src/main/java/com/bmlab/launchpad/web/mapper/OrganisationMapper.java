package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Organisation;
import com.bmlab.launchpad.web.dto.OrganisationDto;
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