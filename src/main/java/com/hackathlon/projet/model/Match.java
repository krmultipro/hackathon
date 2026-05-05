package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player1_id", nullable = false)
    private Player player1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player2_id", nullable = false)
    private Player player2;

    private Integer scorePlayer1;
    private Integer scorePlayer2;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player winner;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;
}
