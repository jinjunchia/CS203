package com.cs203.cs203system.dtos;

import com.cs203.cs203system.enums.PlayerBracket;
import com.cs203.cs203system.enums.PlayerStatus;
import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.UserType;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ResultRequestDTO implements Serializable{
    @NotNull
    Long winnerID;

    @NotNull
    Long matchID;

    @NotNull
    Long tournamentID;
}
