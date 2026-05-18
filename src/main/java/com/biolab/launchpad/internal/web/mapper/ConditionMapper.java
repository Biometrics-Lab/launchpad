package com.biolab.launchpad.internal.web.mapper;

import com.biolab.launchpad.internal.repository.model.Condition;
import com.biolab.launchpad.internal.web.dto.ConditionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConditionMapper {
    ConditionMapper conditionMapper = Mappers.getMapper(ConditionMapper.class);

    ConditionDto toDto(Condition condition);
    Condition toModel(ConditionDto conditionDto);
    List<ConditionDto> toDtos(List<Condition> conditions);
    List<Condition> toModels(List<ConditionDto> conditionDtos);
}
