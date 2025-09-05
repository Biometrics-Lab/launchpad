package com.bmlab.launchpad.web.mapper;

import com.bmlab.launchpad.repository.model.Measurement;
import com.bmlab.launchpad.web.dto.MeasurementDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {
    MeasurementMapper measurementMapper = Mappers.getMapper(MeasurementMapper.class);

    MeasurementDto toDto(Measurement measurement);
    Measurement toModel(MeasurementDto measurementDto);
    List<MeasurementDto> toDtos(List<Measurement> measurements);
    List<Measurement> toModels(List<MeasurementDto> measurementDtos);
}
