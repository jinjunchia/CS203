package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.model.Player;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerWithOutStatsDtoMapper {
    Player toEntity(PlayerWithOutStatsDto playerWithOutStatsDto);

    PlayerWithOutStatsDto toDto(Player player);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Player partialUpdate(PlayerWithOutStatsDto playerWithOutStatsDto, @MappingTarget Player player);
}