package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "question")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    private String statement;

    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    private Integer minElo;

    private Integer maxElo;

    private Integer timeLimit;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}
