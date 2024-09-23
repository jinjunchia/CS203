package com.cs203.cs203system.dtos.players;

import com.cs203.cs203system.model.Admin;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateAdminMapper {
    Admin toEntity(CreateUserRequest createUserRequest);

    CreateUserRequest toDto(Admin admin);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Admin partialUpdate(CreateUserRequest createUserRequest, @MappingTarget Admin admin);
}