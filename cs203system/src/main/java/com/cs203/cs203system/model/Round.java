package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;
import com.cs203.cs203system.enums.RoundType;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "round")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    private RoundType roundType;

    @OneToMany(mappedBy = "round", orphanRemoval = true)
    private Set<Match> matches = new LinkedHashSet<>();

}
