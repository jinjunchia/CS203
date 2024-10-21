package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.Match;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InputMatchDTOMapper {
    Match toEntity(InputMatchDTO inputMatchDTO);

    InputMatchDTO toDto(Match match);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Match partialUpdate(InputMatchDTO inputMatchDTO, @MappingTarget Match match);
}