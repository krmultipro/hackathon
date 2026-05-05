export default class BootScene extends Phaser.Scene {
    constructor() {
        super({ key: 'BootScene' });
    }

    preload() {
        // Charger uniquement les assets nécessaires au chargement (barre de progression, etc.)
    }

    create() {
        this.scene.start('PreloadScene');
    }
}
