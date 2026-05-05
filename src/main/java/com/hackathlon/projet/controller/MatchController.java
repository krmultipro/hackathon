package com.hackathlon.projet.controller;

import com.hackathlon.projet.model.Match;
import com.hackathlon.projet.repository.MatchRepository;
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
@RequestMapping("/matches")
public class MatchController {

    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        Optional<Match> match = matchRepository.findById(id);
        return match.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        Match savedMatch = matchRepository.save(match);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMatch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable Long id, @RequestBody Match matchDetails) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        if (!matchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        matchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
