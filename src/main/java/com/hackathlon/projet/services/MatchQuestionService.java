package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.MatchQuestion;
import com.hackathlon.projet.repository.MatchQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchQuestionService {

    private final MatchQuestionRepository matchQuestionRepository;

    public MatchQuestionService(MatchQuestionRepository matchQuestionRepository) {
        this.matchQuestionRepository = matchQuestionRepository;
    }

    public List<MatchQuestion> findAll() {
        return matchQuestionRepository.findAll();
    }

    public MatchQuestion findById(Long id) {
        return matchQuestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Association match-question introuvable"));
    }

    public MatchQuestion create(MatchQuestion matchQuestion) {
        // validate
        validateMatchQuestion(matchQuestion);
        validateDuplicateLink(matchQuestion);
        return matchQuestionRepository.save(matchQuestion);
    }

    public MatchQuestion update(Long id, MatchQuestion details) {
        MatchQuestion existing = findById(id);

        // validate
        validateMatchQuestion(details);
        if (isLinkChanged(existing, details)) {
            validateDuplicateLink(details);
        }

        existing.setMatch(details.getMatch());
        existing.setQuestion(details.getQuestion());

        return matchQuestionRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        matchQuestionRepository.deleteById(id);
    }

    private void validateMatchQuestion(MatchQuestion matchQuestion) {
        // validate
        if (matchQuestion == null || matchQuestion.getMatch() == null) {
            throw new BadRequestException("Le match est obligatoire");
        }

        if (matchQuestion.getQuestion() == null) {
            throw new BadRequestException("La question est obligatoire");
        }
    }

    private void validateDuplicateLink(MatchQuestion matchQuestion) {
        // check link
        Long matchId = matchQuestion.getMatch().getId();
        Long questionId = matchQuestion.getQuestion().getId();

        if (matchId != null && questionId != null
                && matchQuestionRepository.existsByMatchIdAndQuestionId(matchId, questionId)) {
            throw new BadRequestException("Cette question est déjà associée à ce match");
        }
    }

    private boolean isLinkChanged(MatchQuestion existing, MatchQuestion details) {
        Long existingMatchId = existing.getMatch() == null ? null : existing.getMatch().getId();
        Long existingQuestionId = existing.getQuestion() == null ? null : existing.getQuestion().getId();
        Long nextMatchId = details.getMatch() == null ? null : details.getMatch().getId();
        Long nextQuestionId = details.getQuestion() == null ? null : details.getQuestion().getId();

        if (existingMatchId == null && nextMatchId != null) {
            return true;
        }

        if (existingQuestionId == null && nextQuestionId != null) {
            return true;
        }

        if (existingMatchId != null && !existingMatchId.equals(nextMatchId)) {
            return true;
        }

        return existingQuestionId != null && !existingQuestionId.equals(nextQuestionId);
    }
}
