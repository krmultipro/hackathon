package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence au match
    @Column(name = "match_id", nullable = false)
    private Long matchId;

    // Référence à la question
    @Column(name = "question_id", nullable = false)
    private Long questionId;

}