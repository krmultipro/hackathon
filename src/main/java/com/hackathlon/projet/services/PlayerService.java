package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));
    }

    public Player registerPlayer(Player player) {
        validatePlayer(player);

        // check duplicate
        if (playerRepository.existsByUsername(player.getUsername())) {
            throw new BadRequestException("Le nom d'utilisateur est déjà utilisé");
        }

        return playerRepository.save(player);
    }

    public Player loginPlayer(Player player) {
        validateLogin(player);

        Player existingPlayer = playerRepository.findByUsername(player.getUsername());
        if (existingPlayer == null) {
            throw new BadRequestException("Identifiants invalides");
        }

        if (!existingPlayer.getPassword().equals(player.getPassword())) {
            throw new BadRequestException("Identifiants invalides");
        }

        return existingPlayer;
    }

    public Player findById(Long id) {
        return getPlayerById(id);
    }

    public Player findByUsername(String username) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            throw new NotFoundException("Joueur introuvable");
        }
        return player;
    }

    public Player create(Player player) {
        return registerPlayer(player);
    }

    public Player update(Long id, Player details) {
        Player existingPlayer = getPlayerById(id);

        validatePlayer(details);
        if (!existingPlayer.getUsername().equals(details.getUsername())
                && playerRepository.existsByUsername(details.getUsername())) {
            throw new BadRequestException("Le nom d'utilisateur est déjà utilisé");
        }

        existingPlayer.setUsername(details.getUsername());
        existingPlayer.setPassword(details.getPassword());
        existingPlayer.setGlobalElo(details.getGlobalElo());
        existingPlayer.setCreatedAt(details.getCreatedAt());

        return playerRepository.save(existingPlayer);
    }

    public void delete(Long id) {
        getPlayerById(id);
        playerRepository.deleteById(id);
    }

    private void validatePlayer(Player player) {
        // validate
        if (player == null || isBlank(player.getUsername())) {
            throw new BadRequestException("Le nom d'utilisateur est invalide");
        }

        if (isBlank(player.getPassword())) {
            throw new BadRequestException("Le mot de passe est invalide");
        }

        if (player.getPassword().length() < 8) {
            throw new BadRequestException("Le mot de passe doit contenir au moins 8 caractères");
        }
    }

    private void validateLogin(Player player) {
        // validate
        if (player == null || isBlank(player.getUsername())) {
            throw new BadRequestException("Le nom d'utilisateur est invalide");
        }

        if (isBlank(player.getPassword())) {
            throw new BadRequestException("Le mot de passe est invalide");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
