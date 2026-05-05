package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.MatchQuestion;
import com.hackathlon.projet.services.MatchQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-questions")
@Tag(name = "Match Questions", description = "Gère les liens entre les matchs et les questions")
public class MatchQuestionController {

    private final MatchQuestionService matchQuestionService;

    public MatchQuestionController(MatchQuestionService matchQuestionService) {
        this.matchQuestionService = matchQuestionService;
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les associations match-question")
    public ResponseEntity<List<MatchQuestion>> getAll() {
        return ResponseEntity.ok(matchQuestionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une association match-question par identifiant")
    public ResponseEntity<MatchQuestion> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchQuestionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une association match-question")
    public ResponseEntity<MatchQuestion> create(@RequestBody MatchQuestion matchQuestion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchQuestionService.create(matchQuestion));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une association match-question")
    public ResponseEntity<MatchQuestion> update(@PathVariable Long id, @RequestBody MatchQuestion details) {
        return ResponseEntity.ok(matchQuestionService.update(id, details));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une association match-question")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchQuestionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
