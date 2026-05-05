package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Question;
import com.hackathlon.projet.services.QuestionService;
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
@RequestMapping("/api/questions")
@Tag(name = "Questions", description = "API de gestion des questions")
public class QuestionController {

        private final QuestionService questionService;

        public QuestionController(QuestionService questionService) {
                this.questionService = questionService;
        }

        @Operation(summary = "Lister toutes les questions", description = "Retourne la liste complète des questions")
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
        @GetMapping
        public List<Question> getAll() {
                return questionService.findAll();
        }

        @Operation(summary = "Obtenir une question par ID", description = "Retourne une question à partir de son identifiant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Question trouvée", content = @Content(schema = @Schema(implementation = Question.class))),
                        @ApiResponse(responseCode = "404", description = "Question non trouvée")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Question> getById(
                        @Parameter(description = "ID de la question", example = "1") @PathVariable Long id) {
                return ResponseEntity.ok(questionService.findById(id));
        }

        @Operation(summary = "Lister les questions par topic", description = "Retourne toutes les questions d'un topic")
        @ApiResponse(responseCode = "200", description = "Questions récupérées avec succès")
        @GetMapping("/topic/{topicId}")
        public List<Question> getByTopic(
                        @Parameter(description = "ID du topic", example = "10") @PathVariable Long topicId) {
                return questionService.findByTopicId(topicId);
        }

        @Operation(summary = "Créer une question", description = "Crée une nouvelle question en base")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Question créée"),
                        @ApiResponse(responseCode = "400", description = "Requête invalide")
        })
        @PostMapping
        public ResponseEntity<Question> create(@RequestBody Question question) {
                Question saved = questionService.create(question);
                return ResponseEntity
                                .created(URI.create("/api/questions/" + saved.getId()))
                                .body(saved);
        }

        @Operation(summary = "Mettre à jour une question", description = "Met à jour une question existante par ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Question mise à jour"),
                        @ApiResponse(responseCode = "404", description = "Question non trouvée")
        })
        @PutMapping("/{id}")
        public ResponseEntity<Question> update(
                        @Parameter(description = "ID de la question", example = "1") @PathVariable Long id,
                        @RequestBody Question details) {
                return ResponseEntity.ok(questionService.update(id, details));
        }

        @Operation(summary = "Supprimer une question", description = "Supprime une question par son ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Question supprimée"),
                        @ApiResponse(responseCode = "404", description = "Question non trouvée")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID de la question", example = "1") @PathVariable Long id) {
                questionService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
