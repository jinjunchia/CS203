package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.Tournament;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TournamentResponseDTOMapper {
//    @Mapping(source = "adminUsername", target = "admin.username")
//    @Mapping(source = "adminId", target = "admin.id")
    Tournament toEntity(TournamentResponseDTO tournamentResponseDTO);

    @AfterMapping
    default void linkMatches(@MappingTarget Tournament tournament) {
        tournament.getMatches().forEach(match -> match.setTournament(tournament));
    }

    @InheritInverseConfiguration(name = "toEntity")
    TournamentResponseDTO toDto(Tournament tournament);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tournament partialUpdate(TournamentResponseDTO tournamentResponseDTO, @MappingTarget Tournament tournament);
}