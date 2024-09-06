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

    private MatchType match_type;

    private String result;  // e.g., "3-2", "Winner: Team A"

    private Integer durationInMinutes;  // Duration of the match

    private String status;  // Could also be an enum: MatchStatus

    private LocalDate match_date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id")
    @JsonIgnore // Prevent Infinite Recursion
    private Tournament tournament;

    @ToString.Exclude
    @ManyToMany(mappedBy = "matches", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Team> teams = new LinkedHashSet<>();

// Implement a Result Database


}
