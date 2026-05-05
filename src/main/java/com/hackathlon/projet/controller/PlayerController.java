package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.PlayerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "API de gestion des joueurs")
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Operation(summary = "Lister tous les joueurs", description = "Retourne la liste complète des joueurs")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    @Operation(summary = "Obtenir un joueur par ID", description = "Retourne un joueur à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur trouvé",
                    content = @Content(schema = @Schema(implementation = Player.class))),
            @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Player> getById(
            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long id) {
        return playerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtenir un joueur par username", description = "Retourne un joueur à partir de son nom d'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur trouvé"),
            @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
    })
    @GetMapping("/by-username/{username}")
    public ResponseEntity<Player> getByUsername(
            @Parameter(description = "Nom d'utilisateur", example = "alice123")
            @PathVariable String username) {
        return playerRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un joueur", description = "Crée un nouveau joueur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Joueur créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Player> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données du joueur à créer",
                    required = true)
            @RequestBody Player player) {
        Player saved = playerRepository.save(player);
        return ResponseEntity
                .created(URI.create("/api/players/" + saved.getPlayerId()))
                .body(saved);
    }

    @Operation(summary = "Mettre à jour un joueur", description = "Met à jour un joueur existant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Player> update(
            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles données du joueur",
                    required = true)
            @RequestBody Player input) {
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

    @Operation(summary = "Supprimer un joueur", description = "Supprime un joueur par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Joueur supprimé"),
            @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long id) {
        if (!playerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        playerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}