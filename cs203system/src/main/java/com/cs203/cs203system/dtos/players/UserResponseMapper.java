package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.model.User;
import com.cs203.cs203system.dtos.UserResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserResponseMapper {
    User toEntity(UserResponseDto userResponseDto);

    UserResponseDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserResponseDto userResponseDto, @MappingTarget User user);
}