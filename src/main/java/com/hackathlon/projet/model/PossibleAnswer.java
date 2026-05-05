package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "possible_answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PossibleAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "possible_answer_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;
}
