package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.MatchQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchQuestionRepository extends JpaRepository<MatchQuestion, Long> {
}
