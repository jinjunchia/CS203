package com.cs203.cs203system.dtos;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class TournamentUpdatePlayerDTO {

    List<Long> playerIds = new ArrayList<>();
}
