package com.hackathlon.projet.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hackathlon.projet.model.Match;
import com.hackathlon.projet.repository.MatchRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    public Match create(Match match) {
        return matchRepository.save(match);
    }

    public Optional<Match> update(Long id, Match details) {
        return matchRepository.findById(id).map(existing -> {
            existing.setPlayer1(details.getPlayer1());
            existing.setPlayer2(details.getPlayer2());
            existing.setScorePlayer1(details.getScorePlayer1());
            existing.setScorePlayer2(details.getScorePlayer2());
            existing.setWinner(details.getWinner());
            existing.setStatus(details.getStatus());
            existing.setCreatedAt(details.getCreatedAt());
            existing.setFinishedAt(details.getFinishedAt());
            return matchRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!matchRepository.existsById(id))
            return false;
        matchRepository.deleteById(id);
        return true;
    }
}
