package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.EloRating;
import com.hackathlon.projet.repository.EloRatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EloRatingService {

    // log creation
    private static final Logger log = LoggerFactory.getLogger(EloRatingService.class);

    private final EloRatingRepository eloRatingRepository;

    public EloRatingService(EloRatingRepository eloRatingRepository) {
        this.eloRatingRepository = eloRatingRepository;
    }

    public List<EloRating> findAll() {
        return eloRatingRepository.findAll();
    }

    public EloRating findById(Long id) {
        return eloRatingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("EloRating introuvable avec l'id : {}", id);
                    return new NotFoundException("EloRating introuvable");
                });
    }

    public List<EloRating> findByPlayerId(Long playerId) {
        return eloRatingRepository.findByPlayerId(playerId);
    }

    public List<EloRating> findBySubjectId(Long subjectId) {
        return eloRatingRepository.findBySubjectId(subjectId);
    }

    public EloRating create(EloRating eloRating) {
        // validate
        validateEloRating(eloRating);

        Long playerId = eloRating.getPlayer().getId();
        Long subjectId = eloRating.getSubject().getId();

        if (eloRatingRepository.existsByPlayerIdAndSubjectId(playerId, subjectId)) {
            log.warn("EloRating déjà existant pour le joueur id {} et la matière id {}", playerId, subjectId);
            throw new BadRequestException("Un EloRating existe déjà pour ce joueur et cette matière");
        }

        log.info("Création d'un EloRating pour le joueur id {} et la matière id {}", playerId, subjectId);
        return eloRatingRepository.save(eloRating);
    }

    public EloRating update(Long id, EloRating details) {
        log.info("Mise à jour de l'EloRating id : {}", id);
        EloRating existing = findById(id);

        // validate
        validateEloRating(details);

        Long newPlayerId = details.getPlayer().getId();
        Long newSubjectId = details.getSubject().getId();
        Long existingPlayerId = existing.getPlayer().getId();
        Long existingSubjectId = existing.getSubject().getId();

        boolean playerChanged = !existingPlayerId.equals(newPlayerId);
        boolean subjectChanged = !existingSubjectId.equals(newSubjectId);

        if ((playerChanged || subjectChanged)
                && eloRatingRepository.existsByPlayerIdAndSubjectId(newPlayerId, newSubjectId)) {
            log.warn("Mise à jour échouée - EloRating déjà existant pour le joueur id {} et la matière id {}", newPlayerId, newSubjectId);
            throw new BadRequestException("Un EloRating existe déjà pour ce joueur et cette matière");
        }

        existing.setPlayer(details.getPlayer());
        existing.setSubject(details.getSubject());
        existing.setEloScore(details.getEloScore());
        existing.setMatchCount(details.getMatchCount());
        existing.setWinCount(details.getWinCount());
        existing.setLossCount(details.getLossCount());
        existing.setLastMatchPlayedAt(details.getLastMatchPlayedAt());

        log.info("EloRating mis à jour avec succès, id : {}", id);
        return eloRatingRepository.save(existing);
    }

    public void delete(Long id) {
        log.info("Suppression de l'EloRating id : {}", id);
        findById(id);
        eloRatingRepository.deleteById(id);
        log.info("EloRating supprimé avec succès, id : {}", id);
    }

    private void validateEloRating(EloRating eloRating) {
        // validate
        if (eloRating == null || eloRating.getPlayer() == null || eloRating.getPlayer().getId() == null) {
            log.warn("Validation échouée - joueur manquant");
            throw new BadRequestException("Le joueur est obligatoire");
        }

        if (eloRating.getSubject() == null || eloRating.getSubject().getId() == null) {
            log.warn("Validation échouée - matière manquante");
            throw new BadRequestException("La matière est obligatoire");
        }

        // check scores
        if (eloRating.getEloScore() != null && eloRating.getEloScore() < 0) {
            log.warn("Validation échouée - score ELO négatif : {}", eloRating.getEloScore());
            throw new BadRequestException("Le score ELO ne peut pas être négatif");
        }

        if (eloRating.getMatchCount() != null && eloRating.getMatchCount() < 0) {
            log.warn("Validation échouée - nombre de matchs négatif");
            throw new BadRequestException("Le nombre de matchs ne peut pas être négatif");
        }

        if (eloRating.getWinCount() != null && eloRating.getWinCount() < 0) {
            log.warn("Validation échouée - nombre de victoires négatif");
            throw new BadRequestException("Le nombre de victoires ne peut pas être négatif");
        }

        if (eloRating.getLossCount() != null && eloRating.getLossCount() < 0) {
            log.warn("Validation échouée - nombre de défaites négatif");
            throw new BadRequestException("Le nombre de défaites ne peut pas être négatif");
        }
    }
}
