package com.cs203.cs203system.model;

import com.cs203.cs203system.enums.MatchStatus;
import com.cs203.cs203system.enums.MatchType;
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

    private MatchType matchType;

    private String result;  // e.g., "3-2", "Winner: Team A"

    private Integer durationInMinutes;  // Duration of the match

    private MatchStatus status;  // Could also be an enum: MatchStatus

    // Implement a Result Database
    //create round here?
    private Integer roundNumber;

    private LocalDate matchDate;
    private Bracket bracket;

    public enum Bracket {
        UPPER, LOWER, FINAL
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id")
    @JsonIgnore // Prevent Infinite Recursion
    private Tournament tournament;

    @ToString.Exclude
    @ManyToMany(mappedBy = "matches", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Player> players = new LinkedHashSet<>();


}
