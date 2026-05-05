package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getById(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<Player> getByUsername(@PathVariable String username) {
        return playerRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Player> create(@RequestBody Player player) {
        Player saved = playerRepository.save(player);
        return ResponseEntity
                .created(URI.create("/api/players/" + saved.getPlayerId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable Long id, @RequestBody Player input) {
        return playerRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(input.getUsername());
                    existing.setPassword(input.getPassword());
                    existing.setGlobalElo(input.getGlobalElo());
                    existing.setCreatedAt(input.getCreatedAt());
                    Player updated = playerRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!playerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        playerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}