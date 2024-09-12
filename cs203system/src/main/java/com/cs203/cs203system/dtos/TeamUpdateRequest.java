package com.cs203.cs203system.dtos;

import jakarta.persistence.Column;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import com.cs203.cs203system.dtos.TeamUpdateRequest;
import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@Data
public class TeamUpdateRequest implements Serializable{


    Optional<String> name = Optional.empty();

    Optional<Integer> eloRating = Optional.empty();

    Optional<Double> points = Optional.empty();

    Optional<Integer> wins = Optional.empty();

    Optional<Integer> losses = Optional.empty();

    Optional<Integer> draws = Optional.empty();
}
