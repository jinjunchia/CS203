package com.cs203.cs203system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an Elo rating record for a player.
 * This class stores information about changes in a player's Elo rating, including the date of change,
 * old and new ratings, the reason for the change, and associations to the player and match involved.
 */
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elo_record")
public class EloRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the Elo rating change occurred.
     */
    private LocalDateTime date;

    /**
     * The previous Elo rating before the change.
     */
    private Double oldRating;

    /**
     * The new Elo rating after the change.
     */
    private Double newRating;

    /**
     * The reason for the change in Elo rating, e.g., "Match against Player X".
     */
    private String changeReason;

    /**
     * The player associated with this Elo rating record.
     */
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /**
     * The match associated with this Elo rating change, if applicable.
     * This reference can be null if the change is not tied to a specific match.
     */
    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

}