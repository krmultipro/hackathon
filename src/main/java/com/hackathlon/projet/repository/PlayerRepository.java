package com.hackathlon.projet.repository;

import com.hackathlon.projet.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByUsername(String username);

    Player findByUsername(String username);
}
