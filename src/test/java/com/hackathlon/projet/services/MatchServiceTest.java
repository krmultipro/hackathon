package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.exception.NotFoundException;
import com.hackathlon.projet.model.Match;
import com.hackathlon.projet.model.MatchStatus;
import com.hackathlon.projet.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MatchServiceTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = playerService.create(buildPlayer("player1", "password123"));
        player2 = playerService.create(buildPlayer("player2", "password456"));
    }

    private Player buildPlayer(String username, String password) {
        Player p = new Player();
        p.setUsername(username);
        p.setPassword(password);
        return p;
    }

    private Match buildMatch(Player p1, Player p2) {
        Match match = new Match();
        match.setPlayer1(p1);
        match.setPlayer2(p2);
        match.setStatus(MatchStatus.PENDING);
        return match;
    }

    @Test
    void shouldCreateMatchSuccessfully() {
        // test success
        Match saved = matchService.create(buildMatch(player1, player2));
        assertNotNull(saved.getId());
        assertEquals(player1.getId(), saved.getPlayer1().getId());
        assertEquals(player2.getId(), saved.getPlayer2().getId());
    }

    @Test
    void shouldFailWhenSamePlayer() {
        // test error
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> matchService.create(buildMatch(player1, player1)));
        assertEquals("Un joueur ne peut pas jouer contre lui-même", ex.getMessage());
    }

    @Test
    void shouldFailWhenScoreNegative() {
        // test error
        Match match = buildMatch(player1, player2);
        match.setScorePlayer1(-1);
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> matchService.create(match));
        assertEquals("Les scores ne peuvent pas être négatifs", ex.getMessage());
    }

    @Test
    void shouldFailWhenMatchNotFound() {
        // test error
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> matchService.getMatchById(9999L));
        assertEquals("Match introuvable", ex.getMessage());
    }
}
