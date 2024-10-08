package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.Tournament;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentMapper {
    Tournament toEntity(TournamentCreateDto tournamentCreateDto);

    TournamentCreateDto toDto(Tournament tournament);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentCreateDto tournamentCreateDto, @MappingTarget Tournament tournament);
}