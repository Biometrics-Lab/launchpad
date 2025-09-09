package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Report;
import com.biolab.launchpad.internal.web.dto.ReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportMapper reportMapper = Mappers.getMapper(ReportMapper.class);

    ReportDto toDto(Report report);
    Report toModel(ReportDto reportDto);
    List<ReportDto> toDtos(List<Report> reports);
    List<Report> toModels(List<ReportDto> reportDtos);
}