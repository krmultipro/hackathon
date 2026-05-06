# Application éducative compétitive

## Objectif du projet

Ce projet vise à concevoir une application web éducative basée sur la gamification et la compétition, permettant aux élèves de collège ou plus d’apprendre et de s’entraîner en mathématiques de manière interactive.

L’application propose un système de jeu compétitif en 1 contre 1, dans lequel les utilisateurs s’affrontent en répondant à des questions liées à une matière, un thème et un cours. Chaque joueur doit répondre rapidement et correctement afin d’obtenir le meilleur score et gagner le match.

Le projet combine plusieurs dimensions :

- Apprentissage : accès à des contenus pédagogiques organisés par matière, thème et cours
- Entraînement : questions adaptées au niveau de l’utilisateur
- Compétition : affrontement entre joueurs avec un système de score
- Classement : système de classement basé sur un Elo global et par matière
- Suivi : historique des matchs et statistiques des performances

## Fonctionnalités principales

- Création de compte et authentification
- Sélection d’une matière
- Sélection d’un thème
- Sélection d’un cours
- Consultation de contenus pédagogiques
- Lancement de matchs 1v1 entre joueurs
- Réponse à des questions en temps limité
- Soumission des réponses
- Calcul automatique des scores et détermination du gagnant
- Mise à jour du classement Elo
- Consultation des statistiques personnelles
- Accès à l’historique des parties

## Vision

L’objectif est de transformer l’apprentissage en une expérience interactive et motivante, en s’inspirant des mécanismes des jeux vidéo compétitifs.

L’architecture du projet est pensée pour être évolutive, avec une séparation entre :

- Le cœur du jeu (matchs et réponses)
- Le système d’apprentissage (matières, thèmes, cours)
- Le système de classement (Elo et statistiques)

Cette approche permet d’ajouter facilement de nouvelles fonctionnalités comme l’adaptation du niveau, de nouveaux types de questions ou des modes de jeu supplémentaires.
---

## Stack technique

### Backend
- **Java 17** avec **Spring Boot 4.0.6**
- **Spring Security** + authentification **JWT** (jjwt 0.12.3)
- **Spring Data JPA** / **Hibernate**
- **PostgreSQL** (base de données principale)
- **H2** (base de données en mémoire pour les tests)
- **Springdoc OpenAPI** (documentation Swagger UI)
- **Spring Boot Validation** (validation des entrées)
- Rate limiting intégré (`RateLimitFilter`)

### Frontend
- **Phaser 3** (moteur de jeu 2D)
- JavaScript ES modules (Vanilla JS)
- Architecture en scènes : Boot → Preload → Menu → Sélection de cours → Jeu
- Matières disponibles : **Mathématiques**, **Français**

---

## Architecture

### Modèle de données

```
PLAYER ──< MATCH >── PLAYER
SUBJECT ──< TOPIC ──< COURSE
TOPIC ──< QUESTION ──< POSSIBLE_ANSWER
MATCH ──< MATCH_QUESTION ──< MATCH_ANSWER
PLAYER ──< ELO_RATING >── SUBJECT
```

Les entités principales sont :

| Entité | Rôle |
|---|---|
| `Player` | Utilisateur avec score Elo global |
| `Subject` | Matière (ex : Maths, Français) |
| `Topic` | Thème au sein d'une matière |
| `Course` | Cours pédagogique lié à un thème |
| `Question` | Question avec type de réponse et limite de temps |
| `PossibleAnswer` | Choix de réponse (avec flag `is_correct`) |
| `Match` | Partie 1v1 entre deux joueurs |
| `MatchQuestion` | Questions tirées pour un match |
| `MatchAnswer` | Réponse d'un joueur à une question de match |
| `EloRating` | Score Elo d'un joueur par matière |

### Couches applicatives (backend)

```
controller/   → Endpoints REST (Auth, Course, Match, Question, Player, Elo…)
services/     → Logique métier
repository/   → Accès données (Spring Data JPA)
model/        → Entités JPA
dto/          → Objets de transfert
security/     → JWT, filtres, configuration Spring Security
config/       → Configuration applicative
exception/    → Gestion globale des erreurs
```

### Scènes du jeu (frontend)

```
BootScene         → Initialisation
PreloadScene      → Chargement des assets
MenuScene         → Sélection de matière/thème/cours
CourseScene       → Visualisation d'un cours
MathCourseScene   → Cours de mathématiques
FrenchCourseScene → Cours de français
CharacterSelectScene → Sélection du personnage
GameScene         → Déroulement du match 1v1
```

---

## Prérequis

- Java 17+
- Maven 3.9+
- PostgreSQL (ou configuration H2 pour les tests)
- Node.js (optionnel, pour le développement frontend)

---

## Configuration

Les variables d'environnement suivantes sont requises :

| Variable | Description |
|---|---|
| `DB_URL` | URL JDBC de la base PostgreSQL |
| `DB_USERNAME` | Nom d'utilisateur PostgreSQL |
| `DB_PASSWORD` | Mot de passe PostgreSQL |
| `DB_DDL_AUTO` | Stratégie DDL Hibernate (`create`, `update`, `validate`…) |
| `JWT_SECRET` | Clé secrète de signature des tokens JWT |
| `JWT_EXPIRATION` | Durée de validité des tokens (en ms) |
| `SEED_PASSWORD` | Mot de passe utilisé pour les données de seed |

---

## Lancement

```bash
# Cloner le projet
git clone <url-du-repo>
cd hackathon

# Copier et remplir le fichier de configuration
cp .env.example .env

# Injecter les variables d'environnement et lancer l'application
set -a
source .env
set +a
./mvnw spring-boot:run
```

L'application est accessible sur `http://localhost:8080`.  
La documentation Swagger UI est disponible sur `http://localhost:8080/swagger-ui.html`.

> **Version mobile / tablette** : l'interface est responsive et accessible depuis n'importe quel appareil sur le même réseau en utilisant l'adresse IP de la machine hôte :  
> `http://<IP_DE_LA_MACHINE_HOTE>:8080`  
> Pour connaître l'IP hôte : `ip a` (Linux/macOS) ou `ipconfig` (Windows).

---

## Tests

```bash
./mvnw test
```

Les tests utilisent une base H2 en mémoire (profil `application-test.yml`).

---

## API REST

Les principaux endpoints exposés :

| Contrôleur | Préfixe | Description |
|---|---|---|
| `AuthController` | `/api/auth` | Inscription / Connexion |
| `PlayerController` | `/api/players` | Gestion des joueurs |
| `SubjectController` | `/api/subjects` | Matières |
| `TopicController` | `/api/topics` | Thèmes |
| `CourseController` | `/api/courses` | Cours |
| `QuestionController` | `/api/questions` | Questions |
| `MatchController` | `/api/matches` | Matchs |
| `MatchQuestionController` | `/api/match-questions` | Questions d'un match |
| `MatchAnswerController` | `/api/match-answers` | Réponses en match |
| `EloRatingController` | `/api/elo` | Classement Elo |