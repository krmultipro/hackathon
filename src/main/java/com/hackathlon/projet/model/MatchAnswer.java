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

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_question_id", nullable = false)
    private MatchQuestion matchQuestion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    private String answer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "response_time")
    private Long responseTime;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
}
