package com.cs203.cs203system.dtos;

import com.cs203.cs203system.dtos.TournamentResponseDto;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.utility.DoubleEliminationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentResponseDTOMapper {

    static final Logger logger = LoggerFactory.getLogger(DoubleEliminationManagerImpl.class);
    Tournament toEntity(TournamentResponseDto tournamentResponseDto);

    @AfterMapping
    default void linkMatches(@MappingTarget Tournament tournament) {
        logger.debug("This is the tournament:" + tournament);
        if (tournament.getMatches() == null || tournament.getMatches().isEmpty()) {
            System.out.println("No matches found for this tournament: " + tournament.getId());
        } else {
            System.out.println("Linking matches to the tournament: " + tournament.getId());
            tournament.getMatches().forEach(match -> {
                System.out.println("Setting tournament for match: " + match.getId());
                match.setTournament(tournament);
            });
        }
    }

    TournamentResponseDto toDto(Tournament tournament);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentResponseDto tournamentResponseDto, @MappingTarget Tournament tournament);
}