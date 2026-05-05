package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.EloRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EloRatingRepository extends JpaRepository<EloRating, Long> {

    List<EloRating> findByPlayerId(Long playerId);

    List<EloRating> findBySubjectId(Long subjectId);

    Optional<EloRating> findByPlayerIdAndSubjectId(Long playerId, Long subjectId);

    boolean existsByPlayerIdAndSubjectId(Long playerId, Long subjectId);
}
