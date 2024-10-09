package com.cs203.cs203system.controller;

import com.cs203.cs203system.model.Tournament;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentResponseDTOMapper {
    Tournament toEntity(TournamentResponseDTO tournamentResponseDTO);

    @AfterMapping
    default void linkMatches(@MappingTarget Tournament tournament) {
        tournament.getMatches().forEach(match -> match.setTournament(tournament));
    }

    TournamentResponseDTO toDto(Tournament tournament);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentResponseDTO tournamentResponseDTO, @MappingTarget Tournament tournament);
}