package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Topic;
import com.hackathlon.projet.services.TopicService;
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
@RequestMapping("/api/topics")
@Tag(name = "Topics", description = "API de gestion des topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @Operation(summary = "Lister tous les topics", description = "Retourne la liste complète des topics")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Topic>> getAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    @Operation(summary = "Obtenir un topic par ID", description = "Retourne un topic à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic trouvé", content = @Content(schema = @Schema(implementation = Topic.class))),
            @ApiResponse(responseCode = "404", description = "Topic non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getById(
            @Parameter(description = "ID du topic", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @Operation(summary = "Lister les topics par matière", description = "Retourne tous les topics d'une matière")
    @ApiResponse(responseCode = "200", description = "Topics récupérés avec succès")
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Topic>> getBySubject(
            @Parameter(description = "ID de la matière", example = "1") @PathVariable Long subjectId) {
        return ResponseEntity.ok(topicService.findBySubjectId(subjectId));
    }

    @Operation(summary = "Créer un topic", description = "Crée un nouveau topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Topic créé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Topic> create(@RequestBody Topic topic) {
        Topic saved = topicService.create(topic);
        return ResponseEntity
                .created(URI.create("/api/topics/" + saved.getId()))
                .body(saved);
    }

    @Operation(summary = "Mettre à jour un topic", description = "Met à jour un topic existant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic mis à jour"),
            @ApiResponse(responseCode = "404", description = "Topic non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Topic> update(
            @Parameter(description = "ID du topic", example = "1") @PathVariable Long id,
            @RequestBody Topic details) {
        return ResponseEntity.ok(topicService.update(id, details));
    }

    @Operation(summary = "Supprimer un topic", description = "Supprime un topic par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Topic supprimé"),
            @ApiResponse(responseCode = "404", description = "Topic non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du topic", example = "1") @PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
