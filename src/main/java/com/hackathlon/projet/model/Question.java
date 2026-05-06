package com.hackathlon.projet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank
    @Size(max = 2000)
    private String statement;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    @Min(0) @Max(3000)
    private Integer minElo;

    @Min(0) @Max(3000)
    private Integer maxElo;

    @Min(1) @Max(300)
    private Integer timeLimit;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}
