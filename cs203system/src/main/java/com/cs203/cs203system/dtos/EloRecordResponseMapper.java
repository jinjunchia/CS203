package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.EloRecord;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EloRecordResponseMapper {
    EloRecord toEntity(EloRecordResponseDto eloRecordResponseDto);

    EloRecordResponseDto toDto(EloRecord eloRecord);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EloRecord partialUpdate(EloRecordResponseDto eloRecordResponseDto, @MappingTarget EloRecord eloRecord);
}