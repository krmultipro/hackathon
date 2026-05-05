package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.AnswerType;
import com.hackathlon.projet.model.Question;
import com.hackathlon.projet.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question introuvable"));
    }

    public List<Question> findByTopicId(Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    public Question create(Question question) {
        // validate
        validateQuestion(question, true);
        return questionRepository.save(question);
    }

    public Question update(Long id, Question details) {
        Question existing = findById(id);

        // validate
        validateQuestion(details, false);

        existing.setTopic(details.getTopic());
        existing.setStatement(details.getStatement());
        existing.setAnswerType(details.getAnswerType());
        existing.setMinElo(details.getMinElo());
        existing.setMaxElo(details.getMaxElo());
        existing.setTimeLimit(details.getTimeLimit());
        existing.setCreationDate(details.getCreationDate());

        return questionRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        questionRepository.deleteById(id);
    }

    private void validateQuestion(Question question, boolean creating) {
        // validate
        if (question == null || isBlank(question.getStatement())) {
            throw new BadRequestException("L'énoncé de la question est invalide");
        }

        if (creating && question.getAnswerType() == null) {
            throw new BadRequestException("Le type de réponse est invalide");
        }

        if (!isValidAnswerType(question.getAnswerType())) {
            throw new BadRequestException("Type de réponse invalide");
        }

        if (question.getTimeLimit() == null || question.getTimeLimit() <= 0) {
            throw new BadRequestException("Le temps de réponse doit être positif");
        }

        // check elo
        if (question.getMinElo() != null && question.getMaxElo() != null
                && question.getMinElo() > question.getMaxElo()) {
            throw new BadRequestException("La plage ELO est invalide");
        }
    }

    private boolean isValidAnswerType(AnswerType answerType) {
        if (answerType == null) {
            return false;
        }

        for (AnswerType value : AnswerType.values()) {
            if (value == answerType) {
                return true;
            }
        }

        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
