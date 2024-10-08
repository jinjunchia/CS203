package com.cs203.cs203system.model;

import com.cs203.cs203system.dtos.MatchResponseDTO;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MatchResponseDTOMapper {
    Match toEntity(MatchResponseDTO matchResponseDTO);

    MatchResponseDTO toDto(Match match);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Match partialUpdate(MatchResponseDTO matchResponseDTO, @MappingTarget Match match);
}