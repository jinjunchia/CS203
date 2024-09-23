package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.model.Player;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreatePlayerMapper {
    Player toEntity(CreateUserRequest createUserRequest);

    CreateUserRequest toDto(Player player);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Player partialUpdate(CreateUserRequest createUserRequest, @MappingTarget Player player);
}