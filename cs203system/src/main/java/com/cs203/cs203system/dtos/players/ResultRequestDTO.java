package com.cs203.cs203system.dtos.players;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class ResultRequestDTO implements Serializable{
    @NotNull
    Long winnerID;

    @NotNull
    Long matchID;

    @NotNull
    Long tournamentID;
}
