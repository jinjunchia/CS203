package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "match")
public class Match implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer durationInMinutes;  // Duration of the match

    @Enumerated(EnumType.STRING)
    private MatchStatus status;  // Could also be an enum: MatchStatus

    private Integer player1Score;  // Score for player1
    private Integer player2Score;  // Score for player2

    private LocalDate matchDate;

    // ---------- Double Elimination -----------
    @Enumerated(EnumType.STRING)
    private MatchBracket bracket;

    // Matches this match depends on
    @ManyToMany
    @JoinTable(
            name = "match_dependencies",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Match> dependencies = new LinkedHashSet<>();

    // Matches that depend on this match
    @ManyToMany(mappedBy = "dependencies")
    @ToString.Exclude
    @Builder.Default
    private Set<Match> dependentMatches = new LinkedHashSet<>();
    // ---------- Double Elimination -----------


    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;

    @ManyToOne(optional = false)
    @JsonIgnore // Prevent Infinite Recursion
    private Tournament tournament;

    @ManyToOne(cascade = CascadeType.ALL)
    private Round round;

    public Player getWinner() {
        if (player1Score == null || player2Score == null) {
            return null;
        }
        if (player1Score > player2Score) {
            return player1;
        }
        return player2;
    }

    public Player getLoser() {
        if (player1Score == null || player2Score == null) {
            return null;
        }
        if (player1Score > player2Score) {
            return player2;
        }
        return player1;
    }

    public boolean isDraw() {
        return player1Score != null && player1Score.equals(player2Score);
    }


}
