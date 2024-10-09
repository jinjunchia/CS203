package com.cs203.cs203system.controller;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentRequestDTOMapper {
    Tournament toEntity(TournamentRequestDTO tournamentRequestDTO);

    @Mapping(target = "playerIds", expression = "java(playersToPlayerIds(tournament.getPlayers()))")
    TournamentRequestDTO toDto(Tournament tournament);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentRequestDTO tournamentRequestDTO, @MappingTarget Tournament tournament);

    default Set<Long> playersToPlayerIds(Set<Player> players) {
        return players.stream().map(Player::getId).collect(Collectors.toSet());
    }
}