package com.hackathlon.projet.services;

import com.hackathlon.projet.model.Question;
import com.hackathlon.projet.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public List<Question> findByTopicId(Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    public Question create(Question question) {
        return questionRepository.save(question);
    }

    public Optional<Question> update(Long id, Question details) {
        return questionRepository.findById(id).map(existing -> {
            existing.setTopic(details.getTopic());
            existing.setStatement(details.getStatement());
            existing.setAnswerType(details.getAnswerType());
            existing.setMinElo(details.getMinElo());
            existing.setMaxElo(details.getMaxElo());
            existing.setTimeLimit(details.getTimeLimit());
            existing.setCreationDate(details.getCreationDate());
            return questionRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!questionRepository.existsById(id)) return false;
        questionRepository.deleteById(id);
        return true;
    }
}
