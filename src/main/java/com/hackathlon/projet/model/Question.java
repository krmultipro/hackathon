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
    private Long questionId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    private String statement;

    private String answerType;

    private Integer minElo;

    private Integer maxElo;

    private Integer timeLimit;

    private LocalDateTime creationDate;
}