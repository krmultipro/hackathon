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
@Tag(name = "Match Questions", description = "Manage the links between matches and questions")
public class MatchQuestionController {

    private final MatchQuestionService matchQuestionService;

    public MatchQuestionController(MatchQuestionService matchQuestionService) {
        this.matchQuestionService = matchQuestionService;
    }

    @GetMapping
    @Operation(summary = "Get all match questions", description = "Return the full list of match-question links.")
    public ResponseEntity<List<MatchQuestion>> getAll() {
        return ResponseEntity.ok(matchQuestionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a match question by id", description = "Return one match-question link using its id.")
    public ResponseEntity<MatchQuestion> getById(@PathVariable Long id) {
        return matchQuestionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a match question", description = "Create a new link between a match and a question.")
    public ResponseEntity<MatchQuestion> create(@RequestBody MatchQuestion matchQuestion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchQuestionService.create(matchQuestion));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a match question", description = "Update the match and question for an existing link.")
    public ResponseEntity<MatchQuestion> update(@PathVariable Long id, @RequestBody MatchQuestion details) {
        return matchQuestionService.update(id, details)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a match question", description = "Delete a match-question link by its id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return matchQuestionService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
