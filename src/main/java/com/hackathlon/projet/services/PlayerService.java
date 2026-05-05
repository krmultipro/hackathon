package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    // log creation
    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

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
                .orElseThrow(() -> {
                    log.warn("Joueur introuvable lors du chargement : {}", username);
                    return new UsernameNotFoundException("Joueur introuvable : " + username);
                });
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Joueur introuvable avec l'id : {}", id);
                    return new NotFoundException("Joueur introuvable");
                });
    }

    public Player registerPlayer(Player player) {
        validatePlayer(player);

        // check duplicate
        if (playerRepository.existsByUsername(player.getUsername())) {
            log.warn("Tentative d'inscription avec un username déjà utilisé : {}", player.getUsername());
            throw new BadRequestException("Le nom d'utilisateur est déjà utilisé");
        }

        log.info("Création du joueur : {}", player.getUsername());
        return playerRepository.save(player);
    }

    public Player loginPlayer(Player player) {
        validateLogin(player);

        log.info("Tentative de connexion : {}", player.getUsername());

        Player existingPlayer = playerRepository.findByUsername(player.getUsername())
                .orElse(null);
        if (existingPlayer == null) {
            log.warn("Identifiants invalides - joueur introuvable : {}", player.getUsername());
            throw new BadRequestException("Identifiants invalides");
        }

        if (!existingPlayer.getPassword().equals(player.getPassword())) {
            log.warn("Identifiants invalides - mauvais mot de passe pour : {}", player.getUsername());
            throw new BadRequestException("Identifiants invalides");
        }

        log.info("Connexion réussie pour : {}", existingPlayer.getUsername());
        return existingPlayer;
    }

    public Player findById(Long id) {
        return getPlayerById(id);
    }

    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public Player create(Player player) {
        return registerPlayer(player);
    }

    public Player update(Long id, Player details) {
        Player existingPlayer = getPlayerById(id);

        validatePlayer(details);
        if (!existingPlayer.getUsername().equals(details.getUsername())
                && playerRepository.existsByUsername(details.getUsername())) {
            log.warn("Mise à jour échouée - username déjà utilisé : {}", details.getUsername());
            throw new BadRequestException("Le nom d'utilisateur est déjà utilisé");
        }

        existingPlayer.setUsername(details.getUsername());
        existingPlayer.setPassword(details.getPassword());
        existingPlayer.setGlobalElo(details.getGlobalElo());
        existingPlayer.setCreatedAt(details.getCreatedAt());

        log.info("Joueur mis à jour : {}", existingPlayer.getUsername());
        return playerRepository.save(existingPlayer);
    }

    public void delete(Long id) {
        getPlayerById(id);
        log.info("Suppression du joueur avec l'id : {}", id);
        playerRepository.deleteById(id);
    }

    private void validatePlayer(Player player) {
        // validate
        if (player == null || isBlank(player.getUsername())) {
            log.warn("Validation échouée - username invalide");
            throw new BadRequestException("Le nom d'utilisateur est invalide");
        }

        if (isBlank(player.getPassword())) {
            log.warn("Validation échouée - mot de passe invalide pour : {}", player.getUsername());
            throw new BadRequestException("Le mot de passe est invalide");
        }

        if (player.getPassword().length() < 8) {
            log.warn("Validation échouée - mot de passe trop court pour : {}", player.getUsername());
            throw new BadRequestException("Le mot de passe doit contenir au moins 8 caractères");
        }
    }

    private void validateLogin(Player player) {
        // validate
        if (player == null || isBlank(player.getUsername())) {
            log.warn("Validation login échouée - username invalide");
            throw new BadRequestException("Le nom d'utilisateur est invalide");
        }

        if (isBlank(player.getPassword())) {
            log.warn("Validation login échouée - mot de passe invalide");
            throw new BadRequestException("Le mot de passe est invalide");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
