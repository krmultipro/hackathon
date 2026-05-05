package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.MatchQuestion;
import com.hackathlon.projet.repository.MatchQuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/match-questions")
@Tag(name = "Match Questions", description = "Manage the links between matches and questions")
public class MatchQuestionController {

    private final MatchQuestionRepository matchQuestionRepository;

    public MatchQuestionController(MatchQuestionRepository matchQuestionRepository) {
        this.matchQuestionRepository = matchQuestionRepository;
    }

    @GetMapping
    @Operation(
            summary = "Get all match questions",
            description = "Return the full list of match-question links."
    )
    public ResponseEntity<List<MatchQuestion>> getAllMatchQuestions() {
        return ResponseEntity.ok(matchQuestionRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a match question by id",
            description = "Return one match-question link using its id."
    )
    public ResponseEntity<MatchQuestion> getMatchQuestionById(@PathVariable Long id) {
        Optional<MatchQuestion> matchQuestion = matchQuestionRepository.findById(id);
        return matchQuestion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a match question",
            description = "Create a new link between a match and a question."
    )
    public ResponseEntity<MatchQuestion> createMatchQuestion(@RequestBody MatchQuestion matchQuestion) {
        MatchQuestion savedMatchQuestion = matchQuestionRepository.save(matchQuestion);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMatchQuestion);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a match question",
            description = "Update the match id and question id for an existing link."
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
            summary = "Delete a match question",
            description = "Delete a match-question link by its id."
    )
    public ResponseEntity<Void> deleteMatchQuestion(@PathVariable Long id) {
        if (!matchQuestionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        matchQuestionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
