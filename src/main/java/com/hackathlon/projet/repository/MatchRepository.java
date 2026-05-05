package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
