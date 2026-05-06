package com.hackathlon.projet.config;

import com.hackathlon.projet.model.Course;
import com.hackathlon.projet.model.Player;
import com.hackathlon.projet.model.Subject;
import com.hackathlon.projet.model.Topic;
import com.hackathlon.projet.repository.CourseRepository;
import com.hackathlon.projet.repository.PlayerRepository;
import com.hackathlon.projet.repository.SubjectRepository;
import com.hackathlon.projet.repository.TopicRepository;
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
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.demo-players:false}")
    private boolean seedDemoPlayers;

    @Value("${app.seed.password:}")
    private String seedPassword;

    public DemoDataSeeder(
            PlayerRepository playerRepository,
            SubjectRepository subjectRepository,
            TopicRepository topicRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedMathCourses();

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
                new DemoPlayer("mohamed", 1665));

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

    private void seedMathCourses() {
        Subject mathSubject = subjectRepository.findByName("Mathématiques")
                .orElseGet(() -> {
                    Subject subject = new Subject();
                    subject.setName("Mathématiques");
                    subject.setDescription("Fiches de révision en mathématiques.");
                    return subjectRepository.save(subject);
                });

        Topic fractions = upsertTopic(
                mathSubject,
                "Fractions",
                "Comparer, lire et manipuler des fractions sans stress.");
        Topic calculMental = upsertTopic(
                mathSubject,
                "Calcul mental",
                "Aller plus vite en décomposant et en vérifiant.");
        Topic problemes = upsertTopic(
                mathSubject,
                "Problèmes",
                "Traduire une situation du quotidien en bonne opération.");

        upsertCourse(fractions, "Vidéo - Voir la fraction comme une pizza", """
                Durée : 4 min

                Pense à une pizza partagée.
                Le nombre du bas indique en combien de parts elle est coupée.
                Le nombre du haut indique combien de parts sont prises.

                Étapes visuelles :
                - Pizza : 8 parts égales
                - Action : tu prends 3 parts
                - Lecture : 3/8

                À retenir :
                - la fraction décrit un partage concret
                - voir l'image aide à répondre vite en duel

                Mini défi :
                Dessine une pizza de 6 parts et colorie 2 parts.
                """);
        upsertCourse(fractions, "Cours - Fractions, l'essentiel", """
                Durée : 10 min

                Une fraction raconte un partage en parts égales.
                Le numérateur dit combien de parts on prend.
                Le dénominateur dit combien de parts existent dans l'unité.

                Objectifs :
                - comprendre le numérateur et le dénominateur
                - comparer deux fractions avec une image mentale claire
                - vérifier rapidement si un résultat semble logique

                Méthode :
                1. Repère le haut et le bas de la fraction.
                2. Mets au même dénominateur si besoin.
                3. Vérifie avec ton image mentale.

                Exemple du quotidien :
                comparer deux parts de pizza ou de gâteau de tailles différentes.
                """);
        upsertCourse(fractions, "Synthèse - Les 3 réflexes sur les fractions", """
                Durée : 2 min

                Essentiel :
                - le numérateur dit combien de parts on prend
                - le dénominateur dit en combien de parts égales le tout est coupé
                - pour comparer, il faut souvent des parts de même taille

                Checklist :
                - j'ai repéré ce que raconte chaque fraction
                - j'ai mis au même dénominateur si besoin
                - mon résultat semble logique avec une image mentale
                """);
        upsertCourse(fractions, "Quiz - Est-ce que tu maîtrises les fractions ?", """
                Durée : 3 min

                Quiz flash :
                - Dans 3/8, que représente le chiffre 8 ?
                - Quelle fraction est la plus grande entre 1/2 et 3/8 ?
                - Pourquoi met-on parfois deux fractions au même dénominateur ?

                Si une question bloque, reviens à la synthèse.
                """);
        upsertCourse(fractions, "Exercices - S'entraîner avec des fractions", """
                Durée : 6 min

                Exercices :
                - dessiner une fraction
                - mettre au même dénominateur
                - dire si un résultat semble logique

                Conseil :
                commence par un exercice facile, puis un moyen.
                """);

        upsertCourse(calculMental, "Vidéo - Découper un calcul sans paniquer", """
                Durée : 4 min

                Décomposer, c'est transformer 58 + 24 en 50 + 8 puis 20 + 4.
                Le calcul devient plus léger dans la tête.

                Étapes visuelles :
                - 58 = 50 + 8
                - 24 = 20 + 4
                - Total = 70 + 12
                """);
        upsertCourse(calculMental, "Cours - Calcul mental par décomposition", """
                Durée : 9 min

                Objectifs :
                - savoir décomposer un calcul en paquets simples
                - gagner du temps sans sacrifier la justesse
                - reprendre confiance sur les calculs rapides

                Réflexes :
                1. Identifier les dizaines et les unités.
                2. Faire les petits calculs séparés.
                3. Recomposer proprement.
                4. Vérifier avec un ordre de grandeur.
                """);
        upsertCourse(calculMental, "Synthèse - Les raccourcis qui marchent", """
                Durée : 2 min

                Essentiel :
                - je coupe les nombres en dizaines et unités
                - je fais les petits calculs séparément
                - je contrôle avec un ordre de grandeur

                Si le résultat final paraît absurde, je recommence calmement.
                """);
        upsertCourse(calculMental, "Quiz - Calcul mental express", """
                Durée : 3 min

                Quiz flash :
                - Pourquoi décomposer 47 en 40 + 7 peut aider ?
                - À quoi sert un ordre de grandeur ?
                - Si 98 + 51 donne 420, que dois-tu faire ?
                """);
        upsertCourse(calculMental, "Exercices - Automatiser les bons réflexes", """
                Durée : 5 min

                Entraînement :
                - dizaines et unités
                - estimation
                - vérification

                Objectif :
                installer la vitesse sans perdre la justesse.
                """);

        upsertCourse(problemes, "Vidéo - Lire un problème comme une histoire", """
                Durée : 4 min

                Un problème est une petite histoire.
                On repère d'abord les informations utiles, puis l'action demandée.

                Réflexes :
                - surligner les mots importants
                - ne pas prendre tous les nombres sans réfléchir
                - choisir l'opération qui correspond à l'histoire
                """);
        upsertCourse(problemes, "Cours - Comprendre un problème pas à pas", """
                Durée : 11 min

                Objectifs :
                - repérer la question précise posée par l'énoncé
                - choisir l'opération adaptée à la situation
                - justifier le résultat avec une phrase complète

                Méthode :
                1. Reformule la question avec tes mots.
                2. Garde seulement les nombres utiles.
                3. Choisis l'opération qui raconte l'histoire.
                4. Écris une phrase-réponse claire.
                """);
        upsertCourse(problemes, "Synthèse - Les 3 questions qui sauvent", """
                Durée : 2 min

                Essentiel :
                - je cherche d'abord la vraie question
                - je garde seulement les nombres utiles
                - je termine par une phrase-réponse

                Cette mini-fiche aide à ne pas se perdre dans l'énoncé.
                """);
        upsertCourse(problemes, "Quiz - Choisir la bonne opération", """
                Durée : 3 min

                Quiz flash :
                - Dans un problème, que dois-tu chercher en premier ?
                - Pourquoi ne faut-il pas utiliser tous les nombres sans réfléchir ?
                - À quoi sert la phrase finale de réponse ?
                """);
        upsertCourse(problemes, "Exercices - Les problèmes du quotidien", """
                Durée : 7 min

                Exercices progressifs :
                - shopping et rendu de monnaie
                - bonbons, points, cartes
                - comparer deux situations

                Conseil :
                écris d'abord la question en une phrase simple avant de calculer.
                """);

        log.info("Seed fiches maths termine");
    }

    private Topic upsertTopic(Subject subject, String name, String description) {
        return topicRepository.findByNameAndSubjectId(name, subject.getId())
                .map(existing -> {
                    existing.setDescription(description);
                    return topicRepository.save(existing);
                })
                .orElseGet(() -> {
                    Topic topic = new Topic();
                    topic.setSubject(subject);
                    topic.setName(name);
                    topic.setDescription(description);
                    return topicRepository.save(topic);
                });
    }

    private void upsertCourse(Topic topic, String title, String content) {
        courseRepository.findByTitleAndTopicId(title, topic.getId())
                .ifPresentOrElse(existing -> {
                    existing.setContent(content);
                    courseRepository.save(existing);
                }, () -> {
                    Course course = new Course();
                    course.setTopic(topic);
                    course.setTitle(title);
                    course.setContent(content);
                    course.setCreatedAt(LocalDateTime.now());
                    courseRepository.save(course);
                });
    }

    private record DemoPlayer(String username, Integer globalElo) {
    }
}
