package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence au match
    @Column(name = "match_id", nullable = false)
    private Long matchId;

    // Référence à la question dans le match
    @Column(name = "match_question_id", nullable = false)
    private Long matchQuestionId;

    // Référence au joueur
    @Column(name = "player_id", nullable = false)
    private Long playerId;

    // Réponse donnée par le joueur
    private String answer;

    // Est-ce que la réponse est correcte ?
    private Boolean isCorrect;

    // Temps de réponse (en secondes ou ms)
    private Long responseTime;

    // Date de réponse
    private LocalDateTime answeredAt;
}