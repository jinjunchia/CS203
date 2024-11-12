package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.MatchBracket;
import com.cs203.cs203system.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Formatter;

/**
 * Represents a Match in a tournament.
 * This class includes details about the match, such as the players involved, their scores,
 * the match status, and any relevant tournament information.
 */
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

    /**
     * The duration of the match in minutes.
     */
    private Integer durationInMinutes;  // Duration of the match

    /**
     * The current status of the match, represented by the {@link MatchStatus} enum.
     */
    @Enumerated(EnumType.STRING)
    private MatchStatus status;  // Could also be an enum: MatchStatus

    /**
     * Score of player 1 in the match.
     */
    private Integer player1Score;  // Score for player1

    /**
     * Score of player 2 in the match.
     */
    private Integer player2Score ;  // Score for player2

    /**
     * The date and time on which the match was held.
     */
    private LocalDateTime matchDate;

    @Column(length = 1000)
    private String description;

    // ---------- Swiss -----------

    /**
     * The round number of the match, applicable for Swiss tournaments.
     */
    private Integer round;

    // ---------- Swiss -----------


    // ---------- Double Elimination -----------

    /**
     * The bracket type of the match, applicable for double elimination tournaments.
     * Represented by the {@link MatchBracket} enum.
     */
    @Enumerated(EnumType.STRING)
    private MatchBracket bracket;

    // ---------- Double Elimination -----------

    /**
     * The first player involved in the match.
     */
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;

    /**
     * The second player involved in the match.
     */
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;

    /**
     * The tournament to which the match belongs.
     */
    @ManyToOne(optional = false)
    private Tournament tournament;

    /**
     * Determines the winner of the match based on the scores of player 1 and player 2.
     *
     * @return The player who won the match, or null if the scores are not available or the match is a draw.
     */

    // --------- New Fields for Punches, KO, and Dodge -----------

    /**
     * Number of punches thrown by player 1.
     */
    @Builder.Default
    private Integer punchesPlayer1 = 0;

    /**
     * Number of punches thrown by player 2.
     */
    @Builder.Default
    private Integer punchesPlayer2 = 0 ;

    /**
     * Number of successful dodges by player 1.
     */
    @Builder.Default
    private Integer dodgesPlayer1 = 0;

    /**
     * Number of successful dodges by player 2.
     */
    @Builder.Default
    private Integer dodgesPlayer2 = 0;

    /**
     * Indicates if the match ended in a knockout (KO).
     */
    private boolean isKO;
    private boolean koByPlayer1;
    private boolean koByPlayer2;


    // --------- New Fields for Punches, KO, and Dodge -----------
    public Player getWinner() {
        if (player1Score == null || player2Score == null) {
            return null;
        }

        if (player1Score - player2Score == 0) {
            return null;
        }

        if (player1Score > player2Score) {
            return player1;
        }
        return player2;
    }

    /**
     * Determines the loser of the match based on the scores of player 1 and player 2.
     *
     * @return The player who lost the match, or null if the scores are not available or the match is a draw.
     */
    public Player getLoser() {
        if (player1Score == null || player2Score == null) {
            return null;
        }
        if (player1Score > player2Score) {
            return player2;
        }
        return player1;
    }

    /**
     * Checks if the match is a draw.
     *
     * @return true if the match is a draw, false otherwise.
     */
    public boolean isDraw() {
        return player1Score != null && player1Score.equals(player2Score);
    }

    public static class MatchBuilder {
        private Integer punchesPlayer1 = 0;
        private Integer punchesPlayer2 = 0;
        private Integer dodgesPlayer1 = 0;
        private Integer dodgesPlayer2 = 0;

        public MatchBuilder punchesPlayer1(Integer punchesPlayer1) {
            this.punchesPlayer1 = punchesPlayer1 != null ? punchesPlayer1 : 0;
            return this;
        }

        public MatchBuilder punchesPlayer2(Integer punchesPlayer2) {
            this.punchesPlayer2 = punchesPlayer2 != null ? punchesPlayer2 : 0;
            return this;
        }

        public MatchBuilder dodgesPlayer1(Integer dodgesPlayer1) {
            this.dodgesPlayer1 = dodgesPlayer1 != null ? dodgesPlayer1 : 0;
            return this;
        }

        public MatchBuilder dodgesPlayer2(Integer dodgesPlayer2) {
            this.dodgesPlayer2 = dodgesPlayer2 != null ? dodgesPlayer2 : 0;
            return this;
        }
    }

}
