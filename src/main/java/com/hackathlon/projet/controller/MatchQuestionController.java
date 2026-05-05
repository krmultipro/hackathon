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
@RequestMapping("/match-questions")
@Tag(name = "Match Questions", description = "Gere les liens entre les matchs et les questions")
public class MatchQuestionController {

    private final MatchQuestionService matchQuestionService;

    public MatchQuestionController(MatchQuestionService matchQuestionService) {
        this.matchQuestionService = matchQuestionService;
    }

    @GetMapping
    @Operation(
            summary = "Recuperer toutes les associations match-question",
            description = "Retourne la liste complete des associations entre les matchs et les questions."
    )
    public ResponseEntity<List<MatchQuestion>> getAllMatchQuestions() {
        return ResponseEntity.ok(matchQuestionRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Recuperer une association match-question par identifiant",
            description = "Retourne une association entre un match et une question a partir de son identifiant."
    )
    public ResponseEntity<MatchQuestion> getMatchQuestionById(@PathVariable Long id) {
        Optional<MatchQuestion> matchQuestion = matchQuestionRepository.findById(id);
        return matchQuestion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Creer une association match-question",
            description = "Cree une nouvelle association entre un match et une question."
    )
    public ResponseEntity<MatchQuestion> createMatchQuestion(@RequestBody MatchQuestion matchQuestion) {
        MatchQuestion savedMatchQuestion = matchQuestionRepository.save(matchQuestion);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMatchQuestion);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre a jour une association match-question",
            description = "Met a jour l'identifiant du match et l'identifiant de la question pour une association existante."
    )
    public ResponseEntity<MatchQuestion> updateMatchQuestion(
            @PathVariable Long id,
            @RequestBody MatchQuestion matchQuestionDetails
    ) {
        Optional<MatchQuestion> existingMatchQuestion = matchQuestionRepository.findById(id);
        if (existingMatchQuestion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MatchQuestion matchQuestion = existingMatchQuestion.get();
        matchQuestion.setMatchId(matchQuestionDetails.getMatchId());
        matchQuestion.setQuestionId(matchQuestionDetails.getQuestionId());

        MatchQuestion updatedMatchQuestion = matchQuestionRepository.save(matchQuestion);
        return ResponseEntity.ok(updatedMatchQuestion);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une association match-question",
            description = "Supprime une association entre un match et une question a partir de son identifiant."
    )
    public ResponseEntity<Void> deleteMatchQuestion(@PathVariable Long id) {
        if (!matchQuestionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        matchQuestionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
