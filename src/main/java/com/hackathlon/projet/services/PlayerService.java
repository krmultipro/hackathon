package com.hackathlon.projet.services;

import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.PlayerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerRepository.findByUsername(username)
                .map(player -> User.builder()
                        .username(player.getUsername())
                        .password(player.getPassword())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Joueur introuvable : " + username));
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public Player create(Player player) {
        return playerRepository.save(player);
    }

    public Optional<Player> update(Long id, Player details) {
        return playerRepository.findById(id).map(existing -> {
            existing.setUsername(details.getUsername());
            existing.setPassword(details.getPassword());
            existing.setGlobalElo(details.getGlobalElo());
            existing.setCreatedAt(details.getCreatedAt());
            return playerRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!playerRepository.existsById(id)) return false;
        playerRepository.deleteById(id);
        return true;
    }
}
