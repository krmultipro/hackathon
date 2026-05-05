package com.hackathlon.projet.model;

import java.time.LocalDateTime;

import org.springframework.beans.factory.config.YamlProcessor.MatchStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "matches")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Joueurs
    @Column(name = "player1_id", nullable = false)
    private Long player1Id;

    @Column(name = "player2_id", nullable = false)
    private Long player2Id;

    // Scores
    private Integer scorePlayer1;
    private Integer scorePlayer2;

    // Gagnant
    @Column(name = "winner_id")
    private Long winnerId;

    // Statut du match
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    // Date
    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;
}