package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.Match;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MatchUpdateDTOMapper {
    Match toEntity(MatchUpdateDTO matchUpdateDto);

    MatchUpdateDTO toDto(Match match);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Match partialUpdate(MatchUpdateDTO matchUpdateDto, @MappingTarget Match match);
}