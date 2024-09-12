package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.MatchBracket;
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
    private Integer id;


    private String result;  // e.g., "3-2", "Winner: Team A"

    private Integer durationInMinutes;  // Duration of the match
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;  // Could also be an enum: MatchStatus

    @ManyToOne
    @JoinColumn(name = "player1_id", referencedColumnName = "id")
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", referencedColumnName = "id")
    private Player player2;

    private Integer player1Score;  // Score for player1
    private Integer player2Score;  // Score for player2

    private LocalDate matchDate;
    @Enumerated(EnumType.STRING)
    private MatchBracket bracket;


    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id")
    @JsonIgnore // Prevent Infinite Recursion
    private Tournament tournament;

    @ToString.Exclude
    @ManyToMany(mappedBy = "matches", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Player> players = new LinkedHashSet<>();

    public Player getWinner() {
        if (player1Score != null && player2Score != null) {
            if (player1Score > player2Score) {
                return player1;
            } else if (player2Score > player1Score) {
                return player2;
            }
        }
        return null; // Return null if the scores are equal or undecided
    }
    public Player getLoser() {
        if (player1Score != null && player2Score != null) {
            if (player1Score < player2Score) {
                return player1;
            } else if (player2Score < player1Score) {
                return player2;
            }
        }
        return null; // Return null if the scores are equal or undecided
    }
    public boolean isDraw() {
        return player1Score != null && player2Score != null && player1Score.equals(player2Score);
    }


}
