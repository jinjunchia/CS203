package com.cs203.cs203system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elo_record")
public class EloRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private Double rating;

    private LocalDateTime localDateTime;

    @OneToOne
    @JoinColumn(name = "previous_elo_record_id")
    private EloRecord previousEloRecord;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}