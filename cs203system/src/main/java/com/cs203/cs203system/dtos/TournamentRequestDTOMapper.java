package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentRequestDTOMapper {
    Tournament toEntity(TournamentRequestDTO tournamentRequestDTO);

    TournamentRequestDTO toDto(Tournament tournament);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentRequestDTO tournamentRequestDTO, @MappingTarget Tournament tournament);

    default List<Long> playersToPlayerIds(List<Player> players) {
        return players.stream().map(Player::getId).collect(Collectors.toList());
    }
}