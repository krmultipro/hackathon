package com.hackathlon.projet.config;

import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.demo-players:false}")
    private boolean seedDemoPlayers;

    @Value("${app.seed.password:}")
    private String seedPassword;

    public DemoDataSeeder(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedDemoPlayers) {
            return;
        }

        if (!StringUtils.hasText(seedPassword)) {
            throw new IllegalStateException("SEED_PASSWORD est requis quand app.seed.demo-players=true");
        }

        List<DemoPlayer> demoPlayers = List.of(
                new DemoPlayer("alice", 1640),
                new DemoPlayer("bilal", 1585),
                new DemoPlayer("chloe", 1710),
                new DemoPlayer("david", 1490),
                new DemoPlayer("emma", 1765),
                new DemoPlayer("farah", 1620),
                new DemoPlayer("hugo", 1540),
                new DemoPlayer("ines", 1680),
                new DemoPlayer("jade", 1455),
                new DemoPlayer("karim", 1735),
                new DemoPlayer("lina", 1510),
                new DemoPlayer("mohamed", 1665)
        );

        int createdCount = 0;
        for (DemoPlayer demoPlayer : demoPlayers) {
            if (playerRepository.existsByUsername(demoPlayer.username())) {
                continue;
            }

            Player player = new Player();
            player.setUsername(demoPlayer.username());
            player.setPassword(passwordEncoder.encode(seedPassword));
            player.setGlobalElo(demoPlayer.globalElo());
            player.setCreatedAt(LocalDateTime.now());
            playerRepository.save(player);
            createdCount++;
        }

        log.info("Seed demo joueurs termine, {} nouveaux joueurs crees", createdCount);
    }

    private record DemoPlayer(String username, Integer globalElo) {
    }
}
