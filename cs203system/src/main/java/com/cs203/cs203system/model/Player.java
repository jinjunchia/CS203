package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;
import com.cs203.cs203system.enums.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DiscriminatorValue("ROLE_PLAYER")
public class Player extends User {

    private String name;

    private double points = 0.0;

    private Integer ranking; //not sure how to deal with this maybe next time
    private Integer totalGamesPlayed = 0; // Specific to players, same for this i will implement not sure when

    private int tournamentLosses = 0;

    private Double eloRating = 1000.0; // Specific to players

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    private int draws = 0;

    @Enumerated(EnumType.STRING)
    private PlayerBracket bracket;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

    @ManyToMany(mappedBy = "players", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private Set<Tournament> tournaments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player", orphanRemoval = true)
    @ToString.Exclude
    private Set<EloRecord> eloRecords = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "player_stats_id")
    private PlayerStats playerStats;

    // Method to add points
    public void addPoints(double points) {
        this.points += points;
    }

    // Method to reset points
    public void resetPoints() {
        this.points = 0.0;
    }

    // Method to check if the player has lost twice
    public boolean hasLostTwice() {
        return losses >= 2;
    }

    // Method to increment losses
    public void incrementLosses() {
        this.losses++;
    }
    public void incrementWins(){this.wins++;}
    public void incrementDraws(){this.draws++;}

    public void incrementTournamentLosses(){this.tournamentLosses++;}

}