package com.hackathlon.projet.services;

import com.hackathlon.projet.exception.BadRequestException;
import com.hackathlon.projet.model.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    private Player buildPlayer(String username, String password) {
        Player p = new Player();
        p.setUsername(username);
        p.setPassword(password);
        return p;
    }

    @Test
    void shouldCreatePlayerSuccessfully() {
        // test success
        Player saved = playerService.create(buildPlayer("testuser", "password123"));
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
    }

    @Test
    void shouldFailWhenUsernameAlreadyExists() {
        // test error
        playerService.create(buildPlayer("duplicate", "password123"));
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> playerService.create(buildPlayer("duplicate", "password456")));
        assertEquals("Le nom d'utilisateur est déjà utilisé", ex.getMessage());
    }

    @Test
    void shouldFailWhenPasswordTooShort() {
        // test error
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> playerService.create(buildPlayer("user1", "short")));
        assertEquals("Le mot de passe doit contenir au moins 8 caractères", ex.getMessage());
    }

    @Test
    void shouldFailWhenUsernameIsEmpty() {
        // test error
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> playerService.create(buildPlayer("", "password123")));
        assertEquals("Le nom d'utilisateur est invalide", ex.getMessage());
    }
}
