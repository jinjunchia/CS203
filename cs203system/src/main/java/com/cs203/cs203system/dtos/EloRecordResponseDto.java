package com.cs203.cs203system.dtos;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.cs203.cs203system.model.EloRecord}
 */
@Value
public class EloRecordResponseDto implements Serializable {
    Long id;
    LocalDateTime date;
    Double oldRating;
    Double newRating;
    String changeReason;
    MatchDto match;

    /**
     * DTO for {@link com.cs203.cs203system.model.Match}
     */
    @Value
    public static class MatchDto implements Serializable {
        Long id;
    }
}