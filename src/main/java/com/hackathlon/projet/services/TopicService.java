package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.Topic;
import com.hackathlon.projet.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    // log creation
    private static final Logger log = LoggerFactory.getLogger(TopicService.class);

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Topic introuvable avec l'id : {}", id);
                    return new NotFoundException("Topic introuvable");
                });
    }

    public List<Topic> findBySubjectId(Long subjectId) {
        return topicRepository.findBySubjectId(subjectId);
    }

    public Topic create(Topic topic) {
        // validate
        validateTopic(topic);

        if (topicRepository.existsByNameAndSubjectId(topic.getName(), topic.getSubject().getId())) {
            log.warn("Tentative de création d'un topic déjà existant : {} pour la matière id {}", topic.getName(), topic.getSubject().getId());
            throw new BadRequestException("Ce topic existe déjà pour cette matière");
        }

        log.info("Création du topic : {}", topic.getName());
        return topicRepository.save(topic);
    }

    public Topic update(Long id, Topic details) {
        log.info("Mise à jour du topic id : {}", id);
        Topic existing = findById(id);

        // validate
        validateTopic(details);

        boolean nameChanged = !existing.getName().equals(details.getName());
        boolean subjectChanged = !existing.getSubject().getId().equals(details.getSubject().getId());
        if ((nameChanged || subjectChanged)
                && topicRepository.existsByNameAndSubjectId(details.getName(), details.getSubject().getId())) {
            log.warn("Mise à jour échouée - topic déjà existant : {} pour la matière id {}", details.getName(), details.getSubject().getId());
            throw new BadRequestException("Ce topic existe déjà pour cette matière");
        }

        existing.setSubject(details.getSubject());
        existing.setName(details.getName());
        existing.setDescription(details.getDescription());

        log.info("Topic mis à jour : {}", existing.getName());
        return topicRepository.save(existing);
    }

    public void delete(Long id) {
        log.info("Suppression du topic id : {}", id);
        findById(id);
        topicRepository.deleteById(id);
        log.info("Topic supprimé avec succès, id : {}", id);
    }

    private void validateTopic(Topic topic) {
        // validate
        if (topic == null || isBlank(topic.getName())) {
            log.warn("Validation échouée - nom de topic invalide");
            throw new BadRequestException("Le nom du topic est invalide");
        }

        // check subject
        if (topic.getSubject() == null || topic.getSubject().getId() == null) {
            log.warn("Validation échouée - matière manquante pour le topic");
            throw new BadRequestException("La matière est obligatoire");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
