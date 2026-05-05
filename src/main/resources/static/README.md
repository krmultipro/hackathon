# Mon Jeu Phaser 3

## Structure du projet

```
testphaser/
├── index.html          # Point d'entrée HTML
├── package.json
├── src/
│   ├── main.js         # Initialisation du jeu
│   ├── config.js       # Configuration Phaser (résolution, physique, scènes)
│   └── scenes/
│       ├── BootScene.js      # Démarrage (chargement minimal)
│       ├── PreloadScene.js   # Chargement des assets + barre de progression
│       ├── MenuScene.js      # Menu principal
│       └── GameScene.js      # Scène de jeu principale
└── assets/
    ├── images/         # Sprites, tiles, backgrounds
    ├── audio/          # Musiques et effets sonores
    └── tilemaps/       # Fichiers Tiled (.json)
```

## Lancer le projet

> Les modules ES nécessitent un serveur HTTP (pas d'ouverture directe du fichier).

```bash
npm run dev
```

Puis ouvrir : http://localhost:8080

## Contrôles (GameScene)

- **Flèche gauche / droite** : déplacer le joueur
- **Flèche haut** : sauter
- **Echap** : retourner au menu
