package com.cs203.cs203system.model;

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
    private Integer teamId;

    private String name;

    private int eloRating = 1200;

    private Integer ranking;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    private int draws = 0;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Team team = (Team) o;
        return getTeamId() != null && Objects.equals(getTeamId(), team.getTeamId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}