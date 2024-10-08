package com.cs203.cs203system.dtos;

import com.cs203.cs203system.dtos.TournamentResponseDto;
import com.cs203.cs203system.model.Tournament;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentResponseDTOMapper {
    Tournament toEntity(TournamentResponseDto tournamentResponseDto);

    @AfterMapping
    default void linkMatches(@MappingTarget Tournament tournament) {
        tournament.getMatches().forEach(match -> match.setTournament(tournament));
    }

    TournamentResponseDto toDto(Tournament tournament);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentResponseDto tournamentResponseDto, @MappingTarget Tournament tournament);
}