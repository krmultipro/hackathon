package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.services.PlayerService;
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

        private final PlayerService playerService;

        public PlayerController(PlayerService playerService) {
                this.playerService = playerService;
        }

        @Operation(summary = "Lister tous les joueurs", description = "Retourne la liste complète des joueurs")
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
        @GetMapping
        public List<Player> getAll() {
                return playerService.findAll();
        }

        @Operation(summary = "Obtenir un joueur par ID", description = "Retourne un joueur à partir de son identifiant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Joueur trouvé", content = @Content(schema = @Schema(implementation = Player.class))),
                        @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Player> getById(
                        @Parameter(description = "ID du joueur", example = "1") @PathVariable Long id) {
                return ResponseEntity.ok(playerService.findById(id));
        }

        @Operation(summary = "Obtenir un joueur par username", description = "Retourne un joueur à partir de son nom d'utilisateur")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Joueur trouvé"),
                        @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
        })
        @GetMapping("/by-username/{username}")
        public ResponseEntity<Player> getByUsername(
                        @Parameter(description = "Nom d'utilisateur", example = "alice123") @PathVariable String username) {
                return ResponseEntity.ok(playerService.findByUsername(username));
        }

        @Operation(summary = "Créer un joueur", description = "Crée un nouveau joueur")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Joueur créé"),
                        @ApiResponse(responseCode = "400", description = "Requête invalide")
        })
        @PostMapping
        public ResponseEntity<Player> create(@RequestBody Player player) {
                Player saved = playerService.create(player);
                return ResponseEntity
                                .created(URI.create("/api/players/" + saved.getId()))
                                .body(saved);
        }

        @Operation(summary = "Mettre à jour un joueur", description = "Met à jour un joueur existant par ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Joueur mis à jour"),
                        @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
        })
        @PutMapping("/{id}")
        public ResponseEntity<Player> update(
                        @Parameter(description = "ID du joueur", example = "1") @PathVariable Long id,
                        @RequestBody Player details) {
                return ResponseEntity.ok(playerService.update(id, details));
        }

        @Operation(summary = "Supprimer un joueur", description = "Supprime un joueur par ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Joueur supprimé"),
                        @ApiResponse(responseCode = "404", description = "Joueur non trouvé")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID du joueur", example = "1") @PathVariable Long id) {
                playerService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
