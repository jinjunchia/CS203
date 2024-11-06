package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.PlayerBracket;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Represents a Player in the system.
 * The Player class extends the {@link User} class and includes additional functionality,
 * such as managing Elo ratings, tournament participation, and player-specific statistics.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DiscriminatorValue("ROLE_PLAYER")
public class Player extends User {

    /**
     * The name of the player.
     */
    private String name;

    /**
     * The number of losses the player has incurred in tournaments.
     */
    private int tournamentLosses = 0;

    /**
     * The Elo rating of the player, used to determine skill level.
     */
    private Double eloRating = 1000.0; // Specific to players

    // ------------- Swiss Fields ----------------

    /**
     * The points earned by the player during a Swiss tournament.
     */
    private double points = 0.0;

    // ------------- Swiss Fields ----------------


    // ------------- Double Elimination Fields ----------------

    /**
     * The bracket in which the player is currently playing in a double elimination tournament.
     * Represented by the {@link PlayerBracket} enum.
     */
    @Enumerated(EnumType.STRING)
    private PlayerBracket bracket;

    // ------------- Double Elimination Fields ----------------

    /**
     * The tournaments in which the player is participating.
     * This is a many-to-many relationship, mapped by the "players" field in the {@link Tournament} class.
     */
    @ManyToMany(mappedBy = "players")
    @ToString.Exclude
    private Set<Tournament> tournaments = new LinkedHashSet<>();

    /**
     * The set of Elo rating records for the player.
     * Each record represents a change in the player's Elo rating.
     */
    @OneToMany(mappedBy = "player", orphanRemoval = true)
    @ToString.Exclude
    private Set<EloRecord> eloRecords = new LinkedHashSet<>();

//    /**
//     * The player's statistical data, such as performance metrics.
//     */
//    @ToString.Exclude
//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "player_stats_id")
//    private PlayerStats playerStats;

}