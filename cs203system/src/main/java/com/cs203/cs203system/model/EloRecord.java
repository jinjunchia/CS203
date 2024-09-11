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
    @Column(name = "id", nullable = false)
    private Integer id;

    private Double rating;

    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

}