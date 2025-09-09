package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Report;
import com.bmlab.launchpad.web.dto.ReportDto;
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