package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "match_participation")
public class MatchParticipation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false) // Correctly points to match
    private Match match;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false) // Correct column name
    private Player player;
}
