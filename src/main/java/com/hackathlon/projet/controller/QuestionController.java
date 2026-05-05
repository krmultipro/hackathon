package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Question;
import com.hackathlon.projet.repository.QuestionRepository;
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

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Operation(summary = "Lister toutes les questions", description = "Retourne la liste complète des questions")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @Operation(summary = "Obtenir une question par ID", description = "Retourne une question à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question trouvée",
                    content = @Content(schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "Question non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(
            @Parameter(description = "ID de la question", example = "1")
            @PathVariable Long id) {
        return questionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Lister les questions par topic", description = "Retourne toutes les questions d'un topic")
    @ApiResponse(responseCode = "200", description = "Questions récupérées avec succès")
    @GetMapping("/topic/{topicId}")
    public List<Question> getByTopic(
            @Parameter(description = "ID du topic", example = "10")
            @PathVariable Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    @Operation(summary = "Créer une question", description = "Crée une nouvelle question en base")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question créée"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Question> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données de la question à créer",
                    required = true)
            @RequestBody Question question) {
        Question saved = questionRepository.save(question);
        return ResponseEntity
                .created(URI.create("/api/questions/" + saved.getQuestionId()))
                .body(saved);
    }

    @Operation(summary = "Mettre à jour une question", description = "Met à jour une question existante par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question mise à jour"),
            @ApiResponse(responseCode = "404", description = "Question non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Question> update(
            @Parameter(description = "ID de la question", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles données de la question",
                    required = true)
            @RequestBody Question input) {
        return questionRepository.findById(id)
                .map(existing -> {
                    existing.setTopicId(input.getTopicId());
                    existing.setStatement(input.getStatement());
                    existing.setAnswerType(input.getAnswerType());
                    existing.setMinElo(input.getMinElo());
                    existing.setMaxElo(input.getMaxElo());
                    existing.setTimeLimit(input.getTimeLimit());
                    existing.setCreationDate(input.getCreationDate());
                    Question updated = questionRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer une question", description = "Supprime une question par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question supprimée"),
            @ApiResponse(responseCode = "404", description = "Question non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la question", example = "1")
            @PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}