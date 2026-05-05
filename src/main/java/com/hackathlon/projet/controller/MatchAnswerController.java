package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.MatchAnswer;
import com.hackathlon.projet.services.MatchAnswerService;
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
@RequestMapping("/match-answers")
@Tag(name = "Réponses de match", description = "API de gestion des réponses des joueurs dans un match")
public class MatchAnswerController {

    private final MatchAnswerService matchAnswerService;

    public MatchAnswerController(MatchAnswerService matchAnswerService) {
        this.matchAnswerService = matchAnswerService;
    }

    @Operation(summary = "Lister les réponses", description = "Récupère la liste complète des réponses enregistrées pour les matchs")
    @ApiResponse(responseCode = "200", description = "Liste des réponses récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<MatchAnswer>> getAll() {
        return ResponseEntity.ok(matchAnswerService.findAll());
    }

    @Operation(summary = "Obtenir une réponse", description = "Récupère une réponse précise à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réponse trouvée",
                    content = @Content(schema = @Schema(implementation = MatchAnswer.class))),
            @ApiResponse(responseCode = "404", description = "Réponse non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MatchAnswer> getById(
            @Parameter(description = "Identifiant de la réponse", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(matchAnswerService.findById(id));
    }

    @Operation(summary = "Créer une réponse", description = "Enregistre la réponse d'un joueur à une question dans un match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réponse créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<MatchAnswer> create(@RequestBody MatchAnswer matchAnswer) {
        MatchAnswer saved = matchAnswerService.create(matchAnswer);
        return ResponseEntity
                .created(URI.create("/match-answers/" + saved.getId()))
                .body(saved);
    }

    @Operation(summary = "Modifier une réponse", description = "Met à jour une réponse existante à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réponse mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Réponse non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MatchAnswer> update(
            @Parameter(description = "Identifiant de la réponse", example = "1")
            @PathVariable Long id,
            @RequestBody MatchAnswer details) {
        return ResponseEntity.ok(matchAnswerService.update(id, details));
    }

    @Operation(summary = "Supprimer une réponse", description = "Supprime une réponse enregistrée à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réponse supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réponse non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de la réponse", example = "1")
            @PathVariable Long id) {
        matchAnswerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
