package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.EloRating;
import com.hackathlon.projet.services.EloRatingService;
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
@RequestMapping("/api/elo-ratings")
@Tag(name = "ELO Ratings", description = "API de gestion des scores ELO des joueurs")
public class EloRatingController {

    private final EloRatingService eloRatingService;

    public EloRatingController(EloRatingService eloRatingService) {
        this.eloRatingService = eloRatingService;
    }

    @Operation(summary = "Lister tous les ELO ratings", description = "Retourne la liste complète des ELO ratings")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<EloRating>> getAll() {
        return ResponseEntity.ok(eloRatingService.findAll());
    }

    @Operation(summary = "Obtenir un ELO rating par ID", description = "Retourne un ELO rating à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ELO rating trouvé", content = @Content(schema = @Schema(implementation = EloRating.class))),
            @ApiResponse(responseCode = "404", description = "ELO rating non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EloRating> getById(
            @Parameter(description = "ID du ELO rating", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(eloRatingService.findById(id));
    }

    @Operation(summary = "Lister les ELO ratings par joueur", description = "Retourne tous les ELO ratings d'un joueur")
    @ApiResponse(responseCode = "200", description = "ELO ratings récupérés avec succès")
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<EloRating>> getByPlayer(
            @Parameter(description = "ID du joueur", example = "1") @PathVariable Long playerId) {
        return ResponseEntity.ok(eloRatingService.findByPlayerId(playerId));
    }

    @Operation(summary = "Lister les ELO ratings par matière", description = "Retourne tous les ELO ratings d'une matière")
    @ApiResponse(responseCode = "200", description = "ELO ratings récupérés avec succès")
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<EloRating>> getBySubject(
            @Parameter(description = "ID de la matière", example = "1") @PathVariable Long subjectId) {
        return ResponseEntity.ok(eloRatingService.findBySubjectId(subjectId));
    }

    @Operation(summary = "Créer un ELO rating", description = "Crée un nouveau ELO rating pour un joueur et une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ELO rating créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<EloRating> create(@RequestBody EloRating eloRating) {
        EloRating saved = eloRatingService.create(eloRating);
        return ResponseEntity
                .created(URI.create("/api/elo-ratings/" + saved.getId()))
                .body(saved);
    }

    @Operation(summary = "Mettre à jour un ELO rating", description = "Met à jour un ELO rating existant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ELO rating mis à jour"),
            @ApiResponse(responseCode = "404", description = "ELO rating non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EloRating> update(
            @Parameter(description = "ID du ELO rating", example = "1") @PathVariable Long id,
            @RequestBody EloRating details) {
        return ResponseEntity.ok(eloRatingService.update(id, details));
    }

    @Operation(summary = "Supprimer un ELO rating", description = "Supprime un ELO rating par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ELO rating supprimé"),
            @ApiResponse(responseCode = "404", description = "ELO rating non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du ELO rating", example = "1") @PathVariable Long id) {
        eloRatingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
