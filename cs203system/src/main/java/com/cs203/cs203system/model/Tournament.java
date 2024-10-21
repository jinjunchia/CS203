package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Represents a Tournament in the system.
 * This class stores details about a tournament, including its name, dates, location,
 * status, format, and relationships with players, matches, and administrators.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "tournament")
public class Tournament implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the tournament.
     */
    private String name;

    /**
     * The start date of the tournament.
     */
    private LocalDate startDate;

    /**
     * The end date of the tournament.
     */
    private LocalDate endDate;

    /**
     * The location where the tournament is held.
     */
    private String location;

    /**
     * The current status of the tournament, represented by the {@link TournamentStatus} enum.
     */
    private TournamentStatus status;

    /**
     * The minimum Elo rating allowed for participants in the tournament.
     */
    @Builder.Default
    private Double minEloRating = 800.0;

    /**
     * The maximum Elo rating allowed for participants in the tournament.
     * Administrators can set this value.
     */
    @Builder.Default
    private Double maxEloRating = 1200.0;

    /**
     * The format of the tournament, represented by the {@link TournamentFormat} enum.
     */
    @Enumerated(EnumType.STRING)
    private TournamentFormat format;

    // -------------- Swiss Fields --------------

    /**
     * The current round number of the tournament, applicable for Swiss tournaments.
     */
    @Builder.Default
    private Integer currentRoundNumber = 0;

    /**
     * The total number of Swiss rounds in the tournament, calculated dynamically.
     */
    @Builder.Default
    private Integer totalSwissRounds = 0;

    // -------------- Swiss Fields --------------


    // -------------- Double Elimination Fields --------------

    /**
     * The list of players in the winners bracket for a double elimination tournament.
     */
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "tournament_winner_bracket",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id"))
    @ToString.Exclude
    private List<Player> winnersBracket = new ArrayList<>();

    /**
     * The list of players in the losers bracket for a double elimination tournament.
     */
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "tournament_loser_bracket",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id"))
    @ToString.Exclude
    private List<Player> losersBracket = new ArrayList<>();

    // -------------- Double Elimination Fields --------------


    // -------------- Hybrid Fields --------------

    /**
     * Indicates if the tournament is currently using the second format (applicable for hybrid tournaments).
     * For example, a tournament might start with Swiss rounds and then transition to a double elimination phase.
     */
    @Builder.Default
    private Boolean isOnSecondFormat = false;

    // -------------- Hybrid Fields --------------

    /**
     * The list of matches in the tournament.
     */
    @Builder.Default
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Match> matches = new ArrayList<>();

    /**
     * The administrator managing the tournament.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    /**
     * The list of players participating in the tournament.
     */
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id"))
    @ToString.Exclude
    private List<Player> players = new ArrayList<>();

    /**
     * Checks if this Tournament is equal to another object.
     * Two Tournaments are considered equal if they have the same ID.
     *
     * @param o The object to compare.
     * @return True if the Tournaments are equal, false otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Tournament that = (Tournament) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    /**
     * Returns the hash code for the Tournament.
     *
     * @return The hash code of the Tournament.
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
