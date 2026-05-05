package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Subject;
import com.hackathlon.projet.services.SubjectService;
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
@RequestMapping("/api/subjects")
@Tag(name = "Matières", description = "API de gestion des matières")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Operation(summary = "Lister toutes les matières", description = "Retourne la liste complète des matières")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Subject>> getAll() {
        return ResponseEntity.ok(subjectService.findAll());
    }

    @Operation(summary = "Obtenir une matière par ID", description = "Retourne une matière à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière trouvée", content = @Content(schema = @Schema(implementation = Subject.class))),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getById(
            @Parameter(description = "ID de la matière", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(subjectService.findById(id));
    }

    @Operation(summary = "Créer une matière", description = "Crée une nouvelle matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matière créée"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Subject> create(@RequestBody Subject subject) {
        Subject saved = subjectService.create(subject);
        return ResponseEntity
                .created(URI.create("/api/subjects/" + saved.getId()))
                .body(saved);
    }

    @Operation(summary = "Mettre à jour une matière", description = "Met à jour une matière existante par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière mise à jour"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(
            @Parameter(description = "ID de la matière", example = "1") @PathVariable Long id,
            @RequestBody Subject details) {
        return ResponseEntity.ok(subjectService.update(id, details));
    }

    @Operation(summary = "Supprimer une matière", description = "Supprime une matière par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Matière supprimée"),
            @ApiResponse(responseCode = "404", description = "Matière non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la matière", example = "1") @PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
