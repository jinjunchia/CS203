package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.MatchType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;


    private int eloRating = 1200;

    private Integer ranking;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    private int draws = 0;

    private Status status;  // Could also be an enum: MatchStatus

    public enum Status {
        QUALIFIED, ELIMINATED
    }

    // Add relationship to Tournament
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;  // New relationship to Tournament

    // Relationships
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "team_matches",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "matches_id"))
    @ToString.Exclude
    private Set<Match> matches = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "manager_id")
    private User manager;

    @ToString.Exclude
    @OneToMany(mappedBy = "team", orphanRemoval = true)
    private Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "team", orphanRemoval = true)
    @ToString.Exclude
    private Set<EloRecord> eloRecords = new LinkedHashSet<>();

    public Status getStatus() {
        return status;
    }

    // Setter
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Team team = (Team) o;
        return getId() != null && Objects.equals(getId(), team.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}