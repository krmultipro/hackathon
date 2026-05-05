package com.hackathlon.projet.services;

import com.hackathlon.projet.model.MatchQuestion;
import com.hackathlon.projet.repository.MatchQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchQuestionService {

    private final MatchQuestionRepository matchQuestionRepository;

    public MatchQuestionService(MatchQuestionRepository matchQuestionRepository) {
        this.matchQuestionRepository = matchQuestionRepository;
    }

    public List<MatchQuestion> findAll() {
        return matchQuestionRepository.findAll();
    }

    public Optional<MatchQuestion> findById(Long id) {
        return matchQuestionRepository.findById(id);
    }

    public MatchQuestion create(MatchQuestion matchQuestion) {
        return matchQuestionRepository.save(matchQuestion);
    }

    public Optional<MatchQuestion> update(Long id, MatchQuestion details) {
        return matchQuestionRepository.findById(id).map(existing -> {
            existing.setMatch(details.getMatch());
            existing.setQuestion(details.getQuestion());
            return matchQuestionRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!matchQuestionRepository.existsById(id)) return false;
        matchQuestionRepository.deleteById(id);
        return true;
    }
}
