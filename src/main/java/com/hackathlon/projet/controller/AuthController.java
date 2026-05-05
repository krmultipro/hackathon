package com.hackathlon.projet.controller;

import com.hackathlon.projet.dto.AuthResponse;
import com.hackathlon.projet.dto.ErrorResponse;
import com.hackathlon.projet.dto.LoginRequest;
import com.hackathlon.projet.dto.RegisterRequest;
import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.security.JwtUtil;
import com.hackathlon.projet.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Inscription et connexion des joueurs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PlayerService playerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            PlayerService playerService,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.playerService = playerService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Inscription", description = "Crée un nouveau compte joueur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compte créé, token retourné"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou nom d'utilisateur déjà pris")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (playerService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(400, "Nom d'utilisateur déjà pris : " + request.getUsername(),
                            LocalDateTime.now()));
        }

        Player player = new Player();
        player.setUsername(request.getUsername());
        player.setPassword(passwordEncoder.encode(request.getPassword()));
        player.setGlobalElo(1000);
        player.setCreatedAt(LocalDateTime.now());
        playerService.create(player);

        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, request.getUsername()));
    }

    @Operation(summary = "Connexion", description = "Authentifie un joueur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie, token retourné"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, request.getUsername()));
    }
}
