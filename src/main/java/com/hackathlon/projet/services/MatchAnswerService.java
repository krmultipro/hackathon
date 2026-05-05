package com.hackathlon.projet.services;

import com.hackathlon.projet.model.MatchAnswer;
import com.hackathlon.projet.repository.MatchAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchAnswerService {

    private final MatchAnswerRepository matchAnswerRepository;

    public MatchAnswerService(MatchAnswerRepository matchAnswerRepository) {
        this.matchAnswerRepository = matchAnswerRepository;
    }

    public List<MatchAnswer> findAll() {
        return matchAnswerRepository.findAll();
    }

    public Optional<MatchAnswer> findById(Long id) {
        return matchAnswerRepository.findById(id);
    }

    public MatchAnswer create(MatchAnswer matchAnswer) {
        return matchAnswerRepository.save(matchAnswer);
    }

    public Optional<MatchAnswer> update(Long id, MatchAnswer details) {
        return matchAnswerRepository.findById(id).map(existing -> {
            existing.setMatch(details.getMatch());
            existing.setMatchQuestion(details.getMatchQuestion());
            existing.setPlayer(details.getPlayer());
            existing.setAnswer(details.getAnswer());
            existing.setIsCorrect(details.getIsCorrect());
            existing.setResponseTime(details.getResponseTime());
            existing.setAnsweredAt(details.getAnsweredAt());
            return matchAnswerRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!matchAnswerRepository.existsById(id)) return false;
        matchAnswerRepository.deleteById(id);
        return true;
    }
}
