package com.hackathlon.projet.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
}
