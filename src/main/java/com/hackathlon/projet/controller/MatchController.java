package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Match;
import com.hackathlon.projet.repository.MatchRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/matches")
@Tag(name = "Matches", description = "API de gestion des matchs")
public class MatchController {

    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Operation(summary = "Lister tous les matchs", description = "Retourne la liste complète des matchs")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchRepository.findAll());
    }

    @Operation(summary = "Obtenir un match par ID", description = "Retourne un match à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match trouvé",
                    content = @Content(schema = @Schema(implementation = Match.class))),
            @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(
            @Parameter(description = "ID du match", example = "1")
            @PathVariable Long id) {
        Optional<Match> match = matchRepository.findById(id);
        return match.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un match", description = "Crée un nouveau match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Match> createMatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données du match à créer",
                    required = true)
            @RequestBody Match match) {
        Match savedMatch = matchRepository.save(match);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMatch);
    }

    @Operation(summary = "Mettre à jour un match", description = "Met à jour un match existant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match mis à jour"),
            @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(
            @Parameter(description = "ID du match", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles données du match",
                    required = true)
            @RequestBody Match matchDetails) {
        Optional<Match> existingMatch = matchRepository.findById(id);
        if (existingMatch.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Match match = existingMatch.get();
        match.setPlayer1Id(matchDetails.getPlayer1Id());
        match.setPlayer2Id(matchDetails.getPlayer2Id());
        match.setScorePlayer1(matchDetails.getScorePlayer1());
        match.setScorePlayer2(matchDetails.getScorePlayer2());
        match.setWinnerId(matchDetails.getWinnerId());
        match.setStatus(matchDetails.getStatus());
        match.setCreatedAt(matchDetails.getCreatedAt());
        match.setFinishedAt(matchDetails.getFinishedAt());

        Match updatedMatch = matchRepository.save(match);
        return ResponseEntity.ok(updatedMatch);
    }

    @Operation(summary = "Supprimer un match", description = "Supprime un match par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match supprimé"),
            @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "ID du match", example = "1")
            @PathVariable Long id) {
        if (!matchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        matchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}