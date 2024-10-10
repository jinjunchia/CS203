package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.dtos.PlayerResponseDTO;
import com.cs203.cs203system.model.Player;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerResponseDTOMapper {
    Player toEntity(PlayerResponseDTO playerResponseDTO);

    PlayerResponseDTO toDto(Player player);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Player partialUpdate(PlayerResponseDTO playerResponseDTO, @MappingTarget Player player);
}