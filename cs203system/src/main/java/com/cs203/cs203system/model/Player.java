package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@DiscriminatorValue("PLAYER")
public class Player extends User {

    private String name;

    private int eloRating = 1200;

    private double points = 0.0;

    private Integer ranking;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    private int draws = 0;

    private Match.Bracket bracket;

    public enum Bracket {
        UPPER, LOWER
    }

    private Status status;  // Could also be an enum: MatchStatus

    // Method to add points
    public void addPoints(double points) {
        this.points += points;
    }

    // Method to reset points
    public void resetPoints() {
        this.points = 0.0;
    }

    public enum Status {
        QUALIFIED, ELIMINATED
    }


    // Method to check if the player has lost twice
    public boolean hasLostTwice() {
        return losses >= 2;
    }

    // Method to increment losses
    public void incrementLosses() {
        this.losses++;
    }

    // Add relationship to Tournament
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;  // New relationship to Tournament

    // Relationships
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "player_matches",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "matches_id"))
    @ToString.Exclude
    private Set<Match> matches = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player", orphanRemoval = true)
    @ToString.Exclude
    private Set<EloRecord> eloRecords = new LinkedHashSet<>();

}