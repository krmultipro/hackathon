package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "elo_rating")
public class EloRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "elo_rating_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "elo_score")
    private Integer eloScore;

    @Column(name = "match_count")
    private Integer matchCount;

    @Column(name = "win_count")
    private Integer winCount;

    @Column(name = "loss_count")
    private Integer lossCount;

    @Column(name = "last_match_played_at")
    private LocalDateTime lastMatchPlayedAt;
}
