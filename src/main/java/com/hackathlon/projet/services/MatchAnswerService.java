package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.MatchAnswer;
import com.hackathlon.projet.repository.MatchAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchAnswerService {

    private final MatchAnswerRepository matchAnswerRepository;

    public MatchAnswerService(MatchAnswerRepository matchAnswerRepository) {
        this.matchAnswerRepository = matchAnswerRepository;
    }

    public List<MatchAnswer> findAll() {
        return matchAnswerRepository.findAll();
    }

    public MatchAnswer findById(Long id) {
        return matchAnswerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réponse introuvable"));
    }

    public MatchAnswer create(MatchAnswer matchAnswer) {
        // validate
        validateMatchAnswer(matchAnswer);
        return matchAnswerRepository.save(matchAnswer);
    }

    public MatchAnswer update(Long id, MatchAnswer details) {
        MatchAnswer existing = findById(id);

        // validate
        validateMatchAnswer(details);

        existing.setMatch(details.getMatch());
        existing.setMatchQuestion(details.getMatchQuestion());
        existing.setPlayer(details.getPlayer());
        existing.setAnswer(details.getAnswer());
        existing.setIsCorrect(details.getIsCorrect());
        existing.setResponseTime(details.getResponseTime());
        existing.setAnsweredAt(details.getAnsweredAt());

        return matchAnswerRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        matchAnswerRepository.deleteById(id);
    }

    private void validateMatchAnswer(MatchAnswer matchAnswer) {
        // validate
        if (matchAnswer == null
                || matchAnswer.getMatch() == null
                || matchAnswer.getMatchQuestion() == null
                || matchAnswer.getPlayer() == null) {
            throw new BadRequestException("La réponse du match est invalide");
        }

        if (matchAnswer.getResponseTime() != null && matchAnswer.getResponseTime() < 0) {
            throw new BadRequestException("Le temps de réponse est invalide");
        }
    }
}
