package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;
import com.cs203.cs203system.enums.RoundType;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private int roundNumber;

    @Enumerated(EnumType.STRING)
    private RoundType roundType;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<Match> matches;
}
