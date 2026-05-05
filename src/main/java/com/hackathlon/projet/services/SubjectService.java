package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.Subject;
import com.hackathlon.projet.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    // log creation
    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public Subject findById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Matière introuvable avec l'id : {}", id);
                    return new NotFoundException("Matière introuvable");
                });
    }

    public Subject create(Subject subject) {
        // validate
        validateSubject(subject);

        if (subjectRepository.existsByName(subject.getName())) {
            log.warn("Tentative de création d'une matière avec un nom déjà utilisé : {}", subject.getName());
            throw new BadRequestException("Ce nom de matière est déjà utilisé");
        }

        log.info("Création de la matière : {}", subject.getName());
        return subjectRepository.save(subject);
    }

    public Subject update(Long id, Subject details) {
        log.info("Mise à jour de la matière id : {}", id);
        Subject existing = findById(id);

        // validate
        validateSubject(details);

        if (!existing.getName().equals(details.getName()) && subjectRepository.existsByName(details.getName())) {
            log.warn("Mise à jour échouée - nom déjà utilisé : {}", details.getName());
            throw new BadRequestException("Ce nom de matière est déjà utilisé");
        }

        existing.setName(details.getName());
        existing.setDescription(details.getDescription());

        log.info("Matière mise à jour : {}", existing.getName());
        return subjectRepository.save(existing);
    }

    public void delete(Long id) {
        log.info("Suppression de la matière id : {}", id);
        findById(id);
        subjectRepository.deleteById(id);
        log.info("Matière supprimée avec succès, id : {}", id);
    }

    private void validateSubject(Subject subject) {
        // validate
        if (subject == null || isBlank(subject.getName())) {
            log.warn("Validation échouée - nom de matière invalide");
            throw new BadRequestException("Le nom de la matière est invalide");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
