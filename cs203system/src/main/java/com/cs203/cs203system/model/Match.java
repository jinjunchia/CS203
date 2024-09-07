package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.MatchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private MatchType matchType;

    private String result;  // e.g., "3-2", "Winner: Team A"

    private Integer durationInMinutes;  // Duration of the match

    // Setter
    private Status status;  // Could also be an enum: MatchStatus

    public enum Status {
        PLANNED, ONGOING, COMPLETED, CANCELLED
    }

    // Implement a Result Database
    //create round here?
    private Integer roundNumber;

    private LocalDate matchDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id")
    @JsonIgnore // Prevent Infinite Recursion
    private Tournament tournament;

    @ToString.Exclude
    @ManyToMany(mappedBy = "matches", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Team> teams = new LinkedHashSet<>();


}
