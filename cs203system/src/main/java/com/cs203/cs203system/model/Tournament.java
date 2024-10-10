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
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private TournamentStatus status; // Status of the tournament (Planned, Ongoing, Completed, Cancelled)
    private Double minEloRating; // Minimum ELO rating allowed for participants
    private Double maxEloRating; // Maximum ELO rating allowed for participants admins can set this
    @Enumerated(EnumType.STRING)
    private TournamentFormat format;

    // -------------- Swiss Fields --------------
    @Builder.Default
    private Integer roundsCompleted = 0; // Track the number of completed rounds
    private Integer currentRoundNumber; // Tracks the current round of the entire tournament
    private Integer totalSwissRounds; // Total Swiss rounds, calculated dynamically

    // -------------- Swiss Fields --------------


    // -------------- Double Elimination Fields --------------

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "tournament_winner_bracket",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id"))
    @ToString.Exclude
    private List<Player> winnersBracket = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "tournament_loser_bracket",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id"))
    @ToString.Exclude
    private List<Player> losersBracket = new ArrayList<>();

    // -------------- Double Elimination Fields --------------

    @Builder.Default
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Match> matches = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "players_id"))
    @ToString.Exclude
    private List<Player> players = new ArrayList<>();


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

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
