package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Match;
import com.hackathlon.projet.services.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@Tag(name = "Matches", description = "API de gestion des matchs")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Operation(summary = "Lister tous les matchs", description = "Retourne la liste complète des matchs")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Match>> getAll() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @Operation(summary = "Obtenir un match par ID", description = "Retourne un match à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match trouvé",
                    content = @Content(schema = @Schema(implementation = Match.class))),
            @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Match> getById(
            @Parameter(description = "ID du match", example = "1")
            @PathVariable Long id) {
        return matchService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un match", description = "Crée un nouveau match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Match> create(@RequestBody Match match) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(match));
    }

    @Operation(summary = "Mettre à jour un match", description = "Met à jour un match existant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match mis à jour"),
            @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Match> update(
            @Parameter(description = "ID du match", example = "1")
            @PathVariable Long id,
            @RequestBody Match details) {
        return matchService.update(id, details)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un match", description = "Supprime un match par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match supprimé"),
            @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du match", example = "1")
            @PathVariable Long id) {
        return matchService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
