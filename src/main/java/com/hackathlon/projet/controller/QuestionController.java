package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Question;
import com.hackathlon.projet.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(@PathVariable Long id) {
        return questionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/topic/{topicId}")
    public List<Question> getByTopic(@PathVariable Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    @PostMapping
    public ResponseEntity<Question> create(@RequestBody Question question) {
        Question saved = questionRepository.save(question);
        return ResponseEntity
                .created(URI.create("/api/questions/" + saved.getQuestionId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> update(@PathVariable Long id, @RequestBody Question input) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
