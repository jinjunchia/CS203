package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elo_record")
public class EloRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date; // Date of the ELO change

    private Double oldRating;

    private Double newRating;

    private String changeReason; // e.g., "Match against Player X"

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = true)
    private Match match; // Reference to the match that caused the rating change

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = true)
    private Tournament tournament; // Optional reference to the related tournament

}