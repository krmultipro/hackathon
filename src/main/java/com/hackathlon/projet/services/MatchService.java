package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import com.hackathlon.projet.model.Match;
import com.hackathlon.projet.model.MatchStatus;
import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.MatchRepository;
import com.hackathlon.projet.repository.PlayerRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    public MatchService(MatchRepository matchRepository, PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Match introuvable"));
    }

    public Match create(Match match) {
        // validate
        validatePlayers(match);
        validateScores(match);
        validateWinner(match);
        validateCreateStatus(match.getStatus());
        validateFinishedMatch(match);

        match.setPlayer1(getPlayer(match.getPlayer1().getId()));
        match.setPlayer2(getPlayer(match.getPlayer2().getId()));
        if (match.getWinner() != null) {
            match.setWinner(getPlayer(match.getWinner().getId()));
        }

        return matchRepository.save(match);
    }

    public Match update(Long id, Match details) {
        Match existingMatch = getMatchById(id);

        if (existingMatch.getStatus() == MatchStatus.COMPLETED) {
            throw new BadRequestException("Le match est déjà terminé");
        }

        // validate
        validatePlayers(details);
        validateScores(details);
        validateWinner(details);
        validateStatusTransition(existingMatch.getStatus(), details.getStatus());
        validateFinishedMatch(details);

        existingMatch.setPlayer1(getPlayer(details.getPlayer1().getId()));
        existingMatch.setPlayer2(getPlayer(details.getPlayer2().getId()));
        existingMatch.setScorePlayer1(details.getScorePlayer1());
        existingMatch.setScorePlayer2(details.getScorePlayer2());
        existingMatch.setStatus(details.getStatus());
        existingMatch.setCreatedAt(details.getCreatedAt());
        existingMatch.setFinishedAt(details.getFinishedAt());

        if (details.getWinner() != null) {
            existingMatch.setWinner(getPlayer(details.getWinner().getId()));
        } else {
            existingMatch.setWinner(null);
        }

        return matchRepository.save(existingMatch);
    }

    public Match findById(Long id) {
        return getMatchById(id);
    }

    public void delete(Long id) {
        getMatchById(id);
        matchRepository.deleteById(id);
    }

    private void validatePlayers(Match match) {
        // check players
        if (match == null || match.getPlayer1() == null || match.getPlayer2() == null) {
            throw new BadRequestException("Les joueurs sont obligatoires");
        }

        if (match.getPlayer1().getId() == null || match.getPlayer2().getId() == null) {
            throw new NotFoundException("Joueur introuvable");
        }

        if (match.getPlayer1().getId().equals(match.getPlayer2().getId())) {
            throw new BadRequestException("Un joueur ne peut pas jouer contre lui-même");
        }

        getPlayer(match.getPlayer1().getId());
        getPlayer(match.getPlayer2().getId());
    }

    private void validateScores(Match match) {
        // validate
        if (match.getScorePlayer1() != null && match.getScorePlayer1() < 0) {
            throw new BadRequestException("Les scores ne peuvent pas être négatifs");
        }

        if (match.getScorePlayer2() != null && match.getScorePlayer2() < 0) {
            throw new BadRequestException("Les scores ne peuvent pas être négatifs");
        }
    }

    private void validateWinner(Match match) {
        // validate winner
        if (match.getWinner() == null) {
            return;
        }

        if (match.getWinner().getId() == null) {
            throw new BadRequestException("Le gagnant doit être un des joueurs du match");
        }

        Long winnerId = match.getWinner().getId();
        Long player1Id = match.getPlayer1().getId();
        Long player2Id = match.getPlayer2().getId();

        if (!winnerId.equals(player1Id) && !winnerId.equals(player2Id)) {
            throw new BadRequestException("Le gagnant doit être un des joueurs du match");
        }
    }

    private void validateFinishedMatch(Match match) {
        // check completion
        if (match.getStatus() == MatchStatus.COMPLETED
                && (match.getScorePlayer1() == null || match.getScorePlayer2() == null)) {
            throw new BadRequestException("Impossible de terminer un match sans score");
        }
    }

    private void validateCreateStatus(MatchStatus status) {
        // validate status
        if (status == null) {
            return;
        }

        if (status != MatchStatus.PENDING && status != MatchStatus.IN_PROGRESS) {
            throw new BadRequestException("Changement de statut invalide");
        }
    }

    private void validateStatusTransition(MatchStatus currentStatus, MatchStatus nextStatus) {
        // validate status
        if (nextStatus == null || currentStatus == nextStatus) {
            return;
        }

        if (currentStatus == null) {
            validateCreateStatus(nextStatus);
            return;
        }

        if (currentStatus == MatchStatus.PENDING
                && (nextStatus == MatchStatus.IN_PROGRESS || nextStatus == MatchStatus.ABANDONED)) {
            return;
        }

        if (currentStatus == MatchStatus.IN_PROGRESS
                && (nextStatus == MatchStatus.COMPLETED || nextStatus == MatchStatus.ABANDONED)) {
            return;
        }

        throw new BadRequestException("Changement de statut invalide");
    }

    private Player getPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));
    }
}
